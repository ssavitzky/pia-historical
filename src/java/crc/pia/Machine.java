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

import java.io.IOException;
import crc.pia.Content;
import crc.pia.Transaction;
import crc.pia.Resolver;
import crc.util.regexp.RegExp;

public class Machine {
  /**
   * Attribute index - client address
   */
  protected String address;

  /**
   * Attribute index - port
   */
  protected int port;

  /**
   * Attribute index - client socket
   */
  protected Socket socket;

  /**
   * Attribute index - proxy
   */
  protected String proxy;

  /**
   * Attribute index - input stream
   */
  protected InputStream inputStream;

  /**
   * Attribute index - output stream
   */
  protected OutputStream outputStream;

  /**
   * Return inputStream
   */
  public InputStream inputStream() throws IOException{
    if( socket )
      try
      {
	inputStream = socket.getInputStream();
	return inputStream;
      }catch(IOException e){
      }    
      errLog( this, "Exception while getting socket input stream." );
      throw new IOException( e.getMessage() );
  }

  /**
   * Return outputStream
   */
  public OutputStream outputStream() throws IOException{
    if( socket )
      try
      {
	outputStream = socket.getOutputStream();
	return outputStream;
      }catch(IOException e){
      }    
      errLog( this, "Exception while getting socket output stream." );
      throw new IOException( e.getMessage() );
  }

 /**
  * Closing socket.
  * @returns nothing. 
  */
  public void closeConnection() {
    try {
      socket.close();
      inputStream.close();
      outputStream.close();
    }catch(IOException e) {
      errLog( this, "Exception while closing socket streams." );
    }
  }

  /**
   * send_response sends a response to whatever this machine refers to.
   * @returns the number of bytes read. 
   */
  public void sendResponse (Transaction reply, Resolver resolver) {
    PrintStream out;
    StringBuffer ctrlStrings = new StringBuffer();
    String content;
    String plainContent;

    out = new PrintStream( outputStream );

    String message = reply.statusMsg();
    String outputString = "HTTP/1.0 " + reply.statusCode() + " " + message + "\n";

    String type = reply.contentType();
    if( type && type.toLowerCase().indexOf("/text/html") != -1 ){
      
      Thing[] c = reply.controls();
      if( c ){
	for( int i = 0; i < c.length; i++ ){
	  Object o = c[i];
	  if( o instanceof String )
	    ctrlString.append( (String)o + " " );
	}
      }

    }

    if( ctrlString.length() > 0 ){
      if( reply.contentLength() != -1 )
	reply.setContentLength( reply.contentLength() + ctrlString.length() );
      plainContent = content = reply.contentObj().source();
    }
    
    out.println( outputString );
    out.println( reply.headersAsString() );
    out.println( "\n" );

    RegExp re = new RegExp("<body[^>]*>");
    MatchInfo mi = re.match( content.toLowerCase() );
    if( ctrlString && mi != null ){
      String ms = mi.matchString();
      StringBuffer buf = new StringBuffer( ms );
      buf.append( ctrlString );
      content   = re.simpleSubstitute(ms, new String( buf ));
    }

    else if (ctrlString){
      out.println( ctrlString );
    }

    if( ctrlString )
       out.println( content );
    else
    if( plainContent )
       out.println( plainContent );
    closeConnection();
  }

  /**
   * getRequest string.
   * @returns the number of bytes read. 
   */
  public Transaction getRequest(Transaction request, Resolver resolver) {
    String proxy;
    Transaction reply;
    InputStream in;

    try{
      URL urlObject = new URL( request.requestURL() );
      URLConnection agent = urlObject.openConnection();
      // not sure what to do here
      proxy = proxy( request.protocol() );
      if( proxy )
	agent.setRequestProperty("proxy", proxy);

      return new Transaction(request, Content( URLConnection ) );
    }catch(IOException e){
    }
  }

  /**
   * get proxy string.
   * @returns the number of bytes read. 
   */
  public String proxy (String scheme, String proxystring) {
    return proxy;
  }

  /**
   * set proxy string.
   *
   */
  public void setProxy (String scheme, String proxystring) {
    if( proxystring )
      proxy = proxystring;

    if(!proxy){
	String mainproxy= Pia.agency().proxyFor(address, scheme);	
	if ( mainproxy )
	  proxy = mainproxy;
    }
    return proxy;
  }

  /**
   * Constructor 
   * @returns nothing. 
   */ 
  public Machine( String address, int port, Socket socket ){
    this.address = address;
    this.port = port;
    this.socket = socket ;
  }

}









