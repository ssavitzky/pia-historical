////// Active.java: Interface for things with actions.
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;

/**
 * Interface for objects that have an Action. <p>
 *
 *	By convention, a class that implements Active, or an interface
 *	that extends it, has the name <code>Active<em>Xxxx</em></code>. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dom.Node
 * @see crc.dps.Action
 * @see crc.dps.Context
 * @see crc.dps.Processor
 */

public interface Active {

  /** Gets the Action for this object.  */
  public Action getAction();

  /** Sets the Action for this object.  May be a no-op if the Active
   *	object is immutable.
   */
  public void setAction(Action newAction);

}
