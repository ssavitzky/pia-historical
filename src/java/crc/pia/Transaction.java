// Transaction.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.


/**
 * Transactions generalize the HTTP classes Request and Response.
 * They are used in the rule-based resolver, which associates 
 * transactions with the interested agents.
 *
 * A Transaction has a queue of ``handlers'', which are called
 * (from the $transaction->satisfy() method) after all agents
 * have acted on it.  At least one must return true, otherwise
 * the transaction will ``satisfy'' itself.
 *
 * In a proper implementation, Transaction would be a subclass of 
 * DS::Thing, and Response and Request would be subclasses of it.
 * It's done backwards here in order to re-use existing libraries.
 *
 */

package crc.pia;
import java.util.Enumeration;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataInputStream;
import java.lang.Runnable; // added by Greg
import java.util.Vector;

import crc.ds.Features;
import crc.ds.Queue;
import crc.ds.UnaryFunctor;

import crc.pia.Machine;
import crc.pia.Content;
import crc.pia.Resolver;
import crc.tf.Registry;
import crc.util.Utilities;

import crc.pia.Athread;
import crc.tf.UnknownNameException;

public abstract class Transaction implements Runnable{ // implements Runnable added by Greg
  public boolean DEBUG = false;
  /**
   * Attribute index - execution thread
   */
  protected Athread executionThread;

  /**
   * Attribute index - features of this transaction
   */
  protected Features features;

  /**
   * Attribute index - if true transaction is a response.
   *
   */
  public boolean isResponse = false ;

  /**
   * Attribute index - from machine -- the machine that originates the request.
   *
   */
  protected Machine fromMachine;

  /**
   * Attribute index - to machine -- target machine that will process the request.
   *
   */
  protected Machine toMachine;

  /**
   * Attribute index - content obj of this transaction.
   *
   */
  protected Content contentObj;

  /** 
   *  class variable-- factory to generate  content objects
   */
  // subclasses probably want to use different factories
  static public ContentFactory cf = new ContentFactory();

  /**
  *  class variable-- factory to generate headers
  */
  static public HeaderFactory hf = new HeaderFactory();

  /** Class variable-- resolver
   * transactions need to communicate with the resolver
   *  PIA main should set this
   */

  static public Resolver  resolver;
  
  /** 
   *  Attribute index - has the resolver finished with this transaction?
   */
  protected boolean resolved = false;
  
  /**
   * Attribute index - header obj of this transaction.
   */
  protected Headers headersObj;

  /**
   * Attribute index - queue of handlers.
   *
   */
  protected Queue handlers;

  /** protocol
   * the protocol of this request
   */
  protected String protocol;

  /** protocol major number
   *
   */
  protected String major = "1";

  /** protocol minor number
   *
   */
  protected String minor = "0";

  /**
   * Attribute index - store request URL string
   */
  protected Transaction requestTran;

  /**
   * Below are interfaces related to Request transaction.  
   *
   */

  /**
   * @return header object
   */
  public Headers headers(){
    return headersObj;
  }

  /**
   *  @return the content length for this request, or -1 if not known. 
   */
  public int contentLength(){
    int res = -1;
    if( headers() != null )
      res = headers().contentLength();
    return res;
  } 

  /**
   *  @returns the content type for this request, or null if not known. 
   *
   */
  public String contentType(){
    String res = null;
    if( headers() != null )
      res =  headers().contentType();
    return res;
  }

  /**
   * @returns the value of a header field, or null if not known. 
   *
   */
  public String header(String name){
    String res = null;
    if( headers() != null )
      res = headers().header( name );
    return res;
  }

  /**
   * @returns all header information as string. Machine.java uses this.
   *
   */
   public String headersAsString(){
     String res = null;
     if( headers() != null )
       res = headers().toString();
     return res;
   }

 /**
   * the initial protocol string (e.g. everything before the header)
   */
 public String protocolInitializationString;

  /**
   * @returns the initial protocol string (e.g. everything before the header)
   */
  public String protocolInitializationString(){
    return protocolInitializationString;
  }

  /**
   * @set the content object
   */
  public void setContentObj( Content source ){
    if( source != null )
      contentObj = source;
  }

