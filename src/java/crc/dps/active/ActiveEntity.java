////// ActiveEntity.java: Active Entity node (parse tree element) interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.active;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Entity;

import crc.dps.Input;
import crc.dps.Output;
import crc.dps.Context;

/**
 * A DOM Entity node which includes extra syntactic and semantic
 *	information, making it suitable for use in active documents in
 *	the DPS.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dom.Node
 * @see crc.dps.Active
 * @see crc.dps.ActiveNode
 * @see crc.dps.Action
 * @see crc.dps.Syntax
 * @see crc.dps.Processor
 */

public interface ActiveEntity extends Entity, ActiveNode {

  public NodeList getValue();
  public void setValue(NodeList value);

  /** Get the node's value as an Input. */
  public Input getValueInput(Context cxt);

  /** Get an Output that writes into the node's value. */
  public Output getValueOutput(Context cxt);

  public boolean getIsAssigned();
  public void setIsAssigned(boolean value);
}
