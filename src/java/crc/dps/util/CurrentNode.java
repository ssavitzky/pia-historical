////// CurrentNode.java: base class for objects with a current node.
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.aux;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Element;
import crc.dom.Attribute;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.aux.Copy;

/**
 * The base class for objects with a current node.
 *
 *	This is the basic implementation of Cursor, and the base class
 *	for most implementations of its extensions, Input and Output. <p>
 *
 *	A sufficient selection of protected navigation functions is provided
 *	to implement most subclasses (including implementations of Input, 
 *	Output, TreeIterator, and so on) efficiently, provided they operate
 *	on complete trees.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * 
 * @see crc.dps.Cursor
 */

public class CurrentNode implements Cursor {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected int depth = 0;

  /** The current Node. */
  protected Node node;

  /** The Action handler associated with the current Node. */
  protected Action action;

  /** If <code>node</code> is an element, this is equal to it. 
   *	Otherwise it's <code>null</code>. 
   */
  protected Element element;
  protected String tagName;

  /** If <code>node</code> is an active node, this is equal to it. 
   *	Otherwise it's <code>null</code>. 
   */ 
  protected ActiveNode active;

  protected boolean retainTree = false;
  protected boolean atFirst = false;

  /************************************************************************
  ** State Accessors:
  ************************************************************************/

  public final Node       getNode() 	{ return node; }
  public final Element    getElement()	{ return element; }
  public final ActiveNode getActive() 	{ return active; }
  public final int	  getDepth() 	{ return depth; }
  public final String 	  getTagName() 	{ return tagName; }

  /** === could implement this with another state variable... */
  public Attribute getAttribute() {
    if (active != null) return active.asAttribute();
    else if (node.getNodeType() == NodeType.ATTRIBUTE) 
      return (Attribute)node;
    else
      return null;
  }

  /** Set the current node.  Set <code>active</code> and <code>element</code>
   *	if applicable. */
  protected void  setNode(Node aNode) {
    node   = aNode;
    active = (node instanceof ActiveNode)? (ActiveNode)node : null;
    action = (active == null)? null : active.getAction();

    if (node.getNodeType() == NodeType.ELEMENT) {
      element = (active == null)? (Element) aNode : active.asElement();
      tagName = element.getTagName();
    } else {
      element = null;
      tagName = null;
    }
  }

  /** Set the current node to an element */
  protected void setNode(Element anElement, String aTagName) {
    node   = anElement;
    element= anElement;
    active = (node instanceof ActiveNode)? (ActiveNode)node : null;
    action = (active == null)? null : active.getAction();
    tagName = (aTagName == null)? element.getTagName() : aTagName;
  }

  protected void setNode(ActiveNode aNode) {
    node   = aNode;
    active = aNode;
    action = (active == null)? null : active.getAction();

    if (node != null && node.getNodeType() == NodeType.ELEMENT) {
      element = active.asElement();
      tagName = element.getTagName();
    } else {
      element = null;
      tagName = null;
    }
  }

  protected void setNode(ActiveElement aNode, String aTagName) {
    node   = aNode;
    active = aNode;
    action = active.getAction();
    element = aNode;
    tagName = aTagName;
  }



  /************************************************************************
  ** Information:
  ************************************************************************/

  public Syntax getSyntax() {
    return (active == null)? null : active.getSyntax();
  }

  public boolean atTop() 	{ return depth == 0; }
  public boolean atFirst() 	{ return atFirst; }

  /** This will have to be overridden if the tree is being built on the fly. */
  protected boolean atLast() {
    return node.getNextSibling() == null;
  }

  /** This will have to be overridden if the tree is being built on the fly. */
  protected boolean hasChildren() {
    return node.hasChildren();
  }

  /** This should be overridden to if more information is available. */
  public boolean hasActiveChildren() {
    return hasChildren();
  }

  /** This may have to be overridden if the tree is being built on the fly. */
  public boolean hasAttributes() {
    if (element == null) return false;
    crc.dom.AttributeList atts = element.getAttributes();
    return (atts != null) && (atts.getLength() > 0);
  }

  /** This should be overridden to if more information is available. */
  public boolean hasActiveAttributes() {
    return hasAttributes();
  }

  public String getTagName(int level) {
    Node n = getNode(level);
    return (n == null || !(n instanceof Element))
      ? null : ((Element)n).getTagName();
  }

  public Node getNode(int level) {
    if (level > depth || level < 0) return null;
    Node n = node;
    int d = depth;
    for ( ; n != null ; n = n.getParentNode(), d--) if (d == level) return n;
    return null;
  }

  public boolean insideElement(String tag, boolean ignoreCase) {
    Node n = node;
    int d = depth;
    String tn = null;
    for ( ; n != null && d >= 0 ; n = n.getParentNode(), d--) {
      if (n instanceof Element) {
	tn = ((Element)n).getTagName();
	if (ignoreCase && tag.equalsIgnoreCase(tn)) return true;
	else if (!ignoreCase && tag.equals(tn)) return true;
      }
    }
    return false;
  }

  /************************************************************************
  ** Navigation Operations:
  ************************************************************************/