  /**
   * @returns the request method. 
   */
  public String method() {
    return null;   
  }

  /**
   * set the request method. 
   */
  public void setMethod(String method) {
  }

 /**
   * set the request url. 
   */
  public void setRequestURL(String url) {
  }
	
  /**
   *   @returns the protocol of the request. 
   *
   */
  public String protocol() {
    return protocol;
  }
  
  /**
   *   @returns the host name from the header, or null if not known. 
   *
   */
  public String host() {
    String res = null;

    if( headers() != null )
      res = headers().header( "Host" );
    return res;
  }

  /**
   * @return url string
   */
  public String url(){
    return null;
  }

  /**
   *   @returns the full request URI. 
   *
   */    
  public URL requestURL(){
    if(isResponse()){
      if( requestTran() != null )
	return requestTran().requestURL();
      else return null;
    }
    else
      return null;
  }

  /**
   * Set header info.
   *
   */
  public void setHeader(String key, String value){
    if( headers() != null )
      headers().setHeader(key, value);
   }


  /**
   * Interfaces related to Response.
   */


  /**
   *   @returns the status code for this response. 
   *
   */
  public int statusCode(){
      return -1;
  }
  
  /**
   *   Sets the content length for this response. 
   *
   */
  public void setContentLength(int len){
    if( headers() != null )
      headers().setContentLength( len );
  }
  
  /**
   *   Sets the content type for this response. 
   *
   */
  public void setContentType(String type){
    if( headers() != null ){
      try{
	headers().setContentType( type ); 
      }catch(Exception e){;}
    }
  }

    /**
     * Set this reply status code.
     * This will also set the reply reason, to the default HTTP/1.1 reason
     * phrase.
     * @param status The status code for this reply.
     */

    public void setStatus(int status) {
	return;
    }

    /**
     * Get the reason phrase for this reply.
     * @return A String encoded reason phrase.
     */

    public String reason() {
	return null;
    }

    /**
     * Set the reason phrase of this reply.
     * @param reason The reason phrase for this reply.
     */

    public void setReason(String reason) {
	return;
    }


  /**
   * @return requested transaction
   */
  public Transaction requestTran(){
    if( requestTran != null )
      return requestTran;
    else
      return null;
  }

  /**
   * true if this transaction is a response
   */
  public boolean isResponse(){
    return false;
  }

  /**
   * true if this transaction is a request
   */
  public boolean isRequest(){
    return false;
  }

  /**
   * @return HTTP version number
   */
  public String version(){
    return "HTTP/"+major+"."+minor;
  }

  /**
   * true if this transaction is a request and has parameters
   */
  public boolean hasQueryString(){
    return false;
  }

  /**
   * return parameters associated with a request
   */
  public String queryString(){
    return null;
  }

  /**
   * output protocolInitializationString, headers, and content if request.
   * don't know yet about response
   */
  public void printOn(OutputStream out) throws IOException{
  }

  /**
   * controls such as buttons -- usually inserted with a response
   */
  public Object[] controls(){
    return null;
  }


  /**
   * queue -- returns handler list
   * 
   */ 
  protected Enumeration queue(){
    return handlers.queue();
  }
 
  /**
   * shift -- remove and return the handler at front of list .
   * If there is no transaction returns null.
   */
  public Object shift(){
    return handlers.shift();
  }

  /**
   * unshift -- put a handler to the front of the list. 
   * @return the number of handlers
   */ 
  public int unshift( Object obj ){
    return handlers.unshift( obj );
  }

  /**
   * push -- push a handler onto the end of the list. 
   * @return the number of elements
   */  
  public int push( Object obj ){
    return handlers.push( obj );
  }

  /**
   * pop -- removes a handler from the back of the queue and returns it. 
   * @return the number of elements
   */ 
  public Object pop(){
    return handlers.pop();
  }

  /**
   * Number of handlers in queue
   *
   */
  public int size(){
    return handlers.size();
  }

  /**
   * return a ds's Feature object 
   */

  public Features features (){
    return features;
  }

  /**
   * set Features object
   */
  public void setFeatures(Features features){
    if( features != null )
      this.features = features;
  }

