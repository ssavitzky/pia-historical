// EditableNodeList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
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
