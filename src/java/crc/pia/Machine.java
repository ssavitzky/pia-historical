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
import crc.pia.Content;
import crc.pia.Transaction;
import crc.pia.Resolver;

public class Machine {
  /**
   * Attribute index - client address
   */
  protected InetAddress address;

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
  public InputStream getInputStream(){
    return inputStream;
  }

  /**
   * Return outputStream
   */
  public OutputStream getOutputStream(){
    return outputStream;
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
    StringBuffer finalContent;

    out = new PrintStream( outputStream );

    String message = reply.getStatusMsg();
    String outputString = "HTTP/1.0 " + reply.getStatusCode() + " " + message + "\n";

    if( reply.contentType().indexOf("/text/") != -1 ){
      int limit = reply.controls().size();
      String[] c = reply.controls();
      for( int i = 0; i < limit; i++ ){
	ctrlString.append( c[i] + " " );
      }
    }

    if( ctrlString.length() > 0 ){
      if( reply.contentLength() != -1 )
	reply.setContentLength( reply.contentLength() + ctrlString.length() );
      content = reply.getContent();
    }
    
    out.println( outputString );
    out.println( reply.headersAsString() );
    out.println( "\n" );

    /*
    if( ctrlString && content.indexOf("<body[^>]*>"){ //match <body and 0 or more anything except >
      //finalContent = substitute( s/(\<body[^>]*\>)/$1$control/is );  do some magic
    }else if (ctrlString){
      out.println( ctrlString );
    }
    */
    if( ctrlString )
       out.println( content );
    else
       out.println( reply.getContent() );
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
      URL urlObject = new URL( request.getURI() );
      URLConnection agent = urlObject.openConnection();
      // not sure what to do here
      /*
      proxy = proxy( request.getProtocol() );
      if( proxy )
	agent.setRequestProperty("proxy", proxy);
      */

      return new Transaction(request, Content( URLConnection ) );
    }catch(IOException e){
    }
  }

  /**
   * get proxy string.
   * @returns the number of bytes read. 
   */
  public String getProxy (String scheme, String proxystring) {
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
      /*
	String mainproxy= $main::agency->proxy_for($$self{address},$scheme);	
	if ( mainproxy )
	  proxy = mainproxy;
	  */
    }
    return proxy;
  }

  /**
   * Constructor 
   * @returns nothing. 
   */ 
  public Machine( InetAddress address, int port, Socket socket ) throws IOException {
    this.address = address;
    this.port = port;
    this.socket = socket ;
    if( socket )
      try
      {
	inputStream = socket.getInputStream();
	outputStream = socket.getOutputStream(); 
      }catch(IOException e){
	errLog( this, "Exception while getting socket streams." );
	throw new IOException( e.getMessage() );
      }    
  }

}









