//  Httprequest.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/** 
 * implement transaction for HTTP request
 */


package crc.pia;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.net.URLEncoder;
import java.io.IOException;
import java.io.PrintStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.StringBufferInputStream;
import java.util.Properties;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;

import crc.pia.Machine;
import crc.pia.agent.AgentMachine;
import crc.pia.Content;
import crc.pia.ByteStreamContent;
import crc.pia.Transaction;
import crc.pia.HTTPResponse;

import crc.ds.Queue;
import crc.ds.Features;
import crc.util.Utilities;
import crc.tf.Registry;


public class  HTTPRequest extends Transaction {

  /**  method
   * should be get, post, put, head, etc.
   */
  protected String method;

  /**  
   * the url string of this request
   */
  protected String url;


  /**
   * Return url string associated with this request
   * @return url string
   */
  public String url(){
    return url;
  }

  /** 
   * query string associated with this transaction
   */
  protected String queryString;
  
  /**
   * true if this transaction is a response
   */
  public boolean isResponse(){
    return  false;
  }

  /**
   * true if this transaction is a request
   */
  public boolean isRequest(){
    return  true;
  }


  /**
   * @return request method
   */
  public String method(){
    return method;
  }

  /**
   * @return host at which a request is directed 
   */
  public String host(){
    URL u = null;
    String host = null;
    
    host = super.host();

    if( host == null ){
      u = requestURL();
      if( u != null )
	host = u.getHost();
    }
    return host;
      
  }

  /**
   * @return this request's protocol
   */
  public String protocol(){
    URL u = null;

    if( protocol != null )
      return protocol;
    else{
      u = requestURL();
      if( u != null )
	protocol = u.getProtocol();
    }
    return protocol;
      
  }

  /**
   * set the method
   */
  public void setMethod(String method){
    this.method = method;
  }

  /**
   * set the url
   */
  public void setRequestURL(String url){
    this.url = url;
  }

 /**
   *   @returns the full request URI. 
   *
   */    
  public URL requestURL(){
    URL u = null;
    URL myurl = null;

    if( url != null ){
      try{
	u = new URL( Pia.instance().url() + "/" );

	myurl = new URL( u, url );
      }catch( MalformedURLException e ){
      }
      return myurl;
    }
    else
      return null;   
  }

  /**
   * true if this transaction is a request and has parameters
   */
  public boolean hasQueryString(){
    Content c = contentObj();
    if( c!=null ){
      FormContent fc;

      if( c instanceof FormContent ){
	fc = (FormContent) c;
	return ( fc.size() > 0 ) ? true : false;
      }
      else return false;
    }
    else
      return false;
  }


  /**
   * return parameters associated with a request( urlencoded ).
   * i. e. text=Dalai%27s+Llama
   */
  public String queryString(){
    if ( queryString != null )
      return queryString;

    Content c = contentObj();
    if( c!= null ){
      FormContent fc;

      if( c instanceof FormContent ){
	fc = (FormContent) c;
	queryString = fc.queryString();
	return queryString;
      }
      else return null;
    }
    else
      return null;
  }

  /**
   * Create a FormContent from fromMachine
   */
  protected void initializeContent() throws PiaRuntimeException{
    InputStream in;
    String test = null;
    String ztype= null;
    
    Pia.instance().debug(this, "inside initialize content");

    try{
      in = fromMachine().inputStream();

      if( contentType() == null && method().equals("GET") )
	ztype = "application/x-www-form-urlencoded";
      else
	ztype = contentType();

      contentObj = cf.createContent( ztype, in );

      if( contentObj != null )
	contentObj.setHeaders( headers() );
      else{
	Pia.instance().debug(this, "Unknown header type...");
	String msg = "Unknown header type...\n";
	throw new PiaRuntimeException (this
				       , "initializeContent"
				       , msg) ;
      }

      Pia.instance().debug(this, "before set param");
      setParam();
      Pia.instance().debug(this, "after set param");
    }catch(IOException e){
      Pia.instance().debug(this,  e.toString() );
    }
  }


