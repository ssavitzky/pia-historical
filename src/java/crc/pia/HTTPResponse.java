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

    Pia.debug(this, "The first response line" + firstLine);
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
    Pia.debug(this, "Transmitting response...");

    Machine machine = toMachine();
    
    if ( machine!= null ){
      try{
	machine.sendResponse(this, resolver);
      }catch(PiaRuntimeException e){
	Pia.debug(this, "User stop" );
	//errorResponse( e.getMessage() );
      }
    }
    else{
      Pia.debug(this, "dropping  response" );
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
    Pia.debug(this, "The header : \n" + response.headersAsString() );
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



  /************************************************************************
  ** Construction:
  ************************************************************************/

  /**
   * Header and content is created from the fromMachine.
   * @param from where request is originated
   * @param to where response is sent to
   */
  public HTTPResponse( Machine from, Machine to ){
    super();

    Pia.debug(this, "Constructor-- [ machine from, machine to ] on duty...");
    
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
    Pia.debug(this, "Constructor-- [ transaction t, boolean startThread ] on duty...");

    handlers = new Queue();

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
    super();

    Pia.debug(this, "Constructor-- [ Transaction t, machine from ] on duty...");
    
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
    super();

    Pia.debug(this, "Constructor-- [ machine from, machine to, content ct ] on duty...");

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
   * Use to create error response to a transaction
   * @param t the transaction to which this is a response.
   * @param from where request is originated
   * @param to where to send response
   * @param ct a content
   * @param doStart if false thread does not start -- allows user to set header information.
   */
  public HTTPResponse( Transaction t, Machine from, Content ct,
		       boolean doStart ){
    super();

    Pia.debug(this, "Constructor-- [ transaction t, machine from, content ct ] on duty...");

    contentObj = ct;
    headersObj = new Headers(); //  blank header

    if( contentObj != null )
      contentObj.setHeaders( headersObj );

    requestTran = t;
    fromMachine( from );
    toMachine(  t.fromMachine() );
    
    if( doStart )
      startThread();
  }
 


  /**
   * Content is known and a blank header is created.
   * @param t request transaction
   * @param ct a content
   */
  public HTTPResponse(  Transaction t, Content ct ){
    super();

    Pia.debug(this, "Constructor-- [ transaction t, content ct ] on duty...");

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
    super();

    Pia.debug(this, "Constructor-- [ transaction t, content ct, headers hd ] on duty...");

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
	Pia.debug(this, "Unknown header type...");
	String msg = "Unknown header type...\n";
	throw new PiaRuntimeException (this
				       , "initializeContent"
				       , msg) ;
      }

    }catch(IOException e){
      Pia.debug( e.toString() );
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


  /************************************************************************
  ** Debugging:
  ************************************************************************/

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
      Pia.debug(this, "Done running");
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
    super();

    DEBUG = debug;
    Pia.debug(this, "Constructor-- [ machine from, machine to ] on duty...");
    
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
    super();

    DEBUG = debug;
    Pia.debug(this, "Constructor-- [ machine from, machine to, content ct ] on duty...");

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
    super();

    DEBUG = debug;
    Pia.debug(this, "Constructor-- [ transaction t, content ct ] on duty...");

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
    super();

    DEBUG = debug;
    Pia.debug(this, "Constructor-- [ transaction t, boolean startThread ] on duty...");

    contentObj = null;
    headersObj = new Headers(); //  blank header

    requestTran = t;
    fromMachine( t.toMachine() );
    toMachine( t.fromMachine() );

  }
  
  
}









