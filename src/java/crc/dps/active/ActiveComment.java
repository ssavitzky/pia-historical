////// ActiveComment.java: Active Comment node (parse tree element) interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.active;
import crc.dom.Node;
import crc.dom.Comment;

import crc.dps.Action;
import crc.dps.Syntax;
import crc.dps.Handler;

/**
 * A DOM Comment node which includes extra syntactic and semantic
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

public interface ActiveComment extends Comment, ActiveNode {

}
