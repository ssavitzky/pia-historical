// HTTPResponse.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/** 
 * implement transaction for HTTP response
 */


package crc.pia;

import java.net.URL;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.OutputStream;
import java.util.Vector;
import java.util.StringTokenizer;

import crc.ds.Queue;
import crc.ds.Features;
import crc.ds.List;
import crc.pia.Machine;
import crc.pia.Content;
import crc.pia.Transaction;

import crc.tf.Registry;

public class  HTTPResponse extends Transaction {
  public boolean DEBUG = false;  

  /** 
   * status code of response
   */
  protected int  code = 200;

  /**
   * reason
   */
  protected  String reason = "ok";

  /**
   * Attribute index - controls to be insert into a response
   *
   */
  protected List controls = new List();



  /**
   * true if this transaction is a response
   */
  public boolean isResponse(){
    return   true;
  }

  /**
   * true if this transaction is a request
   */
  public boolean isRequest(){
    return   false;
  }


  /**
   * Returns HTTP + statuscode + message
   * @returns HTTP statuscode  message
   */
  public String protocolInitializationString(){
   //subclass should implement
    return "HTTP/"+major+"."+minor+ " "+  code + " " +reason();
 }	

/** 
 * parse the first line
 * parse the request line to get method, url, http's major and minor version numbers
   */
  protected void parseInitializationString(String firstLine)throws IOException{
    StringTokenizer tokens = new StringTokenizer(firstLine, " ");
    protocol = tokens.nextToken();
    if( protocol==null ) throw new RuntimeException("Bad reply.  Invalid status line.");

    Pia.instance().debug(this, "The first response line" + firstLine);
    String majorMinor = protocol.substring("HTTP/".length());
    StringTokenizer mytokens = new StringTokenizer( majorMinor, "." );
    String zmajor = mytokens.nextToken();
    if( zmajor!=null ){
	major = zmajor;
	String zminor = mytokens.nextToken();
        minor = zminor;
    }

    if( tokens.hasMoreTokens() )
      code = Integer.parseInt( tokens.nextToken() );

    if( tokens.hasMoreTokens() )
      reason = tokens.nextToken();
  }
  
  
  /** 
   * Send a response back to requester
   */
  public void defaultHandle( Resolver resolver ){
    sendResponse( resolver );
  }
 


  // response specific stuff
  /**
   * sendResponse -- Utilities to actually respond to a transaction, get a request, 
   * or generate (return) an error response transaction.
   *
   * These pass the resolver down to the Machine that actually does the 
   * work, because it might belong to an agent.
   *
   */
  public void sendResponse( Resolver resolver ){
    /*
     * Default handling for a response:
     * send it to the toMachine.  If the destination is not a reference 
     * to a machine, the response just gets dropped.  'Nowhere' is a good
     * non-reference to use in this case.
     */
    Pia.instance().debug(this, "Transmitting response...");

    Machine machine = toMachine();
    
    if ( machine!= null ){
      try{
	machine.sendResponse(this, resolver);
      }catch(PiaRuntimeException e){
	Pia.instance().debug(this, "User stop" );
	//errorResponse( e.getMessage() );
      }
    }
    else{
      Pia.instance().debug(this, "dropping  response" );
    }
    
  }

