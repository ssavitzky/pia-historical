////// ActiveNodeList.java: ActiveNode List interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.active;

import crc.dps.active.ActiveNode;
import crc.dps.active.ActiveNodeList;

/**
 * A list or sequence of ActiveNode objects.  
 *
 *	An ActiveNodeList is not necessarily a NodeList; it might be a Java
 *	Collection or crc.ds.List.  The contents are not necessarily 
 *	from the same level in the parse tree, though this is usual.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.ActiveNode
 * @see crc.dom.NodeList
 * @see java.util.Collection
 * @see crc.ds.List
 */

public interface ActiveNodeList  {

  /**
   * Returns the indexth item in the collection, as a ActiveNode.
   * 	If index is greater than or equal to the number of nodes in the
   * 	list, null is returned.  
   *
   * @return a ActiveNode at index position.
   * @param index Position to get node.
   */
  public ActiveNode activeNodeAt(long index);

  /** Append a new ActiveNode.
   */
  public void append(ActiveNode newChild);

  /** 
   * @return length
   */
  public long getLength();
}
