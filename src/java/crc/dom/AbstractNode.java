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

public abstract class AbstractNode implements Node, Cloneable {

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

  /** Inserts a child node (newChildbefore the existing child node refChild. If
   *  refChild is null, insert newChild at the end of the list of children. If
   *  refChild is not a child of the Node that insertBefore is being invoked on, a
   *  NotMyChildException is thrown. 
   */
  public void insertBefore(Node newChild, Node refChild)
       throws NotMyChildException
  {
    NodeEnumerator e = null;

    if( newChild == null ) return;

    //newChild.setParent( this );

    if( refChild == null ) 
      insertAtEnd( newChild );
    else if( refChild.getParentNode() != this ){
      String err = ("The reference child is not mine.");
      throw new NotMyChildException(err);
    }else{
      children.doInsertBefore( (AbstractNode)newChild, (AbstractNode)refChild );
    }
  }

  public Node replaceChild(Node oldChild, Node newChild)
       throws NotMyChildException
  {
    if( oldChild.getParentNode() != this ){
      String err = ("The reference child is not mine.");
      throw new NotMyChildException(err);
    }
    else
      return children.doReplaceChild((AbstractNode)oldChild, (AbstractNode)newChild);
  }

  public Node removeChild(Node oldChild)
       throws NotMyChildException
  {
    if( oldChild.getParentNode() != this ){
      String err = ("The reference child is not mine.");
      throw new NotMyChildException(err);
    }else
      return children.doRemoveChild((AbstractNode)oldChild);
  }

  /* mutator for parent and siblings */
  protected void setParent(AbstractNode parent){ this.parent = parent; }
  protected void setPrevious(AbstractNode leftSibling){ this.leftSibling = leftSibling; }
  protected void setNext(AbstractNode rightSibling){ this.rightSibling = rightSibling; }
  protected AbstractNode getPrevious(){ return leftSibling; }
  protected AbstractNode getNext(){ return rightSibling; }

  protected void insertAtEnd( Node newChild ){
    if( !hasChildren() ) 
      children = new ChildNodeList( (AbstractNode)newChild ); 
    else
      children.doInsertLast( (AbstractNode)newChild );
  }

  /**
   * The parent 
   */
  protected AbstractNode parent;

  /**
   * Left sibling
   */
  protected AbstractNode leftSibling;

  /**
   * Right sibling 
   */
  protected AbstractNode rightSibling;

  /**
   * The child collection
   */
  protected ChildNodeList children;

}




