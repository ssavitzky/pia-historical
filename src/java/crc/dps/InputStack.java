////// InputStack.java: Input stack interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;

/**
 * The interface for a Processor's input stack.
 * <p>
 *	The interface is designed so that an InputStack can easily be
 *	implemented using a linked list of InputStackFrame objects; it
 *	is expected that most Input implementations will conform to both
 *	interfaces.  <p>
 *
 *	However, since a Processor <em>is</em> an Input, and so can
 *	be pushed onto an InputStack in addition to having one of its 
 *	own, it is important that a Processor <em>not</em> implement
 *	InputStackFrame.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * 
 * @see crc.dps.Token
 * @see crc.dps.Processor
 * @see java.util.Enumeration
 * @see java.util.NoSuchElementException
 */

public interface InputStack extends Input {

  /************************************************************************
  ** Stack Operations:
  ************************************************************************/

  /** Push an Input onto the stack.  
   *	Defined so that either an array or a linked list implementation
   *	is possible; a linked list would be somewhat more efficient. 
   *
   * @return the new InputStack.
   */
  public InputStack pushInput(Input anInput);

  /** Push an InputStackFrame onto the stack.  
   *	an InputStackFrame is basically an Input with a ``next'' pointer.
   *
   * @return the new InputStack.
   */
  public InputStack pushFrame(InputStackFrame aFrame);

  /** Pop the old top Input.  
   *	Defined so that either an array or a linked list implementation
   *	is possible; a linked list would be somewhat more efficient. 
   *
   * @return the new InputStack, or <code>null</code> if there is no
   *	next Input.
   */
  public InputStack popInput();

  /** Returns the Input object at this level.  
   *	(Note that it is usually more efficient to perform Input operations
   *	 directly on the InputStack, since it is probably a linked list
   *	 of objects that implement both the linked list and Input operations.)
   */
  public Input topInput();


  /************************************************************************
  ** Construction:
  ************************************************************************/

}
