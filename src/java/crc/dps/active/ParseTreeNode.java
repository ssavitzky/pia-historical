// ParseTreeNode.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dps.active;

import java.io.*;
import crc.dps.*;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.NodeEnumerator;
import crc.dom.NotMyChildException;
import crc.dom.DOMFactory;

/**
 * Abstract base class for all Nodes used to build parse trees in the
 *	Document Processing System.  <p>
 *
 *	Implements the Node interface of the w3c's Document Object Model, and
 *	the additional methods of the ActiveNode interface.  Blythely assumes
 *	that all nodes in the parse tree are subclasses of ParseTreeNode,
 *	and throws a runtime exception when this assumption is violated.  <p>
 *
 * @see crc.dom.Node
 * @see crc.dps.active.ActiveNode
 * @see crc.dps.active.NotActiveNodeException
 */
public abstract class ParseTreeNode implements ActiveNode, Serializable {

  public static int nodesCreated = 0;

  /************************************************************************
  ** Constructors:
  ************************************************************************/

  public ParseTreeNode() {
    parent = null;
    head   = null;
    tail   = null;
    next   = null;
    prev   = null;
    handler= null;
    action = null;
    nodesCreated ++;
  }

  public ParseTreeNode(Handler h) {
    parent = null;
    head   = null;
    tail   = null;
    next   = null;
    prev   = null;
    setHandler(h);
    nodesCreated ++;
  }

  public ParseTreeNode(ParseTreeNode n, boolean copyChildren) {
    handler = n.handler;
    action = n.action;
    if (copyChildren) copyChildren(n);
  }

  public ParseTreeNode(ActiveNode n, boolean copyChildren) {
    handler = (Handler)n.getSyntax();
    action  = n.getAction();
    if (copyChildren) copyChildren(n);
  }

  /************************************************************************
  ** Node interface:
  ************************************************************************/

  public abstract int getNodeType();

  /** Returns the parent of the given Node instance. If this node is the root
   * 	of the document object tree, null is returned.
   */
  public Node     getParentNode()	{ return getParent(); }

  /** Returns a NodeList object containing the children of this node. 
   *
   *	If there are no children, null is returned. The content of the
   *	returned NodeList is "live" in the sense that changes to the children
   *	of the Node object that it was created from will be immediately
   *	reflected in the set of Nodes the NodeList contains; it is not a
   *	static snapshot of the content of the Node. Similarly, changes made to
   *	the NodeList will be immediately reflected in the set of children of
   *	the Node that the NodeList was created from.
   */
  public NodeList getChildren()
  {
    return (getHead() == null) ? null : new ParseChildList( this ); 
  }

  /** Returns true if the node has any children, false if the node has no
   *	children at all. <p>
   *
   *	This method exists both for convenience as well as to allow
   *	implementations to be able to bypass object allocation, which may be
   *	required for implementing getChildren().
   */
  public boolean  hasChildren()  	{ return  getHead() != null; }

  public Node     getFirstChild()	{ return getHead(); }
  public Node     getPreviousSibling()	{ return getPrev(); }
  public Node     getNextSibling()	{ return getNext(); }

  /** Inserts a child node (newChildbefore the existing child node refChild. 
   *
   *	If refChild is null, insert newChild at the end of the list of
   *	children. If refChild is not a child of the Node that insertBefore is
   *	being invoked on, a NotMyChildException is thrown.
   */
  public synchronized void insertBefore(Node newChild, Node refChild)
       throws NotMyChildException 
  {
    ParseTreeNode nc = null;
    if( newChild == null ) return;

    if( refChild != null && (refChild.getParentNode() != this) )
      throw new NotMyChildException("refChild in insertBefore not mine.");
    else if( refChild != null && !(refChild instanceof ParseTreeNode) )
      throw new NotActiveNodeException("refChild in insertBefore not active.");
    else if( !(newChild instanceof ParseTreeNode) )
      throw new NotActiveNodeException("newChild in insertBefore not active.");
    else nc = (ParseTreeNode)newChild;

    doInsert(nc, (ParseTreeNode)refChild);
  }

  /** 
   * Replaces the child node oldChild with newChild in the set of children
   *	of the given node, and return the oldChild node. 
   *
   *	If oldChild was not already a child of the node that the replaceChild
   *	method is being invoked on, a NotMyChildException is thrown.
   */
  public synchronized Node replaceChild(Node oldChild, Node newChild)
       throws NotMyChildException
  {
    ParseTreeNode nc = null;
    if( newChild == null || oldChild == null ) return null;

    if( oldChild.getParentNode() != this )
      throw new NotMyChildException("oldChild in replaceChild is not mine.");
    else if( !(newChild instanceof ParseTreeNode) )
      throw new NotActiveNodeException("newChild in replaceChild not active.");
    else nc = (ParseTreeNode)newChild;

    return replaceChild((ParseTreeNode) oldChild, nc); 
  }