  /**
   * Construct and return an error response.
   *	This is a new transaction, sent to <em>this</em> transaction's
   *	toMachine, in effect replacing this one.
   */
  public void errorResponse(int code, String msg){
    int mycode = code;
    StringBufferInputStream foo = null;
    String masterMsg = "Agency is unable to process " + requestURL() + ": ";

    if ( msg != null ){
      masterMsg += msg;
      foo = new StringBufferInputStream( masterMsg );
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
    Transaction response = new HTTPResponse( Pia.instance().thisMachine,
					     toMachine(), ct, false);
    response.setStatus( mycode );
    response.setContentType( "text/plain" );
    response.setContentLength( masterMsg.length() );
    Pia.instance().debug(this, "The header : \n" + response.headersAsString() );
    response.startThread();
  }

  /**
   * Get the reason phrase for this reply.
   * @return A String encoded reason phrase.
   */
  

  public String reason() {
    if(reason ==  null) reason = standardReason(code);

    return reason;
    
  }
  

  /**
   * Returns status code for this response.
   * @returns the status code for this response. 
   */
  public int statusCode(){
    return  code;
  }
  

    /**
     * Set this reply status code.
     * This will also set the reply reason, to the default HTTP/1.1 reason
     * phrase.
     * @param status The status code for this reply.
     */

    public void setStatus(int s) {
	if ((statusCode() != s) || (reason() == null))
	    reason = standardReason(s);
	code = s;
    }

    /**
     * Get the reason phrase for this reply.
     * @return A String encoded reason phrase.
     */


    /**
     * Set the reason phrase of this reply.
     * @param reason The reason phrase for this reply.
     */

    public void setReason(String reason) {
	this.reason = reason;
    }



  /**
   * controls -- Add controls (buttons,icons,etc.) for agents to this response
   * @param aThing any control
   */
  public void addControl( Object aThing ){
    controls.push( aThing );
  }

  /**
   * Return controls as an array of Objects.
   * @return an array of Objects
   */
  public List controls(){
    return controls;
  }



  /**
   * Header and content is created from the fromMachine.
   * @param from where request is originated
   * @param to where response is sent to
   */
  public HTTPResponse( Machine from, Machine to ){
    Pia.instance().debug(this, "Constructor-- [ machine from, machine to ] on duty...");
    handlers = new Queue();
    new Features( this );
    
    fromMachine( from );
    toMachine(  to );

    startThread();
  }
 
  /**
   * A response to a request transaction -- a blank header is created.
   * @param t request transaction
   * @param doStart if false thread does not start automatically
   */
  public HTTPResponse(  Transaction t, boolean doStart  ){
    Pia.instance().debug(this, "Constructor-- [ transaction t, boolean startThread ] on duty...");

    handlers = new Queue();
    new Features( this );

    contentObj = null;
    headersObj = new Headers(); //  blank header

    requestTran = t;
    fromMachine( t.toMachine() );
    toMachine( t.fromMachine() );

    if( doStart )
      startThread();
  }
  

  /**
   *  @param t request transaction
   *  @param from source from which header and content are created
   */
  public HTTPResponse(Transaction t,  Machine from ){
    Pia.instance().debug(this, "Constructor-- [ Transaction t, machine from ] on duty...");
    handlers = new Queue();
    new Features( this );
    
    requestTran = t;
    fromMachine( from );
    toMachine(  t.fromMachine() );

    startThread();
  }
 


  /**
   * Use to create error response -- a blank header is created.
   * @param from where request is originated
   * @param to where to send response
   * @param ct a define content
   * @param doStart if false thread does not start -- allows user to set header information.
   */
  public HTTPResponse( Machine from, Machine to, Content ct, boolean doStart ){
    Pia.instance().debug(this, "Constructor-- [ machine from, machine to, content ct ] on duty...");

    handlers = new Queue();
    new Features( this );
    
    contentObj = ct;
    headersObj = new Headers(); //  blank header

    if( contentObj != null )
      contentObj.setHeaders( headersObj );

    fromMachine( from );
    toMachine(  to );
    
    if( doStart )
      startThread();
     
  }
 


  /**
   * Content is known and a blank header is created.
   * @param t request transaction
   * @param ct a define content
   */
  public HTTPResponse(  Transaction t, Content ct ){
    Pia.instance().debug(this, "Constructor-- [ transaction t, content ct ] on duty...");

    handlers = new Queue();
    new Features( this );

    contentObj = ct;
    headersObj = new Headers(); //  blank header

    if( contentObj != null )
      contentObj.setHeaders( headersObj );
  
    requestTran = t;
    fromMachine( t.toMachine() );
    toMachine( t.fromMachine() );
    startThread();
  }
  
  /**
   * Content and  header are known.
   * @param t request transaction
   * @param ct a defined content
   * @param hd a defined header
   */
  public HTTPResponse(  Transaction t, Content ct, Headers hd ){
    Pia.instance().debug(this, "Constructor-- [ transaction t, content ct, headers hd ] on duty...");

    handlers = new Queue();
    new Features( this );

    contentObj = ct;
    headersObj = hd; //  maybe generate?

    if( contentObj != null )
      contentObj.setHeaders( headersObj );
  
    requestTran = t;
    fromMachine( t.toMachine() );
    toMachine( t.fromMachine() );

    startThread();
  }
  
  /**
   *  Create a content object from the fromMachine.
   * 
   */
  protected void initializeContent() throws PiaRuntimeException{
    InputStream in;
    String ztype = null;

    try{
      in = fromMachine().inputStream();

      if( (ztype = contentType()) == null )
	ztype = "text/html";

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

    }catch(IOException e){
      Pia.instance().debug( e.toString() );
    }
  }

  /**
   * output protocolInitializationString, headers, and content.
   */
  public void printOn(OutputStream stream) throws IOException{
     PrintStream out = new PrintStream( stream );

     String responseLine = protocolInitializationString();
     if( responseLine != null )
       out.println( responseLine );
     
     String headersString= headersAsString();
     if( headersString != null )
       out.print( headersString );
     
     Content c = contentObj();
     if( c!= null )
       out.print( c.toString() );
       
     out.flush();
  }


  private static void sleep(int howlong){
   Thread t = Thread.currentThread();

   try{
     t.sleep( howlong );
   }catch(InterruptedException e){;}

  }

  /* ========================================
  private static void test1( String filename ){
    try{
      System.out.println("Testing response w/ from and to machines as arguments.");
      System.out.println("From machine gets its data from response.txt file.");

      InputStream in = new FileInputStream (filename);
      Machine machine1 = new Machine();
      machine1.setInputStream( in );

      Machine machine2 = new Machine();

      boolean debug = true;
      Transaction trans1 = new HTTPResponse( machine1, machine2, debug );
      Thread thread1 = new Thread( trans1 );
      thread1.start();

      while( true ){
	sleep( 1000 );
	if( !thread1.isAlive() )
	  break;
      }

      printMethods( trans1 );
      System.exit( 0 );
    }catch(Exception e ){
      System.out.println( e.toString() );
    }
  }

  private static void test2(String filename){
    //testing second constructor
    // with from, to machines and content
    System.out.println("Testing response w/ from, to, and content as arguments.");
    System.out.println("This response has a blank header and a content which is read from responsebody.txt.");
    System.out.println("In the run(), processInput() gets called.");

    try{
      InputStream in = new FileInputStream (filename);
      Machine machine1 = new Machine();
      Machine machine2 = new Machine();

      ByteStreamContent c = new ByteStreamContent();
      c.source( in );

      boolean dostart = false;
      boolean debug   = true;
      Transaction trans1 = new HTTPResponse( machine1, machine2, c, false, debug );
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

  private static void test3( String filename ){
    System.out.println("Testing response w/ request transaction and content as arguments.");
    System.out.println("This response has a blank header and content.");
    System.out.println("In the run(), processInput() gets called.");
    System.out.println("The request transaction is read from get.txt.");

    try{
      InputStream in = new FileInputStream (filename);
      Machine machine1 = new Machine();
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


      ByteStreamContent c = new ByteStreamContent();
      Transaction trans2 = new HTTPResponse( trans1, c, debug );
      Thread thread2 = new Thread( trans2 );
      thread2.start();

      while( true ){
	sleep( 1000 );
	if( !thread2.isAlive() )
	  break;
      }

      printMethods( trans2 );
      System.exit(0);
    }catch(Exception e){
    }
  }

  private static void test4( String requestfile, String responsefile ){
    try{
      System.out.println("Testing response w/ Trasaction as the only argument.");
      System.out.println("The request transaction's from and to machine will be switched.");
      System.out.println("Also test setStatus, setReason, and setting headers information.");
      InputStream in = new FileInputStream (requestfile);
      Machine machine1 = new Machine();
      machine1.setInputStream( in );

      InputStream out = new FileInputStream (responsefile);
      Machine machine2 = new Machine();
      machine2.setInputStream( out );

      boolean debug = true;
      Transaction trans1 = new HTTPRequest( machine1, debug );
      trans1.toMachine( machine2 );
      Thread thread1 = new Thread( trans1 );
      thread1.start();

      while( true ){
	sleep( 1000 );
	if( !thread1.isAlive() )
	  break;
      }

      boolean start = false;
      Transaction trans2 = new HTTPResponse( trans1, start, debug );
      Thread thread2 = new Thread( trans2 );
      thread2.start();

      while( true ){
	sleep( 1000 );
	if( !thread2.isAlive() )
	  break;
      }

      System.out.println( "----->>>> Testing setting reason, message,... <<<<<------ " );
      Headers head = null;
      if( (head = trans2.headers())!= null ){
	head.setHeader("Server","FOOBAR/1.1");
	head.setHeader("MIME-version", "5.0");
	head.setHeader("Content-type", "image/gif");
      }
      trans2.setStatus(400);
      trans2.setReason("not found");
      printMethods( trans2 );
      System.exit(0);

    }catch(Exception e){
    }
  }

 private static void test5( String requestfile, String responsefile ){
    try{
      System.out.println("Testing response w/ from and to machines as arguments.");
      System.out.println("From machine gets its data from response.txt file.");
      System.out.println("Also, test transaction's features.");

      InputStream in = new FileInputStream (requestfile);
      Machine machine1 = new Machine();
      machine1.setInputStream( in );

      InputStream out = new FileInputStream (responsefile);
      Machine machine2 = new Machine();
      machine2.setInputStream( out );

      boolean debug = true;
      Transaction trans1 = new HTTPRequest( machine1, debug );
      trans1.toMachine( machine2 );
      Thread thread1 = new Thread( trans1 );
      thread1.start();

      while( true ){
	sleep( 1000 );
	if( !thread1.isAlive() )
	  break;
      }


      boolean start = false;
      Transaction trans2 = new HTTPResponse( trans1, start, debug );

      Thread thread2 = new Thread( trans2 );
      thread2.start();

      while( true ){
	sleep( 1000 );
	if( !thread2.isAlive() )
	  break;
      }

      Headers head = null;
      if( (head = trans2.headers())!= null ){
	head.setHeader("Version","PIA/blah.blah");
	head.setHeader("Content-type", "image/gif");
      }

      System.out.println( "Is agent response? ->" + trans2.compute("IsAgentResponse").toString() ); 

      Object o = trans2.compute("Title");
      if( o != null && o instanceof String ){
	String title = (String)o;
	System.out.println( "What is the tile, ya? ->" + title ); 
      }
      System.exit(0);
    }catch(Exception e ){
      System.out.println( e.toString() );
    }
  }


 private static void printusage(){
    System.out.println("Needs to know what kind of test");
    System.out.println("For test 1, (const. 1) --> java crc.pia.HTTPResponse -1 response.txt");
    System.out.println("For test 2, (const. 2) --> java crc.pia.HTTPResponse -2 responsebody.txt");
    System.out.println("For test 3, (const. 3) --> java crc.pia.HTTPResponse -3 get.txt");
    System.out.println("For test 4, (const. 4) --> java crc.pia.HTTPResponse -4 get.txt responsebody.txt");
    System.out.println("For test 5, (trans. features) --> java crc.pia.HTTPResponse -5 post.txt response.txt");
  }

  public static void main(String[] args){

    if( args.length == 0 ){
      printusage();
      System.exit( 1 );
    }

    if( args.length == 1 ){
	printusage();
	System.exit( 1 );
    }else if (args.length == 2 ){
      if( args[0].equals ("-1") && args[1] != null )
	test1( args[1] );
      else if( args[0].equals ("-2") && args[1] != null )
	test2( args[1] );
      else if( args[0].equals ("-3") && args[1] != null )
	test3( args[1] );
    } else if( args.length == 3){ 
	if( args[0].equals ("-4") && args[1] != null && args[2] != null )
	  test4( args[1], args[2] );
	else if( args[0].equals ("-5") && args[1] != null && args[2] != null )
	  test5( args[1], args[2] );
	else{
	  printusage();
	  System.exit( 1 );
	}
    }

  }

  private static void printMethods(Transaction t){
      PrintStream out = new PrintStream( System.out );
      out.println( "----->>>> Testing response headers <<<<<------ " );
      out.print( t.headersAsString() );
      out.println( "----->>>> End testing headers <<<<<------ " );

      out.println("\n\n");
      out.println( "----->>>> Testing response methods <<<<<------ " );
      out.println( "method         -->" + t.method() );
      out.println( "content length -->" + Integer.toString( t.contentLength() ) );
      out.println( "content type   -->" + t.contentType() );
      out.println( "protocol       -->" + t.protocol() );
      out.println( "host           -->" + t.host() );
      out.println( "reason   -->" + t.reason() );
      out.println( "status code -->" + Integer.toString( t.statusCode()) );
      out.println( "proInitializationString -->" + t.protocolInitializationString() );
      if( t.requestURL() != null )
	out.println( "url            -->" + t.requestURL().getFile() );
      out.println( "HTTP version   -->" + t.version() ); 
      if( t.hasQueryString() )
	out.println( "query string -->" + t.queryString() );
      out.println( "----->>>> End testing methods <<<<<------ " );
      out.println("\n\n");

      out.println( "----->>>> The whole response message <<<<<------ " );
      try{
	t.printOn( out );
      }catch(Exception e){;}


  }
========================================= */
  
  public void run(){
    if(!DEBUG)
      super.run();
    else{
      // make sure we have the header information
      try{
	if(headersObj ==  null) initializeHeader();

	// and the content
	if(contentObj ==  null) initializeContent();
      }catch (PiaRuntimeException e){
	errorResponse(500, "Server internal error");
	Thread.currentThread().stop();
      }

      //This is actually call if constructor
      //HTTPResponse( Machine from, Machine to, Content ct )
      //is used
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
   * Header and content is created from the fromMachine -- for debugging only.
   * @param from where request is originated
   * @param to where response is sent to
   * @param debug set DEBUG flag -- if true use local run method and thread does not start
   * automatically
   */
  public HTTPResponse( Machine from, Machine to, boolean debug ){
    DEBUG = debug;

    Pia.instance().debug(this, "Constructor-- [ machine from, machine to ] on duty...");
    handlers = new Queue();
    new Features( this );
    
    fromMachine( from );
    toMachine(  to );

  }


  /**
   * Use to create error response -- a blank header is created -- for debugging only.
   * @param from where request is originated
   * @param to where to send response
   * @param ct a define content
   * @param doStart if false thread does not start -- allows user to set header information.
   * @param debug set DEBUG flag -- if true use local run method and thread does not start
   * automatically
   */
  public HTTPResponse( Machine from, Machine to, Content ct, boolean doStart, boolean debug ){
    DEBUG = debug;

    Pia.instance().debug(this, "Constructor-- [ machine from, machine to, content ct ] on duty...");

    handlers = new Queue();
    new Features( this );
    
    contentObj = ct;
    headersObj = new Headers(); //  blank header

    if( contentObj != null )
      contentObj.setHeaders( headersObj );

    fromMachine( from );
    toMachine(  to );
    
  }

  /**
   * Content is known and a blank header is created -- for debugging only.
   * @param t request transaction
   * @param ct a define content
   * @param debug set DEBUG flag -- if true use local run method and thread does not start
   * automatically
   */
  public HTTPResponse(  Transaction t, Content ct, boolean debug ){
    DEBUG = debug;
    Pia.instance().debug(this, "Constructor-- [ transaction t, content ct ] on duty...");

    handlers = new Queue();
    new Features( this );

    contentObj = ct;
    headersObj = new Headers(); //  blank header

    if( contentObj != null )
      contentObj.setHeaders( headersObj );
  
    requestTran = t;
    fromMachine( t.toMachine() );
    toMachine( t.fromMachine() );
  }
  
  /**
   * A response to a request transaction -- a blank header is created -- for debugging only.
   * @param t request transaction
   * @param doStart if false thread does not start automatically
   * @param debug set DEBUG flag -- if true use local run method and thread does not start
   * automatically
   */
  public HTTPResponse(  Transaction t, boolean doStart, boolean debug  ){
    DEBUG = debug;
    Pia.instance().debug(this, "Constructor-- [ transaction t, boolean startThread ] on duty...");

    handlers = new Queue();
    new Features( this );

    contentObj = null;
    headersObj = new Headers(); //  blank header

    requestTran = t;
    fromMachine( t.toMachine() );
    toMachine( t.fromMachine() );

  }
  
  
}






