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
import crc.ds.Table;
import crc.ds.List;

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


    int hlen = headers.contentLength();
    if( hlen == totalRead ) return -1;

    int len = hlen - totalRead;
    Pia.debug( this, "content len..." + Integer.toString( len ) );

    if( len > 0 ){
      byte[]buffer = new byte[ len ];
      try{

	howmany = body.read( buffer, 0, len );
	Pia.debug( this, "amt read..." + Integer.toString( len ) );

	totalRead += howmany;
	numberOfBytes += howmany;
	zbuf.append( buffer, 0, howmany );

	return howmany;
      }catch(IOException e){
	throw e;
      }
    }
    return howmany;
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
  

  /************************************************************
  ** Content producers:
  ************************************************************/

  /**
   *  generic source throws exception
   */
  public void source(Object o)  throws ContentOperationUnavailable {
    InputStream s;
    if ( o instanceof InputStream ){
      s = ( InputStream ) o;
      source( s );
    }else
    throw(new ContentOperationUnavailable(" cannot handle type as source"));
  }

 /** 
  * set a source stream for this object
  * usually this will come from a machine
  */
  public void source(InputStream stream){
    if( stream != null )
      body = stream;
  }


  /**
   * create form request from a string
   */
  public void source(String s){
    if( s != null ){
      setParameters(s);
    }
  }

  /**
   * If the content object exists as a data structure in memory, then
   * it is persistent. Otherwise, it is not persistent (streams are
   * not persistent).
   */

 public boolean isPersistent() {
   //  for get and post requests we create data structures
   return true;
  
 }

 /** 
  * Access functions 
  * machine objects read content as a stream
  * two primary uses: acting as a source and sink for machines,
  * and allowing processing by agents " in stream "
  */


  /**
   * send all of data out the output stream
   */

  public int writeTo(OutputStream output)  throws ContentOperationUnavailable, IOException{
     int written = 0;
     // should use existing buffers -- temporary hack until rewrite
      byte[] myBuffer = new byte[4096];
      int j=0;
      while (j >= 0){
	 j = read(myBuffer);
         output.write(myBuffer,0,j);
	 written += j;
      }
      
      return written;
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
	if( headers != null ){
	  int hlen = headers.contentLength();
	  if( hlen == totalRead ) return -1;
	}

	len = body.read( buffer );
	if( len != -1 )
	  incReadCount( len );
      }
      return len;
    }catch(IOException e){
      throw e;
    }
  }

  private synchronized void incReadCount( int c ){
    totalRead += c;
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
	if( headers != null ){
	  int hlen = headers.contentLength();
	  if( hlen == totalRead ) return -1;
	}

	len = body.read( buffer, offset, length );
	if( len != -1 )
	  incReadCount( len );
      }
      return len;
    }catch(IOException e){
      throw e;
    }
  }


  /************************************************************
  ** agent interactions:
  ************************************************************/
  /** 
   * add an output stream to "tap" the data before it is written
   * any taps will get data during a read operation
   * before the data "goes out the door"
   */
  public void tapIn(OutputStream tap) throws ContentOperationUnavailable{
    throw( new ContentOperationUnavailable("Tapping not implemented for FormContent"));
  }
  public void tapOut(OutputStream tap) throws ContentOperationUnavailable{
    throw( new ContentOperationUnavailable("Tapping not implemented for FormContent"));
  }
  
  /**  
   * specify an agent to be notified when a condition is satisfy  
   * for example the object is complete
   */
  public void notifyWhen(Agent interested, String state, Object condition) throws ContentOperationUnavailable{
    // Need a hash of (condition, vector of agents)
    // Given a condition, get the corresponding vector
    // if no the interested agent is not previously registered
    // register it
    throw( new ContentOperationUnavailable("Notification not implemented for FormContent"));
  }

