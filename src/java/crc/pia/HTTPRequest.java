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
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.StringReader;
import java.util.Properties;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;

import crc.pia.Machine;
import crc.pia.agent.AgentMachine;
import crc.pia.Content;
import crc.pia.Transaction;
import crc.pia.HTTPResponse;

import crc.ds.Queue;
import crc.ds.Features;
import crc.ds.Table;
import crc.util.Utilities;
import crc.tf.Registry;


public class  HTTPRequest extends Transaction {
  public boolean DEBUG;

  /**  method
   * should be get, post, put, head, etc.
   */
  protected String httpMethod;

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
    return httpMethod;
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
    this.httpMethod = method;
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

	Pia mypia = Pia.instance();
	if( mypia != null)
	  u = new URL( mypia.url() + "/" );
	else
	  u = new URL("file" + "://" + "localhost" + "/");

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
   * return parameters associated with a request in a table
   * urldecoded.
   */
  public Table getParameters(){
    Table zTable = null;

    Content c = contentObj();
    if( c!= null ){
      FormContent fc;

      if( c instanceof FormContent ){
	fc = (FormContent) c;
	zTable = fc.getParameters();
	return zTable;
      }
      else return null;
    }
    else
      return null;
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
    
    Pia.debug(this, "inside initialize content");

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
	Pia.debug(this, "Unknown header type...");
	String msg = "Unknown header type...\n";
	throw new PiaRuntimeException (this
				       , "initializeContent"
				       , msg) ;
      }

