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

  /**
   * Returns the parent of the given Node instance. If this node is the root of the
   *  document object tree, null is returned. 
   */
  public Node     getParentNode(){ return getParent(); }

  /**
   *Returns a NodeList object containing the children of this node. If there are no
   *children, null is returned. The content of the returned NodeList is "live" in
   *the sense that changes to the children of the Node object that it was created
   *from will be immediately reflected in the set of Nodes the NodeList contains;
   *it is not a static snapshot of the content of the Node. Similarly, changes made
   *to the NodeList will be immediately reflected in the set of children of the
   *Node that the NodeList was created from. 
   */
  public NodeList getChildren()
  {
    return (getHead() == null) ? null : new ChildNodeList( this ); 
  }

  /**
   *Returns true if the node has any children, false if the node has no children at
   *all. This method exists both for convenience as well as to allow
   *implementations to be able to bypass object allocation, which may be required
   *for implementing getChildren(). 
   */
  public boolean  hasChildren()
  {
    return  getHead() != null && getTail() != null;
  }

  public Node     getFirstChild(){ return getHead(); }
  public Node     getPreviousSibling(){ return getPrevious(); }
  public Node     getNextSibling(){ return getNext(); }

  /** Inserts a child node (newChildbefore the existing child node refChild. If
   *  refChild is null, insert newChild at the end of the list of children. If
   *  refChild is not a child of the Node that insertBefore is being invoked on, a
   *  NotMyChildException is thrown. 
   */
  public synchronized void insertBefore(Node newChild, Node refChild)
       throws NotMyChildException 
  {
    AbstractNode nc = null;
    if( newChild == null ) return;

    if( refChild != null && !(refChild instanceof AbstractNode) )
      throw new NotMyChildException("The reference child is not mime.");
    else if( !(newChild instanceof AbstractNode) )
      nc = new NodeWrapper( newChild );
    else nc = (AbstractNode)newChild;

    insertBefore(nc, (AbstractNode)refChild);
  }

  /**
   *Replaces the child node oldChild with newChild in the set of children of the
   *given node, and return the oldChild node. If oldChild was not already a child
   *of the node that the replaceChild method is being invoked on, a NotMyChildException is
   *thrown. 
   */
  public synchronized Node replaceChild(Node oldChild, Node newChild)
       throws NotMyChildException
  {

    AbstractNode nc = null;
    if( newChild == null || oldChild == null ) return null;

    if( !(oldChild instanceof AbstractNode) )
      throw new NotMyChildException("The old child is not mime.");
    else if( !(newChild instanceof AbstractNode) )
      nc = new NodeWrapper( newChild );
    else nc = (AbstractNode)newChild;

    return replaceChild((AbstractNode) oldChild, nc); 
  }

  /**
   *Removes the child node indicated by oldChild from the list of children and
   *returns it. If oldChild was not a child of the given node, a NotMyChildException is
   *thrown. 
   */
  public synchronized Node removeChild(Node oldChild)
       throws NotMyChildException
  {
    if( oldChild == null ) return null;

    if( !(oldChild instanceof AbstractNode) ){
      String err = ("The old child is not mine.");
      throw new NotMyChildException(err);
    }else
      return removeChild((AbstractNode)oldChild);
  }

  public Object clone(){
    try{
      AbstractNode n = (AbstractNode)super.clone();
      n.setParent( null );
      n.setPrevious( null );
      n.setNext( null );
      n.setHead( null );
      n.setTail( null );
      n.setChildCount( 0 );
      return n;
    }catch(CloneNotSupportedException e){
      return null;
    }
  }


  /**
   * toString
   */
  public String toString(){
    return startString() + contentString() + endString();
  }

  /** Return SGML/XML comment left bracket; that is "<!--".
   *  Subclasses are suppose to override this function
   *  to return appropriate start tag.
   */
  public String startString(){
    return "<!--";
  }
  
  /** Return the String equivalent of this node type.
   *  Subclasses are suppose to override this function
   *  to return appropriate content string.
   */
  public String contentString(){
    return Integer.toString( getNodeType() );
  }

  /** Return SGML/XML comment right bracket; that is "-->".
   *  Subclasses are suppose to override this function
   *  to return appropriate end tag.
   *	
   */
  public String endString(){
    return "-->";
  }

  /**************************************
   * protected functions
   */

  /* mutator for parent and siblings */
  protected synchronized void setParent(AbstractNode parent){ this.parent = parent; }
  protected synchronized void setPrevious(AbstractNode leftSibling){ this.leftSibling = leftSibling; }
  protected synchronized void setNext(AbstractNode rightSibling){ this.rightSibling = rightSibling; }
  protected synchronized void setHead(AbstractNode head){ this.head = head; }
  protected synchronized void setTail(AbstractNode tail){ this.tail = tail; }
  protected void incChildCount(){ childCount++; }
  protected void decChildCount(){ childCount--; }
  
  

  protected synchronized AbstractNode getPrevious(){ return leftSibling; }
  protected synchronized AbstractNode getNext(){ return rightSibling; }
  protected synchronized AbstractNode getParent(){ return parent; }
  protected synchronized AbstractNode getHead(){ return head; }
  protected synchronized AbstractNode getTail(){ return tail; }
  protected synchronized int getChildCount(){ return childCount; }
  protected synchronized void setChildCount(int count){ childCount = count; }

  /*******************************************************************
   * Implementing insert, remove, and replace child node.
   */
  protected void insertBefore(AbstractNode newChild, AbstractNode refChild)
       throws NotMyChildException
  {
    NodeEnumerator e = null;

    if( newChild == null ) return;

    newChild.setParent( this );

    if( refChild == null ){ 
      insertAtEnd( newChild );
    }
    else if( refChild.getParentNode() != this ){
      String err = ("The reference child is not mine.");
      throw new NotMyChildException(err);
    }else{
      doInsertBefore( newChild, refChild );
    }
  }

  /**
   * If the children list is previously empty, make head and tail refer to new child
   * otherwise, append to end.
   */
  protected void insertAtEnd( AbstractNode newChild ){
    if( !hasChildren() ){ 
      newChild.setPrevious( null );
      newChild.setNext( null );
      
      setHead( newChild );
      setTail( newChild );
      incChildCount();
    }
    else append( (AbstractNode)newChild );
  }

  /**
   * Append child to end.  Move tail
   */
  protected void append( AbstractNode newChild ){
    //Report.debug("Appending...");
    incChildCount();
    AbstractNode last = getTail();
    
    newChild.setPrevious( last );
    newChild.setNext( last.getNext() );
    
    last.setNext( newChild );
    
    setTail( newChild );
  }

  /**
   * Inserting child at start. Move head
   */
  protected void doInsertAtStart(AbstractNode newChild){
    //Report.debug(this, "doInsertAtStart");
    if( newChild == null || getHead() == null ) return;

    incChildCount();
    newChild.setPrevious( null );
    newChild.setNext( head );

    getHead().setPrevious( newChild );

    setHead( newChild );
  }

  
  /**
   * Normal case insert somewhere in the middle
   */
  protected void doInsertBefore(AbstractNode newChild, AbstractNode refChild){
    if( newChild == null || refChild == null ) return;
    
    if( refChild == getHead() ){
      doInsertAtStart(newChild);      
      return;
    }
    
    incChildCount();
    AbstractNode temp = refChild.getPrevious();
    
    newChild.setPrevious( temp );
    newChild.setNext( refChild );
    
    refChild.setPrevious( newChild );
    
    temp.setNext( newChild );
  }


  /**
   * remove a child
   */
  protected synchronized Node removeChild( AbstractNode p )
       throws NotMyChildException 
  {
    if( p == null ) return null;
    
    if( p.getParentNode() != this )
      throw new NotMyChildException("The child is not mime.");

    if( getHead() == getTail() && hasChildren() ){ // only one child
      //Report.debug("Removing only one child...");
      setHead( null );
      setTail( null );
    }else if( getTail() == p ){// remove the last child, should adjust header's next reference
      //Report.debug("Removing last child...");
      p.getPrevious().setNext( p.getNext() );
      setTail( p.getPrevious() ); 
    }else if( getHead() == p ){ // the first item, but more than one child
      //Report.debug("Removing first child...");
      p.getNext().setPrevious( null );
      setHead( p.getNext() );
    }else{
      //Report.debug("Removing somewhere in the middle...");
      p.getPrevious().setNext( p.getNext() );
      p.getNext().setPrevious( p.getPrevious() );
    }
    p.setPrevious( null );
    p.setNext( null );
    p.setParent( null );

    decChildCount();
    return p;
  }

  /**
   * replace a child
   */
  protected Node replaceChild(AbstractNode oldChild, AbstractNode newChild)
       throws NotMyChildException
  {
    //Report.debug(this, "do replace child...");
    if( oldChild == null || newChild == null ) return null;
    
    if( oldChild.getParentNode() != this )
      throw new NotMyChildException("The old child is not mime.");
    
    AbstractNode previous = oldChild.getPrevious();
    AbstractNode next = oldChild.getNext();
    
    newChild.setPrevious( previous );
    newChild.setNext( next );
    newChild.setParent( this );

    if ( getHead() == getTail() && hasChildren() ){ // only one item
      //Report.debug("Replacing only one item...");
      setHead( newChild );
      setTail( newChild );
    }else if( oldChild == getHead() ){ // sub the first guy, adjust header
      //Report.debug("Replacing the first child...");
      next.setPrevious( newChild );
      setHead( newChild );
    }else if ( oldChild == getTail() ){ // replacing the last item
      //Report.debug("Replacing the last child...");
      previous.setNext( newChild );
      setTail( newChild );
    }else{
      //Report.debug("Replacing somewhere in the middle...");
      previous.setNext( newChild );
      previous.setPrevious( newChild );
    }

    oldChild.setPrevious (null );
    oldChild.setNext ( null );
    oldChild.setParent( null );

    //Report.debug("Leaving replaceChild.");
    return oldChild;
  }

  /**
   * Use to implement deep copy.
   * For foreign node, no cloning of children will be made.
   * 
   */
  protected void copyChildren(Node nodeB){
    //Report.debug("Copying children...");
    AbstractNode child = null;
    Node elem = null;

    if( nodeB.hasChildren() ){
      NodeEnumerator enum = nodeB.getChildren().getEnumerator();

      elem = enum.getFirst();
	  
      while( elem != null ) {

	if( elem instanceof AbstractNode ){
	  child = (AbstractNode)elem;
	  AbstractNode clone = (AbstractNode)child.clone();
	  try{
	    insertBefore( clone, null );
	  }catch(NotMyChildException e){
	    Report.debug(e.toString());
	  }
	}

	//copyChildren( clone, child );
	elem = enum.getNext();
      }
    }

    
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
   * Reference to first child
   */
  protected AbstractNode head;

  /**
   * Reference to last child
   */
  protected AbstractNode tail;

  /**
   * How many children do I have
   */
  protected int childCount = 0;
}