  /**
   * Removes the child node indicated by oldChild from the list of children and
   *	returns it.  <p>
   *
   *	If oldChild was not a child of the given node, a NotMyChildException is
   *	thrown. 
   */
  public synchronized Node removeChild(Node oldChild)
       throws NotMyChildException
  {
    if( oldChild == null ) return null;

    if( oldChild.getParentNode() != this )
      throw new NotMyChildException("oldChild in removeChild is not mine.");
    else
      return removeChild((ParseTreeNode)oldChild);
  }

  /************************************************************************
  ** ActiveNode interface:
  ************************************************************************/

  ////////////////////////////////////////////////////////////////////////
  // Navigation:

  public ActiveNode getPrevActive()	{ return prev; }
  public ActiveNode getNextActive()	{ return next; }
  public ActiveNode getActiveParent()	{ return parent; }
  public ActiveNode getFirstActive()	{ return head; }
  public ActiveNode getLastActive()	{ return tail; }

  ////////////////////////////////////////////////////////////////////////
  // Syntax:

  public Syntax getSyntax() 		{ return handler; }
  public Action getAction() 		{ return action; }
  public Handler getHandler() 		{ return handler; }
  public void setAction(Action newAction) { action = newAction; }

  public void setHandler(Handler newHandler) {
    handler = newHandler;
    action  = handler;
  }

  ////////////////////////////////////////////////////////////////////////
  // Conversion convenience functions:
  // 	At most one of the following conversion functions 
  // 	must be overridden to return <code>this</code>:

  public ActiveElement	 asElement() 	{ return null; }
  public ActiveText 	 asText()	{ return null; }
  public ActiveAttribute asAttribute() 	{ return null; }
  public ActiveEntity 	 asEntity() 	{ return null; }
  public ActiveDocument  asDocument() 	{ return null; }

  ////////////////////////////////////////////////////////////////////////
  // Copying:

 /** Return a shallow copy of this Token.  Attributes, if any, are
   *	copied, but children are not.
   */
  public abstract ActiveNode shallowCopy();

  /** Return a deep copy of this Token.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    ParseTreeNode node = (ParseTreeNode)shallowCopy();
    for (ParseTreeNode child = getHead();
	 child != null;
	 child = child.getNext()) {
      ParseTreeNode newChild = (ParseTreeNode)child.deepCopy();
      node.doInsert(newChild, null);
    }
    return node;
  }

  ////////////////////////////////////////////////////////////////////////
  // Construction:

  /** Append a new child.
   *	Can be more efficient than <code>insertBefore()</code>
   */
  public void addChild(ActiveNode newChild) {
    if (newChild == null) return;
    if (newChild.getParentNode() == this) return;
    if (newChild.getParentNode() != null) newChild = newChild.deepCopy();
    doInsert((ParseTreeNode)newChild, null);
  }

  ////////////////////////////////////////////////////////////////////////
  // Presentation:

  /** Return new node corresponding to this Token, made using the given 
   *	DOMFactory.  Children <em>are not</em> copied.
   *
   * === Worry about document, attribute, entity-ref ===
   */
  public abstract Node createNode(DOMFactory f);

  /**
   * Convert the node and its content to a String.
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
  
  /** Return the String equivalent of this node type's content or data.
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

  /************************************************************************
  ** Link Management:
  ************************************************************************/

  ////////////////////////////////////////////////////////////////////////
  // Access:

  protected final void setParent(ParseTreeNode parent){ this.parent = parent; }
  protected final void setPrev(ParseTreeNode prev){ this.prev = prev; }
  protected final void setNext(ParseTreeNode next){ this.next = next; }
  protected final void setHead(ParseTreeNode head){ this.head = head; }
  protected final void setTail(ParseTreeNode tail){ this.tail = tail; }
  
  protected final ParseTreeNode getPrev(){ return prev; }
  protected final ParseTreeNode getNext(){ return next; }
  protected final ParseTreeNode getParent(){ return parent; }
  protected final ParseTreeNode getHead(){ return head; }
  protected final ParseTreeNode getTail(){ return tail; }

  protected final int  getChildCount(){ return childCount; }
  protected final void setChildCount(int count){ childCount = count; }
  protected final void incChildCount(){ childCount++; }
  protected final void decChildCount(){ childCount--; }

  ////////////////////////////////////////////////////////////////////////
  // List Processing:

  /** Simple implementation of insertBefore assuming all synchronization
   *	and error-checking have been done.  Overriding this method will
   *	take care of <em>all</em> operations that add children.
   */
  protected void doInsert(ParseTreeNode newChild, ParseTreeNode refChild)
  {
    if (refChild == null) 		doInsertAtEnd(newChild); 
    else if (refChild == getHead())	doInsertAtStart(newChild);
    else				doInsertBefore( newChild, refChild );
  }

