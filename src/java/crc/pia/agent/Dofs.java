// Dofs.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/** 
 * This is the class for the ``DOFS'' agent, which handles requests
 *	for local files.
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
import crc.pia.Content;
import crc.pia.FileAccess;
import crc.ds.Features;
import crc.ds.Table;
import crc.ds.List;

import crc.util.Utilities;

import w3c.www.http.HTTP;
public class Dofs extends GenericAgent {
  /**
   * Respond to a DOFS request. 
   * 	Figure out whether it's for a file or an interform, and whether it's
   * 	to a sub-agent or to /DOFS/ itself.
   */
  public void respond(Transaction request, Resolver res)
       throws PiaRuntimeException{
    Transaction reply = null;
    String replyString = null;
    Pia.debug(this, "Inside Dofs respond...");

    if( !request.isRequest() ) return;
    Pia.debug(this, "After testing is request...");

    URL url = request.requestURL();
    if( url == null )
      return;

    String path   = url.getFile();
    String myname = name();
    String mytype = type();
    Agent agnt	  = this;

    Pia.debug(this, "path-->"+path);
    Pia.debug(this, "myname-->"+myname);
    Pia.debug(this, "mytype-->"+mytype);

    /*
     * Examine the path to see what we have:
     *	 myname/path   -- this is a real file request.
     *	 myname        -- redirect to myname/index.html if it exists.
     *	 mytype/myname -- Interforms for myname
     *	 mytype/path   -- Interforms for DOFS
     */

    if (!myname.equals(mytype)
	&& (path.equals("/"+myname) || path.startsWith("/"+myname+"/"))) {
      // (need to make sure that the name isn't a prefix of something)
      Pia.debug(this, ".../"+myname+"... -- file.");
      if (path.equals("/"+myname)) {
	redirectTo( request, path+"/" );
	return;
      }
      try {
	if( !request.method().equalsIgnoreCase("put") )
	  FileAccess.retrieveFile(urlToFilename(url), request, this);
	else
	  FileAccess.writeFile(urlToFilename(url), request, this);
      }catch(PiaRuntimeException e){
	throw e;
      }
    } else {
      if (!respondToInterform(request, res))
	  respondNotFound(request, url);
    }
  }
    

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /**
   * name and type needs to be set after this
   */
  public Dofs(){
    super();
  }

  public Dofs(String name, String type){
    super(name, type);
  }



  /************************************************************************
  ** Attribute access:
  ************************************************************************/

  /**
   * @return the path to the DOFS's root directory.
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


  /************************************************************************
  ** File access:
  ************************************************************************/

  /**
   *Transaction handling.
   * === We need to be able to handle CGI scripts and plain HTML
   *     eventually.  We need this for the DOFS, in particular.
   * === URL-to-filetype mappings, whether interforms are permitted,
   *     local interforms, and similar annotations belong in the
   *     interform directory that corresponds to the DOFS agent.
   */

  /**
   * Convert a DOFS URL to a corresponding filename.
   * @param url the URL to convert
   * @return the file name corresponding to this url.
   * 	Returns null unless url path begins with <code>/AgentName</code>
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
