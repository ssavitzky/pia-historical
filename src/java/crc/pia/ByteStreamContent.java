// ByteStreamContent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.


package crc.pia;
import java.io.IOException;
import crc.pia.Content;


public class ByteStreamContent implements Content
{

  protected InputStream source;
  
  protected Hashtable headers;
  
  protected OutputStream[] taps;

  /** 
   * Return as a string all existing header information for this
   * object.
   * @return String with HTTP style header <tt> name: value </tt><br>
   */
  public String header(){}
  
  /** 
   * Return the  value of the given header or void if none.
   * @param  field name of header field
   * @return String value of a header attribute.
   */
  public String header(String field){}
  
  /** 
   * Set a header field to value
   * throws exception if not allowed to set.
   */
  public void header(String field, String value) throws NoSuchFieldException{}

  /** 
   * Sets all the headers to values given in hash table
   * hash keys are field names
   * throws exception if not allowed to set.
   */
  public void header(Hashtable table) throws NoSuchFieldException{}

 /** 
  * Access functions 
  * machine objects read content as a stream
  *  two primary uses: acting as a source and sink for machines,
  * and allowing processing by agents " in stream "
  */

 /**  stream like functions go here */


 /** set a source stream for this object
  * usually this will come from a machine
   */
  public void source(InputStream stream){}
  

 /**  get the next chunk of data as bytes
  *  @return number of bytes read -1 means EOF
  */
  public int read(byte buffer[]) throws IOException{}
 
 /**  get the next chunk of data as bytes
  * @param offset position in buffer to start placing data
  * @param length number of bytes to read
  *  @return number of bytes read
  */
  public int read(byte buffer[], int offset, int length) throws IOException{}

  /** add an output stream to "tap" the data before it is written
   * any taps will get data during a read operation
   * before the data "goes out the door"
   */
  public void addTap(InputStream tap){}
  
  /**  specify an agent to be notified when a condition is satisfy
   *for example the object is complete
   */
  public void notifyWhen(Agent interested, Object condition){}

  public ByteStreamContent(InputStream source){
    this.source = source;
  }

  // rest is junk  
  
  public  void insert(int location, Object anObject) throws CannotDoThat{}
  
 /** add variables to give names to special locations, like start and end
 */

  public void delete(int startLocation, int endLocation) throws CannotDoThat{}
  public void printOn(OutputStream output) throws IOException{}
  public byte read(int startLocation, int endLocation){}
  

  


 /** something like filter object should go here */
  
}	








