// FileAccess.java
// $Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


package crc.pia;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.FileReader;
import java.io.StringReader;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Date;
import java.util.Vector;
import java.util.Enumeration;
import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.util.Locale;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.net.URL;
import java.net.MalformedURLException;

import crc.ds.Sorter;
import crc.ds.SortTree;
import crc.ds.Association;
import crc.ds.List;

// import crc.sgml.Tokens;

import crc.content.ByteStreamContent;

import gnu.regexp.RegExp;
import gnu.regexp.MatchInfo;
import crc.util.Utilities;

import java.util.Properties;
import w3c.www.http.HTTP;

/** File-handling utilities.  
 *	All methods in this class are static. 
 */
public class FileAccess {

  /** If this flag is true, fix &lt;BASE&gt; tags in HTML files.  Expensive. */

  public static boolean FIX_BASE = false;

  private static String filesep = System.getProperty("file.separator");

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
	request.errorResponse( HTTP.NOT_FOUND, 
			       "File " + path + " does not exist" );
	return;
      }

      if ( !myfile.canRead() ){
	request.errorResponse( HTTP.FORBIDDEN,
			       "User does not have read permission" );
	return;
      }

      //check if-modified-since
      long mtime = myfile.lastModified();
      String zdate = request.header("If-Modified-Since");
      if( zdate != null && !"".equals(zdate)) try {
	Date mydate = new Date(zdate); // === no good non-deprecated equivalent 
	long time = mydate.getTime();
	if ( time >= mtime ){
	  response =  new HTTPResponse( request, false );
	  response.setStatus( HTTP.NOT_MODIFIED );
	  response.startThread();
	  return;
	}
      } catch (java.lang.IllegalArgumentException e) {
	// Sometimes Netscape produces a date Java can't handle.
      }

      // Ok, should be an OK response by now...
      String[] ls;
      int i;

      File f = null;
      String filepath = null;
      String head = null;

      String entry;
      SortTree entries = new SortTree();

      boolean noTrailingSlash = false;

      boolean all = false;
      if(agent.get("all") != null)
	all = true;

      // Ensure that the base URL is "/" terminated
      URL myurl = request.requestURL();
      String mybase = myurl.toExternalForm(); // mybase = base for href's
      if( !mybase.endsWith("/") ) {
	noTrailingSlash = true;
	mybase += "/";
      }

      String mypath = myurl.getFile(); // mypath = path for heading and title

      if (noTrailingSlash) {
	mypath += "/";
	f = new File(myfile, "index.html");
	if (f.exists()) {
	  redirectTo(request, mypath + "index.html");
	  return;
	}
      }

      for (ls = myfile.list(), i = 0; ls != null && i < ls.length; i++){
	String zfile = ls[i];

	f = new File(myfile, zfile);
	filepath = f.getPath();

	if (zfile.toLowerCase().equals("header.html") 
	    && ! ignoreFile(zfile, filepath)) {
	  head = suckBody(filepath);
	  if (!all) continue;
	} else if (zfile.toLowerCase().equals("header")
		   && ! ignoreFile(zfile, filepath)) {
	  head = "<pre>"+suckBody(filepath)+"</pre>";
	  if (!all) continue;
	}

	if (all || !ignoreFile(zfile, filepath)) {
	  entry = "<LI> <a href=\"" + mypath + zfile + "\">" + zfile + "</a>" ;
	  if ( f.isDirectory() )
	    entry += " <a href=\"" + mypath + zfile + "/\">" + " / " + "</a>";

	  entries.insert(Association.associate(entry, zfile));
	}
      }

      /* Java doesn't list "..", so include it here. */

      entry = "<LI> <a href=\"" + mypath + ".." + "\">" + ".." + "</a>";
      entry += " <a href=\"" + mypath +  ".." + "/\">" + " / " + "</a>";
      entries.insert(Association.associate(entry, ".."));

      if (head == null) head = "<H1>Directory listing of "+ mybase +"</H1>";

      List sortList = new List();
      entries.ascendingValues(sortList);
      String allurls = sortList.join("\n");

      String html = "\n" + "<HTML>\n<HEAD>" + "<TITLE>" + mypath + "</TITLE>"
	//	+ "<BASE href=\"" + mybase + "\">"
	+ "</HEAD>\n<BODY>" + head
	+ "<h3><a href=\"/" + agent.type() + "/" + agent.name() + "\">/" 
	  + agent.type() + "/" + agent.name() + ":</a> " + mypath + "</h3>"
	+ "<h4><a href=\"file:" + path + "\">file:" + path + "</a></h4>"
	+ "<UL>" + allurls + "</UL>" + "</BODY>\n</HTML>\n";
	  
      Content bs = new crc.content.text.Default(new StringReader(html));
      
      response = new HTTPResponse( request, false);
      response.setContentObj( bs );
      response.setStatus( HTTP.OK );
      Date mDate = new Date( mtime );
      response.setHeader( "Last-Modified", toGMTString(mDate) ); 
      response.setContentType("text/html");
      //response.setContentLength( html.length() );
      response.setHeader("Version", agent.version());
      response.startThread();
    }

  /** Convert a Date to a String in the GMT timezone according to
   *	the Internet (IETF) standard.  Replaces the deprecated
   *	Date.toGMTString, from which the code has been shamelessly
   *	copied. 
   *
   * @see java.util.Date#toGMTString
   */
  public static String toGMTString(Date date) {
    DateFormat formatter
      = new SimpleDateFormat("d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
    formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    return formatter.format(date);
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
	agent.respondNotFound(request, filename);
      } else if( zfile.isDirectory() ) {
	retrieveDirectory( filename, request, agent );
      } else {
	
	reply = new HTTPResponse( request, false );
	reply.setStatus( 200 );
	reply.setReason( "OK" );
	reply.setHeader( "Version", agent.version() );
	Date mDate = new Date(zfile.lastModified());
	reply.setHeader( "Last-Modified", toGMTString(mDate) ); 
	
	try{
	  Pia.instance().debug(agent, "Retrieving file :"+ filename );
	  
	  String contentType = contentType( filename );
	  if(contentType.indexOf("html") == -1 ){

	    FileInputStream newdata = new FileInputStream(filename);
	    Content finalContent = new ByteStreamContent( newdata );
	    
	    reply.setContentType( contentType );
	    reply.setContentObj( finalContent );
	    reply.startThread();

	  } else if (! FIX_BASE ) {
	    FileReader newdata = new FileReader(filename);
	    Content finalContent = new crc.content.text.html( newdata );
	    
	    reply.setContentType( contentType );
	    reply.setContentObj( finalContent );
	    reply.startThread();

	  }else{
	    // yes I am a html file
	    
	    String data = Utilities.readStringFrom( filename );

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
	    Pia.instance().debug(agent, "before creating reply" );
	    String ts = new String(tmp);
	    Reader newdata  = new StringReader( ts );
	    Content finalContent = new crc.content.text.html( newdata );
	    
	    reply.setContentType( contentType );
	    reply.setContentObj( finalContent );
	    reply.startThread();
	  }
	  
	}catch(NullPointerException e1){
	  String msg = "Bad file name.\n";
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
   * Decide whether to ignore a file, based on its name.
   *	This should really be done by a filter.
   *	@see java.io.FilenameFilter
   */
  public static boolean ignoreFile( String filename, String path ){
    if ( filename.startsWith("#") ) 	return true;
    if ( filename.startsWith(".#") ) 	return true;
    if ( filename.endsWith("~") )	return true;
    if ( filename.endsWith(".bak") )	return true;
    if ( filename.equals(".") )		return true;
    if ( filename.equals("CVS") )	return true;
    if ( filename.equals("RCS") )	return true;
    return false;
  }
   

  /**
   * Send redirection to client
   */
  public static boolean redirectTo( Transaction req, String path ) {
    URL oldUrl = req.requestURL();

    URL redirUrl = null;
    String redirUrlString = null;

    try{
      redirUrl = new URL(oldUrl, path);
      redirUrlString = redirUrl.toExternalForm();
    }catch(MalformedURLException e){
      String msg = "Malformed URL redirecting to "+path;
      throw new PiaRuntimeException(null, "redirectTo", msg);
    }

    String msg ="Redirecting " + oldUrl.toExternalForm()
      + " to:" + redirUrlString; 

    Pia.debug(msg);

    Content ct = new crc.content.text.html( new StringReader(msg) );
    Transaction response = new HTTPResponse( Pia.instance().thisMachine,
					     req.fromMachine(), ct, false);
    response.setHeader("Location", redirUrlString);
    response.setStatus(HTTP.MOVED_PERMANENTLY);
    response.setContentLength( msg.length() );
    response.startThread();
    return true;
  }

  /**
   * Suck in the body part of an HTML file, as a string.
   *	If there is no &lt;body&gt; tag the entire file is returned.
   */
  protected static String suckBody( String filename ){
    StringBuffer data = new StringBuffer();

    try{
      data = new StringBuffer ( Utilities.readStringFrom(filename) );
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
   * Content type mapping given file name or URL
   * If content type cannot be determined, return deflt
   */
  public static String contentType(String fn, String deflt)
    {
      String lfilename = fn.toLowerCase();
      
      String fileExt     = null;
      String contentType = deflt;
      
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
   * Content type mapping given file name or URL
   * Use text/plain as the default if the content type
   * cannot be determined
   * (For backwards compatibility)
   */
  public static String contentType(String fn)
    {
      return contentType(fn,"text/plain");
    }


  private static void sendReply(int code, String reason, Transaction request,
				Agent agent ){
    Transaction reply = null;
    
    reply = new HTTPResponse( request, false );
    reply.setStatus( code );
    reply.setReason( reason );
    reply.setHeader( "Version", agent.version() );
    Content c =
      new crc.content.text.StringContent("Your file has been written.");
    reply.setContentType( "text/plain" );
    reply.setContentObj( c );
    reply.startThread();
  }

    /** 
     * Write a file and respond to the given request.
     */
    public static void writeFile ( String filename, Transaction request,
				      Agent agent )
      throws PiaRuntimeException {
      FileOutputStream destination = null;
      
      if( filename == null ){
	String msg = "No file specified.\n";
	throw new PiaRuntimeException (agent, "writeFile" , msg) ;
      }
      
      File destFile = new File( filename );
      File parentDir = parent( destFile );

      if( parentDir == null || !parentDir.exists() ){
	String msg = "Directory does not exist:"+filename;
	throw new PiaRuntimeException(null, "writeFile", msg);
      }

      if( !parentDir.canWrite() ){
	String msg = "Directory is unwritable:"+filename;
	throw new PiaRuntimeException(null, "writeFile", msg);
      }
      
      try{
	Content putcontent = request.contentObj();

	destination = new FileOutputStream( filename );
	if( putcontent != null && destination !=null ){
	  putcontent.writeTo( destination );
	  destination.flush();
	}

	sendReply(200,"OK", request, agent);

      }catch(IOException e){
	e.printStackTrace();
	String msg = e.getMessage();
	throw new PiaRuntimeException (agent, "writeFile", msg) ;
      }catch(crc.pia.ContentOperationUnavailable ee){
	ee.printStackTrace();
	String msg = ee.getMessage();
	throw new PiaRuntimeException (agent, "writeFile", msg) ;
      }finally{
	if( destination!=null )
	  try{ destination.close(); 
	}catch(IOException e){}
      }
    }

  private static File parent(File f){
    String dirname = f.getParent();
    return new File(dirname);
  }

}



