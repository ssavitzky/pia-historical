// ParseChildList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dps.active;

import java.io.*;
import crc.dps.*;
import crc.dom.*;

/**
 * Implementation of ActiveNodeList for the common special case where
 *	the contents are the children of a single Node. 
 */
public class ParseChildList implements /* Active */ NodeList {

  public ParseChildList(ParseTreeNode parentNode){
    parent = parentNode;
  }


  /**
   * Returns a NodeEnumerator.
   */
  public NodeEnumerator getEnumerator(){
    return new ParseChildEnum( this );
  }

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
      throw new NoSuchNodeException("ParseChildList is empty.");

    Node n = parent.getFirstChild();
    long howmany = getLength();

    //Report.debug(this, "index is-->"+Integer.toString( (int)index ));
    if( index <= howmany && index >= 0 ){
      while( i < index ){
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
   * Returns the number of nodes in the NodeList instance. 
   *	The range of valid child node indices is 0 to getLength()-1 inclusive. 
   * @return length of list.
   */
  public long getLength()
  {
    Node ptr = parent.getFirstChild();
    long count = 0;
    while( ptr != null ){
      count++;
      ptr = ptr.getNextSibling();
    }
    return count;
    /*
    if( parent == null ) return 0;
    return parent.getChildCount(); // === getChildCount is buggy ===
    */
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
  protected ParseTreeNode getParent(){ return parent; }

  /** 
   * @return string corresponding to content
   */
  public String toString() {
    Node n = parent.getFirstChild();
    if (n == null) return "";

    String result = "";
    for (; n != null; n = n.getNextSibling()) result += n.toString();
    return result;
  }

  /**
   * This ParseChildList is expanded through parent's
   * first node.
   * 
   */
  protected ParseTreeNode parent;
}



