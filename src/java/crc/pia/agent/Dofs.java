// Dofs.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/**
 * This is the class for the ``agency'' agent; i.e. the one that
 * handles requests directed at agents.  It slso owns the resolver,
 * which may not be a good idea.
 */

package crc.pia.agent;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.DataInputStream;
import java.io.StringBufferInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.util.Date;
import java.util.NoSuchElementException;
import java.io.FileNotFoundException;
import java.util.Vector;
import java.util.Properties;

import java.net.URL;
import java.net.MalformedURLException;

import crc.pia.PiaRuntimeException;
import crc.pia.GenericAgent;
import crc.pia.FormContent;
import crc.pia.Resolver;
import crc.pia.Agent;
import crc.pia.Pia;
import crc.pia.Transaction;
import crc.pia.Machine;
import crc.pia.HTTPRequest;
import crc.pia.HTTPResponse;
import crc.pia.Content;
import crc.pia.ByteStreamContent;
import crc.ds.Features;
import crc.ds.Table;
import crc.ds.List;

import crc.util.regexp.RegExp;
import crc.util.regexp.MatchInfo;
import crc.util.Utilities;

import w3c.www.http.HTTP;
public class Dofs extends GenericAgent {
  /**
   * Respond to a DOFS request. 
   * Figure out whether it's for a file or an in erform, and whether it's
   * to a sub-agent or to /dofs/ itself.
   */
  public void respond(Transaction request, Resolver res) throws PiaRuntimeException{
    Transaction reply = null;
    String replyString = null;
    Pia.debug(this, "Inside Dofs respond...");

    if( !request.isRequest() ) return;
    Pia.debug(this, "After tesing is request...");

    URL url = request.requestURL();
    if( url == null )
      return;

    String path   = url.getFile();
    String myname = name();
    String mytype = type();
    Pia.debug(this, "path-->"+path);
    Pia.debug(this, "myname-->"+myname);
    Pia.debug(this, "mytype-->"+mytype);

    /*
     * Examine the path to see what we have:
     *	 myname/path   -- this is a real file request.
     *	 myname        -- home page InterForm === should really be index.html
     *	 mytype/myname -- Interforms for myname
     *	 mytype/path   -- Interforms for DOFS
     */

    Agent agnt = this;

    if (!myname.equals(mytype) && path.startsWith("/"+myname+"/")){
      Pia.debug(this, ".../"+myname+"/... -- file.");
      try{
	retrieveFile( url, request );
      }catch(PiaRuntimeException e){
	throw e;
      }
    } else {
      if (!myname.equals(mytype) && path.startsWith("/"+myname)
	  && path.endsWith("/"+myname)) {
	Pia.debug(this, ".../"+myname+" -- home.");
	path = "/"+myname+"/home.if";
      } else {
	// http://napa:7777/dofs/doc/foobar.if
	RegExp re = null;
	MatchInfo mi = null;
	try{
	  re = new RegExp("^/" + mytype + "/([^/]+)" + "/");
	  mi = re.match( path );
	}catch(Exception e ){;}
	
	if(mi!=null){
	  String search = "/" + mytype + "/";
	  try{
	    re = new RegExp("[^/]+/");
	    mi = re.match( path.substring( search.length() ));
	  }catch(Exception e){;}
	  
	  String match = mi.matchString();
	  
	  // get name only
	  String name = null;
	  if(mytype.equals( myname ))
	    name = myname;
	  else
	    name  = match.substring(0, match.length() -1);
	  
	  Agent zAgnt = res.agent( name );
	  if( zAgnt != null ){
	    agnt = zAgnt;
	    path = path.substring( ("/"+mytype).length() );
	    Pia.debug(this, "The path for type-->"+path);
	  }
	}
      }
      if( agnt != null ){
	Pia.debug(this, "Running interform...");
	URL myurl = null;

	try{
	  myurl = new URL(url.getProtocol(), myname, url.getPort(), path);
	}catch(MalformedURLException e){}

	if (! agnt.respondToInterform( request, myurl, res )) {
	  throw new PiaRuntimeException(this, "respond",
					"No InterForm file found for "+
					url.toExternalForm());
	}
      }
    }
  }
    
