////// ActiveText.java: Active Text node (parse tree element) interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.active;
import crc.dom.Node;
import crc.dom.Text;

import crc.dps.Action;
import crc.dps.Syntax;
import crc.dps.Handler;

/**
 * A DOM Text node which includes extra syntactic and semantic
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

public interface ActiveText extends Text, ActiveNode {

  /** Returns <code>true</code> if the Text corresponds to whitespace. */
  public boolean getIsWhitespace();

  /** Sets the flag that determines whether the Text corresponds to
   *	whitespace. */
  public void setIsWhitespace(boolean value);

}
