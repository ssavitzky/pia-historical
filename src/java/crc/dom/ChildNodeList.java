// ChildNodeList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;

/**
 * This list expands a children collection through the start node.
 */

public class ChildNodeList implements NodeList{

  public ChildNodeList(AbstractNode parentNode){
    parent = parentNode;
  }

  public ChildNodeList(NodeList list)
  {
    if( list == null ) return;
    if( list instanceof ChildNodeList ){
      AbstractNode p = ((ChildNodeList)list).getParent();
      if( p != null )
	parent = (AbstractNode)p.clone();
      else
	parent = null;
    }else
      initialize( list );
  }


  public NodeEnumerator getEnumerator(){ return new ChildNodeListEnumerator( this ); }

  public Node item(long index)
       throws NoSuchNodeException
  {
    long i = 0;
    if (parent == null)
      throw new NoSuchNodeException("ChildNodeList is empty.");

    Node n = parent.getFirstChild();
    long howmany = getLength();

    if( index < howmany - 1 && index >= 0 ){
      while( i != index ){
	n = n.getNextSibling();
      }
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
   */
  public long getLength()
  {
    if( parent == null ) return 0;

    Node ptr = parent.getFirstChild();
    long count = 1;
    while( ptr != null ){
      ptr = ptr.getNextSibling();
      count++;
    }
    return count;
  }


  protected void initialize(NodeList list)
  {
    //punt
  }

  protected AbstractNode getParent(){ return parent; }

  /**
   * Header node where previous points to start and next points to last.
   * Make list circular
   */
  protected AbstractNode parent;
}

