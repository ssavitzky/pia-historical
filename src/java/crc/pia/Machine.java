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
  protected Hashtable proxyTab = new Hashtable();

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
  public Transaction getRequest(Transaction request, Resolver resolver) throws UnknownHostException, IOException  {
    String proxy;
    URL agentURL;
    URL url;
    URLConnection agent;
    int zport;
    int zhost;

    Transaction reply;

    url = request.requestURL();


    proxy = proxy( request.protocol() );
    if( proxy ){
	int p        = proxy.getPort();
	int zport    = (p == -1) ? 80 : p;
	zhost        = proxy.getHost();
    }else{
      zport        = ( port == -1 ) ? 80 : port;
      zhost        = hostName;
    }
    
    try{
      socket       = new Socket(zhostName, zport);
      outputStream = new BufferedOutputStream(socket.getOutputStream());
      inputStream  = new BufferedInputStream(socket.getInputStream());
      //Actually, need to spit the first line too
      //spit header, Becareful about the content length;
      //spit body if there is a body;
      //outputStream.flush()
      //need to create a response transaction with the inputStream tie to its
      //content.  Return the transaction.
    }catch(UnknownHostException ue){
      throw ue;
    }catch(IOException e){
      throw e;
    }
  }

  /**
   * get proxy string.
   * @returns the number of bytes read. 
   */
  public URL proxy ( String scheme ) {
    URL myproxy = null;

    Object o = proxyTab.get( scheme );
    if( o )
      myproxy = (URL) o;
    return myproxy;
  }

  /**
   * set proxy string.
   *
   */
  public void setProxy (String scheme, String proxystring) {
    String p = null;

    if( proxystring )
      p = proxystring;

    if(!p){
	String mainproxy= Pia.agency().proxyFor(hostName, scheme);	
	if ( mainproxy )
	  p = mainproxy;
    }
    if(p){
      try{
	myproxy = new URL( p );
	proxyTab = put(scheme, myproxy);
      }catch(MalformedURLException e ){
      }
    }
  }

  /**
   * Constructor 
   * @returns nothing. 
   */ 
  public Machine( String hostName, int port, Socket socket ){
    this.hostName = hostName;
    this.port = port;
    this.socket = socket ;
  }

  public Machine( String hostName, int port ){
    this.hostName = hostName;
    this.port = port;
    socket = null;
  }

}