  /**
   * name and type needs to be set after this
   */
  public Dofs(){
    Pia.debug(this, "From Dofs generic constructor.");
  }

  public Dofs(String name, String type){
    super(name, type);
  }

  /**
   * initialize 
   */
  public void initialize(){
    String myname = name();
    String mytype = "Dofs";
    type( mytype );

    // === [ss] I don't think these are needed; Agency routes it.
    //matchCriterion("IsRequest", true);
    //matchCriterion("IsAgentRequest",true);
    /*
    String myurl = "/"+ mytype + "/" + myname + "/" + "initialize.if";
    if( DEBUG )
      System.out.println("[GenericAgent]-->"+"Hi, I am in debugging mode.  No interform request is put onto the resolver.");
    else{
      request = createRequest("GET", myurl );
    }
    */
  }

  /**
   *
   */
  public String root(){
    List f = fileAttribute("root");
    if( f != null && f.nItems() > 0 ){
      String zroot = (String)f.at(0);
      Pia.debug(this, "the root is--->" + zroot);
      return zroot;
    }
    else{
      Pia.debug(this, "can not find root path");
      return null;
    }
  }

  /**
   *Transaction handling.
   * === We need to be able to handle CGI scripts and plain HTML
   * eventually.  We need this for the DOFS, in particular.
   * === URL-to-filetype mappings, whether interforms are permitted,
   * local interforms, and similar annotations belong in the
   * interform directory that corresponds to the DOFS agent.
   */

  /************************************************************************
  ** File handling:
  **	These utilities are static, and really belong in their own class.
  **	They can be used by other agents for retrieving non-interform files.
  ************************************************************************/

  /**
   * generate the HTML for a local directory.
   */
    protected static void retrieveDirectory( String path, Transaction request,
					     Agent agent ){
      File myfile = new File( path );
      Transaction response =  null;

      if ( !myfile.exists() ){
	response =  new HTTPResponse( request, false );
	response.setStatus( HTTP.NOT_FOUND );
	response.setReason( "File " + path + " does not exist" );
	response.startThread();
      }

      if ( !myfile.canRead() ){
	response =  new HTTPResponse( request, false );
	response.setStatus( HTTP.FORBIDDEN );
	response.setReason( "User does not have read permission" );
	response.startThread();
      }

      //check if-modified-since
      long mtime = myfile.lastModified();
      String zdate = request.header("If-Modified-Since");
      if( zdate != null ){
	Date mydate = new Date( zdate );
	long time = mydate.getTime();
	if ( time >= mtime ){
	  response =  new HTTPResponse( request, false );
	  response.setStatus( HTTP.NOT_MODIFIED );
	  response.startThread();
	}
      }

      // Ok, should be an OK response by now...
      String[] ls;
      int i;

      File f = null;
      String filepath = null;
      String head = null;
      Vector url = new Vector();
      RegExp re = null;
      MatchInfo mi = null;

      boolean all = agent.optionAsBoolean("all");

      // Ensure that the base URL is "/" terminated
      URL myurl = request.requestURL();
      String mybase = myurl.toExternalForm(); // mybase = base for href's
      if( !mybase.endsWith("/") ) mybase += "/";

      String mypath = myurl.getFile(); // mypath = path for heading and title

      for (ls = myfile.list(), i = 0; ls != null && i < ls.length; i++){
	String zfile = ls[i];

	f = myfile;
	f = new File(f, zfile);

	filepath = f.getPath();
	if ( f.isDirectory() )
	  filepath += f.separatorChar;

	try{
	  re = new RegExp("^HEADER.*$");
	  mi = re.match( zfile );
	}catch(Exception e){;}
	if( mi != null && !zfile.endsWith("~") )
	  head = suckBody(filepath);

	if (all || !ignoreFile(zfile, filepath))
	  url.addElement( "<LI> <a href=\"" + mybase+zfile + "\">"
			  + zfile + "</a>" );
	  
      }

      if (head == null) head = "<H1>Directory listing of "+ mybase +"</H1>";

      String allurls = new String();
      for(int j = 0; j < url.size(); j++){
	allurls += (String)url.elementAt( j );
	allurls += "\n";
      }

      String html = "\n" + "<HTML>\n<HEAD>" + "<TITLE>" + mypath + "</TITLE>"
	+ "</HEAD>\n<BODY>" + head
	+ "<h3>local path: " + path + "</h3>"
	+ "<h3>DOFS path: " + mypath + "</h3>"
	+ "<UL>" + allurls + "</UL>" + "</BODY>\n</HTML>\n";
     
      InputStream in = new StringBufferInputStream( html );
      ByteStreamContent bs = new ByteStreamContent( in );

      response = new HTTPResponse( request, false);
      response.setContentObj( bs );
      response.setStatus( HTTP.OK );
      Date mDate = new Date( mtime );
      response.setHeader( "Last-Modified", mDate.toGMTString() ); 
      response.setContentType("text/html");
      response.setContentLength( html.length() );
      response.setHeader("Version", agent.version());
      response.startThread();
    }