  /**
   * Return original request's first line.
   * @returns HTTP method url
   */
  public String protocolInitializationString(){
    URL proxy = null;
    URL myurl = null;
    StringBuffer buf = null;
    String temp = null;

    myurl = requestURL();
    if ( myurl == null || method() == null ) return null;

    proxy = fromMachine().proxy( protocol() );

    buf = new StringBuffer();
    buf.append( method() );
    buf.append( ' ' );

    if( myurl != null ){
      if ( proxy != null ){
	temp = myurl.toExternalForm();
	if( temp != null )
	  buf.append( temp );
      }
      else{
	temp = myurl.getFile();
	if( temp != null )
	  buf.append( temp );
      }
    }


    if( hasQueryString() && method().equalsIgnoreCase("GET") )
      buf.append( queryString() );

    buf.append(' ');
    buf.append(version());
    buf.append('\r');
    buf.append('\n');
    return new String( buf );
 }	

  /**
   * output protocolInitializationString, headers, and content if request.
   */
  public void printOn(OutputStream stream) throws IOException{
     PrintStream out = new PrintStream( stream );

     String requestLine = protocolInitializationString();
     if( requestLine != null )
       out.print( requestLine );

     String headersString= headersAsString();
     if( headersString != null )
       out.print( headersString );

     if( method().equals("POST")  ){
       String qs = queryString(); 
       if( qs != null ){
	 Pia.instance().debug(this, "the content is ..." + qs);
	 out.println( qs );
       }
     }
     out.flush();
  }


  /**
   * parse the request line to get method, url, http's major and minor version numbers
   */
  protected void  parseInitializationString( String firstLine )throws IOException, MalformedURLException{
    StringTokenizer tokens = new StringTokenizer(firstLine, " ");
    
    try{
      method = tokens.nextToken();
    }catch( NoSuchElementException e ){
      throw new RuntimeException("Bad request, no method.");
    }

    String zurlandmore; 
    try{
      zurlandmore = tokens.nextToken();
    }catch( NoSuchElementException e2 ){
      throw new RuntimeException("Bad request, no url.");
    }

    try{
      URL u;

      u = new URL( Pia.instance().url() + "/" );

      URL myurl = new URL(u, zurlandmore );
      protocol = myurl.getProtocol();
    }catch(MalformedURLException e){
      throw e;
    }
    
    if( method == "GET" ){
      int pos;
      if( (pos = zurlandmore.indexOf("?")) == -1 )
	url = zurlandmore;
      else{
	String zurl = zurlandmore.substring(0, pos);
	url = zurl;
	String qs = zurl.substring(pos+1);
	if( qs!= null )
	  queryString = Utilities.unescape( qs );
      }
    }
    else
	url = zurlandmore;

      String zscheme;
      try{
	zscheme = tokens.nextToken();
	if( protocol() == null )
	  protocol = "HTTP";
	String majorMinor = zscheme.substring( "HTTP/".length() );
	StringTokenizer mytokens = new StringTokenizer( majorMinor, "." );
	try{
	  String zmajor = mytokens.nextToken();
	  major = zmajor;
	  String zminor = mytokens.nextToken();
	  minor = zminor;
	}catch(Exception e4){
	  major = "0" ;
	  minor = "9" ;
	}
      }catch( NoSuchElementException e3 ){
	
      }
      
  }


  /**
   * set query parameters for request
   */
  protected void setParam(){
    String mymethod = method();
    FormContent fc = null;

    fc = (FormContent)contentObj();
    if( queryString()!= null && mymethod.equalsIgnoreCase( "GET" ) ){
      fc.setParameters( queryString() );
    }else {
      //      if( mymethod.equalsIgnoreCase( "POST" )  )
      //	fc.setParameters(null);
    }

  }