      Pia.debug(this, "before set param");
      setParam();
      Pia.debug(this, "after set param");
    }catch(IOException e){
      Pia.debug(this,  e.toString() );
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

    Machine m = fromMachine();
    if( m != null )
      proxy = m.proxy( protocol() );

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

    Pia.debug(this, new String( buf ) );
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
     PrintWriter out = new PrintWriter( new OutputStreamWriter(stream) );

    String requestLine = protocolInitializationString();
     if( requestLine != null )
       out.print( requestLine );

     String headersString= headersAsString();
     if( headersString != null )
       out.print( headersString );

     if( method().equalsIgnoreCase("POST")  ){
       String qs = queryString(); 
       if( qs != null ){
	 Pia.debug(this, "the content is ..." + qs);
	 out.println( qs );
       }
     }
     if( method().equalsIgnoreCase("PUT")  ){
       if( contentObj() != null ){
	 Pia.debug(this, "outputting the content");
         out.flush();
	 
         try{contentObj().writeTo(stream);
	 }
	 catch( Exception e){
	   Pia.debug(this," failed writing content");
	 }
       }
     }
     out.flush();
  }


  /**
   * Create header from fromMachine
   */
  protected void initializeHeader() throws PiaRuntimeException, IOException{
    try{
      super.initializeHeader();
      if( headersObj == null ){
	String msg = "Can not create header...\n";
	throw new PiaRuntimeException (this
				       , "initializeHeader"
				       , msg) ;
      }
    }catch(PiaRuntimeException e){
      throw e;
    }catch(IOException ioe){
      throw ioe;
    }
  }


  /**
   * parse the request line to get method, url, http's major and minor version numbers
   */
  protected void  parseInitializationString( String firstLine )throws IOException, MalformedURLException,
    PiaRuntimeException{
    if( firstLine == null ){
      String msg = "firstLine is null...\n";
      throw new PiaRuntimeException (this
				     , "parseInitializationString"
				     , msg) ;
    }
    
    StringTokenizer tokens = new StringTokenizer(firstLine, " ");

    try{
      httpMethod = tokens.nextToken();
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

      Pia mypia = Pia.instance();
      if( mypia != null)
	u = new URL( mypia.url() + "/" );
      else
	u = new URL("file" + "://" + "localhost" + "/");

      URL myurl = new URL(u, zurlandmore );
      protocol = myurl.getProtocol();
    }catch(MalformedURLException e){
      throw e;
    }
    

    if( httpMethod.equalsIgnoreCase("GET") ){
      int pos;

      if( (pos = zurlandmore.indexOf('?')) == -1 )
	url = zurlandmore;
      else{
	String zurl = zurlandmore.substring(0, pos);
	url = zurl;
	//String qs = zurlandmore.substring(pos+1);
	String qs = zurlandmore.substring(pos);

	if( qs!= null )
	  queryString = Utilities.unescape( qs );

	Pia.debug(this, "The query string is: "+qs);
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
      
      firstLineOk = true;
  }


  /**
   * set query parameters for request
   */
  //protected void setParam(){
  public void setParam(){
    String mymethod = method();
    FormContent fc = null;

    fc = (contentObj() instanceof FormContent)? (FormContent)contentObj()
                                              : new FormContent();
    if( queryString()!= null && mymethod.equalsIgnoreCase( "GET" ) ){
      String qs = queryString().substring( 1 );
      Pia.debug(this, "Before setting parameters, query string w/o ? is" + qs );
      fc.setParameters( qs );
    }else {
      if( mymethod.equalsIgnoreCase( "POST" )  )
	// sucks actual parameters from body of content
	fc.setParameters(null);
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
  
  /************************************************************************
  ** Responses:
  ************************************************************************/

  /**
   * handleRequest -- Default handling for a request:
   * ask the destination machine to get it.
   * complain if there's no destination to ask.
   */
  public void handleRequest( Resolver resolver ){
    Pia.debug(this, "Starting handle request...");

    Machine destination = toMachine();
  
    if( destination == null )
      errorResponse(500, null);
    else{
      try{
	Pia.debug(this, "Going to get request...");
	destination.getRequest( this, resolver );
      }catch(PiaRuntimeException e){
	errorResponse(e);
      }catch(IOException e){
	errorResponse(e);
      }
    }
  }
  
  /**
   * Construct and return an error response.
   */
  public void errorResponse(int code, String msg){
    msg = errorMessage(code, msg);
    StringReader inputStream = new StringReader( msg );
    Content ct = new crc.content.text.html( inputStream );
    Transaction response = new HTTPResponse( this, Pia.instance().thisMachine,
					     ct, false);    
    response.setStatus( code );
    response.setContentType( "text/html" );
    response.setContentLength( msg.length() );
    Pia.debug(this, "The header : \n" + response.headersAsString() );
    response.startThread();
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /**
   *  Default constructor: client needs to set fromMachine, toMachine, 
   *	and start the thread.
   */
  public HTTPRequest(){
    super();

    Pia.debug(this, "Constructor-- [ default ] -- on duty...");

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
    super();

    Pia.debug(this, "Constructor-- [ machine from ] -- on duty...");

    // we probably only need one instance of these objects
    
    fromMachine( from );
    startThread();
  }


  /**
   * A request transaction with default blank header and a define content.
   * @param from originator of request -- later data will be sent to this machine.
   * @param ct a define content.
   */
  public HTTPRequest( Machine from, Content ct ){
    super();

    Pia.debug(this, "Constructor-- [ machine from, content ct ] on duty...");

    contentObj = ct;
    headersObj = new Headers(); // blank header  

    if( contentObj != null )
      contentObj.setHeaders( headersObj );

    fromMachine( from );
    toMachine( null );

    startThread();
  }

 /**
   * A request transaction with default blank header and a define content.
   * @param from originator of request -- later data will be sent to this machine.
   * @param ct a define content.
   * @param start flag -- if true starts thread automatically;otherwise,
   * user must issue dostart()
   */

  public HTTPRequest( Machine from, Content ct, boolean start ){
    super();

    Pia.debug(this, "Constructor-- [ machine from, content ct ] on duty...");

    contentObj = ct;
    headersObj = new Headers(); // blank header  

    if( contentObj != null )
      contentObj.setHeaders( headersObj );

    fromMachine( from );
    toMachine( null );

    if( start )
      startThread();
  }
  
 
 
  /**
   * A request transaction with define header and content.
   * @param from originator of request -- later data will be sent to this machine.
   * @param ct a define content.
   * @param hd a define header.
   */
  public HTTPRequest( Machine from, Content ct, Headers hd ){
    super();

    Pia.debug(this, "Constructor-- [ machine from, content ct, headers hd ] -- on duty...");

    contentObj = ct;
    headersObj = hd; 

    if( contentObj != null )
      contentObj.setHeaders( headersObj );

    fromMachine( from );
    toMachine( null );

    startThread();
  }

  /************************************************************************
  ** Debugging versions:
  ************************************************************************/

  private static void sleep(int howlong){
   Thread t = Thread.currentThread();

   try{
     t.sleep( howlong );
   }catch(InterruptedException e){;}

  }

  /**
   * Here for debugging purpose -- if DEBUG flag is false call Transaction's run method()
   */
  public void run(){
    if(!DEBUG){
      try{
	// make sure we have the header information
	if(headersObj ==  null) initializeHeader();
	
	Pia.debug(this, "Got a head...");

	// and the content
	if( method().equalsIgnoreCase( "POST" ) || method().equalsIgnoreCase( "GET" )){
	  if(contentObj ==  null) initializeContent();
	  Pia.debug(this, "Got a body...");
	  // incase body needs to update header about content length
	  if( headersObj!= null && contentObj != null )
	    contentObj.setHeaders( headersObj );
	}
      }catch (PiaRuntimeException e){
	errorResponse(e );
	Thread.currentThread().stop();      
	notifyThreadPool();
      }catch( IOException e){
	errorResponse(e);
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
	
	Pia.debug(this, "Waiting to be resolved");
	
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
      Pia.debug(this, "Running HTTPRequest's run method");

      try{
	if(headersObj ==  null) initializeHeader();
	//Pia.debug(this, "Got a head...");
	System.out.println("Got a head...");

	// and the content
	if(contentObj ==  null) initializeContent();
	Pia.debug(this, "Got a body...");
	// incase body needs to update header about content length
	if( headersObj!= null && contentObj != null )
	  contentObj.setHeaders( headersObj );
      }catch (PiaRuntimeException e){
	errorResponse(e);
	Thread.currentThread().stop();      
      }catch( IOException e ){
	errorResponse(e);
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

      Pia.debug(this, "Done running");
    }
  }

  /**
   * Take machine as source of input -- this is a debugging constructor
   * @param from source of input for header and content
   * @param debugflag set DEBUG flag -- if true use local run method;
   *	thread does not start automatically.
   */
  public HTTPRequest( Machine from, boolean debugflag ){
    super();

    DEBUG = debugflag;
    Pia.debug(this, "Constructor-- [ machine from ] -- on duty...");

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

  public HTTPRequest( Machine from, Content ct, Boolean debugflag ){
    super();

    DEBUG = debugflag.booleanValue();
    Pia.debug(this, "Constructor-- [ machine from, content ct ] on duty...");

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
    super();

    DEBUG = debug;

    Pia.debug(this, "Constructor-- [ machine from, content ct, headers hd ] -- on duty...");

    contentObj = ct;
    headersObj = hd; 

    if( contentObj != null )
      contentObj.setHeaders( headersObj );

    fromMachine( from );
    toMachine( null );
  }



}








