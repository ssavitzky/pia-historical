// FormContent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.pia;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Vector;


import java.io.IOException;
import java.io.EOFException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.ByteArrayInputStream;


import crc.util.Utilities;

import crc.pia.Headers;
import crc.pia.HttpBuffer;


public class FormContent extends Properties implements Content{
  // conditions to notify agents

  // public static final Condition foo = new Condition-A(...);
  // public static final Condition bar = new Condition-B(...);
 
  /**
   * headers
   */
  private Headers headers;

  /**
   * body
   */
  private InputStream body;

  /**
   * store parameter keys in order
   */
  private Vector paramKeys = new Vector();

  /**
   * original query string
   */
  private String queryString;

  /**
   * buffer that stores data from processInput
   */
  private HttpBuffer zbuf;

  /**
   * number of bytes read during processInput
   */
  private int numberOfBytes = 0;

  /**
   * max size of zbuf;
   */
  private int maxBufSize = 2048;

  /**
   * total number of bytes read
   */
  private int totalRead = 0;

  /**
   * current position 
   */
  private int pos = 0;


  /**
   * read into buffer
   */
  private synchronized int pullContent() throws IOException{
    int howmany = -1;
    byte[]buffer = new byte[1024];

    //if( body.available() != 0 ){
      try{
	//howmany = body.read( buffer, numberOfBytes, 1024 );
	howmany = body.read( buffer, 0, 1024 );
	if( howmany == -1 ){
	  setContentLength( totalRead );
	  return -1;
	}
	totalRead += howmany;
	numberOfBytes += howmany;
	zbuf.append( buffer, 0, howmany );
	return howmany;
      }catch(IOException e){
	throw e;
      }
      //}
      //return howmany;
  }

  /**
   * Read data into buffer.  
   * This gets called from a transaction that points to this content.
   * When the transaction waits for itself to be resolved, it calls
   * this method.
   * @return false if there is no more data to process
   */
  public boolean processInput(){
    int len = -1;

    try{
      if( body == null || numberOfBytes >= maxBufSize ) return false;

      // buffer not full yet
      len = pullContent();

      if( len == -1 )
	return false;
      else
	return true;
    }catch(IOException e2){
      return false;
    }
    
  }

 /** 
  * Return as header associated with this content.
  * @return header associated w/ this content.
  */
  public Headers headers(){
    if( headers!= null )
      return headers;
    else
      return null;
  }
  
  /** 
   * Set a header associated with this content.
   */
  public void setHeaders( Headers headers ){
    if( headers!= null )
      this.headers = headers;
  }
  
 /** 
  * Access functions 
  * machine objects read content as a stream
  * two primary uses: acting as a source and sink for machines,
  * and allowing processing by agents " in stream "
  */


 /** 
  * set a source stream for this object
  * usually this will come from a machine
  */
  public void source(InputStream stream){
    if( stream != null )
      body = stream;
  }

  /**
   * Return input stream
   * @return input stream
   */
  public InputStream source(){
    return body;
  }

  /**
   * number of bytes available from buffer
   */
  private int available(){
    return zbuf.length() - pos;
  }

  /**
   * read from buffer
   */
  private synchronized int getFromReadBuf(byte[] buffer, int offset, int amtToRead) throws IOException{
    int limit = 0;
    int len   = -1;

    int available = available();

    if( available == 0 ){
      //buffer is dry

      zbuf.reset();
      pos           = 0;
      numberOfBytes = 0;

      try{
	len = pullContent();

	if( len == -1 )
	  return -1;
	else
	  available = available();
      }catch(IOException e2){
	throw e2;
      }

    }
    if( amtToRead > available )
      limit = available;
    else
      limit = amtToRead;
    System.arraycopy(zbuf.getBytes(), pos, buffer, offset, limit);
    pos += limit;
    return limit;
  }

 /**  
  * Get the next chunk of data as bytes and store in byte array passed in as parameter.
  * If there is data in buffer, read from stream.
  * @param buffer byte array to store read data.
  * @return number of bytes read -1 means EOF.
  */
  public int read(byte buffer[]) throws IOException{
    int len = -1;

    if( body == null ) return len;
    try {
      if( available() > 0 )
	len = getFromReadBuf( buffer, 0, buffer.length );
      else{
	//if( body.available() != 0 ){
	  len = body.read( buffer );
	  if( len != -1 )
	    totalRead += len;
	  else
	    setContentLength( totalRead );
	  //}
      }
      return len;
    }catch(IOException e){
      throw e;
    }
  }

  /**
   * set content length
   */
  private void setContentLength( int len ){
    Headers myheader = headers();
    if( myheader != null )
      myheader.setContentLength( len );
  }
 
  /**
  * Get the next chunk of data as bytes and store in byte array passed in as parameter.
  * If there is data in buffer, read from stream.
  * @param buffer place to store read data
  * @param offset position in buffer to start placing data
  * @param length number of bytes to read
  *  @return number of bytes read
  */
  public int read(byte buffer[], int offset, int length) throws IOException{
    int len = -1;

    if( body == null ) return len;

    try {
      if( available() > 0 )
	len = getFromReadBuf( buffer, offset, buffer.length );
      else{
	//if( body.available() != 0 ){
	  len = body.read( buffer, offset, length );
	  if( len != -1 )
	    totalRead += len;
	  else
	    setContentLength( totalRead );
	  //}
      }
      return len;
    }catch(IOException e){
      throw e;
    }
  }

  /** 
   * add an output stream to "tap" the data before it is written
   * any taps will get data during a read operation
   * before the data "goes out the door"
   */
  public void addTap(InputStream tap){}
  