  /**
   * Return the machine that is the target of the request.
   * 
   */
  public Machine toMachine(){
    String host = null;
    int port = 80;
    URL url = requestURL();
    String urlString;

    if( toMachine == null ){
      urlString = url.getFile();
      if( urlString != null )
	host = url.getHost();
      if( host != null ){
	port = url.getPort();
	String zport = Integer.toString( port );
	if( host.equals( Pia.instance().host() ) && zport.equals( Pia.instance().port() ))
	  toMachine = new AgentMachine( null );
	else
	  toMachine = new Machine( host, port );
      }
    }
    return toMachine;
  }


  /** 
   * Serves a request not directed at the agency.
   */
  public void defaultHandle( Resolver resolver ){
    handleRequest(  resolver);
  }
  
  /**
   * handleRequest -- Default handling for a request:
   * ask the destination machine to get it.
   * complain if there's no destination to ask.
   */
  public void handleRequest( Resolver resolver ){
    Pia.instance().debug(this, "Starting handle request...");

    Machine destination = toMachine();
  
    if( destination == null )
      errorResponse(500, null);
    try{
      destination.getRequest( this, resolver );
    }catch(PiaRuntimeException e){
      errorResponse( 500, e.getMessage() );
    }catch(UnknownHostException e2){
      errorResponse( 400 , null );
    }
    
  }
  
  /**
   * errorResponse -- Return a "not found" error along with reason.
   *
   *
   */
  protected void errorResponse(int code, String msg){
    int mycode = code;
    Pia.instance().debug(this, "This is the err msg :"+msg);
    StringBufferInputStream foo = null;
    String masterMsg = "Agency could not retrieve " + requestURL() + ": ";

    if ( msg != null ){
      masterMsg += msg;
      foo = new StringBufferInputStream( masterMsg  );
    }
    else{
      String standardMsg = standardReason( mycode );
      if ( standardMsg == null )
	masterMsg += ".\n";
      else
	masterMsg = standardMsg;
      foo = new StringBufferInputStream( masterMsg );
    }

    Content ct = new ByteStreamContent( foo );
    Transaction response = new HTTPResponse( Pia.instance().thisMachine, toMachine(), ct, false);
    
    response.setStatus( mycode );
    response.setContentType( "text/plain" );
    response.setContentLength( masterMsg.length() );
    Pia.instance().debug(this, "The header : \n" + response.headersAsString() );
    response.startThread();
  }

  /**
   *  Client needs to set fromMachine, toMachine, and start the thread.
   * 
   */
  public HTTPRequest(){
    Pia.instance().debug(this, "Constructor-- [ machine from ] -- on duty...");
    handlers = new Queue();
    new Features( this );

    // we probably only need one instance of these objects
    
    fromMachine( null );
    toMachine( null );// done by default anyway

  }



  /**
   *  Take a machine as source of input for header and content.
   *  @param from source of input for this transaction
   *  
   */
  public HTTPRequest( Machine from ){
    Pia.instance().debug(this, "Constructor-- [ machine from ] -- on duty...");
    handlers = new Queue();
    new Features( this );

    // we probably only need one instance of these objects
    
    fromMachine( from );
    toMachine( null );// done by default anyway

    startThread();
  }


  /**
   * A request transaction with default blank header and a define content.
   * @param from originator of request -- later data will be sent to this machine.
   * @param ct a define content.
   */
  public HTTPRequest( Machine from, Content ct ){
    Pia.instance().debug(this, "Constructor-- [ machine from, content ct ] on duty...");
    handlers = new Queue();
    new Features( this );

    contentObj = ct;
    headersObj = new Headers(); // blank header  

    if( contentObj != null )
      contentObj.setHeaders( headersObj );

    fromMachine( from );
    toMachine( null );

    startThread();
  }
 
  /**
   * A request transaction with define header and content.
   * @param from originator of request -- later data will be sent to this machine.
   * @param ct a define content.
   * @param hd a define header.
   */
  public HTTPRequest( Machine from, Content ct, Headers hd ){
    Pia.instance().debug(this, "Constructor-- [ machine from, content ct, headers hd ] -- on duty...");
    handlers = new Queue();
    new Features( this );

    contentObj = ct;
    headersObj = hd; 

    if( contentObj != null )
      contentObj.setHeaders( headersObj );

    fromMachine( from );
    toMachine( null );

    startThread();
  }

