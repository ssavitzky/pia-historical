//   HTTPResponse.java

// (c) COPYRIGHT Ricoh California Research Center, 1997.

/** 
 * implement transaction for HTTP response
 */


package crc.pia;

import java.net.URL;
import java.io.IOException;
import crc.pia.Machine;
import crc.pia.Content;
import crc.pia.Transaction;

import crc.tf.Registry;

public class  HTTPResponse extends Transaction {
  

/** 
 * status code of response
 */
  public int  status;
  public  String reason;

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
   * @returns HTTP   statuscode  message
   */
  public String protocolInitializationString{
   //subclass should implement
    return "HTTP "+  status + " " +reason();// where is url stored?
 }	

/** 
 * parse the first line
 * parse the request line to get method, url, http's major and minor version numbers
   */
  protected void parseInitializationString()throws IOException{

    StringTokenizer tokens = new StringTokenizer(firstLine, " ");

    String protocol = tokens.nextToken();
    if( !zmethod ) 
      throw new RuntimeException("Bad request, no  protocol.");

    assert("Protocol", zmethod );

     status = tokens.nextToken();
    if( !zurlandmore )
      throw new RuntimeException("Bad request, no url.");
    
    while( tokens.hasMoreElements()){
      reason += tokens.nextToken();
      
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
 * defaultHandle --   do a proxy for request
 */
 public boolean defaultHandle( Resolver resolver ){
    return  sendResponse(  resolver);
      
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
    Machine machine = toMachine();
    
    if ( machine ){
      machine.sendResponse(this, resolver);
    }
    else{
      System.out.println( "dropping  response to $machine\n" );
      return;
    }
    
  }

  public String reason() {
    if(reason ==  null) reason = standardReason(status);
    
    return reason;
    
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
   *   @returns the status code for this response. 
   *
   */
  public int statusCode(){
    return  status;
  }
  

    /**
     * Set this reply status code.
     * This will also set the reply reason, to the default HTTP/1.1 reason
     * phrase.
     * @param status The status code for this reply.
     */

    public void setStatus(int s) {
	if ((statusCode() != s) || (reason() == null))
	    assert("Reason", standardReason(s) );
	assert("Statuscode", s );
	status= s;
	
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
   * controls -- Add controls (buttons,icons,etc.) for agents to this response
   * actual final form determined by machine
   * NOTE: not implemented  // maybe move to content?
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
   *  constructors
   */
  public Transaction( Machine from, Machine to ){
    handlers = new Queue();
    
    fromMachine( from );
    toMachine(  to );

  }
 /**
  * content is known
   */
  public Transaction(  Transaction t, Content ct ){
    handlers = new Queue();

    contentObj = ct;
    headerObj = ct.header(); //  maybe generate?
  
    requestTran = t;
    fromMachine( t.toMachine() );
    toMachine( t.fromMachine() );
  }
  
  
}

