////// AbstractInputFrame.java: Input stack frame interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

/**
 * The base class for Input implementations that also implement the
 *	InputStackFrame interface, and so can be used to efficiently
 *	implement an InputStack using a linked list. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * 
 * @see crc.dps.InputStack
 * @see crc.dps.Input
 */

package crc.dps;

public abstract class AbstractInputFrame 
	extends 	AbstractInput
	implements 	InputStackFrame
{

  /************************************************************************
  ** Stack Operations:
  ************************************************************************/

  /** Push an Input onto the stack.  
   *	Since at this point we know that the Input is not (or should not be
   *	treated as) an InputStackFrame, we will have to construct a new
   *	frame to go around it.
   *
   * @return the new InputStack.
   */
  public InputStack pushInput(Input anInput) {
    return new BasicInputFrame(anInput, this);
  }

  /** Push an InputStackFrame onto the stack.  
   *	an InputStackFrame is basically an Input with a ``next'' pointer.
   *
   * @return the new InputStack.
   */
  public InputStack pushFrame(InputStackFrame aFrame) {
    return aFrame.pushOnto(this);
  }

  /** Pop the old top Input.  
   *	Defined so that either an array or a linked list implementation
   *	is possible; a linked list would be somewhat more efficient. 
   *
   * @return the new InputStack, or <code>null</code> if there is no
   *	next Input.
   */
  public InputStack popInput() {
    return nextInputFrame;
  }

  /** Returns the Input object at this level.  
   *	(Note that it is usually more efficient to perform Input operations
   *	 directly on the InputStack, since it is probably a linked list
   *	 of objects that implement both the linked list and Input operations.)
   */
  public Input topInput() { return this; }


  /************************************************************************
  ** Stack Frame Operations:
  ************************************************************************/

  protected InputStackFrame nextInputFrame;

  /** Return the next frame in the stack. */
  public InputStackFrame getNextInputFrame() {
    return nextInputFrame;
  }

  /** set the next-frame pointer and return <code>this</code> */
  public InputStackFrame pushOnto(InputStackFrame nextFrame) {
    nextInputFrame = nextFrame;
    return this;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  protected AbstractInputFrame() {}

  protected AbstractInputFrame(InputStackFrame next) {
    nextInputFrame = next;
  }
}