  private static void sleep(int howlong){
   Thread t = Thread.currentThread();

   try{
     t.sleep( howlong );
   }catch(InterruptedException e){;}

  }

  private static void test1( String filename ){
    System.out.println("Testing request w/ from machine as the only argument.");
    System.out.println("Input is read from post.txt and set as machine's source.");

    try{
      InputStream in = new FileInputStream (filename);
      Machine machine1 = new Machine();
      machine1.setInputStream( in );

      // make use of debugging version, which uses HTTPRequest run method
      boolean debug = true;
      Transaction trans1 = new HTTPRequest( machine1, debug );

      Thread thread1 = new Thread( trans1 );
      thread1.start();
   

      while( true ){
	sleep( 1000 );
	if( !thread1.isAlive() )
	  break;
      }
      
      printMethods( trans1 );
      System.exit(0);
    }catch(Exception e ){
      System.out.println( e.toString() );
    }
  }

  private static void test2( String filename ){
    System.out.println("Testing request w/ from machine and content as argument.");
    System.out.println("A blank header is created in the contstructor.");
    System.out.println("Content's processInput should get called.");
    System.out.println("Content is set to data from requestbody.");

    try{
      Machine machine = new Machine();
      FormContent c = new FormContent();

      InputStream in = new FileInputStream (filename);
      c.source( in );

      boolean debug = true;
      Transaction trans = new HTTPRequest( machine, c, debug );
      //printOn only prints if method is post and content has data
      trans.setMethod( "POST" );

      Thread thread1 = new Thread( trans );
      thread1.start();
   

      while( true ){
	sleep( 1000 );
	if( !thread1.isAlive() )
	  break;
      }

      printMethods( trans );
      System.exit(0);
    }catch(Exception e ){
      System.out.println( e.toString() );
    }

  }

  private static void test3( ){
    System.out.println("Testing request w/ from machine, content, and header as argument.");

    Machine machine = new Machine();
    Headers h = null;
    try{
      h = new Headers();
      h.setHeader("Host", "napa.crc.ricoh.com:9999");
      h.setContentType("text/html");
      h.setContentLength( 555 );
      h.setHeader("Content-Type", "image/gif");
    }catch(Exception e){;}

    FormContent c = new FormContent();

    boolean debug = true;
    Transaction trans = new HTTPRequest( machine, c, h, debug );
    Thread thread = new Thread( trans );
    thread.start();

    while( true ){
      sleep( 1000 );
      if( !thread.isAlive() )
	break;
    }

    printMethods( trans );
    System.exit( 0 );
  }

