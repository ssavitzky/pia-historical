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

import crc.ds.Thing;
import crc.ds.Queue;
import crc.pia.Machine;
import crc.pia.Content;
import crc.tf.Registry;

public class Transaction extends Thing{
  /**
   * Attribute index - if true transaction is a response.
   *
   */
  public boolean isResponse = false ;

  /**
   * Attribute index - if true transaction is a request.
   *
   */
  public boolean isRequest = false;


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
  protected String firstLine;

  /**
   * Attribute index - content obj of this transaction.
   *
   */
  protected Content contentObj;

  /**
   * Attribute index - header obj of this transaction.
   */
  protected Headers headerObj;

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
   * @returns the request method. 
   */
  public String method() {
    if(isResponse())
      return null;

    String m = null;
    Object result =  is("Method");
    if( result )
      m = (String) result;

    return m;   
  }

  /**
   *   @returns the protocol of the request. 
   *
   */
  public String protocol() {
    return "HTTP";
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
   *   @returns the status code for this response. 
   *
   */
  public int statusCode(){
    int s = -1;
    if(isRequest())
      return s;

    Object result =  is("Statuscode");
    if( result )
      s = (int) result;

    return s;
  }
  
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
	      if ((catcode >= 0) && (catcode < msg_100.length))
		  return HTTP.msg_100[catcode];
	      break;
	  case 2:
	      if ((catcode >= 0) && (catcode < msg_200.length))
		  return HTTP.msg_200[catcode];
	      break;
	  case 3:
	      if ((catcode >= 0) && (catcode < msg_300.length))
		  return HTTP.msg_300[catcode];
	      break;
	  case 4:
	      if ((catcode >= 0) && (catcode < msg_400.length))
		  return HTTP.msg_400[catcode];
	      break;
	  case 5:
	      if ((catcode >= 0) && (catcode < msg_500.length))
		  return HTTP.msg_500[catcode];
	      break;
	}
	return null;
    }


    /**
     * Set this reply status code.
     * This will also set the reply reason, to the default HTTP/1.1 reason
     * phrase.
     * @param status The status code for this reply.
     */

    public void setStatus(int status) {
	if ((statusCode() != this.status) || (reason() == null))
	    assert("Reason", standardReason(status) );
	assert("Statuscode", status );
    }

    /**
     * Get the reason phrase for this reply.
     * @return A String encoded reason phrase.
     */

    public String reason() {
      if( isRequest() )
	return null;

      String s = null;
      Object result =  is("Reason");
      if( result )
	s = (String) result;

      return result;
    }

    /**
     * Set the reason phrase of this reply.
     * @param reason The reason phrase for this reply.
     */

    public void setReason(String reason) {
	assert("Reason", reson);
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
    return isResponse;
  }

  /**
   * true if this transaction is a request
   */
  public boolean isRequest(){
    return isRequest;
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
   * parse the request line to get method, url, http's major and minor version numbers
   */
  protected void parseRequestLine(){
    if( !isRequest() ) return;
    StringTokenizer tokens = new StringTokenizer(firstLine, " ");
    String zmethod = tokens.nextToken();
    
  }

  /**
   * temporary treatment of content objects
   * while transitioning to full objectIvity...
   * Append previous transaction's content to this trasaction content
   */
  protected void initialize(HeaderFactory hf, ContentFactory cf ){
    //content source set in fromMachine method
    InputStream in;

    try{
      in = fromMachine().inputStream();

      DataInputStream input = new DataInputStream( in );
      firstLine = input.readLine();

      headerObj  = hf.createHeader( in );
      parseRequestLine();
      parseResponseLine();
      contentObj = cf.createContent( in );
    }catch(IOException e){
    }
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
    String host;
    URL url = requestURL();
    String urlString;

    if(!toMachine && is_request){
      urlString = url.getFile();
      if( urlString )
	host = url.getHost();
      if( host )
	toMachine = new Machine( host );
    }
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
    Machine machine = toMachine();
    
    if ( machine ){
      machine.sendResponse(this, resolver);
    }
    else{
      System.out.println( "dropping  response to $machine\n" );
      return;
    }
    
  }

  /**
   * handleRequest -- Default handling for a request:
   * ask the destination machine to get it.
   * complain if there's no destination to ask.
   */
  public Transaction handleRequest( Resolver resolver ){
    Machine destination = toMachine();
    Transaction response;


    if(!destination)
      return errorResponse();
    response = destination.getRequest( this, resolver );
    return response;
  }
  
  /**
   * errorResponse -- Return a "not found" error for a request with no destination.
   *
   *
   */
  protected Transaction errorResponse(){
    Transaction response = new Transaction( pia.thisMachine, fromMachine() );
    
    response.setStatus( 404, "Not found" );
    response.setContentType( "text/plain" );
    response.setContent("Agency could not find " + requestURL() + ".\n");
    return response;
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
      if( isResponse() )
	sendResponse( resolver );
      else {
	if ( isRequest() )
	  resolver.push( handleRequest( resolver ) );
      }
    }

  }

  /**
   * controls -- Add controls (buttons,icons,etc.) for agents to this response
   * actual final form determined by machine
   * NOTE: not implemented
   */
  public void addControl( Thing aThing ){
    controls.addElement( aThing );
  }

  /**
   * controls -- Add controls (buttons,icons,etc.) for agents to this response
   * actual final form determined by machine
   * NOTE: not implemented
   */
  public Thing[] controls(){
    int size = 0;

    size = controls.size();
    if( size > 0 ){
      Thing[] c = new Thing[ size ];
      for(int i = 0; i < size; i++ ) 
	c[i] = (Thing) controls[i];
      return c;
    }
    return null;
  }

  /**
   * A request transaction 
   * 
   */
  public Transaction( Machine from ){
    handlers = new Queue();
    HeaderFactory hf  = new HeaderFactory();
    ContentFactory cf = new ContentFactory();
    
    initialize( hf, cf );
    isRequest = true ;
    fromMachine( from );
    toMachine( null );
  }

  /**
   * A Request transaction
   */
  public Transaction( Machine from, Content ct ){
    handlers = new Queue();

    initializeContent( ct );
    isRequest =  true;
    fromMachine( from );
    toMachine( null );

  }

  /**
   * A Response transaction
   */
  public Transaction(Transaction t, Content ct ){
    handlers = new Queue();

    requestTran = t;
    initializeContent( ct );
    isResponse = true;
    fromMachine( t.toMachine() );
    toMachine( t.fromMachine() );
  }
} 
















