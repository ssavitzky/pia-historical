// Machine.java
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


/** 
 * The PIA end of an HTTP transaction.  The Machine serves as a
 * source or sink for data being received from or sent to a client or
 * server.  <p>
 *
 * Ideally these should be persistent so that we can keep track of
 * what kind of browser or server we're talking to, but at the moment
 * we don't do that.  */

package crc.pia;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.StringBufferInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.net.ServerSocket;
import java.net.InetAddress;

import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.Enumeration;

import crc.pia.Content;
import crc.pia.HTTPResponse;
import crc.pia.Transaction;
import crc.pia.Resolver;
import crc.util.Timer;

import crc.ds.Table;
import crc.ds.List;

import w3c.www.http.HTTP;

import crc.ds.UnaryFunctor;


public class Machine implements java.io.Serializable {

  /************************************************************************
  ** Components:
  ************************************************************************/

  /**
   * Attribute index - client hostName
   */
  protected String hostName;

  /**
   * Attribute index - port
   */
  protected int port = -1;

  /**
   * Attribute index - client socket
   */
  protected Socket socket;

  /**
   * Attribute index - proxy
   */
  protected Table proxyTab = new Table();

  /**
   * Attribute index - input stream
   */
  protected InputStream inputStream;

  /**
   * Attribute index - output stream
   */
  protected OutputStream outputStream;


  /************************************************************************
  ** Access Functions:
  ************************************************************************/

  /**
   * set inputStream -- use for debuggging
   */
  public void setInputStream( InputStream in ){
    inputStream = in;
  }

  /**
   * set outputStream -- use for debugging
   */
  public void setOutputStream( OutputStream out ){
    outputStream = out;
  }

  /**
   * Return inputStream.  If it doesn't exist, create one from the socket. 
   */
  public InputStream inputStream() throws IOException{
    if( inputStream!= null )
      return inputStream;

    if( socket != null )
      try
      {
	inputStream = socket.getInputStream();
	return inputStream;
      }catch(IOException e){
	Pia.debug( this, "Exception while getting socket input stream." );
	Pia.errLog( this, "Exception while getting socket input stream." );
	throw new IOException( e.getMessage() );
      }
    else
      throw new IOException( "Socket is not valid." );
  }

  /**
   * Return outputStream.  If it hasn't been initialized, create one from
   *	the socket. 
   */
  public OutputStream outputStream() throws IOException{
    if( outputStream!=null ) 
      return outputStream;

    if( socket != null )
      try
      {
	outputStream = socket.getOutputStream();
	return outputStream;
      }catch(IOException e){
	  Pia.debug(this, "Exception while getting socket output stream." );
	  Pia.errLog( this, "Exception while getting socket output stream." );
	throw new IOException( e.getMessage() );
      }    
    else
      throw new IOException( "Socket is not valid." );
  }

 /**
  * Closing socket.
  */
  public void closeConnection() {
    try {
      if( socket != null ) socket.close();
      if( inputStream != null ) inputStream.close();
      if( outputStream != null ) outputStream.close();
    }catch(IOException e) {

	Pia.debug( this, "Exception while closing socket streams." );
	Pia.errLog( this, "Exception while closing socket streams." );
    }
  }

  /************************************************************************
  ** Sending Responses to Client:
  ************************************************************************/