  private static void test4( ){
    System.out.println("Testing request w/ from machine, content, and header as argument.");
    System.out.println("Also, test transaction features.");
    Machine machine = new Machine();
    Headers h = null;
    try{
      h = new Headers();
      h.setHeader("Host", "napa.crc.ricoh.com:9999");
      h.setContentType("text/html");
      h.setContentLength( 555 );
      h.setHeader("Content-Type", "image/gif");
    }catch(Exception e){;}

    FormContent c = new FormContent();

    boolean debug = true;
    Transaction trans = new HTTPRequest( machine, c, h, debug );
    trans.setRequestURL("http://agency/im3/initialize.inf");
    System.out.println( "----->>>> Headers are the following: <<<<<------ " );
    System.out.println(trans.headersAsString());
    System.out.println("\n");
    System.out.println( "----->>>> Testing Transaction Features <<<<<------ " );
    System.out.println( "----->>>> url is http://agency/im3/initialize.inf  " );
    System.out.println( "Is agent request? ->" + trans.compute("IsAgentRequest").toString() );
    System.out.println( "Getting agent ->" + trans.compute("Agent").toString() );
    System.out.println( "Checking for netscape -->"+ trans.compute("IsClientNetscape") );
    
    System.out.println("\n------>>>> Changing url to file:/slackware/home/foo.html <<<<-------------------");
    trans.setRequestURL("file:/slackware/home/foo.if");
    System.out.println("Is this a file request? ->" + trans.compute("IsFileRequest").toString() );
    System.out.println("Is content-type [text/html]? ->" + trans.compute("IsHtml").toString() );
    System.out.println("Is content-type [image]? ->" + trans.compute("IsImage").toString() );
    System.out.println("Is interform request? ->" + trans.compute("IsInterform").toString() );
    System.out.println("Is local request? ->" + trans.compute("IsLocal").toString() );
    System.out.println( "----->>>> url is back to http://agency/im3/initialize.inf  " );
    trans.setRequestURL("http://agency/im3/initialize.inf");
    trans.setHeader("Host", "agency");
    System.out.println("Is proxy request? ->" + trans.compute("IsProxyRequest").toString() );
    System.out.println("Is content-type text? ->" + trans.compute("IsText").toString() );  

    System.out.println("\n------>>>> Testing assertion <<<<-------------------");
    System.out.println("Asserting IsText to true\n");
    trans.assert( "IsText" );
    System.out.println("Make use of test(...) to check IsText ->" + trans.test( "IsText" ));
    trans.deny( "IsText" );
    System.out.println("Recheck IsText (should be false) ->" + trans.test( "IsText" ));
    System.out.println("Checking has( IsText ) (should be true) ->" + trans.has( "IsText" ));
    System.out.println("Checking is ( IsImage ) (should be true) ->" + trans.is( "IsImage" ).toString());
    System.exit( 0 );
  }



  private static void printusage(){
    System.out.println("Needs to know what kind of test");
    System.out.println("For test 1, here is the command --> java crc.pia.HTTPRequest -1 post.txt");
    System.out.println("For test 2, here is the command --> java crc.pia.HTTPRequest -2 requestbody.txt");
    System.out.println("For test 3, here is the command --> java crc.pia.HTTPRequest -3");
    System.out.println("For test 4, here is the command --> java crc.pia.HTTPRequest -4");
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

    if( args.length == 1 ){
      if ( args[0].equals ("-3") )
	test3();
      else if ( args[0].equals ("-4") )
	test4();
      else {
	printusage();
	System.exit( 1 );
      }
    }else if (args.length == 2 ){
      if( args[0].equals ("-1") && args[1] != null )
	test1( args[1] );
      else if( args[0].equals ("-2") && args[1] != null )
	test2( args[1] );
      else{
	printusage();
	System.exit( 1 );
      }
    }

  }

  private static void printMethods(Transaction t){
    PrintStream out = new PrintStream( System.out );
    out.println( "----->>>> Testing headers <<<<<------ " );
    out.print( t.headersAsString() );
    out.println( "----->>>> End testing headers <<<<<------ " );

    out.println("\n\n");
    out.println( "----->>>> Testing request methods <<<<<------ " );
    out.println( "content length -->" + Integer.toString( t.contentLength() ) );
    out.println( "content type   -->" + t.contentType() );
    out.println( "method         -->" + t.method() );
    out.println( "protocol       -->" + t.protocol() );
    out.println( "host           -->" + t.host() );
    if( t.requestURL() != null )
      out.println( "url            -->" + t.requestURL().getFile() );
    out.println( "HTTP version   -->" + t.version() ); 
    if( t.hasQueryString() )
      out.println( "query string -->" + t.queryString() );
    out.println( "----->>>> End testing methods <<<<<------ " );
    out.println("\n\n");
    out.println( "----->>>> The whole request message <<<<<------ " );
    try{
      t.printOn( out );
    }catch(Exception e){;}
  }  

