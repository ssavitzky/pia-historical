// AbstractNode.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's dom-Node interface.  This object
 * stores reference to parent node and a collection of
 * children.
 */

package crc.dom;

import java.io.*;
import w3c.dom.Node;
import w3c.dom.NodeList;
import w3c.dom.NotMyChildException;


public abstract class AbstractNode implements Node {

  public static final class NodeType {
    public static final int DOCUMENT   = 0;
    public static final int ELEMENT    = 1;
    public static final int ATTRIBUTE  = 2;
    public static final int PI         = 3;
    public static final int COMMENT    = 4;
    public static final int TEXT       = 5;
  };

  /**
   * implementing Node methods
   */

  public abstract int getNodeType();

  public Node     getParentNode(){ return parent; }
  public NodeList getChildren(){ return null; }
  public boolean  hasChildren(){ return false; }
  public Node     getFirstChild(){ return null; }
  public Node     getPreviousSibling(){ return leftSibling; }
  public Node     getNextSibling(){ return rightSibling; }

  public void insertBefore(Node newChild, Node refChild)
       throws NotMyChildException{}

  public Node replaceChild(Node oldChild, Node newChild)
       throws NotMyChildException{ return null; }

  public Node removeChild(Node oldChild)
       throws NotMyChildException{ return null; }

  /* mutator for parent and siblings */
  protected void setParent(Node parent){ this.parent = parent; }
  protected void setLeftSibling(Node leftSibling){ this.leftSibling = leftSibling; }
  protected void setRightSibling(Node rightSibling){ this.rightSibling = rightSibling; }

  /**
   * The parent 
   */
  protected Node parent;

  /**
   * Left sibling
   */
  protected Node leftSibling;

  /**
   * Right sibling 
   */
  protected Node rightSibling;


}




