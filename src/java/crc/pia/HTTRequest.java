//  HTTPRequest.java

// (c) COPYRIGHT Ricoh California Research Center, 1997.

/** 
 * implement transaction for HTTP request
 */


package crc.pia;

import java.net.URL;
import java.io.IOException;
import crc.pia.Machine;
import crc.pia.Content;
import crc.pia.Transaction;

import crc.tf.Registry;

public class  HTTPRequest extends Transaction {
  

 

/**  method
 * should be get, post, put, head, etc.
 */
  public String method;
  
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
   * @returns HTTP method url
   */
  public String protocolInitializationString{
   //subclass should implement
    return "HTTP "+ method  +" "+ url;// where is url stored?
 }	

/** 
 * parse the first line
 * parse the request line to get method, url, http's major and minor version numbers
   */
  protected void parseInitializationString()throws IOException{

    StringTokenizer tokens = new StringTokenizer(firstLine, " ");

    String zmethod = tokens.nextToken();
    if( !zmethod ) 
      throw new RuntimeException("Bad request, no method.");

    assert("Method", zmethod );

    String zurlandmore = tokens.nextToken();
    if( !zurlandmore )
      throw new RuntimeException("Bad request, no url.");
    
    if( zmethod == "GET" ){
      int pos;
      if( (pos = zurlandmore.indexOf("?")) == -1 )
	assert("Url", zurlandmore);
      else{
	String zurl = zurlandmore.substring(0, pos);
	assert("Url", zurl);
	assert("Querystring", zurl.substring(pos+1);
      }
    }
      
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

    if(!toMachine ){
      urlString = url.getFile();
      if( urlString )
	host = url.getHost();
      if( host )
	toMachine = new Machine( host );
    }
    return toMachine;
  }


/** 
 * defaultHandle --   do a proxy for request
 */
 public boolean defaultHandle( Resolver resolver ){
    return handleRequest(  resolver);
      
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
   * A request transaction 
   *  constructor
   */
  public Transaction( Machine from ){
    handlers = new Queue();

// we probably only need one instance of these objects
    
    fromMachine( from );
    toMachine( null );// done by default anyway

  }
 /**
   * A Request transaction
   */
  public Transaction( Machine from, Content ct ){
    handlers = new Queue();

    contentObj = ct;
    headerObj = ct.header(); //  maybe generate?
    fromMachine( from );
    toMachine( null );

  }
  
}