  /**
   * Here for debugging purpose -- if DEBUG flag is false call Transaction's run method()
   */
  public void run(){
    if(!DEBUG){
      try{
	// make sure we have the header information
	if(headersObj ==  null) initializeHeader();
	
	Pia.instance().debug(this, "Got a head...");

	// and the content
	if( method().equalsIgnoreCase( "POST" ) ){
	  if(contentObj ==  null) initializeContent();
	  Pia.instance().debug(this, "Got a body...");
	  // incase body needs to update header about content length
	  if( headersObj!= null && contentObj != null )
	    contentObj.setHeaders( headersObj );
	}
      }catch (PiaRuntimeException e){
	errorResponse(500, "Server internal error");
	Thread.currentThread().stop();      
	notifyThreadPool();
      }

      // now we are ready to be resolved
      resolver.push(this);
      
      
      // loop until we get resolution, filling content object
      // (up to some memory limit)
      while(!resolved){
	//contentobject returns false when object is complete
	//if(!contentObj.processInput(fromMachine)) 
	
	Pia.instance().debug(this, "Waiting to be resolved");
	
	long delay = 1000;

	if( method().equalsIgnoreCase( "POST" ) ){
	  if(!contentObj.processInput()) {
	    try{
	      Thread.currentThread().sleep(delay);
	    }catch(InterruptedException ex){;}
	  }
	}else{
	  try{
	    Thread.currentThread().sleep(delay);
	  }catch(InterruptedException ex){;}
	}
	
      }
    
      // resolved, so now satisfy self
      satisfy( resolver);
      
      
      // cleanup?
      notifyThreadPool();
      

    }
    else{
      // make sure we have the header information
      Pia.instance().debug(this, "Running HTTPRequest's run method");

      try{
	if(headersObj ==  null) initializeHeader();
	Pia.instance().debug(this, "Got a head...");
    
	// and the content
	if(contentObj ==  null) initializeContent();
	Pia.instance().debug(this, "Got a body...");
	// incase body needs to update header about content length
	if( headersObj!= null && contentObj != null )
	  contentObj.setHeaders( headersObj );
      }catch (PiaRuntimeException e){
	errorResponse(500, "Server internal error");
	Thread.currentThread().stop();      
      }      


      if( contentObj != null ){
	boolean done = false;
	while( ! done ){
	  if(! contentObj.processInput()){
	    done = true;
	  }
	}
      }

      Pia.instance().debug(this, "Done running");
   
    }
  }

  /**
   * Take machine as source of input -- this is a debugging constructor
   * @param from source of input for header and content
   * @param debugflag set DEBUG flag -- if true use local run method and thread does not start
   * automatically
   */
  public HTTPRequest( Machine from, boolean debugflag ){
    DEBUG = debugflag;

    Pia.instance().debug(this, "Constructor-- [ machine from ] -- on duty...");
    handlers = new Queue();
    new Features( this );

    // we probably only need one instance of these objects
    
    fromMachine( from );
    toMachine( null );// done by default anyway
  }

  /**
   * A request transaction with default blank header and a define content -- debugging version.
   * @param from originator of request -- later data will be sent to this machine.
   * @param ct a define content.
   * @param debugflag set DEBUG flag -- if true use local run method and thread does not start
   * automatically
   */

  public HTTPRequest( Machine from, Content ct, boolean debugflag ){
    DEBUG = debugflag;

    Pia.instance().debug(this, "Constructor-- [ machine from, content ct ] on duty...");
    handlers = new Queue();
    new Features( this );

    contentObj = ct;
    headersObj = new Headers(); // blank header  

    if( contentObj != null )
      contentObj.setHeaders( headersObj );

    fromMachine( from );
    toMachine( null );
  }

  /**
   * A request transaction with define header and content -- debugging version.
   * @param from originator of request -- later data will be sent to this machine.
   * @param ct a define content.
   * @param hd a define header.
   * @param debug set DEBUG flag -- if true use local run method and thread does not start
   * automatically
   */ 
  public HTTPRequest( Machine from, Content ct, Headers hd, boolean debug ){
    DEBUG = debug;

    Pia.instance().debug(this, "Constructor-- [ machine from, content ct, headers hd ] -- on duty...");
    handlers = new Queue();
    new Features( this );

    contentObj = ct;
    headersObj = hd; 

    if( contentObj != null )
      contentObj.setHeaders( headersObj );

    fromMachine( from );
    toMachine( null );
  }



}








