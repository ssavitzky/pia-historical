////// InputStackFrame.java: Input stack frame interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;

/**
 * The interface for a stack frame on a linked-list InputStack. <p>
 *
 *	An InputStackFrame is basically an Input with a pointer to the
 *	next frame in the stack.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * 
 * @see crc.dps.InputStack
 * @see crc.dps.Input
 */

public interface InputStackFrame extends InputStack {

  // === it would be possible for these to take and return InputStack ===

  /** Return the next frame in the stack. */
  public InputStackFrame getNextInputFrame();

  /** set the next-frame pointer and return <code>this</code> */
  public InputStackFrame pushOnto(InputStackFrame nextFrame);

}
