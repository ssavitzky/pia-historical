// Transaction.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.


/**
 * Transactions  abstract the HTTP classes Request and Response.
 * They are used in the rule-based resolver, which associates 
 * transactions with the interested agents.
 * The actual contents are stored in a content instance variable which contains
 * objects which implement the content interface which allows agents to operate on the contents.
 *
 * A Transaction has a queue of ``handlers'', which are called
 * (from the $transaction->satisfy() method) after all agents
 * have acted on it.  At least one must return true, otherwise
 * the transaction will ``satisfy'' itself.
 *
 * Each transaction runs in its own thread, so that blocking io operation
 * don't affect the core system.  This thread interacts with the resolver thread
 * in two ways: after the header has been created, the transaction notifies resolver
 * that the transaction is ready for resolution,  then the resolver notifies
 * the transaction to satisfy itself once it has been resolved.
 *
 */

package crc.pia;
import java.util.Enumeration;
import java.net.URL;
import java.io.IOException;
import java.lang.Runnable;


import crc.ds.Thing;
import crc.ds.Queue;
import crc.pia.Machine;
import crc.pia.Content;
import crc.tf.Registry;

public class Transaction implements Runnable{

// many of the methods should be abstract method implemented by
// subclasses
  /**
   * Attribute index - if true transaction is a response.
   *
   */
  public boolean isResponse = false ;

  /**

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
   * Attribute index - first line -- can be request or response line
   */
  // not needed see protocolInitializationString method
  protected String firstLine;

  /**
   * Attribute index - content obj of this transaction.
   *
   */
  protected Content contentObj;

/** 
 *  class variable-- factory to generate  content objects
 */
  // subclasses probably want to use different factories
  static public ContentFactory cf;

  /**
   * Attribute index - header obj of this transaction.
   */
  protected Headers headerObj;

/** 
 *  class variable-- factory to generate headers
 */

  static public HeaderFactory hf;

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
   * Attribute index - queue of handlers.
   *
   */
  protected Queue handlers;

  /**
   * Attribute index - controls to be insert into a response
   *
   */
  protected Vector controls = new Vector();


  /**
   * Attribute index - store request URL string
   */
  protected Transaction requestTran;

  /**
   * Below are interfaces related to Request transaction.  
   * 
   */

// TBD change to use header object
  /**
   *  @returns the content length for this request, or -1 if not known. 
   */
  public int contentLength(){
    int res = -1;
    Content c = contentObj();
    if( c && c.headers() != null )
      res = c.headers().contentLength();
    return res;
  } 

  /**
   *  @returns the content type for this request, or null if not known. 
   *
   */
  public String contentType(){
    String res = null;
    Content c = contentObj();
    if( c && c.headers() != null )
      res =  c.headers().contentType();
    return res;
  }

  /**
   * @returns the value of a header field, or null if not known. 
   *
   */
  public String header(String name){
    String res = null;
    Content c = contentObj();
    if( c && c.headers() != null )
      res = c.headers().header( name );
    return res;
  }

  /**
   * @returns all header information as string. Machine.java uses this.
   *
   */
   public String headerAsString(){
     String res = null;
     Content c = contentObj();
     if( c && c.headers() != null )
       res = c.headers().toString();
     return res;
   }


  /**
   * @returns the initial protocol string (e.g. everything before the header)
   */
  public String protocolInitializationString{
   //subclass should implement
   return  null;
 }	


  /**
   *   @returns the host name of the remote agent, or null if not known. 
   *
   */
  public String remoteHost() {
    String res = null;

    Content c = contentObj();
    if( c && c.headers() != null )
      res = c.headers().getValue( "Host" );
    return res;
  }
  
  /**
   *   @returns the full request URI. 
   *
   */    
  public URL requestURL(){
 
// should not this return the request url that we are respond to?
   if(isResponse())
      return null;

    URL myUrl = null;
    Object result = is("Url");
    if( result )
      myUrl = (URL) result;

    return myUrl;   
  }

