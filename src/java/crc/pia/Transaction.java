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
   * Attribute index - content obj of this transaction.
   *
   */
  protected Content contentObj;

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
  public int getContentLength(){} 

  /**
   *  @returns the content type for this request, or null if not known. 
   *
   */
  public String getContentType(){}

  /**
   *  @returns the value of the nth header field, or null if there are fewer than n fields. 
   *
   */
  public String getHeader(int pos){}

  /**
   * @returns the value of a header field, or null if not known. 
   *
   */
  public String getHeader(String name){}

  /**
   * @returns the name of the nth header field, or null if there are fewer than n fields.  
   *
   */
  public String getHeaderName(int pos){}

  /**
   * @returns all header information as string. Machine.java uses this.
   * 
   public String getHeaderAsString(){}

  /**
   * @returns the value of an integer header field. 
   * @parameters:
   * name - the header field name 
   * default - the value to return if the field is unset or invalid.
   */
  public int getIntHeader(String name, int def) {}

  /**
   * @returns the request method. 
   */
  public String getMethod() {}

  /**
   *   @returns the protocol of the request. 
   *
   */
  public String getProtocol() {}
  
  /**
   *     @returns the query string, or null if none. 
   *
   */
  public String getQueryString(){} 
  
  /**
   *   @returns the IP address of the remote agent, or null if not known. 
   *
   */
  public String getRemoteAddr() {}
  
  /**
   *   @returns the host name of the remote agent, or null if not known. 
   *
   */
  public String getRemoteHost() {}
  
  /**
   * @returns the user name for this request, or null if not known. 
   *
   */
  public String getRemoteUser() {}

  /**
   *   @returns the full request URI. 
   *
   */    
  public URL getRequestURL(){
      URL url = contentObj.getRequestURL();
      return url;
  }

  /**
   *      @returns the host on which this request was received. 
   *
   */
  public String getServerName() {}

  /**
   *    @returns the port on which this request was received. 
   *
   */
  public int getServerPort() {}
  
  /**
   * Set header info.
   *
   */
  public void setRequestProperty(String key, String value){}


  /**
   * Interfaces related to Response.
   */


  /**
   *   @returns the status code for this response. 
   *
   */
  public int getStatusCode() {}
  
  /**
   *   Writes an error response using the specified status code. 
   * @Parameters: 
   *       sc - the status code 
   *  Throws: IOException 
   *       If an I/O error has occurred.
   */
  public void sendError(int sc) throws IOException {}
  
  /**
   *   Writes an error response using the specified status code and detail message. 
   *   @Parameters: 
   *       sc - the status code 
   *       msg - the detail message 
   *  Throws: IOException 
   *       If an I/O error has occurred. 
   */
  public void sendError(int sc, String msg) throws IOException{} 
 
  /**
   *     Sends a redirect response to the client using the specified redirect location. 
   * @Parameters:
   *  location - the redirect location URL 
   *  Throws: IOException 
   *       If an I/O error has occurred. 
   */
  public void sendRedirect(String location) throws IOException{}

  /**
   *   Sets the content length for this response. 
   *
   */
  public void setContentLength(int len) {}
  
  /**
   *   Sets the content type for this response. 
   *
   */
  public void setContentType(String type) {}
  
  /**
   *    Sets the value of a date header field. 
   *Parameters: 
   *       name - the header field name 
   *       value - the header field value 
   */
  public void setDateHeader(String name, long value){} 

  /**
   * Sets the value of a header field.
   * Parameters: 
   *       name - the header field name 
   *       value - the header field value 
   */
  public void setHeader(String name, String value) {}

  /**
   * Sets the value of an integer header field. 
   *Parameters: 
   * name - the header field name 
   *       value - the header field value 
   *
   */
  public void setIntHeader(String name, int value) {}

  /**
   * Sets the status code for this response with a default status message. 
   * Parameters: 
   *       sc - the status code 
   */
  public void setStatus(int sc) {}

  /**
   * Sets the status code and message for this response. 
   *
   */
  public void setStatus(int sc, String msg){}
     
  /**
   * @returns a default status message for the specified status code. 
   *
   */
  protected String statusMsg(int sc) {}

  /**
   * @returns status message. Use in Machine.java
   *
   */
  public String getStatusMsg() {}
     
  /**
   * Unsets the value of a header field. 
   *Parameters: 
   *name - the header field name 
   *
   */
  public void unsetHeader(String name){} 
     

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
  }

  /**
   * true if this transaction is a request
   */
  public boolean isRequest(){
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

  public Features getFeatures (){
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
     c = (UnaryFunctor)Registry.getCalculatorFor( featureName );
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
   * temporary treatment of content objects
   * while transitioning to full objectIvity...
   * Append previous transaction's content to this trasaction content
   */
  protected void initializeContent( ContentFactory cf ){
    //content source set in fromMachine method

    contentObj = cf.createContent( fromMachine().getInputStream() );
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
      contentObj.source( machine.getInputStream() );
      machine.sLength( contentObj.getContentLength() );
    }
  }

  /**
   * toMachine get toMachine-- the machine that is the target of the request.
   * 
   */
  public Machine toMachine(){
    String host;
    URL url = getRequestURL();
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
    Content responseContent;


    if(!destination)
      return errorResponse();
    responseContent = destination.getRequest( this, resolver );
    return new Transaction(this, responseContent);
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
    response.setContent("Agency could not find " + getURI() + ".\n");
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
   * title -- Return the title of an HTML page, if it has one.
   * @return the URL if the content-type is not HTML.
   * This should be a feature.
   */
  public String title(){
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
  public String[] getControls(){
    int size = controls.size();
    String[] c = new String[ size ];
    for(int i = 0; i < size; i++ ) 
      c[i] = controls[i];
    return c;
  }

  /**
   * A request transaction 
   * 
   */
  public Transaction( Machine from ){
    handlers = new Queue();
    ContentFactory cf = new ContentFactory();
    
    initializeContent( cf );
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
















