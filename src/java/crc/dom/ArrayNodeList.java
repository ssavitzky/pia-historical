// ArrayNodeList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;

import java.util.Vector;

/**
 * Mutable node collection.  This list could be used
 * to return accumulated links in a document.  Each node
 * in this collection bears no relation to each other.
 */

public class ArrayNodeList implements EditableNodeList {

  /** The actual elements.  Should be protected, but the enumerator needs it. */
  Vector elements = new Vector();
  protected long size() { return elements.size(); }

  public ArrayNodeList(){
  }

  public ArrayNodeList(Node aNode){
    if (aNode != null) elements.addElement(aNode);
  }

  /**
   * Deep copy of another list.
   */
  public ArrayNodeList(NodeList list)throws NullPointerException{
    if( list == null ){
      String err = ("Illegal list.");
      throw new NullPointerException(err);
    }
    initialize( list );
  }

  /**
   *Replace the indexth item the list with replacedNode, and return the old node
   *object at that index (null is returned if the index is equal to the previous
   *number of nodes in the list). If index is greater than the number of nodes in
   *the list, a NoSuchNodeException is thrown.
   *@param index Ranges from 0 to size - 1
   *@param replaceNode new node to be put at index
   *@return previous node at index
   *@exception NoSuchNodeException when indexed out of bound.
   */
  public Node replace(long index,Node replacedNode) 
       throws NoSuchNodeException
  {
    if( index >= elements.size() || index < 0){
      String err = ("No such node exists.");
      throw new NoSuchNodeException(err);
    }
    Node old = (Node)elements.elementAt( (int)index );
    elements.setElementAt(replacedNode, (int)index);
    return old;
  }

  /**
   * Inserts a child node into the list BEFORE zero-based location index. Nodes from
   * index to the end of list are moved up by one. If index is 0, the node is added
   * at the beginning of the list; if index is self.getLength(), the node is added at the
   * end of the list. 
   * @param index If 0, the node is added
   * at the beginning of the list; if index is self.getLength(), the node is added at the
   * end of the list. 
   * @param newNode new node to be put at index
   * @exception NoSuchNodeException when indexed out of bound.
   */
  public void insert(long index,Node newNode) 
       throws NoSuchNodeException
  {
    if( index == 0 ){
      elements.insertElementAt( newNode, 0 );
      return;
    }
    if( index == size() ){
      elements.addElement( newNode );
      return;
    }

    if( index >= 0 && index <= size() )
      elements.insertElementAt( newNode, (int)index );
    
  }

  /**
   *Removes the node at index from the list and returns it. The indices of the
   *members of the list which followed this node are decremented by one following
   *the removal. If the index is provided is larger than the number of nodes in the
   *list, the NoSuchNodeException is thrown.
   *@param index Ranges from 0 to size - 1
   *@return node at index
   *@exception NoSuchNodeException when indexed out of bound.
   */
  public Node remove(long index)
       throws NoSuchNodeException
  {
    if( index >= size() || index < 0){
      String err = ("No such node exists.");
      throw new NoSuchNodeException(err);
    }

    Node n = (Node)elements.elementAt( (int)index );
    elements.removeElementAt( (int)index );
    return n;
  }

  /**
   * @return NodeEnumerator
   */
  public NodeEnumerator getEnumerator()
  {
    return new ArrayNodeListEnumerator( this );
  }

  /**
   *Returns the indexth item in the collection. If index is greater than or equal
   *to the number of nodes in the list, a NoSuchNodeException is thrown. 
   *@param index Ranges from 0 to size - 1
   *@return node at index
   *@exception NoSuchNodeException when indexed out of bound.
   */
  public Node item(long index)
       throws NoSuchNodeException
  {
    if( index >= size() || index < 0){
      String err = ("No such node exists.");
      throw new NoSuchNodeException(err);
    }

    return  (Node)elements.elementAt( (int)index );
  }


  /**
   *Returns the number of nodes in the NodeList instance. The range of valid child
   *node indices is 0 to getLength()-1 inclusive. 
   */
  public long getLength(){ return size(); }
  
  /**
   * Deep copy
   */
  private void initialize( NodeList list ){
    NodeEnumerator e = list.getEnumerator();

    Node n = null;
    for(n=e.getFirst(); n != null; n=e.getNext())
      elements.addElement( n );
  }

  /** Append a new element.
   */
  public void append(Node newChild) { elements.addElement(newChild); }

  /** Append a list of elements.
   */
  public void append(NodeList aNodeList) {
    if (aNodeList == null) return;
    crc.dom.NodeEnumerator e = aNodeList.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      append(node);
    }
  }

  /** 
   * @return string corresponding to content
   */
  public String toString() {
    String result = "";
    long length = getLength();
    for (long i = 0; i < length; ++i) try {
      Node attr = item(i);
      result += attr.toString();
      if (i < length - 1) result += " ";
    }catch(NoSuchNodeException e){
    }
    return result;
  }
}



