////// ActiveNodeList.java: ActiveNode List interface
//	$Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


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