  /** 
   * Send a response through the output stream.  The data to be sent
   * 	come from the Transaction's Content object.  If there are
   * 	controls associated with this response they are added.  <p>
   *
   *	Note that controls may be added by the Machine 
   *	(makes request to content)  because the machine is in
   *	a position to know what client the response is destined for, 
   *	and hence in what form to send the controls.  (Of course,
   *	at the moment we are not taking advantage of this ability.)
   *
   *	@param reply	the Transaction being responded to.
   *	@param resolver	the resolver on which to queue the Transaction.
   *	@exception PiaRuntimeException if errors occur while transmitting
   *		the data.  (Consolidates information from several possible
   *		exceptions so that the caller only has to handle one.)
   */
  public void sendResponse (Transaction reply, Resolver resolver)
       throws PiaRuntimeException {
    OutputStream out = null;
    StringBuffer ctrlStrings = null;
    Content content = null;
    Content plainContent = null;
    boolean isTextHtml = false;
    String contentString = null;

    try{
      out = outputStream();

      String outputString =
	"HTTP/1.0 " + reply.statusCode() + " " + reply.reason() + "\n";

      String type = reply.contentType();

      /* If the type is "text/html", see if there are controls that we
       * can append.  From here on we use the presence of ctrlStrings
       * to decide whether we need to do anything with the controls. */

      if( type != null && type.toLowerCase().indexOf("text/html") != -1 ){
      
	isTextHtml = true;

	Pia.debug(this, "Sucking controls...");
	List c = reply.controls();
	if( c != null ){
	  ctrlStrings = new StringBuffer();
	  Enumeration els = c.elements();
	  while( els.hasMoreElements() ){
	    try{
	      Object o = els.nextElement();
	      if( o instanceof String )
		ctrlStrings.append( (String)o + " " );
	    }catch(Exception e){}
	  }
	}
      }
      
      plainContent = content = reply.contentObj();

      // Increase content length by the size of the control string.
      
      if( ctrlStrings!= null && ctrlStrings.length() > 0 ){
	try{
	   content.add( ctrlStrings, 0); // 0 = front, -1 =  at end
	}catch(Exception e){
	  Pia.debug(this, " CONTROLS did not make it");
	}
      }
      
      // dump header
      Pia.debug(this, "Transmitting firstline and header...");
//      System.out.println("Transmitting firstline and header...\n"+outputString);
     
      shipOutput( out, outputString, false );

      Pia.debug(this, outputString);
      String headers = reply.headersAsString(); 
      Pia.debug(this, headers);

      shipOutput( out, headers, false );

      // send the content down the wire
      content.writeTo(out);

      Pia.debug(this, "Flushing...");
      out.flush();
      closeConnection();
      
    } catch (PiaRuntimeException e){
      Pia.debug(this, " unknown PIA error...");
      String msg = "Client closed connection...\n";
      closeConnection();
      throw new PiaRuntimeException (this, "sendResponse", msg) ;

    } catch (ContentOperationUnavailable e1) {
      Pia.debug(this, "  content will not send data to stream...");
      String msg = " content got corrupted...\n";
      closeConnection();
      throw new PiaRuntimeException (this, "sendResponse", msg) ;

    }catch(IOException e2){
      Pia.debug(this, "Client close connection...");
      String msg = "Client close connection...\n";
      closeConnection();
      // no REASON TO THROW EXCEPTION HERE -- CLIENT just bailed
    }
    

  }
  /**
   * Ship a string to the OutputStream.
   */
  private void shipOutput(OutputStream out, String s, boolean withnewline)
       throws PiaRuntimeException{
    byte[] bytestring = null;

    if( s == null )
      return;
//    System.out.println("shipping "+s);
    
    bytestring = s.getBytes();

    try{
      if( bytestring != null ){
//	System.out.println("bytestring is "+bytestring.length +" long ..."+s);
	
	out.write( bytestring, 0, bytestring.length  );
      } else 
	{System.out.print("bytestring is null");
	}
      

      
      if( withnewline ){
//	System.out.println("(newline)");
	out.write( '\n' );
      }
      
    } catch(IOException e) {
//      System.out.println("SHIPPING FAILED");

      
      Pia.debug(this, e.getClass().getName());
      String msg = "Can not write...\n";
      throw new PiaRuntimeException (this, "shipOutput", msg) ;
    }
  }

  private void sendImageContent(OutputStream out, Content c)
       throws PiaRuntimeException{
     byte[]buffer = new byte[1024];
    int bytesRead;
    
    try{
 //     while(true){
//	bytesRead = c.read( buffer, 0, 1024 );
//	if( bytesRead == -1 ) break;
bytesRead=c.writeTo(out);
//	out.write( buffer, 0, bytesRead );
	Pia.debug(this, "the write length is---->"+Integer.toString(bytesRead));
//      }
    }catch(IOException e){
      Pia.debug(this, e.getClass().getName());
      String msg = "Can not write...\n";
      throw new PiaRuntimeException (this
				     , "sendImageContent"
				     , msg) ;
      }catch(ContentOperationUnavailable e){
      Pia.debug(this, e.getClass().getName());
      String msg = "Can not write...\n";
      throw new PiaRuntimeException (this
				     , "sendImageContent"
				     , msg) ;
    }
  }

  /************************************************************************
  ** Sending Requests, Receiving Responses from Server:
  ************************************************************************/

  private byte[] suckData( InputStream input )throws IOException{
    byte[]buffer = new byte[1024];
    int bytesRead;
    HttpBuffer data = new HttpBuffer(); 
    
    try{
      while(true){
	bytesRead = input.read( buffer, 0, 1024 );
	if( bytesRead == -1 ) break;
	  data.append( buffer, 0, bytesRead );
      }
      Pia.debug("the read length is---->"+Integer.toString(data.length()));
      return data.getByteCopy(); 
    }catch(IOException e2){
      throw e2;
    }

    }


  /**
   * Get request through a proxy by opening the proxy socket
   */
  protected void getReqThruSock(URL url, Transaction request, Resolver resolver)
       throws PiaRuntimeException, IOException, UnknownHostException {

    int zport = 80;
    String zhost;

    Pia.debug(this, "Getting data through proxy request") ;
    int p        = url.getPort();
    zport        = (p == -1) ? 80 : p;
    zhost        = url.getHost();
    try{
      socket = new Socket(zhost, zport);
      outputStream = socket.getOutputStream();
      inputStream  = new BufferedInputStream( socket.getInputStream() );

      request.printOn( outputStream );
      outputStream.flush();
      
      new HTTPResponse(request, this);


    }catch(UnknownHostException ue){
      throw ue;
    }catch(IOException e){
      throw e;
    }
    Pia.debug(this, "Leaving proxy request");
  }