  /**
   * content type mapping
   */
  protected static String contentType( String fn ){
    String lfilename = fn.toLowerCase();

    String fileExt     = null;
    String contentType = "text/plain";
    
    Properties map = Pia.instance().piaFileMapping();
       
    //find extension
    int i = lfilename.lastIndexOf('.');
    if( i != -1 )
      fileExt = lfilename.substring( i + 1);
    
    //get content type
    if( fileExt != null && map.containsKey( fileExt ) )
      contentType = (String) map.get( fileExt );
    
    return contentType;
  }

  /**
   * Retrieve the file at $url in order to satisfy $request.
   */
  protected void retrieveFile ( URL url, Transaction request ) {
    String filename = urlToFilename( url );

    retrieveFile(urlToFilename(url), request, this);
  }

  /** 
   * Retrieve a file or directory and respond to the given request with it.
   */
  public static void retrieveFile ( String filename, Transaction request,
				    Agent agent )
       throws PiaRuntimeException {

    Transaction reply = null;

    URL u = null;
    URL myurl = null;

    RegExp re = null;
    MatchInfo mi = null;

    if( filename == null ){
      String msg = "No file specified.\n";
      throw new PiaRuntimeException (agent, "retrieveFile" , msg) ;
    }

    File zfile = new File( filename );
    if ( ! zfile.exists()) {
      String msg = "File <code>"+filename+"</code> not found by agent "
	+ agent.name();
      request.errorResponse(HTTP.NOT_FOUND, msg);
    } else if( zfile.isDirectory() ) {
      retrieveDirectory( filename, request, agent );
    } else {

      reply = new HTTPResponse( request, false );
      reply.setStatus( 200 );
      reply.setReason( "OK" );
      reply.setHeader("Version", agent.version());

      try{
	if( filename != null )
	  Pia.debug(agent, "Retrieving file :"+ filename );
	String data = null;
	byte[] fromFile = null;


	fromFile = Utilities.readFrom( filename );
	data = new String ( fromFile,0,0, fromFile.length);

	String contentType = contentType( filename );
	if( contentType.indexOf("html") == -1 ){

	  InputStream newdata  = new ByteArrayInputStream( fromFile );
	  Content finalContent = new ByteStreamContent( newdata );

	  reply.setContentType( contentType );
	  reply.setContentObj( finalContent );
	  reply.startThread();

	}else{

	  // yes I am a html file

	  // Fixing <base> wastes time === probably best not to do it.

	  String base = "<BASE HREF";
	  String baseRefBegin = null;
	  String afterBaseRef = null;
	  String search = null;
	  StringBuffer tmp = new StringBuffer( data );
	  
	  int index;
	  if( (index = data.indexOf( base )) != -1 ){
	    baseRefBegin = "<BASE HREF=\"";
	    
	    // data after <BASE HREF=" 
	    afterBaseRef = data.substring( index + baseRefBegin.length() );
	    
	    search     = "<BASE HREF=\".*\">";
	    
	    int afterindex = -1;
	    re = new RegExp( search );
	    mi = re.match( data );
	    
	    if( mi != null )
	      afterindex = mi.end();
	    
	    tmp = new StringBuffer();
	    
	    tmp.append( data.substring(0, index + baseRefBegin.length() ) );
	    tmp.append( myurl.toExternalForm()+ "\">" );
	    tmp.append( data.substring( afterindex ) );
	  }
	  Pia.debug(agent, "before creating reply" );
	  InputStream newdata  = new StringBufferInputStream( new String(tmp) );
	  Content finalContent = new ByteStreamContent( newdata );
	  
	  reply.setContentType( contentType );
	  reply.setContentObj( finalContent );
	  reply.startThread();

	}


      }catch(NullPointerException e1){
	String msg = "Bad file name.\n";
	e1.printStackTrace();
	throw new PiaRuntimeException (agent, "retrieveFile", msg) ;
      }catch(FileNotFoundException e2){
	String msg = "File not found.\n";
	throw new PiaRuntimeException (agent, "retrieveFile", msg) ;
      }catch(IOException e2){
	throw new PiaRuntimeException (agent, "retrieveFile", e2.toString()) ;
      }catch(Exception e3){
      }
    }

  }