  /**  
   * specify an agent to be notified when a condition is satisfy  
   * for example the object is complete
   */
  public void notifyWhen(Agent interested, Object condition){
    // Need a hash of (condition, vector of agents)
    // Given a condition, get the corresponding vector
    // if no the interested agent is not previously registered
    // register it
  }

  public FormContent(){
      zbuf = new HttpBuffer();
  }

  public FormContent(InputStream in){
    zbuf = new HttpBuffer();
    source( in );
  }


  /**
   * Return a copy of this content's data as bytes.
   * @return a copy of this content's data as bytes.
   */
  public byte[] toBytes(){
    byte[]buffer = new byte[1024];
    int bytesRead;
    HttpBuffer data = new HttpBuffer(); 

    try{
      while(true){
	bytesRead = read( buffer, 0, 1024 );
	if(bytesRead == -1) break;
	data.append( buffer, 0, bytesRead );
      }
      
      return data.getByteCopy();
      
    }catch(IOException e){
    }
    return null;
  }


  /**
   * Return this content's data as a string.
   * @return this content's data as a string.
   */

  public String toString(){
    byte[]buffer = new byte[1024];
    int bytesRead;
    HttpBuffer data = new HttpBuffer();

    Pia.instance().debug(this, "toString is processing...");
    try{
      while(true){
	bytesRead = read( buffer, 0, 1024 );
	if(bytesRead == -1) break;
	data.append( buffer, 0, bytesRead );
      }

      Pia.instance().debug(this, "data length is: " + Integer.toString( data.length() ) );
      if( data.length() == 0 ){
	return null;
      }
      String zdata = null;
      zdata = data.toString();
      return zdata;
      
    }catch(IOException e){
    }
    return null;
  }

  /**
   * Split query string into key-value pairs and store them into
   * property list.
   */
  public void setParameters(String toSplit){
    StringTokenizer tokens = null;
    String token = null;
    String[] pairs = null;
    int count;
    int i = 0;
    int pos;

    Pia.instance().debug(this, "processing...");
    if( toSplit != null ){
      tokens = new StringTokenizer(toSplit,"&");
      queryString = toSplit;
    }
    else{
      String zcontent = toString();
      Pia.instance().debug(this, "the content is :"+zcontent);
      if( zcontent == null ) return;

      queryString  = zcontent;
      tokens = new StringTokenizer(zcontent,"&");

    }
    if(tokens==null) return;

    count = tokens.countTokens();
    if( count > 0 )
      pairs = new String[ count ];
    else return;

    while ( tokens.hasMoreElements() ){
      token = tokens.nextToken();
      pairs[i++] = token;
    }
    i = 0;
    for(; i < pairs.length; i++){
      String s = pairs[i];
      pos = s.indexOf('=');
      String p = s.substring(0, pos);
      String pt = p.trim();
      paramKeys.addElement( pt );
      String param = Utilities.unescape( pt );

      String v = s.substring( pos+1 );
      String vt = v.trim();
      paramKeys.addElement( vt );
      String value = Utilities.unescape( vt );
      put(param, value);
    }
  }

  /**
   * Return parameter key words in the original order.
   * @return parameter key words in the original order
   */
  public String[]paramKeys(){
    int size =0;
    if ( (size=paramKeys.size()) > 0){
      String[] p = new String[size];
      for(int i=0; i < size; i++)
	p[i] = (String)paramKeys.elementAt(i);
      return p;
    }else
      return null;
  }

  /**
   * Return original query string
   * @return original query string
   */
  public String queryString(){
    if( queryString != null )
      return queryString;
    else {
      //last shot
      setParameters( null );
      return queryString;
    }
  }

  /**
   * for testing only
   */
  protected OutputStream printParametersOn(OutputStream out){
    PrintStream ps = new PrintStream( out );
    Enumeration e = propertyNames();
    while( e.hasMoreElements() ){
      try{
	String key = (String)e.nextElement();
	ps.print( key + "-->" );
	ps.println( getProperty( key ) ); 
      }catch(Exception ex){ return out;}
    }
    return out;
  }

  private static void printusage(){
    System.out.println("Needs to know what kind of test");
    System.out.println("For test 1, here is the command --> java crc.pia.HTTPRequest -1 post.txt");
    System.out.println("For test 2, here is the command --> java crc.pia.HTTPRequest -2 post.txt");
  }


 public static void main(String[] args){

    if( args.length == 0 ){
      printusage();
      System.exit( 1 );
    }

    if (args.length == 2 ){
      if( args[0].equals ("-1") && args[1] != null )
	test1( args[1] );
      else if( args[0].equals ("-2") && args[1] != null )
	test2( args[1] );
      else{
	printusage();
	System.exit( 1 );
      }
    }

  }


  /**
  * For testing.
  * 
  */ 
  private static void test1(String filename){
    try{
      InputStream in = (new BufferedInputStream
			(new FileInputStream (filename)));
    
      FormContent c = new FormContent( in );
      c.setParameters( null );
      c.printParametersOn( System.out );
    }catch(Exception e ){
      System.out.println( e.toString() );
    }
  }
 
  /**
  * For testing.
  * 
  */ 
  private static void test2(String filename){
    System.out.println( "in test2" );
    try{
      InputStream in = (new BufferedInputStream
			(new FileInputStream (filename)));
    
      FormContent c = new FormContent( in );
      boolean done = false;
      while( !done ){
	if( !c.processInput() )
	  done = true;
      }

      c.setParameters( null );
      c.printParametersOn( System.out );
    }catch(Exception e ){
      System.out.println( e.toString() );
    }
  }
 
  


}