  /**
   * Get request data and create a response with the data 
   */
  public void getRequest(Transaction request, Resolver resolver)
       throws PiaRuntimeException, IOException, UnknownHostException  {

    URL proxy;
    URL agentURL;
    URL url;
    URLConnection agent;

    url = request.requestURL();

    Timer ztimer = new Timer( new zTimeout(), request );
    ztimer.setTimeout(Pia.instance().requestTimeout());
    proxy = proxy( request.protocol() );
    try{
      if( proxy != null )
	getReqThruSock( proxy, request, resolver);
      else
	if( url != null )
	  getReqThruSock( url, request, resolver );
    }catch(PiaRuntimeException e){
      throw e;
    }catch(UnknownHostException e2){
      throw e2;
    } catch (IOException e) {
      throw e;
    }finally{
      ztimer.stop();
    }
  }


  /**
   * Get proxy url given protocol scheme 
   * @return the url of the proxy machine 
   */
  public URL proxy ( String scheme ) {
    URL myproxy = null;

    Object o = proxyTab.get( scheme );
    if( o!=null ){
      myproxy = (URL) o;
      return myproxy;
    }
    else{
      // default to get from pia
      try{
	return setProxy( scheme, null );
      }catch( MalformedURLException e ){ return null;}
    }
  }

  // === The following proxy stuff came from Agency -- seems to be the only use

  /**
   * Set proxy string given protocol scheme and proxy string.
   *	If proxy string is not defined, retrieve it from the Pia.
   * @param scheme protocol scheme
   * @param proxyString a url string
   * @return url to proxy
   */
  public URL setProxy (String scheme, String proxystring)
       throws MalformedURLException {
    String p = null;

    if( proxystring!=null )
      p = proxystring;

    if (p==null) {
      String mainproxy = null;

      mainproxy = proxyFor(hostName, scheme);
      if ( mainproxy!= null ) p = mainproxy;
    }

    if (p!=null) {
      try {
	URL myproxy = new URL( p );
	proxyTab.put(scheme, myproxy);
	return myproxy;
      }catch(MalformedURLException e ){
	throw e;
      }
    }
    return null;
  }


  /**
   * return a string indicating the proxy to use for retrieving this request
   * this is for standard proxy notions only, for automatic redirection
   * or re-writes of addresses, use an appropriate agent
   */
  protected  String proxyFor(String destination, String protocol){
    String s = null;
    List list = noProxies();

    if (list != null && destination != null) {
      Enumeration e = list.elements();
      while( e.hasMoreElements() ){
	s = (String)e.nextElement();
	if( s.indexOf(destination) != -1 )
	  return null;
      }
    }
    return proxyForProtocol(protocol);
  }

  /**
   * @return no proxies list from PIA
   */
  protected List noProxies() {
    return Pia.instance().noProxies();
  }

  /**
   * @return proxy string given protocol
   */
  protected String proxyForProtocol(String protocol){
    if( protocol == null ) return null;

    Table ht = Pia.instance().proxies();

    String myprotocol = protocol.toLowerCase().trim();
    //    if( !ht.isEmpty() && ht.containsKey( myprotocol ) ){
    if( ht.isEmpty() != false ){
      if(ht.containsKey( myprotocol ) ){
	String v = (String)ht.get( myprotocol );
	return v;
      }
    }

    return null;
  }
  /************************************************************************
  ** Construction:
  ************************************************************************/

  /**
   * Construct from a host name, port, and socket.
   * @param hostName a string representing host name. 
   * @param port port number
   * @param socket from accepter's listening socket
   */ 
  public Machine( String hostName, int port, Socket socket ){
    this.hostName = hostName;
    this.port = port;
    this.socket = socket ;
  }

  /**
   * Construct from a host name and port.
   * @param hostName a string representing host name. 
   * @param port port number
   */ 
  public Machine( String hostName, int port ){
    this.hostName = hostName;
    this.port = port;
    socket = null;
  }

  /** 
   * Construct from an InputStream.  This constructor is used when we 
   *	are creating the Content for a response Transaction from an
   *	InputStream (for example a file or the InterForm interpretor).
   * @param anInputStream typically from a Content
   * @see crc.pia.Content
   */ 
  public Machine( InputStream anInputStream ){
    this.inputStream = anInputStream;
  }

  /**
   * Default constructor.  Typically called from a subclass.
   */
  public Machine(){
  }


  /************************************************************************
  ** Construction:
  ************************************************************************/

  /**
   * for debugging only
   */
  private static void sleep(int howlong){
    Thread t = Thread.currentThread();
    
    try{
      t.sleep( howlong );
    }catch(InterruptedException e){;}
  }

}


/************************************************************************
*************************************************************************
** Auxiliary Classes:
*************************************************************************
************************************************************************/


/** Timeout callback. */
class zTimeout implements UnaryFunctor{
  public Object execute( Object object ){
    Transaction req = (Transaction) object;
    Machine m = req.toMachine();
    m.closeConnection();

    String msg = "Request timed out.  Server may be down.";
    req.errorResponse(HTTP.REQUEST_TIMEOUT, msg);

    return object;
  }

}
