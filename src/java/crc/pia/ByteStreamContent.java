// ByteStreamContent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.


package crc.pia;
import java.io.IOException;
import java.io.EOFException;
import crc.pia.Content;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;

import crc.pia.Headers;
import crc.pia.HttpBuffer;

public class ByteStreamContent implements Content
{

  // conditions to notify agents

  // public static final Condition foo = new Condition-A(...);
  // public static final Condition bar = new Condition-B(...);

  protected OutputStream[] taps;
 
  /**
   * headers
   */
  private Headers headers;

  /**
   * body
   */
  private InputStream body;

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
   * input stream derived from processInput's zbuf
   */
  private ByteArrayInputStream processInputStream;

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
  * Return as header associated with this content.
  * @return header associated w/ this content.
  */
  public Headers headers(){
    if( headers != null )
      return headers;
    else
      return null;
  }
  
 /** 
  * Set a header associated with this content.
  */
  public void setHeaders( Headers headers ){
    if( headers != null )
      this.headers = headers;
  }
    
 /**  stream like functions go here */


  /**
   * Set a source stream for this object
   * usually this will come from a machine.
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
    closeStream();
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
    try{
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
    }catch(IOException e2){
      throw e2;
    }
  }

  /** Add an output stream to "tap" the data before it is written
   * any taps will get data during a read operation
   * before the data "goes out the door".
   */
  public void addTap(InputStream tap){}
  
  /**  Specify an agent to be notified when a condition is satisfy
   *,for example, the object is complete.
   */
  public void notifyWhen(Agent interested, Object condition){}

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
	if(bytesRead == -1)
	  break;
	data.append( buffer, 0, bytesRead );
      }
    }catch(IOException e2){
    }finally{
      return data.getByteCopy();
    }
  }



  /**
   * Return this content's data as a string.
   * @return this content's data as a string.
   */
  public String toString(){
    byte[]buffer = new byte[1024];
    int bytesRead;
    HttpBuffer data = new HttpBuffer(); 
    
    try{
      while(true){
	bytesRead = read( buffer, 0, 1024 );
	if(bytesRead == -1) break;
	data.append( buffer, 0, bytesRead );
      }
    }catch(IOException e2){
    }finally{
      return data.toString();
    }
  }

  public void closeStream(){
    if( body != null ){
      try{
	body.close();
      }catch(IOException e){}
    }
  }

  public ByteStreamContent(){
    zbuf = new HttpBuffer();
  }


  public ByteStreamContent(InputStream in){
    zbuf = new HttpBuffer();
    source( in );
  }
  
}	




















































