// InputContent.java

// (c) COPYRIGHT Ricoh California Research Center, 1998.

/** Interface for Content objects representing
 * HTML forms, extends basic Content interface
 * by adding a method allowing Content Length to be fetched
 */

package crc.pia;


public interface InputContent extends Content {


  /** Return (if possible) the length of the
   * current content body as an integer
   *
   * @see crc.pia.GenericAgent
   */
  public int getCurrentContentLength() throws ContentOperationUnavailable;


}
