// ArrayNodeList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;

import java.util.Vector;

/**
 * Mutable node collection.  This list could be used
 * to return accmulated links in a document.  Each node
 * in this collection bears no relation to each other.
 */

public class ArrayNodeList extends Vector implements EditableNodeList {

  public ArrayNodeList(){
    //    nodeCollection = new Vector();
  }

  public ArrayNodeList(NodeList list)throws NullPointerException{
    if( list == null ){
      String err = ("Illegal list.");
      throw new NullPointerException(err);
    }
    //nodeCollection = new Vector( (int)list.getLength() );
    initialize( list );
  }

  /**
   *Replace the indexth item the list with replacedNode, and return the old node
   *object at that index (null is returned if the index is equal to the previous
   *number of nodes in the list). If index is greater than the number of nodes in
   *the list, a NoSuchNodeException is thrown. 
   */
  public Node replace(long index,Node replacedNode) 
       throws NoSuchNodeException
  {
    if( index >= size() || index < 0){
      String err = ("No such node exists.");
      throw new NoSuchNodeException(err);
    }
    Node old = (Node)elementAt( (int)index );
    setElementAt(replacedNode, (int)index);
    return old;
  }

  /**
   * Inserts a child node into the list BEFORE zero-based location index. Nodes from
   * index to the end of list are moved up by one. If index is 0, the node is added
   * at the beginning of the list; if index is self.getLength(), the node is added at the
   * end of the list. 
   */
  public void insert(long index,Node newNode) 
       throws NoSuchNodeException
  {
    if( index == 0 ){
      insertElementAt( newNode, 0 );
      return;
    }
    if( index == size() ){
      addElement( newNode );
      return;
    }

    if( index >= 0 && index <= size() )
      insertElementAt( newNode, (int)index );
    
  }

  /**
   *Removes the node at index from the list and returns it. The indices of the
   *members of the list which followed this node are decremented by one following
   *the removal. If the index is provided is larger than the number of nodes in the
   *list, the NoSuchNodeException is thrown. 
   */
  public Node remove(long index)
       throws NoSuchNodeException
  {
    if( index >= size() || index < 0){
      String err = ("No such node exists.");
      throw new NoSuchNodeException(err);
    }

    Node n = (Node)elementAt( (int)index );
    removeElementAt( (int)index );
    return n;
  }

  public NodeEnumerator getEnumerator()
  {
    return new ArrayNodeListEnumerator( this );
  }

  /**
   *Returns the indexth item in the collection. If index is greater than or equal
   *to the number of nodes in the list, a NoSuchNodeException is thrown. 
   */
  public Node item(long index)
       throws NoSuchNodeException
  {
    if( index >= size() || index < 0){
      String err = ("No such node exists.");
      throw new NoSuchNodeException(err);
    }

    return  (Node)elementAt( (int)index );
  }


  /**
   *Returns the number of nodes in the NodeList instance. The range of valid child
   *node indices is 0 to getLength()-1 inclusive. 
   */
  public long getLength(){ return size(); }
  

  private void initialize( NodeList list ){
    NodeEnumerator e = list.getEnumerator();

    Node n = null;
    for(n=e.getFirst(); n != null; n=e.getNext())
      addElement( n );
  }

}



