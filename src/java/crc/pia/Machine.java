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
import java.util.Hashtable;

import crc.pia.Content;
import crc.pia.Transaction;
import crc.pia.Resolver;
import crc.util.regexp.RegExp;
import crc.util.regexp.MatchInfo;
import crc.util.Timer;
import w3c.www.http.HTTP;

import crc.ds.UnaryFunctor;
class zTimeout implements UnaryFunctor{
  public Object execute( Object object ){
    Transaction req = (Transaction) object;
    Machine m = req.toMachine();
    m.closeConnection();

    String msg = "Your request has used up alotted time.  Server is possibly down.";
    Content ct = new ByteStreamContent( new StringBufferInputStream( msg ) );
    Transaction abort = new HTTPResponse(Pia.instance().thisMachine, req.fromMachine(), ct, false);
    abort.setStatus(HTTP.REQUEST_TIMEOUT);
    abort.setContentType( "text/plain" );
    abort.setContentLength( msg.length() );
    abort.startThread();

    return object;
  }

}


public class Machine {
  public boolean DEBUG = false;
  public boolean DEBUGPROXY = false;
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
	if( DEBUG )
	  System.out.println("Exception while getting socket input stream." );
	else
	  if( DEBUG )
	    System.out.println( "Exception while getting socket input stream." );
	  else
	    Pia.instance().errLog( this, "Exception while getting socket input stream." );
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
	if( DEBUG )
	  System.out.println("Exception while getting socket output stream." );
	else
	  Pia.instance().errLog( this, "Exception while getting socket output stream." );
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
      if( DEBUG )
	System.out.println("Exception while closing socket streams." );
      else
	Pia.instance().errLog( this, "Exception while closing socket streams." );
    }
  }

  /**
   * Sends a response through output stream, and if there are controls associated with
   * this response they are added.
   */
  public void sendResponse (Transaction reply, Resolver resolver)throws PiaRuntimeException {
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

	Pia.instance().debug(this, "Sucking controls...");
	Object[] c = reply.controls();
	if( c != null ){
	  ctrlStrings = new StringBuffer();
	  for( int i = 0; i < c.length; i++ ){
	    Object o = c[i];
	    if( o instanceof String )
	      ctrlStrings.append( (String)o + " " );
	  }
	}
	
      }
      
      plainContent = content = reply.contentObj();
      
      if( ctrlStrings!= null && ctrlStrings.length() > 0 ){
	if( reply.contentLength() != -1 )
	  // changing length
	  reply.setContentLength( reply.contentLength() + ctrlStrings.length() );
      }
      
      // dump header
      Pia.instance().debug(this, "Transmitting firstline and header...");
     
      shipOutput( out, outputString, false );

      Pia.instance().debug(this, outputString);
      String headers = reply.headersAsString(); 
      Pia.instance().debug(this, headers);

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
	Pia.instance().debug(this, "Transmitting control strings...");
	shipOutput( out, contentString, true );
      }
      else if( contentString != null && isTextHtml ){
	Pia.instance().debug(this, "Transmitting text/html content...");
	Pia.instance().debug(this, contentString );
	shipOutput( out, contentString, true );
      }else if( plainContent != null ){
	Pia.instance().debug(this, "Transmitting images content...");
	sendImageContent( out, plainContent );
      }
      
      
      Pia.instance().debug(this, "Flushing...");
      out.flush();
      closeConnection();
      
    }catch(PiaRuntimeException e){
      Pia.instance().debug(this, "Client close connection...");
      String msg = "Client close connection...\n";
      closeConnection();
      throw new PiaRuntimeException (this
				     , "sendResponse"
				     , msg) ;

    }catch(IOException e2){
      Pia.instance().debug(this, "Client close connection...");
      String msg = "Client close connection...\n";
      closeConnection();
      throw new PiaRuntimeException (this
				     , "sendResponse"
				     , msg) ;
    }
    

  }

  private void shipOutput(OutputStream out, String s, boolean withnewline)throws PiaRuntimeException{
    byte[] bytestring = null;

      bytestring = getBytes( s );
      try{
	if( bytestring != null )
	  out.write( bytestring, 0, bytestring.length  );
	if( withnewline )
	  out.write( '\n' );
      }catch(IOException e){

	Pia.instance().debug(this, e.getClass().getName());
	String msg = "Can not write...\n";
	throw new PiaRuntimeException (this
				       , "shipOutput"
				       , msg) ;
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

  private void sendImageContent(OutputStream out, Content c)throws PiaRuntimeException{
     byte[]buffer = new byte[1024];
    int bytesRead;
    
    try{
      while(true){
	bytesRead = c.read( buffer, 0, 1024 );
	if( bytesRead == -1 ) break;
	out.write( buffer, 0, bytesRead );
	Pia.instance().debug(this, "the write length is---->"+Integer.toString(bytesRead));
      }
    }catch(IOException e){
      Pia.instance().debug(this, e.getClass().getName());
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
      Pia.instance().debug("the read length is---->"+Integer.toString(data.length()));
      return data.getByteCopy(); 
    }catch(IOException e2){
      throw e2;
    }

    }


  /**
   * Get request through a proxy by opening the proxy socket
   */
  protected void getReqThruSock(URL url, Transaction request, Resolver resolver) throws PiaRuntimeException, UnknownHostException {
    int zport = 80;
    String zhost;

    Pia.instance().debug(this, "Getting data through proxy request");
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
  public void getRequest(Transaction request, Resolver resolver) throws PiaRuntimeException, UnknownHostException  {
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

      if( DEBUGPROXY )
	p = "http://int-gw.crc.ricoh.com:80/";
      else{
	mainproxy= Pia.instance().agency().proxyFor(hostName, scheme);	
	if ( mainproxy!= null )
	  p = mainproxy;
      }
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


  private static void test1( String filename, boolean proxy ){
    System.out.println("This test make use of a server that returns a text/html page.");
    System.out.println("The server is in the test directory and it runs with default port =6666.");
    System.out.println("The file get_machine.txt contains a request for the page.");
    System.out.println("If proxy is true, request is tested thru a proxy.  In the setProxy method, there is a line w/ p=http://..., you should substitute appropriate proxy address for your machine.");
    System.out.println("If proxy is false, java's URL is used to get the page.");

    try{
      InputStream in = new FileInputStream (filename);
      Machine machine1 = new Machine();
      machine1.DEBUG = true;
      if( proxy )
	machine1.DEBUGPROXY = true;
      machine1.setInputStream( in );
      
      boolean debug = true;
      Transaction trans1 = new HTTPRequest( machine1, debug );
      Thread thread1 = new Thread( trans1 );
      thread1.start();

      while( true ){
	sleep( 1000 );
	if( !thread1.isAlive() )
	  break;
      }

      //Resolver res = new Resolver();
      Resolver res = null;
      machine1.getRequest( trans1, res );
      System.exit( 0 );
    }catch(Exception e ){
      System.out.println( e.toString() );
    }
  }

  private static void test2(String filename){
    try{
      System.out.println("Testing response w/ from and to machines as arguments.");
      System.out.println("From machine gets its data from response.txt file.");
      
      InputStream in = new FileInputStream (filename);
      Machine machine1 = new Machine();
      machine1.DEBUG = true;
      machine1.setInputStream( in );
      
      Machine machine2 = new Machine();
      machine2.DEBUG = true;
      machine2.setOutputStream( System.out );
      
      boolean debug = true;
      Transaction trans1 = new HTTPResponse( machine1, machine2, debug );
      Thread thread1 = new Thread( trans1 );
      thread1.start();
      
      while( true ){
	sleep( 1000 );
	if( !thread1.isAlive() )
	  break;
      }

      trans1.addControl( "major" );
      trans1.addControl( "tom" );
      Resolver res = null;
      machine2.sendResponse( trans1, res );
      System.exit( 0 );
    }catch(Exception e ){
      System.out.println( e.toString() );
    }
  }


  private static void printusage(){
    System.out.println("Needs to know what kind of test");
    System.out.println("For test 1, here is the command --> java crc.pia.Machine -1 -proxy get_machine.txt");
    System.out.println("For test 1, here is the command --> java crc.pia.Machine -1 -noproxy get_machine.txt");
    System.out.println("For test 2, here is the command --> java crc.pia.HTTPRequest -2 response.txt");
  }


  /**
   * For testing.
   * 
   */ 
  public static void main(String[] args){
    if( args.length == 0 ){
      printusage();
      System.exit( 1 );
    }
    if(args.length == 2){
      if( args[0].equals ("-2") && args[1] != null )
	test2( args[1] );
    }else
    
    if (args.length == 3 ){
      if( args[0].equals ("-1") && (args[1].equals ("-proxy") || args[1].equals ("-noproxy")) && args[2] != null )
	if( args[1].equals ("-proxy"))
	  test1( args[2], true );
	else
	  test1( args[2], false );
      else{
	printusage();
	System.exit( 1 );
      }
    }
    
  }
}

  /**
   * For testing only, ya
   */
  class Server extends Thread {
    public final static int DEFAULT_PORT = 6789;
    protected int port;
    protected ServerSocket listen_socket;
    
    // Exit with an error message, when an exception occurs.
    public static void fail(Exception e, String msg) {
      System.err.println(msg + ": " +  e);
      System.exit(1);
    }
    
    // Create a ServerSocket to listen for connections on;  start the thread.
    public Server(int port) {
        if (port == 0) port = DEFAULT_PORT;
        this.port = port;
        try { listen_socket = new ServerSocket(port); }
        catch (IOException e) { fail(e, "Exception creating server socket"); }
        System.out.println("Server: listening on port " + port);
        this.start();
    }
    
    // The body of the server thread.  Loop forever, listening for and
    // accepting connections from clients.  For each connection, 
    // create a Connection object to handle communication through the
    // new Socket.
    public void run() {
        try {
            while(true) {
	      Socket client_socket = listen_socket.accept();
	      /*
	      InetAddress iaddr = client_socket.getInetAddress();
	      String hostName = iaddr.getHostName();
	      Machine machine =  new Machine(hostName, port, client_socket);
	      machine.test1();
	      */
            }
        }
        catch (IOException e) { 
            fail(e, "Exception while listening for connections");
        }
    }
  }