  /** Returns the parent of the current Node.
   *	After calling <code>toParent</code>, <code>toNextNode</code> will
   *	return the parent's next sibling.
   */
  public Node toParent() {
    if (atTop()) return null;
    Node p = node.getParentNode();
    if (p == null) return null;
    setNode(p);
    depth--;
    atFirst = false;
    return node;
  }

  public Element toParentElement() {
    if (atTop()) return null;
    Node p = node.getParentNode();
    if (p == null) return null;
    setNode(p);
    depth--;
    atFirst = false;
    return element;
  }

  /************************************************************************
  ** Input Operations:
  ************************************************************************/

  /** Returns the first child of the current Node. 
   *	A subsequent call on <code>toNextNode</code> will return the 
   *	second child, if any.
   *
   *	Must be overridden if the tree is being built on the fly.
   */
  protected Node toFirstChild() {
    Node n = node.getFirstChild();
    if (n == null) return null;
    descend();
    setNode(n);
    atFirst = true;
    return node;
  }

  /** Returns the next node from this source and makes it current.  
   *	May descend or ascend levels.  This can be detected by tracking
   *	the depth with <code>getDepth()</code>. <p>
   *
   * @return  <code>null</code> if and only if no more nodes are
   *	available from this source. 
   */
  public Node toNextNode() {
    Node n = node.getNextSibling(); // toNextNode bogus at this point ===
    if (n == null) return null;
    setNode(n);
    atFirst = false;
    return node;
  }

  /** Returns the next node at this level and makes it current.  
   *	May require traversing all of the (old) current node if its
   *	children have not yet been seen. <p>
   *
   * @return  <code>null</code> if and only if no more nodes are
   *	available at this level. 
   */
  protected Node toNextSibling() {
    Node n = node.getNextSibling();
    if (n == null) return null;
    setNode(n);
    atFirst = false;
    return node;
  }

  /************************************************************************
  ** Processing Operations:
  ************************************************************************/

  /** Returns the action, if known, for the current node. 
   */
  public Action getAction() { return action; }

  /** Ensures that all descendents of the current node will be appended to
   *	it as they are traversed.  
   */
  public void retainTree() { retainTree = true; }
  
  /** Ensures that all descendents of the current node have been seen
   *	and appended to it.  May be expensive.  
   */
  protected Node getTree() { return node; }


  /************************************************************************
  ** Output Operations:
  **
  **	These may also be used in an Input to build a parse tree as a
  **	side effect while parsing.
  **
  ************************************************************************/

  /** Adds <code>aNode</code> and its children to the document under 
   *	construction as a new child of the current node.  The new node
   *	is copied unless it has no parent and has a type compatible with
   *	the document under construction.  <p>
   *
   *	If the current node is an Element and <code>aNode</code> is an
   *	Attribute, it is added to the attribute list of the curren node.
   */
  protected void putNode(Node aNode) {
    Node p = aNode.getParentNode();
    if (p == node) {
      if (p != null) return;	// already a child.  Nothing to do.
      else setNode(aNode);	// no current node: make it current
    } else if (p != null) {	// someone else's child: deep copy.
      startNode(aNode);
      for (Node n = aNode.getFirstChild();
	   n != null;
	   n = aNode.getNextSibling()) 
	putNode(n);
      endNode();
    } else {
      Copy.appendNode(aNode, node);
    }
  }

  /** Adds <code>aNode</code> to the document under construction, and
   *	makes it the current node.
   */
  protected void startNode(Node aNode) {
      Node p = aNode.getParentNode();
      if (p == node) {		// already a child.  descend.
	if (p != null) descend();
	setNode(aNode);
	return;
      }
      if (p != null || aNode.hasChildren()) {
	p = shallowCopy(aNode);
      }
      Copy.appendNode(aNode, node);
      descend();
      setNode(aNode);
  }

  /** Ends the current Node and makes its parent current.
   * @return <code>false</code> if the current Node has no parent.
   */
  protected boolean endNode() {
    return toParent() != null;
  }

  /** Adds <code>anElement</code> to the document under construction, and
   *	makes it the current node.  An element may be ended with either
   *	<code>endElement</code> or <code>endNode</code>.  */
  protected void startElement(Element anElement) {
    Element e = shallowCopyElt(anElement);
    Copy.appendNode(anElement, node);
    descend();
    setNode(anElement, null);
  }

  /** Ends the current Element.  The end tag may be optional.  
   *	<code>endElement(true)</code> may be used to end an empty element. 
   */
  public boolean endElement(boolean optional) {
    // === set optional end-tag flag if the element has one. ===
    return toParent() != null;
  }

  /** Perform any necessary actions before descending a level. 
   *	Normally this just increments <code>depth</code>
   */
  protected void descend() {
    depth++;
  }


  /** === Subclasses that need it MUST override shallowCopy === */
  protected Node shallowCopy(Node aNode) {
    return null;		// === should throw a runtime exception ===
  }

  /** === Subclasses that need it MUST override shallowCopyElt === */
  protected Element shallowCopyElt(Element anElement) {
    return null;		// === should throw a runtime exception ===
  }

  /** === Subclasses that need it MUST override createAttr === */
  protected Attribute createAttr(String name, NodeList value) {
    return null;		// === should throw a runtime exception ===
  }
}
