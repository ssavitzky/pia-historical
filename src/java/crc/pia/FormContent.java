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

  public FormContent(String s){
    Pia.debug( this, "string constructor..." + s );
    if( s != null ){
      setParameters(s);
    }
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
    String token = null;
    String[] pairs = null;
    int count;
    int i = 0;
    int pos;

    Pia.debug(this, "processing...");
    if( toSplit != null ){
      Pia.debug(this, "the toSplit string is-->"+ toSplit);

      tokens = new StringTokenizer( toSplit,"&" );

      queryString = toSplit;
    }
    else{
      String zcontent = toString();

      Pia.debug(this, "the content is below:");
      Pia.debug(this, zcontent);
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
    String param = null;
    Object value = null;

    for(; i < pairs.length; i++){
      String s = pairs[i];

      Pia.debug(this, "a pair-->"+ s);

      pos = s.indexOf('=');
      if( pos == -1 ){
	String eparam = s.trim();
	paramKeys.addElement( eparam );
	param = Utilities.unescape( eparam );
	Pia.debug(this, "a param1-->"+ param);

	value = crc.sgml.Token.empty;
	Pia.debug(this, "a val1-->"+ value);
      }else{
	String p = s.substring(0, pos);
	String eparam = p.trim();
	paramKeys.addElement( eparam );
	param = Utilities.unescape( eparam );
	Pia.debug(this, "a param2-->"+ param);
	
	String v = s.substring( pos+1 );
	String evalue = v.trim();
	paramKeys.addElement( evalue );
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

}