  /**
   * Get the value of the named feature.  If does not exist,
   * compute it and return the value
   */
  public Object is( String name ) {
    return features.feature( name, this );
  }

  /**
   * Test a named feature and return a boolean.
   */
  public boolean test( String name ) {
    return features.test(name, this);
  }

  /**
   * Compute and assert the value of the given feature.
   * Can be used to recompute features after changes
   */
  public Object compute( String name ){
    return features.compute(name, this);
  }

  /**
   * assert a given feature with the given value default to Boolean true
   */
  public void assert( String name ) {
    features.assert(name, new Boolean( true ) );
  }

  /**
   * assert a given feature with the given value
   */
  public void assert( String name, Object value ) {
    features.assert(name, value);
  }

  /**
   * deny a given feature
   */
  public void deny( String name ) {
    features.deny(name);
  }

  public boolean has( String name ) {
    return features.has(name);
  }

  public Object computeFeature( String featureName ) throws UnknownNameException{
    UnaryFunctor c;
    try{
     c = (UnaryFunctor)Registry.calculatorFor( featureName );
     return c.execute( this );
    }catch(UnknownNameException e){
      // log here
      throw e;
    }
  }

  /**
   * Accessing function to content object
   * 
   */
  public Content contentObj(){
    return contentObj;
  }

  /*
  private String readFirstLine(InputStream in){
    StringBuffer buf = new StringBuffer();
   
    int ch;
    for(int i= 0; i < 32; i++){
      try{
	ch = in.read();
	buf.append( (char)ch );
      }catch(Exception e){;}
    }
    return new String( buf );

  }
  */

  /**
   * temporary treatment of content objects
   * while transitioning to full objectIvity...
   * Append previous transaction's content to this trasaction content
   */
  protected void initializeHeader(){
    //content source set in fromMachine method
    InputStream in;
    String line;
    String firstLine;

    try{
      in = fromMachine().inputStream();

      DataInputStream input = new DataInputStream( in );
      firstLine = input.readLine();
    
      Pia.instance().debug(this, "the firstline-->" + firstLine);
   
      headersObj  = hf.createHeader( in );
      
      parseInitializationString( firstLine );

    }catch(IOException e){
    }
  }

  /**
   * temporary treatment of content objects
   * while transitioning to full objectIvity...
   * Append previous transaction's content to this trasaction content
   */
  protected void initializeContent(){
    //content source set in fromMachine method
  }



 /** 
  * parse the first line
  */
  protected abstract void parseInitializationString( String firstLine )throws IOException;
  
  
  /**
   * fromMachine returns machine that initialize the request.
   * 
   */
  public Machine fromMachine(){
    return fromMachine;
  }
  
  /**
   * fromMachine sets to fromMachine the machine that initializeed the request.
   * 
   */
  public void fromMachine(Machine machine){
    if( machine != null ){
      fromMachine = machine;
      try{
	Content c = contentObj();
	if( c != null )
	  c.source( machine.inputStream() );
      }catch(IOException e){
	//fix me what should i do
      }
    }
  }

  /**
   * toMachine get toMachine-- the machine that is the target of the request.
   * 
   */
  public Machine toMachine(){
    return toMachine;
  }

  /**
   * toMachine sets toMachine-- the machine that is the target of the request.
   * 
   */
  public void toMachine(Machine machine){
    if( machine != null ){
      toMachine = machine;
    }
  }



  /**
   * handle -- A transaction can handle a request by pushing itself
   * onto the given resolver.  This allows agents to push
   * responses onto a *transaction* to be handled.  We return
   * success, indicating that the request has been satisfied.
   *
   */
  public boolean handle( Resolver resolver ){
    // deliver responses before processing more request
    resolver.unshift( this );
    return true;
  }

  /**
   * defaultHandle --  what to do if nothing else satisfies us
   */
   public void defaultHandle( Resolver resolver ){
   // subclass should implement
     Pia.instance().debug(this, "defaultHandle ...");
   }

  /** 
   * resolved:
   * called by resolver when we are ready to be satisfied
   */
  public void resolved() 
  {
    resolved = true;
    
  }

  /**
   * sendResponse -- Utilities to actually respond to a transaction, get a request, 
   * or generate (return) an error response transaction.
   *
   * These pass the resolver down to the Machine that actually does the 
   * work, because it might belong to an agent.
   *
   */
  public void sendResponse( Resolver resolver ){
  }

