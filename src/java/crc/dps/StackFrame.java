////// StackFrame.java: Base class for stack frames
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

/**
 * The base class for stacks implemented as linked lists.
 *
 *	Because we want the link variable and the push and pop operations
 *	to be typed to avoid the inefficiency of casting, there really isn't
 *	much we can do in the base class.  A depth counter is included for
 *	debugging. <p>
 *
 *	There are really two possible ways to implement linked-list stacks:
 *	one is to have the stack frame <em>point to</em> the corresponding
 *	item; the other is to have it <em>be</em> the item, i.e. to make
 *	the base class for whatever is on the stack descend from StackFrame.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * 
 */

package crc.dps;

public abstract class StackFrame  {

  /************************************************************************
  ** Depth:
  ************************************************************************/

  protected int depth = 0;

  /** Returns the depth of nesting in the stack, with 0 being the topmost
   *	stack frame.  In other words, a stack of depth 0 cannot be popped.
   */
  public int getDepth() {
    return depth;
  }

  /************************************************************************
  ** Basic stack traversal:
  ************************************************************************/

  /** Returns the next stack frame in the linked list.  Normally one
   *	uses a typed version, but this makes it possible to operate on
   *	a stack without knowing which concrete subclass it belongs to. <p>
   *
   *	By convention the strongly-typed version of this operation is 
   *	called <code>getNext</code>.
   */
  public abstract StackFrame getNextFrame();

  /** Returns the item at the top of the stack. */

  /************************************************************************
  ** Construction:
  ************************************************************************/

  StackFrame() {
  }

  StackFrame(int d) {
    depth = d;
  }

}
