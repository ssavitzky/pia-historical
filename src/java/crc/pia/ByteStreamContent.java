// ByteStreamContent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.


package crc.pia;
import java.io.IOException;
import crc.pia.Content;


public class ByteStreamContent implements Content
{

  // conditions to notify agents

  // public static final Condition foo = new Condition-A(...);
  // public static final Condition bar = new Condition-B(...);

  protected OutputStream[] taps;
 
  /**
   * headers
   */
  private Header headers;

  /**
   * body
   */
  private InputStream body;

  /**
   * filter for body
   */
  private DataInputStream in;

 /** 
  * Return as a string all existing header information for this
  * object.
  * @return String with HTTP style header <tt> name: value </tt><br>
  */
  public Headers headers(){
    if( headers )
      return headers;
    else
      return null;
  }
  
 /** 
  * Return the  value of the given header or void if none.
  * @param  field name of header field
  * @return String value of a header attribute.
  */
  public void setHeaders( Headers headers ){
    if( headers )
      this.headers = headers;
  }
    
 /**  stream like functions go here */


 /** set a source stream for this object
  * usually this will come from a machine
   */
  public void source(InputStream stream){
    if( stream ){
      body = stream;
      in = new DataInputStream( body );
    }
  }
  

 /**  get the next chunk of data as bytes
  *  @return number of bytes read -1 means EOF
  */
  public int read(byte buffer[]) throws IOException{
    try {
      return in.read( buffer );
    }catch(Exception e){
      throw e;
    }
  }
 
 /**  get the next chunk of data as bytes
  * @param offset position in buffer to start placing data
  * @param length number of bytes to read
  *  @return number of bytes read
  */
  public int read(byte buffer[], int offset, int length) throws IOException{
    try {
      return in.read( buffer, offset, length );
    }catch(Exception e){
      throw e;
    }
  }

  /** add an output stream to "tap" the data before it is written
   * any taps will get data during a read operation
   * before the data "goes out the door"
   */
  public void addTap(InputStream tap){}
  
  /**  specify an agent to be notified when a condition is satisfy
   *for example the object is complete
   */
  public void notifyWhen(Agent interested, Object condition){}

  public ByteStreamContent(InputStream in){
    source( in );
  }
  
}	








