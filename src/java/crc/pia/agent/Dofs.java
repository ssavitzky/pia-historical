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
import java.io.File;
import java.io.DataInputStream;
import java.io.StringBufferInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Vector;

import java.net.URL;
import java.net.MalformedURLException;

import crc.pia.GenericAgent;
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

import crc.util.regexp.RegExp;
import crc.util.regexp.MatchInfo;
import crc.util.Utilities;

import w3c.www.http.HTTP;
public class Dofs extends GenericAgent {
  public boolean DEBUG = false;  
  /**
   * Respond to a DOFS request. 
   * Figure out whether it's for a file or an interform, and whether it's
   * to a sub-agent or to /dofs/ itself.
   */
  public Transaction respond(Transaction request, Resolver res){
    Transaction reply = null;
    String replyString = null;

    if( !request.isRequest() ) return null;

    URL url = request.requestURL();
    if( url == null )
      return null;

    String path   = url.getFile().toLowerCase();
    String myname = name().toLowerCase();
    String mytype = type().toLowerCase();

    /*
      Examine the path to see what we have:
      myname/path   -- this is a real file request.
      myname        -- home page InterForm
      mytype/myname -- Interforms for myname
      mytype/path   -- Interforms for DOFS
     */

    Agent agnt = this;

    if( !myname.equals(mytype) && path.startsWith("/"+myname+"/")){
      return retrieveFile( url, request );
    }else if( !myname.equals(mytype) && path.startsWith("/"+myname) && path.endsWith("/"+myname)){
      path = "/"+myname+"/home.if";
    }else {
      /* http://napa:7777/dofs/doc/foobar.if */
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
	}
      }
      if( agnt != null ){
	replyString = agnt.respondToInterform( request, path, res );
	InputStream in = null;

	if( replyString!= null )
	  in = new StringBufferInputStream( replyString );
	ByteStreamContent c = new ByteStreamContent( in );
	
	reply = new HTTPResponse( request, c );
      }
    }
    return reply;
  }

  /**
   * name and type needs to be set after this
   */
  public Dofs(){
    if( DEBUG )
      System.out.println("From Dofs generic constructor.");
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

    features = new Features( this );

    String myurl = "/"+ mytype + "/" + myname + "/" + "initialize.if";
    if( DEBUG )
      System.out.println("[GenericAgent]-->"+"Hi, I am in debugging mode.  No interform request is put onto the resolver.");
    else{
      Transaction request = createRequest("GET", myurl );
      Pia.instance().resolver().unshift( request );
    }
  }

  /**
   *
   */
  public String root(){
    String[] f = fileAttribute("root");
    if( f != null && f.length == 1 )
      return f[0];
    else
      return null;
  }

  /**
   *Transaction handling.
   * === We need to be able to handle CGI scripts and plain HTML
   * eventually.  We need this for the DOFS, in particular.
   * === URL-to-filetype mappings, whether interforms are permitted,
   * local interforms, and similar annotations belong in the
   * interform directory that corresponds to the DOFS agent.
   */


  /**
   * generate the HTML for a local directory.
   */
    protected Transaction retrieveDirectory( String path, Transaction request ){
      File myfile = new File( path );
      Transaction response =  null;

      if ( !myfile.exists() ){
	response =  new HTTPResponse( request );
	response.setStatus( HTTP.NOT_FOUND );
	response.setReason( "File " + path + " does not exist" );
	return response;
      }

      if ( !myfile.canRead() ){
	response =  new HTTPResponse( request );
	response.setStatus( HTTP.FORBIDDEN );
	response.setReason( "User does not have read permission" );
	return response;
      }

      //check if-modified-since
      long mtime = myfile.lastModified();
      String zdate = request.header("If-Modified-Since");
      if( zdate != null ){
	Date mydate = new Date( zdate );
	long time = mydate.getTime();
	if ( time >= mtime ){
	  response =  new HTTPResponse( request );
	  response.setStatus( HTTP.NOT_MODIFIED );
	  return response;
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
	  head = suckBody(  filepath );

	boolean all = optionAsBoolean("all");
	boolean ignore = ignoreFile(zfile, filepath);
	boolean todo = ! (all && ignore);
	if( todo )
	  url.addElement( "<LI> <a href=\"" + zfile + "\">" + zfile + "</a>" );
	  
      }
      // Ensure that the base URL is "/" terminated
      URL myurl = request.requestURL();
      String dpath = null;
      String mybase = myurl.toExternalForm();
      if( !mybase.endsWith("/") )
	dpath = mybase + "/";

      String next = null;
      if( head != null ) 
	next = head;
      else
	next = "<H1>Directory listing of "+ mybase +" </H1>";

      String allurls = new String();
      for(int j = 0; j < url.size(); j++){
	allurls += (String)url.elementAt( j );
	allurls += "\n";
      }

      String html = "\n" + "<HTML>\n<HEAD>" + "<TITLE>" + dpath + "</TITLE>" + "</HEAD>\n<BODY>" + next + "<h3>local path: " + path + "</h3>" + "<h3>DOFS path: " + dpath + "</h3>" + "<UL>" + allurls + "</UL>" + "</BODY>\n</HTML>\n";
     
      InputStream in = new StringBufferInputStream( html );
      ByteStreamContent bs = new ByteStreamContent( in );

      response = new HTTPResponse( request, bs);
      response.setStatus( HTTP.OK );
      Date mDate = new Date( mtime );
      response.setHeader( "Last-Modified", mDate.toGMTString() ); 
      response.setContentType("text/html");
      response.setContentLength( html.length() );
      response.setHeader("Version", version());
      return response;
    }


  /**
   * Retrieve the file at $url in order to satisfy $request.
   */
  protected Transaction retrieveFile ( URL url, Transaction request ){
    Transaction reply = null;
    File zfile = null;
    URL u = null;
    URL myurl = null;

    String filename = urlToFilename( url );

    if( filename == null ) return null;

    zfile = new File( filename );
    if( zfile.isDirectory() )
      return retrieveDirectory( filename, request );
    else{
      try{
	u = new URL( Pia.instance().url() + "/" );
	myurl = new URL( u, filename );
	
	String data = null;
	if( DEBUG ){
	  System.out.println("\n\nNote: result is suck in from foobar.html");
	  InputStream in = new FileInputStream ("foobar.html");
	  DataInputStream di = new DataInputStream( in );
	  String line;
	  data = new String();
	  try{
	    for(;;){
	      line = di.readLine();
	      if(line==null)break;
	      data += line;
	    }
	  }catch(IOException e){;}

	}else{
	  InputStream in = myurl.openStream();
	  Content c      = new ByteStreamContent( in );
	  data           = c.toString();
	}
	int index = -1;
	RegExp re = null;
	MatchInfo mi = null;

	String base = "<BASE HREF";
	String baseRefBegin = null;
	String afterBaseRef = null;
	String search = null;
	StringBuffer tmp = null;

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

	  InputStream newdata  = new StringBufferInputStream( new String(tmp) );
	  Content finalContent = new ByteStreamContent( newdata );
		      
	  reply = new HTTPResponse( request, finalContent );
	  reply.setHeader("Version", version());

	}

      }catch( MalformedURLException e1 ){
	if( DEBUG )
	  System.out.println( e1.toString() );
      }catch(IOException e2){
	if( DEBUG )
	  System.out.println( e2.toString() );
      }catch(Exception e3){
	if( DEBUG )
	  System.out.println( e3.toString() );
      }
    }

    return reply;
  }

  /**
   * @return the file name corresponding to this url
   * @return null unless url path begins with prefix
   */
  protected String urlToFilename(URL url){
    if( url == null ) return null;

    String myroot = root();
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
   *
   */
  protected String suckBody( String filename ){
    StringBuffer data = new StringBuffer();

    try{
      data = Utilities.readFrom( filename, data );
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
      if( DEBUG )
	System.out.println( e.toString() );
      return null;
    }
  }

  /**
   *
   */
  public boolean ignoreFile( String filename, String path ){
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

  public static Agency setupAgency(){
    Agency pentagon = new Agency("pentagon", "agency");

    System.out.println("\n\nDumping options -- name , type");
    System.out.println("Option for name: "+ pentagon.optionAsString("name"));
    System.out.println("Option for type: "+pentagon.optionAsString("type"));
    System.out.println("Version " + pentagon.version());
    String path = null;
    System.out.println("Agent url: " + pentagon.agentUrl( path ));
    pentagon.option("agent_directory", "~/pia/pentagon");
    System.out.println("Agent directory: " + pentagon.agentDirectory());
    pentagon.option("agent_file", "~/pia/pentagon/foobar.txt");
    String files[] = pentagon.fileAttribute("agent_file");
    System.out.println("Agent file: " + files[0]);


    System.out.println("\n\nTesting proxyFor -- http");
    String proxyString = pentagon.proxyFor("napa", "http");
    if( proxyString != null )
      System.out.println( proxyString );
    return pentagon;
  }

 private static void printusage(){
    System.out.println("Needs to know what kind of test");
    System.out.println("For test 1, here is the command --> java crc.pia.agent.Dofs -1 dofsagent.txt");
    System.out.println("For test 2, here is the command --> java crc.pia.agent.Dofs -2 dofsgetdir.txt");
    System.out.println("For test 3, here is the command --> java crc.pia.agent.Dofs -3 dofsgetfile.txt");
    System.out.println("For test 4, here is the command --> java crc.pia.agent.Dofs -4 dofsheader.txt");
  }

  /**
   * For testing.
   * 
   */ 
 public static void main(String[] args){

    if( args.length != 2 ){
      printusage();
      System.exit( 1 );
    }

    if( args[0].equals ("-1") && args[1] != null )
      test1( args[1] );
    else if( args[0].equals ("-2") && args[1] != null )
      test2( args[1] );
    else if( args[0].equals ("-3") && args[1] != null )
      test2( args[1] );
    else if( args[0].equals ("-4") && args[1] != null )
      test2( args[1] );
    else{
      printusage();
      System.exit( 1 );
    }

  }

  public static void test2(String filename){
    Agency pentagon = setupAgency();
    try{
      InputStream in = new FileInputStream (filename);
      Machine machine1 = new Machine();
      machine1.setInputStream( in );

      Transaction trans1 = new HTTPRequest( machine1 );
      Thread thread1 = new Thread( trans1 );
      thread1.start();

      for(;;){
	if( !thread1.isAlive() )
	  break;
      }
      trans1.assert("IsAgentRequest", new Boolean( true ) );

      System.out.println("\n\n------>>>>>>> Installing a Dofs agent <<<<<-----------");
      Hashtable ht = new Hashtable();
      ht.put("agent", "popart");
      ht.put("type", "dofs");
      ht.put("root", "~/");
      ht.put("all", "false");
      pentagon.install( ht );
      
      Resolver res = Pia.instance().resolver();

      // put dofs' machine as toMachine of transaction
      pentagon.actOn( trans1, res );

      // will eventually call getRequest of dofs' machine

      Transaction reply = trans1.handleRequest( res );
      if( reply != null ){
	String zheader = null;
	if (reply.headers()!=null)
	  zheader = reply.headersAsString();
	System.out.println("\n\nHere is the response's header from request's handleRequest "); 
	System.out.print( zheader );

	Content c = reply.contentObj();
	if( c!= null ){
	  System.out.println("\n\nHere is the response from request's handleRequest "); 
	  System.out.print( c.toString() );
	}
      }
    }catch(Exception e ){
      System.out.println( e.toString() );
    }

    System.out.println("done");
  }


  public static void test1(String filename){
    Agency pentagon = setupAgency();
    try{
      InputStream in = new FileInputStream (filename);
      Machine machine1 = new Machine();
      machine1.setInputStream( in );

      Transaction trans1 = new HTTPRequest( machine1 );
      Thread thread1 = new Thread( trans1 );
      thread1.start();

      for(;;){
	if( !thread1.isAlive() )
	  break;
      }
      trans1.assert("IsAgentRequest", new Boolean( true ) );

      System.out.println("\n\n------>>>>>>> Installing a Dofs agent <<<<<-----------");
      Hashtable ht = new Hashtable();
      ht.put("agent", "dofs");
      ht.put("type", "dofs");
      ht.put("root", "~/");
      ht.put("all", "false");
      pentagon.install( ht );
      
      Resolver res = Pia.instance().resolver();

      // put dofs' machine as toMachine of transaction
      pentagon.actOn( trans1, res );

      // will eventually call getRequest of dofs' machine
      Transaction reply = trans1.handleRequest( res );
      if( reply != null ){
	String zheader = null;
	if (reply.headers()!=null)
	  zheader = reply.headersAsString();
	System.out.println("\n\nHere is the response's header from request's handleRequest "); 
	System.out.print( zheader );

	Content c = reply.contentObj();
	if( c!= null ){
	  System.out.println("\n\nHere is the response from request's handleRequest "); 
	  System.out.print( c.toString() );
	}
      }
    }catch(Exception e ){
      System.out.println( e.toString() );
    }

    System.out.println("done");
  }





}






















































