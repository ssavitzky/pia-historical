// NodeWrapper.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's Document interface. 
 */

package crc.dom;

import java.io.*;

public class NodeWrapper extends AbstractNode{

  public NodeWrapper(Node n){
    foreignNode = n;
  }

  /**
   * implements DOMFactory interfaces
   */
  public int getNodeType(){ return foreignNode.getNodeType(); }
  public NodeList getChildren(){ return foreignNode.getChildren(); }
  public boolean  hasChildren(){ return foreignNode.hasChildren(); }
  public Node     getFirstChild(){ return foreignNode.getFirstChild(); }
  public Node     getPreviousSibling(){ return foreignNode.getPreviousSibling(); }
  public Node     getNextSibling(){ return foreignNode.getNextSibling(); }
  
  public void insertBefore(Node newChild, Node refChild)
       throws NotMyChildException
  {
    foreignNode.insertBefore( newChild, refChild );
  }

  public Node replaceChild(Node oldChild, Node newChild)
       throws NotMyChildException
  {
    return foreignNode.replaceChild( oldChild, newChild );
  }

  public Node removeChild(Node oldChild)
    throws NotMyChildException
  {
    return foreignNode.removeChild( oldChild );
  }
  private Node foreignNode;
}




