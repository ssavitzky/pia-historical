////// ParseNodeList.java: ActiveNodeList implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.active;

import crc.dom.ArrayNodeList;
import crc.dom.Node;

/**
 * A list or sequence of ActiveNode objects.  
 *
 *	The contents need not be children of the same Node.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Token
 * @see crc.dom.Node
 */

public class ParseNodeList extends ArrayNodeList implements ActiveNodeList {

  /**
   * Returns the indexth item in the collection, as a ActiveNode.
   * 	If index is greater than or equal to the number of nodes in the
   * 	list, null is returned.  
   *
   * @return a ActiveNode at index position.
   * @param index Position to get node.
   */
  public ActiveNode activeNodeAt(long index) { 
    try {
      return (ActiveNode)item(index);
    } catch (crc.dom.NoSuchNodeException e) {
      return null;
    }
  }

  /** Append a new ActiveNode.
   */
  public void append(ActiveNode newChild) { append((Node)newChild); }

  public ParseNodeList() {}

  public ParseNodeList(ActiveNode initialChild) {
    super((Node)initialChild);
  }
}
