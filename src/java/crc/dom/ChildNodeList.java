// ChildNodeList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;

/**
 * Given a node, this list is expanded through the node's
 * first child.  This list also supports deep copy operation 
 * of another node.
 */

public class ChildNodeList implements NodeList{

  public ChildNodeList(AbstractNode parentNode){
    parent = parentNode;
  }

  /**
   * Given a node list, perform deep copy of list
   * only if the parent node is an AbstractNode.
   */
  public ChildNodeList(NodeList list)
  {
    if( list == null ) return;
    if( list instanceof ChildNodeList ){
      AbstractNode p = ((ChildNodeList)list).getParent();
        if( p != null )
      	parent = (AbstractNode)p.clone();
      else
      	parent = null;
    }else initialize( list );
  }

  /**
   * Returns a NodeEnumerator.
   */
  public NodeEnumerator getEnumerator(){ return new ChildNodeListEnumerator( this ); }

  /**
   * Returns the indexth item in the collection. If index is greater than or equal
   * to the number of nodes in the list, a NoSuchNodeException is thrown. 
   * @return a node at index position.
   * @param index Position to get node.
   * @exception NoSuchNodeException is thrown if index out of bound.
   */
  public Node item(long index)
       throws NoSuchNodeException
  {
    long i = 0;
    if (parent == null)
      throw new NoSuchNodeException("ChildNodeList is empty.");

    Node n = parent.getFirstChild();
    long howmany = getLength();

    //Report.debug(this, "index is-->"+Integer.toString( (int)index ));
    if( index <= howmany && index >= 0 ){
      while( i != index ){
	i++;
	n = n.getNextSibling();
      }
      //Report.debug(this, "i is-->"+Integer.toString( (int)i ));
      return n;
    }
    else{
      String err = ("Index out of bound.");
      throw new NoSuchNodeException(err);
    }
   
  }

  /**
   * Returns the number of nodes in the NodeList instance. The range of valid child
   * node indices is 0 to getLength()-1 inclusive. 
   * @return length of list.
   */
  public long getLength()
  {
    if( parent == null ) return 0;

    Node ptr = parent.getFirstChild();
    long count = 0;
    while( ptr != null ){
      count++;
      ptr = ptr.getNextSibling();
    }
    return count;
  }

  /**
   * For copying foreign node list.
   */
  protected void initialize(NodeList list)
  {
    //punt
  }

  /**
   * return parent so that this list can be copied.
   */
  protected AbstractNode getParent(){ return parent; }

  /**
   * This ChildNodeList is expanded through parent's
   * first node.
   * 
   */
  protected AbstractNode parent;
}