  /** Insert at the end of the list.  Handles empty list.
   */
  protected final void doInsertAtEnd(ParseTreeNode newChild) {
    incChildCount();
    newChild.setParent( this );
    if( !hasChildren() ){ 
      newChild.setPrev( null );
      newChild.setNext( null );
      setHead( newChild );
      setTail( newChild );
    } else {
      ParseTreeNode last = getTail();
    
      newChild.setPrev( last );
      newChild.setNext( last.getNext() );
    
      last.setNext( newChild );
    
      setTail( newChild );
    }
  }


  /**
   * Insert a new child at the start.  Handles empty list.
   */
  protected final void doInsertAtStart(ParseTreeNode newChild){
    incChildCount();
    newChild.setParent( this );
    newChild.setPrev( null );
    newChild.setNext( head );

    if (getHead() != null) {
      getHead().setPrev( newChild );
    }

    setHead( newChild );
    if (getTail() == null) setTail(newChild);
  }

  
  /**
   * Normal case insert somewhere in the middle
   */
  protected final void doInsertBefore(ParseTreeNode newChild,
				      ParseTreeNode refChild) {
    incChildCount();
    newChild.setParent( this );

    ParseTreeNode temp = refChild.getPrev();
    
    newChild.setPrev( temp );
    newChild.setNext( refChild );
    
    refChild.setPrev( newChild );
    
    temp.setNext( newChild );
  }


  /**
   * remove a child, assuming that all error checking has been done.
   */
  protected synchronized Node removeChild( ParseTreeNode p )
  {
    if( p == null ) return null;
    
    if( getHead() == getTail() && hasChildren() ){ // only one child
      //Report.debug("Removing only one child...");
      setHead( null );
      setTail( null );
    }else if( getTail() == p ){
      // remove the last child, should adjust header's next reference
      //Report.debug("Removing last child...");
      p.getPrev().setNext( p.getNext() );
      setTail( p.getPrev() ); 
    }else if( getHead() == p ){ // the first item, but more than one child
      //Report.debug("Removing first child...");
      p.getNext().setPrev( null );
      setHead( p.getNext() );
    }else{
      //Report.debug("Removing somewhere in the middle...");
      p.getPrev().setNext( p.getNext() );
      p.getNext().setPrev( p.getPrev() );
    }
    p.setPrev( null );
    p.setNext( null );
    p.setParent( null );

    decChildCount();
    return p;
  }

  /**
   * replace a child, assuming that all error checking has been done.
   */
  protected Node replaceChild(ParseTreeNode oldChild, ParseTreeNode newChild)
  {
    //Report.debug(this, "do replace child...");
    if( oldChild == null || newChild == null ) return null;
    
    ParseTreeNode previous = oldChild.getPrev();
    ParseTreeNode next = oldChild.getNext();
    
    newChild.setPrev( previous );
    newChild.setNext( next );
    newChild.setParent( this );

    if ( getHead() == getTail() && hasChildren() ){ // only one item
      //Report.debug("Replacing only one item...");
      setHead( newChild );
      setTail( newChild );
    }else if( oldChild == getHead() ){ // sub the first guy, adjust header
      //Report.debug("Replacing the first child...");
      next.setPrev( newChild );
      setHead( newChild );
    }else if ( oldChild == getTail() ){ // replacing the last item
      //Report.debug("Replacing the last child...");
      previous.setNext( newChild );
      setTail( newChild );
    }else{
      //Report.debug("Replacing somewhere in the middle...");
      previous.setNext( newChild );
      previous.setPrev( newChild );
    }

    oldChild.setPrev (null );
    oldChild.setNext ( null );
    oldChild.setParent( null );

    //Report.debug("Leaving replaceChild.");
    return oldChild;
  }

  /**
   * Copy all the children of a Node. 
   * 
   */
  protected void copyChildren(ParseTreeNode nodeB){
    if (nodeB == null) return;
    ParseTreeNode elem = null;
    for (elem = nodeB.getHead(); elem != null; elem = elem.getNext()) {
      ParseTreeNode clone = (ParseTreeNode)elem.deepCopy();
      doInsert( clone, null );
    }
  }

  protected void copyChildren(ActiveNode nodeB) {
    if (nodeB == null) return;
    ActiveNode elem = null;
    for (elem = nodeB.getFirstActive();
	 elem != null;
	 elem = elem.getNextActive()) {
      ParseTreeNode clone = (ParseTreeNode)elem.deepCopy();
      doInsert( clone, null );
    }
  }


  /************************************************************************
  ** ActiveNode state:
  ************************************************************************/

  protected Handler handler = null;
  protected Action  action  = null;

  /************************************************************************
  ** Document Tree Links:
  ************************************************************************/

  /**
   * The parent 
   */
  protected ParseTreeNode parent;

  /**
   * Left sibling
   */
  protected ParseTreeNode prev;

  /**
   * Right sibling 
   */
  protected ParseTreeNode next;

  /**
   * Reference to first child
   */
  protected ParseTreeNode head;

  /**
   * Reference to last child
   */
  protected ParseTreeNode tail;

  /**
   * How many children do I have
   */
  protected int childCount = 0;
}
