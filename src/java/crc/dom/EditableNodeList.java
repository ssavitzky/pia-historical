// EditableNodeList.java
// $Id$

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

package crc.dom;

/**
 *EditableNodeList is a subtype of NodeList that adds operations that modify the list
 *of nodes, such as adding, deleting and replacing Node instances in the list. 
 */
public interface EditableNodeList extends NodeList {

  /**
   *Replace the indexth item the list with replacedNode, and return the old node
   *object at that index (null is returned if the index is equal to the previous
   *number of nodes in the list). If index is greater than the number of nodes in
   *the list, a NoSuchNodeException is thrown. 
   */
  Node replace(long index,Node replacedNode) 
    throws NoSuchNodeException;

  /**
   *Inserts a child node into the list BEFORE zero-based location index. Nodes from
   *index to the end of list are moved up by one. If index is 0, the node is added
   *at the beginning of the list; if index is self.getLength(), the node is added at the
   *end of the list. 
   */
  void insert(long index,Node newNode) 
    throws NoSuchNodeException;

  /**
   * Removes the node at index from the list and returns it. The indices of the
   * members of the list which followed this node are decremented by one following
   * the removal. If the index is provided is larger than the number of nodes in the
   * list, the NoSuchNodeException is thrown. 
   */
   Node remove(long index)
    throws NoSuchNodeException;

};
