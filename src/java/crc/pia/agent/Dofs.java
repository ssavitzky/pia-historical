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
import crc.pia.FileAccess;
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

    boolean redirection = false;
    try{
      redirection = isRedirection( request, url );
    }catch(FileNotFoundException e1){
      return;
    }catch(MalformedURLException e2){
      return;
    }

    if( redirection ) return;

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

  /**
   * Retrieve the file at $url in order to satisfy $request.
   */
  protected void retrieveFile ( URL url, Transaction request ) {
    String filename = urlToFilename( url );

    FileAccess.retrieveFile(urlToFilename(url), request, this);
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
   * should we ignore this request?
   * for now ignore unless file exists
   */
  protected boolean ignore( Transaction request ){
    String filename = urlToFilename( request.requestURL() );
    File zfile = new File( filename );
    return ! zfile.exists();
  }

}






















