  /**
   * handleRequest -- Default handling for a request:
   * ask the destination machine to get it.
   * complain if there's no destination to ask.
   */
  public void handleRequest( Resolver resolver ){
  }
  
  /**
   * errorResponse -- Return a "not found" error for a request with no destination.
   *
   *
   */
  protected void errorResponse(String msg){
  }

  protected boolean matches(Vector criteria){
    return features().matches( criteria, this );
  } 

  /**
   * Satisfying transactions:
   * A transaction can handle a request by pushing itself
   * onto the given resolver.  This allows agents to push
   * responses onto a *transaction* to be handled.  We return
   * success, indicating that the request has been satisfied.
   *
   */
  public void satisfy(Resolver resolver) {
    Object obj;
    boolean satisfied = false;

    Pia.instance().debug(this, "Satisfaction ?");
    
    Enumeration e = handlers.queue();
    while( e.hasMoreElements() ){
      obj = e.nextElement();
      if( obj instanceof Transaction ){
	Transaction tran = (Transaction)obj;
	if ( tran.handle( Pia.instance().resolver() ) == true )
	  satisfied = true;
      }else if( obj instanceof Agent ){
	Agent agnt = (Agent)obj;
	if ( agnt.handle(this, Pia.instance().resolver()) == true )
	  satisfied = true;
	}
      else{
	if ( obj instanceof Boolean  ){
	  Boolean result = (Boolean) obj;
	  if( result.booleanValue() )
	    satisfied = true;
	}
      }
    }
    
    //If still not satisfied, do the default thing:
    //  send a response or get a request.

    if(!satisfied){
      Pia.instance().debug(this, "Got no satisfaction...");
      defaultHandle( resolver );
    }

  }

  /**
   * controls -- Add controls (buttons,icons,etc.) for agents to this response
   * actual final form determined by machine
   * NOTE: not implemented  // maybe move to content?
   */
  public void addControl( Object aThing ){
  }


 // constructor methods should wait until  run method is called to do any
 // initialization that requires IO

/** run - process the transaction
 * THIS should be called  just after a transaction has been but,
 * created before resolution begins.(Resolution should wait until feature values are available).
 * This thread should live until the transaction has been satisfied.
 * each transaction goes through two logical steps.
 * first the content cones in from the FROM machine 
 * then content goes out to the TO machine.
 * for reasons of efficiency,  and interactions with the resolver,
 * the actual processing is not so clean.
 */
  public void run()
  {

    // make sure we have the header information
    if(headersObj ==  null) initializeHeader();
    
    Pia.instance().debug(this, "Got a head...");
    // and the content
    if(contentObj ==  null) initializeContent();

    Pia.instance().debug(this, "Got a body...");
    // incase body needs to update header about content length
    if( headersObj!= null && contentObj != null )
      contentObj.setHeaders( headersObj );

    // now we are ready to be resolved
    resolver.push(this);
  
  
    // loop until we get resolution, filling content object
    // (up to some memory limit)
    while(!resolved){
      //contentobject returns false when object is complete
      //if(!contentObj.processInput(fromMachine)) 

      Pia.instance().debug(this, "Waiting to be resolved");

      long delay = 1000;
      try{
	Thread.currentThread().sleep(delay);
      }catch(InterruptedException ex){;}

      /*
      if(!contentObj.processInput()) {
	try{
	  long delay = (long)(Math.random() * 10000.0);
	  Thread.currentThread().sleep(delay);
	}catch(InterruptedException ex){
	  break;
	}
      }
      */

    }
    
    // resolved, so now satisfy self
    satisfy( resolver);
    
    
    // cleanup?
    ThreadPool tp = Pia.instance().threadPool();
    tp.notifyDone( executionThread );
    
  }

  public void startThread(){
    
    ThreadPool tp = Pia.instance().threadPool();
    Athread zthread = tp.checkOut();
    executionThread = zthread;
    if( zthread != null ) zthread.execute( this );

  }

  /**
   * for debugging only
   */
  public Thread myThread(){
    return executionThread.zthread;
  }

} 
















