////// CursorStack.java: A linked-list stack of current nodes.
//	$Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


package crc.dps.util;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Element;
import crc.dom.Attribute;

import crc.dps.*;
import crc.dps.active.*;

/**
 * A stack frame for a linked-list stack of current nodes. 
 *	It is designed to be used for saving state in a Cursor that is
 *	not operating on a real parse tree.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * 
 * @see crc.dps.Cursor
 */

public class CursorStack implements Cursor {

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

  protected boolean retainTree 	 = false;
  protected boolean atFirst	 = false;
  protected boolean atLast	 = false;
  protected boolean sawChildren  = false;

  protected CursorStack stack = null;

  /************************************************************************
  ** Construction and Copying:
  ************************************************************************/

  protected void initialize() {	/* override if necessary */ }

  protected void copy(CursorStack old) {
    depth 	= old.depth;
    node 	= old.node;
    action 	= old.action;
    element 	= old.element;
    tagName 	= old.tagName;
    active 	= old.active;
    retainTree 	= old.retainTree;
    atFirst 	= old.atFirst;
    atLast 	= old.atLast;
    stack 	= old.stack;
  }

  public CursorStack() {}

  public CursorStack(CursorStack prev) {
    stack = prev;
    depth = prev.depth + 1;
  }

  public CursorStack(CursorStack old, boolean bumpOldDepth) {
    copy(old);
    old.sawChildren = true;
    if (bumpOldDepth) old.depth++;
  }
    

  public CursorStack(int d, Node n, Action act, Element elt, String tag,
		     ActiveNode an, boolean retain, boolean first, 
		     boolean last, CursorStack old) {
    depth 	= d;
    node 	= n;
    action 	= act;
    element 	= elt;
    tagName 	= tag;
    active 	= an;
    retainTree 	= retain;
    atFirst 	= first;
    atLast	= last;
    stack 	= old;
  }

  /** Return the previous stack frame. 
   *	There are two ways to pop:  one is simply:<br>
   *	<code>stack = stack.getPrevious();</code><br>
   *	which works if the object executing it is <em>not</em> itself a
   *	CursorStack. 
   */
  public CursorStack getPrevious() 	{ return stack; }

  /** Replace with the contents of the previous stack frame. 
   *	There are two ways to pop; the other one uses <code>getPrevious</code>.
   *	Using <code>popInPlace</code> is only useful in subclasses of
   *	CursorStack, so it's protected.
   *
   * @see #getPrevious
   */
  protected boolean popInPlace() {
    if (stack == null) return false;
    copy(stack);
    sawChildren = true;
    return true;
  }

  /** Save the current state in a new stack frame. 
   *	This is marginally faster than the alternative,
   *	<code>stack = new CursorStack(this, true);</code> 
   *	the alternative generalizes better to subclasses.
   */
  protected void pushInPlace() { 
    stack = new CursorStack(depth, node, action, element, tagName, active, 
			    retainTree, atFirst, atLast, stack);
    sawChildren = false;
    depth++;
  }


  /************************************************************************
  ** State Accessors:
  ************************************************************************/

  public final Node       getNode() 	{ 
    if (node == null) initialize();
    return node;
  }
  public final Element    getElement()	{  
    if (node == null) initialize();
    return element; }
  public final ActiveNode getActive() 	{  
    if (node == null) initialize();
    return active; }
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
    if (active != null) action = active.getAction();

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
    element = active.asElement();
    tagName = aTagName;
  }



  /************************************************************************
  ** Information:
  ************************************************************************/

  public Syntax getSyntax() {
    return (active == null)? null : active.getSyntax();
  }

  public Action getAction() 	{ return action; }

  public boolean atTop() 	{ return depth == 0; }
  public boolean atFirst() 	{ return atFirst; }

  /** This will have to be overridden if the tree is being built on the fly. */
  public boolean hasAttributes() {
    if (element == null) return false;
    crc.dom.AttributeList atts = element.getAttributes();
    return (atts != null) && (atts.getLength() > 0);
  }

  /** This should be overridden to if more information is available. */
  public boolean hasActiveAttributes() {
    return hasAttributes();
  }

  /** This will have to be overridden if the tree is being built on the fly. */
  protected boolean hasChildren() {
    return node.hasChildren();
  }

  /** This should be overridden to if more information is available. */
  public boolean hasActiveChildren() {
    return hasChildren();
  }

  public String getTagName(int level) {
    if (level > depth) return null;
    else if (level == depth) return tagName;
    else if (stack == null) return null;
    else return stack.getTagName(level);
  }

  public Node getNode(int level) {
    if (level > depth) return null;
    else if (level == depth) return node;
    else if (stack == null) return null;
    else return stack.getNode(level);
  }

  public boolean insideElement(String tag, boolean ignoreCase) {
    for (CursorStack frame = this; frame != null; frame = frame.stack) {
      if (frame.tagName == null) continue;
      if (ignoreCase && tag.equalsIgnoreCase(frame.tagName)) return true;
      else if (!ignoreCase && tag.equals(frame.tagName)) return true;
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
    popInPlace();
    atFirst = false;
    atLast = false;
    sawChildren = true;
    return node;
  }

  public Element toParentElement() {
    if (atTop()) return null;
    popInPlace();
    atFirst = false;
    atLast = false;
    sawChildren = true;
    return element;
  }

}
