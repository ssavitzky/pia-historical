// Transaction.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.


/**
 * Transactions generalize the HTTP classes Request and Response.
 * They are used in the rule-based resolver, which associates 
 * transactions with the interested agents.
 *
 * A Transaction has a queue of ``handlers'', which are called
 * (from the transaction.satisfy() method) after all agents
 * have acted on it.  At least one must return true, otherwise
 * the transaction will ``satisfy'' itself.
 */

package crc.pia;
import java.util.Enumeration;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataInputStream;
import java.lang.Runnable; // added by Greg

import crc.ds.Features;
import crc.ds.HasFeatures;
import crc.ds.Queue;
import crc.ds.Table;
import crc.ds.List;
import crc.ds.UnaryFunctor;
import crc.ds.Criteria;

import crc.sgml.SGML;
import crc.sgml.AttrBase;

import crc.pia.Machine;
import crc.pia.Content;
import crc.pia.Resolver;
import crc.pia.Athread;

import crc.util.Utilities;

import crc.tf.Registry;
import crc.tf.TFComputer;

import w3c.www.http.HTTP;

public abstract class Transaction extends AttrBase
    implements Runnable, HasFeatures {

  public boolean DEBUG = false;

  /**
   * Attribute index - indicates whether first line of transaction is correctly
   * parsed
   */
  protected boolean firstLineOk = false;

  /**
   * Attribute index - use to notify thread pool when this transaction is done
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
   *  Attribute index - factory to generate  content objects
   */
  // subclasses probably want to use different factories
  public static ContentFactory cf = new ContentFactory();

  /**
  *  class variable-- factory to generate headers
  */
  public static HeaderFactory hf = new HeaderFactory();

  /** Class variable-- resolver
   * transactions need to communicate with the resolver
   *  PIA main should set this
   */
  public static Resolver  resolver;
  
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

  /************************************************************************
  ** Request Transaction:
  ************************************************************************/

  /**
   * Attribute index - store a request transaction
   */
  protected Transaction requestTran;

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
   */
  public String contentType(){
    String res = null;
    if( headers() != null )
      res =  headers().contentType();
    return res;
  }

  /**
   * Returns the value of a header field, or null if not known.
   * @returns the value of a header field, or null if not known. 
   */
  public String header(String name){
    String res = null;
    if( headers() != null )
      res = headers().header( name );
    return res;
  }

  /** 
   * Tests to see whether a given header exists.
   */
  public boolean hasHeader(String name) {
    return header(name) != null;
  }

  /**
   * Returns all header information as string.
   * @returns all header information as string.
   */
   public String headersAsString(){
     String res = null;
     if( headers() != null )
       res = headers().toString();
     return res;
   }

 /**
   * the initial protocol string -- everything before the header.
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
   * Returns url string
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


  /************************************************************************
  ** Response Transaction:
  ************************************************************************/

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
    return (isRequest())? this : requestTran;
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
   * return parameters associated with a request in table form
   */
  public Table getParameters(){
    return null;
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
  public List controls(){
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

  /************************************************************************
  ** Features:
  ************************************************************************/

  /**
   * return the Features object.
   */
  public Features features (){
    return features;
  }

  /**
   * Get the value of the named feature.  If does not exist,
   * compute it and return the value
   */
  public Object getFeature( String name ) {
    name = Features.cannonicalName(name);
    return features.feature( name, this );
  }

  /**
   * Get the value of the named feature as a string.  If does not exist,
   * compute it and return the value.
   */
  public String getFeatureString( String name ) {
    name = Features.cannonicalName(name);
    Object f = features.feature( name, this );
    return (f == null)? null : (f == Features.Nil)? "" : f.toString();
  }

  /**
   * Test a named feature and return a boolean.
   */
  public boolean test( String name ) {
    name = Features.cannonicalName(name);
    return features.test(name, this);
  }

  /**
   * Compute and assert the value of the given feature.
   * Can be used to recompute features after changes
   */
  public Object compute( String name ){
    name = Features.cannonicalName(name);
    return features.compute(name, this);
  }

  /**
   * assert a given feature with the given value -- default to Boolean true
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

  /**
   * Test to see if the transaction has this feature
   * @param name feature name
   * @return true if it does
   */
  public boolean has( String name ) {
    name = Features.cannonicalName(name);
    return features.has(name);
  }

  /**
   * Compute a feature by using the Transaction feature Registry.
   * @param featureName the name of the feature.
   */
  public Object computeFeature( String featureName ) {
    TFComputer c = Registry.calculatorFor( featureName );
    if (c == null) {
      Pia.verbose("No feature calculator for "+featureName);
    }
    return (c == null)? Features.Nil : c.computeFeature(this);
  }

  /**
   * Test whether a criteria matches one of those of the features
   * @param criteria a Criteria list.
   * @return true if there is a match
   */
  public boolean matches(Criteria criteria){
    if (criteria == null) return false;
    return criteria.match(features, this);
  } 

  /************************************************************************
  ** Attrs interface: 
  ************************************************************************/

  /** Return the number of defined. */
  public synchronized int nAttrs() {
    return 0;		// === unimplemented
  }

  /** Test whether an attribute exists. */
  public synchronized boolean hasAttr(String name) {
    return has(name) || hasHeader(name);
  }
  
  /** Retrieve an attribute by name.  Returns null if no such
   *	attribute exists. */
  public synchronized SGML attr(String name) {
    Object o = getFeature(name);
    if (o != null) return crc.sgml.Util.toSGML(o);

    String s = header(name);
    if (s != null) return new crc.sgml.Text(name);

    return null;
  }

  /** Enumerate the defined attributes. */
  public java.util.Enumeration attrs() {
    return null;		// === unimplemented
  }

  /** Set an attribute. */
  public synchronized void attr(String name, SGML value) {
    features.assert(name, value);
    if (Character.isUpperCase(name.charAt(0))) 
      setHeader(name, value.toString());
  }
  
  /************************************************************************
  ** Content:
  ************************************************************************/

  /**
   * Accessing function to content object
   * 
   */
  public Content contentObj(){
    return contentObj;
  }

 // code from jigsaw
  /**
   * Get the standard HTTP reason phrase for the given status code.
   * @param status The given status code.
   * @return A String giving the standard reason phrase, or
   * <strong>null</strong> if the status doesn't match any knowned error.
   */

    public String standardReason(int status) {
	int category = status / 100;
	int catcode  = status % 100;
	switch(category) {
	  case 1:
	      if ((catcode >= 0) && (catcode < HTTP.msg_100.length))
		  return HTTP.msg_100[catcode];
	      break;
	  case 2:
	      if ((catcode >= 0) && (catcode < HTTP.msg_200.length))
		  return HTTP.msg_200[catcode];
	      break;
	  case 3:
	      if ((catcode >= 0) && (catcode < HTTP.msg_300.length))
		  return HTTP.msg_300[catcode];
	      break;
	  case 4:
	      if ((catcode >= 0) && (catcode < HTTP.msg_400.length))
		  return HTTP.msg_400[catcode];
	      break;
	  case 5:
	      if ((catcode >= 0) && (catcode < HTTP.msg_500.length))
		  return HTTP.msg_500[catcode];
	      break;
	}
	return null;
    }


  /**
   * Create header from fromMachine
   */
  protected void initializeHeader() throws PiaRuntimeException{
    //content source set in fromMachine method
    InputStream in;
    String line;
    String firstLine;

    try{
      in = fromMachine().inputStream();

      DataInputStream input = new DataInputStream( in );
      firstLine = input.readLine();
    
      Pia.debug(this, "the firstline-->" + firstLine);
      parseInitializationString( firstLine );
   
      headersObj  = hf.createHeader( in );

      if( headersObj != null ) return;

      // we have bad header and bad first line
      if( headersObj == null && !firstLineOk ){
	 Pia.debug(this, "Can not parse header...");
	 String msg = "Can not parse header...\n";
	 throw new PiaRuntimeException (this
					, "initializeHeader"
					, msg) ;
      }
      // someone just give us the first line
      headersObj = new Headers();


    }catch(IOException e){
    }
  }

  /**
   * Create source object from fromMachine
   */
  protected void initializeContent()throws PiaRuntimeException{
    // handled by sub classes
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
    // initialize content will get call in the run method
    // if we are already running this may cause problems...
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


  /************************************************************************
  ** Handlers:
  ************************************************************************/


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
     Pia.debug(this, "defaultHandle ...");
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
   * sendResponse -- Utilities to actually respond to a transaction, 
   *	get a request, or generate (return) an error response transaction.<p>
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
   * errorResponse -- Construct and return an error response transaction.
   */
  public void errorResponse(int code, String msg){
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

    Pia.debug(this, "Satisfaction ?");
    
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
	  if( result.booleanValue() == true )
	    satisfied = true;
	}
      }
    }
    
    //If still not satisfied, do the default thing:
    //  send a response or get a request.

    if(!satisfied){
      Pia.debug(this, "Got no satisfaction...");
      defaultHandle( resolver );
    }

  }

  /**
   * controls -- Add controls (buttons,icons,etc.) for agents to this response
   */
  public void addControl( Object aThing ){
  }


  /************************************************************************
  ** Runnable interface:
  ************************************************************************/

 // constructor methods should wait until  run method is called to do any
 // initialization that requires IO

/** run - process the transaction
 * <pre>
 * THIS should be called  just after a transaction has been created but,
 * created before resolution begins.(Resolution should wait until feature values are available).
 * This thread should live until the transaction has been satisfied.
 * each transaction goes through two logical steps.
 * first the content comes in from the FROM machine 
 * then content goes out to the TO machine.
 * for reasons of efficiency,  and interactions with the resolver,
 * the actual processing is not so clean.
 * </pre>
 */
  public void run()
  {

    try{
      // make sure we have the header information
      if(headersObj ==  null) initializeHeader();
    
      Pia.debug(this, "Got a head...");
      // and the content
      if(contentObj ==  null) initializeContent();
    }catch (PiaRuntimeException e){
      errorResponse(500, "Server internal error");
      Thread.currentThread().stop();      
      notifyThreadPool();
    }

    Pia.debug(this, "Got a body...");
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

      Pia.debug(this, "Waiting to be resolved");

      long delay = 1000;

      /*
      try{
	Thread.currentThread().sleep(delay);
      }catch(InterruptedException ex){;}
      */


      if(!contentObj.processInput()) {
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

  protected void notifyThreadPool(){
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


  /************************************************************************
  ** Construction:
  ************************************************************************/

  protected Transaction() {
    features = new Features();
    handlers = new Queue();
  }
} 
