// EditableNodeList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

public interface EditableNodeList extends NodeList {

  Node replace(long index,Node replacedNode) 
    throws NoSuchNodeException;

  void insert(long index,Node newNode) 
    throws NoSuchNodeException;

  Node remove(long index)
    throws NoSuchNodeException;

};
