////// InputStack.java: Input stack frame
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

/**
 * The implementation of a Processor's input stack, using a linked list.
 *
 *	The Processor maintains a stack of Input objects from which
 *	Token objects are obtained as needed.    <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * 
 * @see crc.dps.Token
 * @see crc.dps.Processor
 * @see java.util.Enumeration
 * @see java.util.NoSuchElementException
 */

package crc.dps;

public class InputStack extends StackFrame {

  /************************************************************************
  ** InputStack Frame information:
  ************************************************************************/

  /** The actual Input object at this level of the stack. */
  protected Input input;

  public final Input getInput() {
    return input;
  }

  /************************************************************************
  ** StackFrame:
  ************************************************************************/

  /** The pointer to the next InputStack frame in the stack. */
  protected InputStack next = null;

  
  /************************************************************************
  ** Basic stack traversal:
  ************************************************************************/

  /** Returns the next InputStack frame in the linked list.
   */
  public final InputStack getNext() {
    return next;
  }

  public final StackFrame getNextFrame() {
    return next;
  }


  /************************************************************************
  ** Construction:
  ************************************************************************/

  public InputStack() {
    StackFrame(0);
  }

  public InputStack(InputStack nxt) {
    StackFrame(nxt.depth + 1);
    next = nxt;
  }

}