  /**
   * @return the file name corresponding to this url
   * @return null unless url path begins with prefix
   */
  protected String urlToFilename(URL url){
    if( url == null ) return null;

    String myroot = root();
    if( myroot == null ) return null;

    if( myroot.endsWith("/") )
      myroot = myroot.substring(0, myroot.length()-1);
    String mypath = url.getFile();
    String prefix = "/" + name();

    if( !mypath.startsWith( prefix ) )
      return null;

    String myfilename = myroot + mypath.substring( prefix.length() );
    return myfilename;
  }


  /**
   * Suck in the body part of an HTML file, as a string.
   */
  protected static String suckBody( String filename ){
    StringBuffer data = new StringBuffer();

    try{
      byte [] fromfile = Utilities.readFrom( filename );
      data = new StringBuffer ( new String ( fromfile,0,0, fromfile.length) );
      String zhead = "<head.*</head>";
      String htmlBeginBrak = "<html>";
      String htmlEndBrak   = "</html>";
      
      RegExp re = new RegExp(zhead);
      String nohead = re.substitute(new String( data ),"", true);

      re = new RegExp( htmlBeginBrak );
      String nobegin = re.substitute(nohead,"", true);

      re = new RegExp( htmlEndBrak );
      String zfinal = re.substitute(nobegin,"", true);

      return zfinal;
    }catch(Exception e ){
      return null;
    }
  }

  /**
   * Decide whether to ignore a file, based on its name.
   */
  public static boolean ignoreFile( String filename, String path ){
    RegExp re = null;
    MatchInfo mi = null; 
    try{
      re = new RegExp("~$");
      mi = re.match( filename );
    }catch(Exception e){;}

    if( mi != null )                 return true;
    if ( filename.startsWith("^#") ) return true;
    if ( filename == "./" )          return true;
    if ( filename == "CVS/" )        return true;
    if ( filename == "RCS/" )        return true;
    return false;
  }

  /**
   * should we ignore this request?
   * for now ignore unless file exists
   */
  protected boolean ignore( Transaction request ){
    String filename = urlToFilename( request.requestURL() );
    File zfile = new File( filename );
    return ! zfile.exists();
  }

}






















































