// LinkedNodeList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;

/**
 * Mutable node collection.  This list could be used
 * to return accmulated links in a document.  Each node
 * in this collection bears no relation to each other.
 */

public class LinkedNodeList extends LinkedList implements EditableNodeList {

  public LinkedNodeList()
  {
    nodeCollection = new LinkedList();
  }
  public LinkedNodeList(NodeList list)throws NullPointerException
  {
    if( list == null ){
      String err = ("Illegal list.");
      throw new NullPointerException(err);
    }
    nodeCollection = new LinkedList();
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
    if( index >= nodeCollection.size() || index < 0){
      String err = ("No such node exists.");
      throw new NoSuchNodeException(err);
    }
    return (Node)nodeCollection.setElementAt(replacedNode, (int)index);
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
      nodeCollection.insertElementAt( newNode, 0 );
      return;
    }
    if( index == nodeCollection.size() ){
      nodeCollection.addElement( newNode );
      return;
    } 
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
    if( index >= nodeCollection.size() || index < 0){
      String err = ("No such node exists.");
      throw new NoSuchNodeException(err);
    }
    
    return (Node)nodeCollection.removeElementAt( (int)index );
  }

  public NodeEnumerator getEnumerator()
  {
    return new LinkedNodeListEnumerator( this );
  }

  /**
   *Returns the indexth item in the collection. If index is greater than or equal
   *to the number of nodes in the list, a NoSuchNodeException is thrown. 
   */
  public Node item(long index)
       throws NoSuchNodeException
  {
    if( index >= nodeCollection.size() || index < 0){
      String err = ("No such node exists.");
      throw new NoSuchNodeException(err);
    }
    return (Node)nodeCollection.elementAt( (int)index ); 
  }


  /**
   *Returns the number of nodes in the NodeList instance. The range of valid child
   *node indices is 0 to getLength()-1 inclusive. 
   */
  public long getLength(){
    return nodeCollection.size();
  }


  private void initialize( NodeList list ){
    NodeEnumerator e = list.getEnumerator();

    Node n = null;
    for(n=e.getFirst(); n != null; n=e.getNext())
      nodeCollection.addElement( n );
  }
  
  protected LinkedList nodeCollection;
}
