// Content.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.


package crc.pia;
import crc.pia.Machine;
import crc.pia.Agent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** Content
 *  is an abstract interface for those objects which can serve as the
 *  content (data portion) of a transaction.    
 *  Content objects sit between a to and a from machine. <p>
 *
 *  A Content is generally created by the ContentFactory which first
 *  parses the stream headers to determine the type of object then
 *  creates the object.  They can also be generated directly by
 *  agents.   <p>
 *
 *  Note that content objects are generally streams, and that
 *  processing should be delayed as long as possible. (In some cases
 *  such as video it may be impossible to create the full object.  In
 *  most other cases, it is simply a matter of wanting to start
 *  feeding data to the client as early as possible.)  Most content
 *  types provide appropriate editing methods.  */

public interface Content {

 /**
  * @return Headers object
  */
  public Headers headers();

  /**
   * set Headers object
   */
  public void setHeaders( Headers headers );

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
  public void source(InputStream stream);

 /** set a source stream for this object
  * usually this will come from a machine
  */
  public InputStream source();


 /**  get the next chunk of data as bytes
  *  @return number of bytes read -1 means EOF
  */
  public int read(byte buffer[]) throws IOException;
 
 /**  get the next chunk of data as bytes
  * @param offset position in buffer to start placing data
  * @param length number of bytes to read
  *  @return number of bytes read
  */
 public int read(byte buffer[], int offset, int length) throws IOException;

  /** add an output stream to "tap" the data before it is written
   * any taps will get data during a read operation
   * before the data "goes out the door"
   */
  public void addTap(InputStream tap);
  
  /**  specify an agent to be notified when a condition is satisfy  
   *for example the object is complete
   */
  public void notifyWhen(Agent interested, Object condition);

  /**  perhaps some methods for filtering objects should go here */

  public String toString();

  public byte[] toBytes();

  /**
   * @return false if content is complete
   */
  public boolean processInput();
}	









