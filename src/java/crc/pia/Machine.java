// Machine.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.


/**
 * This object represents machines that envolve in a communication transaction.
 * Ideally these should be persistent so that we can keep track of what
 * kind of browser or server we're talking to, but at the moment we
 * don't do that.
 */

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
import crc.util.regexp.RegExp;
import crc.util.regexp.MatchInfo;
import crc.util.Timer;

import crc.ds.Table;
import crc.ds.List;

import w3c.www.http.HTTP;

import crc.ds.UnaryFunctor;

/** Timeout callback. */
class zTimeout implements UnaryFunctor{
  public Object execute( Object object ){
    Transaction req = (Transaction) object;
    Machine m = req.toMachine();
    m.closeConnection();

    String msg = "Request timed out.  Server may be down.";
    Content ct = new ByteStreamContent( new StringBufferInputStream( msg ) );
    Transaction abort = new HTTPResponse(Pia.instance().thisMachine,
					 req.fromMachine(), ct, false);
    abort.setStatus(HTTP.REQUEST_TIMEOUT);
    abort.setContentType( "text/html" );
    abort.setContentLength( msg.length() );
    abort.startThread();

    return object;
  }

}


public class Machine {
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
   * Return inputStream
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
   * Return outputStream
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
  * @returns nothing. 
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

  /** 
   * Sends a response through output stream, and if there are
   * controls associated with this response they are added.
   */
  public void sendResponse (Transaction reply, Resolver resolver)
       throws PiaRuntimeException {
    OutputStream out = null;
    StringBuffer ctrlStrings = null;
    Content content = null;
    Content plainContent = null;
    RegExp re = null;
    MatchInfo mi = null;
    boolean isTextHtml = false;
    String contentString = null;

    try{
      out = outputStream();

      String message = reply.reason();
      String outputString = "HTTP/1.0 " + reply.statusCode() + " " + message + "\r";
      // HTTP/1.0 200 OK

      String type = reply.contentType();
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
      
      if( ctrlStrings!= null && ctrlStrings.length() > 0 ){
	if( reply.contentLength() != -1 )
	  // changing length
	  reply.setContentLength(reply.contentLength() + ctrlStrings.length());
      }
      
      // dump header
      Pia.debug(this, "Transmitting firstline and header...");
     
      shipOutput( out, outputString, false );

      Pia.debug(this, outputString);
      String headers = reply.headersAsString(); 
      Pia.debug(this, headers);

      shipOutput( out, headers, false );
      
      if( content != null && isTextHtml  ){
	contentString = content.toString();
	try{
	  re = new RegExp("<body[^>]*>");
	  mi = re.match( contentString.toLowerCase() );
	}catch(Exception e){
	}
      }
      
      if( ctrlStrings != null && mi != null ){
	String ms = mi.matchString();
	StringBuffer buf = new StringBuffer( ms );
	buf.append( ctrlStrings );
	contentString   = re.substitute(contentString, new String( buf ), true);
      } else if (ctrlStrings!= null){
	shipOutput( out, new String( ctrlStrings ), true );
      }
      
      if( ctrlStrings != null ){
	Pia.debug(this, "Transmitting control strings...");
	shipOutput( out, contentString, true );
      }
      else if( contentString != null && isTextHtml ){
	Pia.debug(this, "Transmitting text/html content...");
	// Pia.debug(this, contentString );
	shipOutput( out, contentString, true );
      }else if( plainContent != null ){
	Pia.debug(this, "Transmitting images content...");
	sendImageContent( out, plainContent );
      }
      
      Pia.debug(this, "Flushing...");
      out.flush();
      closeConnection();
      
    } catch (PiaRuntimeException e){
      Pia.debug(this, "Client closed connection...");
      String msg = "Client closed connection...\n";
      closeConnection();
      throw new PiaRuntimeException (this, "sendResponse", msg) ;

    }catch(IOException e2){
      Pia.debug(this, "Client close connection...");
      String msg = "Client close connection...\n";
      closeConnection();
      throw new PiaRuntimeException (this, "sendResponse", msg) ;
    }
    

  }

  private void shipOutput(OutputStream out, String s, boolean withnewline)
       throws PiaRuntimeException{
    byte[] bytestring = null;

      bytestring = getBytes( s );
      try{
	if( bytestring != null )
	  out.write( bytestring, 0, bytestring.length  );
	if( withnewline )
	  out.write( '\n' );
      }catch(IOException e){

	Pia.debug(this, e.getClass().getName());
	String msg = "Can not write...\n";
	throw new PiaRuntimeException (this, "shipOutput", msg) ;
      }
  }

  private byte[] getBytes(String s){
    int len = s.length();
    byte[] data = null;
    if( len > 0 ){
      data = new byte[ len ];
      s.getBytes(0, len, data, 0 );
    }
    return data;
  }

  private void sendImageContent(OutputStream out, Content c)
       throws PiaRuntimeException{
     byte[]buffer = new byte[1024];
    int bytesRead;
    
    try{
      while(true){
	bytesRead = c.read( buffer, 0, 1024 );
	if( bytesRead == -1 ) break;
	out.write( buffer, 0, bytesRead );
	Pia.debug(this, "the write length is---->"+Integer.toString(bytesRead));
      }
    }catch(IOException e){
      Pia.debug(this, e.getClass().getName());
      String msg = "Can not write...\n";
      throw new PiaRuntimeException (this
				     , "sendImageContent"
				     , msg) ;
    }
}


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
       throws PiaRuntimeException, UnknownHostException {

    int zport = 80;
    String zhost;

    Pia.debug(this, "Getting data through proxy request");
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
      String msg = "Can not get data through proxy request\n";
      throw new PiaRuntimeException (this
				     , "getReqThruSock"
				     , msg) ;
    }
  }

  /**
   * Get request data and create a response with the data 
   */
  public void getRequest(Transaction request, Resolver resolver)
       throws PiaRuntimeException, UnknownHostException  {

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
      String msg = e.toString();
      throw new PiaRuntimeException (this
				     , "getRequest"
				     , msg) ;
    }catch(UnknownHostException e2){
      throw e2;
    }finally{
      ztimer.stop();
    }
  }


  /**
   * Get proxy url given protocol scheme 
   * @returns the url of the proxy machine 
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

  /**
   * Set proxy string given protocol scheme and proxy string -- if proxy string is not
   * define, the proxy string is retrieve from the Pia.
   * @param scheme protocol scheme
   * @param proxyString a url string
   * @return url to proxy
   */
  public URL setProxy (String scheme, String proxystring) throws MalformedURLException{
    String p = null;

    if( proxystring!=null )
      p = proxystring;

    if(p==null){
      String mainproxy;

      mainproxy= Pia.instance().agency().proxyFor(hostName, scheme);	
      if ( mainproxy!= null )
	p = mainproxy;
    }


    if(p!=null){
      try{
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
   * @param hostName a string representing host name. 
   * @param port port number
   * @param socket socket receives from accepter's listening socket
   */ 
  public Machine( String hostName, int port, Socket socket ){
    this.hostName = hostName;
    this.port = port;
    this.socket = socket ;
  }

  /**
   * @param hostName a string representing host name. 
   * @param port port number
   */ 
  public Machine( String hostName, int port ){
    this.hostName = hostName;
    this.port = port;
    socket = null;
  }

  /** Create a Machine from an InputStream.
   * @param in an InputStream
   */ 
  public Machine( InputStream in ){
    this.inputStream = in;
  }

  // call from agent machine
  public Machine(){
  }

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