  /**
   * Set header info.
   *
   */
  public void setHeader(String key, String value){
    Content c = contentObj();
    if( c && c.headers() != null )
      res = c.headers().setValue(key, value);
   }


  /**
   * Interfaces related to Response.
   */


  /**
   *   Sets the content length for this response. 
   *
   */
  public void setContentLength(int len){
    Content c = contentObj();
    if( c && c.headers() != null )
      res = c.headers().setContentLength( len );
  }
  
  /**
   *   Sets the content type for this response. 
   *
   */
  public void setContentType(String type){
    Content c = contentObj();
    if( c && c.headers() != null )
      res = c.headers().setContentType( type ); 
  }

 

  /**
   * @return requested transaction
   */
  public Transaction requestTran(){
    if( requestTran )
      return requestTran;
    else
      return null;
  }

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
    return  false;
  }


  /**
   * queue -- returns handler list
   * 
   */ 
  protected Enumeration queue(){
    return handlers.queue;
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
    // return from Thing's _features
    return features;
  }

  /**
   * set Features object
   */
  public void setFeatures(Features features){
    if( features )
      this.features = features;
  }

  /**
   * Get the value of the named feature.  If does not exist,
   * compute it and return the value
   */
  public Object is( String name ) {
    return features.test(name, this);
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
   * assert a given feature with the given value
   */
  public void assert( String name, Object value ) {
    features.assert($feature, $value);
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
     return c.execute();
    }catch(UnknownNameException e){
      // log here
    }
    
  }

  /**
   * Accessing function to content object
   * 
   */
  public Content contentObj(){
    return contentObj;
  }


  /**
   * temporary treatment of content objects
   * while transitioning to full objectIvity...
   */
  protected void initializeContent( Content ct ){
    //content source set in fromMachine method
    contentObj = ct;
  }
 

  /**
   *   read first line and create headers and content objects
   */
  protected void initialize(HeaderFactory hf, ContentFactory cf ){
    //content source set in fromMachine method
    InputStream in;

    try{
      in = fromMachine().inputStream();

      DataInputStream input = new DataInputStream( in );
      firstLine = input.readLine();
      parseInitializationString( firstLine);
      
      
      headerObj  = hf.createHeader( in );
   
      contentObj = cf.createContent( in );
    }catch(IOException e){
    }
  }


/** 
 * parse the first line
   */
  protected void parseInitializationString()throws IOException{
    // subclass should implement
  }
  
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
    if( machine ){
      fromMachine = machine;
      contentObj.source( machine.inputStream() );
      machine.sLength( contentObj.contentLength() );
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
    if( machine ){
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
 public boolean defaultHandle( Resolver resolver ){
   // subclass should implement
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

    Enumeration e = handlers.queue();
    while( e.hasMoreElements() ){
      obj = e.nextElement();
      if( obj instanceof Thing  ){
	Thing agentortran = (Thing)obj;
	if ( agentortran.handle(this, resolver) == true )
	  satisfied = true;
      }
      else{
	if ( obj instanceof boolean  ){
	  boolean result = (boolean) obj;
	  if( result )
	    satisfied = true;
	}
      }
    }

    //If still not satisfied, do the default thing:
    //  send a response or get a request.
    if(!satisfied){
      defaultHandle( resolver ) ;
      
    }

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
    if(headerObj ==  null) initializeHeader();
    
    // and the content
    if(contentObj ==  null) initializeContent();

    // assert precomputed features
    assertFeatures();
  
    // now we are ready to be resolved
    resolver.push(this);
  
  
    // loop until we get resolution, filling content object
    // (up to some memory limit)
    while(!resolved){
      //contentobject returns false when object is complete
      if(!contentObj.processInput(fromMachine)) wait(); 
    }
    
    // resolved, so now satisfy self
    satisfy( resolver);
    
    // cleanup?
  
  }

  


} 
