public String[] states()
  {
    return null; //not  implemented
  }
  


  /************************************************************
  ** Constructors:
  ************************************************************/
  public FormContent(){
      zbuf = new HttpBuffer();
  }

  public FormContent(InputStream in){
    zbuf = new HttpBuffer();
    source( in );
  }

  public FormContent(String s){
    Pia.debug( this, "string constructor..." + s );
    source(s);
  }


  /**
   * Return a copy of this content's data as bytes.
   * @return a copy of this content's data as bytes.
   */
  public byte[] toBytes(){

    int bytesRead;
    HttpBuffer data = new HttpBuffer(); 

    if ( headers == null ){
      byte[] bytes = suckFromSource();
      return bytes;
    }

    int len = headers.contentLength();

    if( len <=0 ) return null;

    byte[]buffer = new byte[ len ];

    try{
      for(; len > 0;){
	bytesRead = read( buffer, 0, len );
	if(bytesRead == -1) break;

	len -= bytesRead;

	data.append( buffer, 0, bytesRead );
      }
      
      return data.getByteCopy();
      
    }catch(IOException e){
    }
    return null;
  }

  private byte[] suckFromSource(){
    int bytesRead;
    int len = 1024;
    HttpBuffer data = new HttpBuffer();

    Pia.debug(this, "sucking from source is processing...");

    try{

      byte[]buffer = new byte[ len ];
      for(;;){
	bytesRead = read( buffer, 0, len );

	if(bytesRead == -1) break;

	Pia.debug(this, "amt read is: " +  Integer.toString( bytesRead ) );
	Pia.debug(this, new String(buffer,0,0,bytesRead));


	data.append( buffer, 0, bytesRead );
      }

      Pia.debug(this, "data length is: " + Integer.toString( data.length() ) );
      if( data.length() == 0 ){
	return null;
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
    int bytesRead;
    HttpBuffer data = new HttpBuffer();

    if ( headers == null ){
      byte[] bytes = suckFromSource();
      if (bytes == null) return null;
      String zdata = new String ( bytes,0,0, bytes.length);
      return zdata; // === should really be setting the headers! ===
    }
      
    int hlen = headers.contentLength();

    int len = hlen;
    if( len <=0 ) return null;

    Pia.debug(this, "toString is processing...");
    Pia.debug(this, "content length is =" + Integer.toString( len ));

    try{

      byte[]buffer = new byte[ len ];
      for(; len > 0;){
	bytesRead = read( buffer, 0, len );

	if(bytesRead == -1) break;

	len -= bytesRead;

	Pia.debug(this, "amt read is: " +  Integer.toString( bytesRead ) );
	Pia.debug(this, new String(buffer,0,0,buffer.length));

	data.append( buffer, 0, bytesRead );
      }

      Pia.debug(this, "data length is: " + Integer.toString( data.length() ) );
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

    if( toSplit != null ){
      Pia.debug(this, "the toSplit string is-->"+ toSplit);
      queryString = toSplit;
    } else{
      String zcontent = toString();

      Pia.debug(this, "the content is below:");
      Pia.debug(this, zcontent);
      if( zcontent == null ) return;

      queryString  = zcontent;
    }
    tokens = new StringTokenizer(queryString, "&");

    while ( tokens.hasMoreElements() ){
      String s = tokens.nextToken();

      Pia.debug(this, "a pair-->"+ s);

      String param = null;
      Object value = null;

      int pos = s.indexOf('=');
      if( pos == -1 ){
	String eparam = s.trim().toLowerCase();
	paramKeys.addElement( eparam );
	param = Utilities.unescape( eparam );
	Pia.debug(this, "a param1-->"+ param);

	value = crc.sgml.Token.empty;
	Pia.debug(this, "a val1-->"+ value);
      }else{
	String p = s.substring(0, pos);
	String eparam = p.trim().toLowerCase();
	paramKeys.addElement( eparam );
	param = Utilities.unescape( eparam );
	Pia.debug(this, "a param2-->"+ param);
	
	String evalue = "";
	if (pos < (s.length() - 1)) {
	  String v = s.substring( pos+1 );
	  evalue = v.trim();
	}
	value = Utilities.unescape( evalue );
	Pia.debug(this, "a val2-->"+ value);
      }
      put(param, value);
    }
  }

  /**
   * Return parameter key words in the original order.
   * @return parameter key words in the original order
   */
  public List paramKeys(){
    int size =0;
    if ( (size=paramKeys.size()) > 0){
      List list = new List();
      for(int i=0; i < size; i++)
	list.push( paramKeys.elementAt(i) );
      return list;
    }else
      return null;
  }

  /**
   * Return original query string
   * @return original query string
   */
  public String queryString(){
    Pia.debug(this , "queryString" );
    // Thus, someone must have called setParameters(...) on HTTPRequest
    return queryString;
  }

  /**
   * Convert to a Table.
   */
  public Table getParameters(){
    Enumeration e = keys();
    Table zTable = new Table( );
    while (e.hasMoreElements()) {
      String prop = e.nextElement().toString();
      zTable.at(prop, get(prop));
    }
    return zTable;
  }

  /**
   * for testing only
   */
  public OutputStream printParametersOn(OutputStream out){
    PrintStream ps = new PrintStream( out );
    Enumeration e = propertyNames();
    Object o;

    while( e.hasMoreElements() ){
      try{
	String key = (String)e.nextElement();
	ps.print( key + "-->" );

	o = getProperty( key );
	if( o instanceof String  )
	  ps.println( (String)o );
	else
	  ps.println( "" );
      }catch(Exception ex){ return out;}
    }
    return out;
  }

  /***********************************
   * content interface operations --  Forms not support editing currently
   ***********************************/

  /**
   * Add an object to the content
   * if object is not a compatible type, throws exception
   * @param  where: interpretation depend on content,  by convention 0 means at front
   * -1 means at end, everything else is subject to interpretation
   */
   public void add(Object moreContent, int where) throws ContentOperationUnavailable {
     throw(new ContentOperationUnavailable("adding " + moreContent.getClass().getName() + " not supported by " + this.getClass().getName()));
   }
  



  /**
   * replace target with replacement
   * subject to interpretation.
   * null  replacement implies removal of target
   */
   public void replace(Object target, Object replacement) throws ContentOperationUnavailable{
     throw(new ContentOperationUnavailable("replacing " +  target.getClass().getName() + " not supported by " + this.getClass().getName()));
    }
  




}










