// Content.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.


package crc.pia;

import crc.pia.Machine;
import crc.pia.Agent;
import crc.pia.ContentOperationUnavailable;

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
 *  creates the appropriate content object.  They can also be generated directly by
 *  agents.   <p>
 *
 *  Implementors of the Content interface act as wrappers for
 *  the objects which contain actual data.
 *  Note that content objects are generally streams, and that
 *  processing should be delayed as long as possible. (In some cases
 *  such as video it may be impossible to create the full object.  In
 *  most other cases, it is simply a matter of wanting to start
 *  feeding data to the client as early as possible.)  Most content
 *  types provide appropriate editing methods that allow agents to operate
 *  on content objects -- the operations are not actually performed until
 *  the data is needed.
 *  See package crc.content for implementations of this interface.
 */

public interface Content {

 /**
  * Headers generally contain meta-data about the object and the transaction.
  * The content object should generally maintain content specific items like
  * content-length, while the transaction maintains most others.
  * @return Headers object
  */
  public Headers headers();

  /**
   * set Headers object
   */
  public void setHeaders( Headers headers );



  /************************************************************
  ** Content producers:
  ************************************************************/

  /**   The actual data object e.g. the stream or data structure,
   *    should be specified
   *    in the construction of this content object.
   *    Normally this would be specified in the constructor, however
   *     content objects often created by name (see ContentFactory)
   *    so need a way to specify source.
   *   Throws exception if processing begun already or wrong type of object
   */
  
  public void source(Object input) throws ContentOperationUnavailable;
  


  /************************************************************
  ** Access functions:
  ************************************************************/

 /** 
  * Access functions 
  *  when the "To machine" is ready to send a response, it reads
  *  data from the content object.  At that point (if not sooner)
  *  the content object should start sucking data from the source,
  *  processing in any matter specified by agents, and then finally spitting
  *  out the data
  *  Essentially stream functions.
  */

  /**
   * sets the target for write operations and writes data to this stream
   * this method will block until all data is written to output stream.
   * @throws exception if content cannot be written to this stream
   * @return the number of items written
   */
  public int writeTo(OutputStream outStream) throws ContentOperationUnavailable, IOException;



  /************************************************************
  ** Agent interactions:
  ************************************************************/


  /** Add an output stream to "tap" the data before it is written. 
   * Taps will get data during a read operation just before the data "goes
   * out the door" */
  public void tapOut(OutputStream tap) throws ContentOperationUnavailable;


  /** Add an output stream to "tap" the data as it is read.
   * Taps will get data  as soon as it is  available to the content 
   * -- before any processing occurs.  
   * tapOut == tapIn if content does not support editing/processing
   */
  public void tapIn(OutputStream tap) throws ContentOperationUnavailable;

  
  /** specify an agent to be notified when  entering a state, for
   * example the object is complete 
   * @param state: string naming the state change agent is interested in
   * @param arg:  arbitrary object that will be sent back to be agent when
   *            the state occurs and the agents contentUpdate method is called
   */
  public void notifyWhen(Agent interested, String state, Object arg) throws ContentOperationUnavailable;

  /**
   * return a list of states that this content can go through
   * agents can use this to determine when to be notified
   */
   public String[] states();
  

  /**
   * If the content object exists as a data structure in memory, then
   * it is persistent. Otherwise, it is not persistent (streams are
   * not persistent).
   */

 public boolean isPersistent();


  /**
   *  the transaction calls us to begin processing the input before
   * the "to machine" is ready to send a response giving us an opportunity to
   * fill up our internal buffers
   * @return false if content is complete
   */
  public boolean processInput();

  /************************************************************
  ** Constructors:
  ************************************************************/
  /**
   * Typically constructors should accept "native" source types as arguments
   */

  /************************************************************
  ** Content specific operations:
  ************************************************************/
  /**
   * Filtering, editing, etc.
   */
  /**
   * Add an object to the content
   * if object is not a compatible type, throws exception
   * @param  where: interpretation depend on content,  by convention 0 means at front
   * -1 means at end, everything else is subject to interpretation
   */
public void add(Object moreContent, int where) throws ContentOperationUnavailable;

/**
 * replace target with replacement
 * subject to interpretation.
 * null  replacement implies removal of target
 */
public void replace(Object target, Object replacement) throws ContentOperationUnavailable;




}	









