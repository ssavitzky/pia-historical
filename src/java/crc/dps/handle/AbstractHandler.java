////// AbstractHandler.java: Node Handler abstract base class
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


package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;

import java.util.Enumeration;

/**
 * An abstract base class for a Node Handler. <p>
 *
 *	This implementation is also an Element, which ensures that 
 *	handlers can easily be stored in and retrieved from XML documents. 
 *	Note that handlers are normally contained in Tagsets, and that
 *	BasicTagset is also an Element.
 *	<p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Context
 * @see crc.dps.Tagset
 * @see crc.dps.BasicTagset
 * @see crc.dps.Input 
 * @see crc.dom.Node
 */

public abstract class AbstractHandler extends ParseTreeGeneric
implements Handler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** The default action is to return the code that tells the Processor
   *	to process the node in the ``usual'' way.
   */
  public int actionCode(Input in, Processor p) {
    return (in.hasActiveChildren() || in.hasActiveAttributes()
	    || in.getNode().getNodeType() == NodeType.ENTITY)
      ? Action.EXPAND_NODE: Action.COPY_NODE;
  }

  /** This sort of action has no choice but to do the whole job.
   */
  public void action(Input in, Context aContext, Output out) {
    Node n = in.getNode();
    if ((in.hasActiveChildren() || in.hasActiveAttributes()
	 || n.getNodeType() == NodeType.ENTITY)) {
      aContext.subProcess(in, out).expandCurrentNode();
    } else {
      Copy.copyNode(n, in, out);
    }
  }

  public void getValue(Node aNode, Context aContext, Output out) {
    out.putNode(aNode);
  }

  public void getValue(String aName, Node aNode, Context aContext, Output out) {
  }



  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/


  /** If the handler corresponds to an Element, this determines its syntax.
   */
  protected int syntaxCode = 0;

  /** What the Handler knows about a Token's syntax without looking at it.
   *
   * @see crc.dps.Syntax
   */
  public int getSyntaxCode() { return syntaxCode; }

  /** Set what the Handler knows about a Token's syntax.
   *
   * @see crc.dps.Syntax
   */
  public void setSyntaxCode(int syntax) {
    syntaxCode = syntax;
  }
  
  /** Called to determine whether the given Node (for which this is
   *	the Handler) is an empty element, or whether content is expected.
   *	It is assumed that <code>this</code> is the result of the Tagset
   *	method <code>handlerForTag</code>.
   *
   * @param t the Token for which this is the handler, and for which the
   *	ssyntax is being checked.
   * @return <code>true</code> if the Token is an empty Element.
   * @see crc.dps.Tagset
   */
  public boolean isEmptyElement(Node n) {
    if (syntaxCode != 0) return (syntaxCode & Syntax.EMPTY) != 0;
    else return false;		// === ought to look at node here.
  }

  /** Called to construct a node for the given handler. 
   *
   *	Internally calls getActionForNode if necessary.  
   *	May perform additional dispatching on <code>name</code> 
   *	or <code>data</code>
   *
   * @param nodeType the node type
   * @param name an optional node name
   * @param data optional string data
   * @return a new ActiveNode having <code>this</code> as its Syntax. 
   */
  public ActiveNode createNode(int nodeType, String name, String data) {
    ActiveNode n = Create.createActiveNode(nodeType, name, data);
    n.setHandler(this);
    n.setAction(getActionForNode(n));
    return n;
  }

  /** Called to construct a node for the given handler. 
   *
   *	Internally calls getActionForNode if necessary.  
   *	May perform additional dispatching on <code>name</code> 
   *	or <code>data</code>
   *
   * @param nodeType the node type
   * @param name an optional node name
   * @param value optional value
   * @return a new ActiveNode having <code>this</code> as its Syntax. 
   */
  public ActiveNode createNode(int nodeType, String name, NodeList value) {
    ActiveNode n = Create.createActiveNode(nodeType, name, value);
    n.setHandler(this);
    n.setAction(getActionForNode(n));
    return n;
  }

  /** Called to construct an element for the given handler. 
   *
   *	Internally calls getActionForNode if necessary.
   *	May perform additional dispatching on <code>tagname</code> or
   *	<code>attributes</code>.
   *
   * @param tagname the Element's tag name.
   * @param attributes the Element's attributes.
   * @param hasEmptyDelim the XML empty-node delimiter is present.
   * @return a new ActiveElement having <code>this</code> as its Syntax. 
   */
  public ActiveElement createElement(String tagname, AttributeList attributes,
				     boolean hasEmptyDelim) {
    ActiveElement e = new ParseTreeElement(tagname, attributes, this);
    if (hasEmptyDelim) e.setHasEmptyDelimiter(hasEmptyDelim);
    e.setIsEmptyElement(hasEmptyDelim || e.getSyntax().isEmptyElement(e));
    e.setAction(getActionForNode(e));
    return e;
  }

  /** Called to determine the correct Action for a given Token.
   *	The default action is to return <code>this</code>, but it is
   *	possible to do additional dispatching based on the Node's 
   *	attributes or other information.
   */
  public Action getActionForNode(ActiveNode n) {
    return this;
  }

  /** Called from <code>getActionForNode</code> to determine whether
   *	dispatching should be done.  It returns true if the given Element
   *	has the given <code>name</code> as either an attribute name or
   *	a period-separated suffix of its tagname.
   */
  public boolean dispatch(ActiveElement e, String name) {
    return (e.getAttribute(name) != null)
      || e.getTagName().endsWith("."+name);
  }

  /** If <code>true</code>, the content is expanded.
   *	The default is to return <code>true</code> -- content is expanded.
   */
  public boolean expandContent() { return true; }

  /** If <code>true</code>, Element tags are recognized in content.
   *	The default is to return <code>true</code>.
   */
  public boolean parseElementsInContent() { return true; }

  /** If <code>true</code>, Entity references are recognized in content.
   *	The default is to return <code>true</code>.
   */
  public boolean parseEntitiesInContent() { return true; }

  /** Return <code>true</code> if Text nodes are permitted in the content.
   */
  public boolean mayContainText() { return true; }

  /** Return <code>true</code> if paragraph elements are permitted in the
   *	content.  If this is <code>true</code> and <code>mayContainText</code>
   *	is false, whitespace is made ignorable and non-whitespace is 
   *	commented out.
   */
  public boolean mayContainParagraphs() { return true; }

  /** Return true if this kind of token implicitly ends the given one. 
   *	This is not as powerful a test as using the DTD, but it will work
   *	in most cases and permits a simpler parser.
   */
  public boolean implicitlyEnds(String tag) { return false; }

  /** Return true if this kind of token is a potential child of the given one.
   */
  public boolean isChildOf(String tag) { return true; }

  /************************************************************************
  ** Presentation Operations:
  ************************************************************************/

  /** Converts the Node to a String. 
   *	Note that a Node is quite capable of doing this using the 
   *	standard defaults; passing it off to the Handler means that
   *	we can give the same Document different physical representations
   *	if necessary.
   */
  public String convertToString(ActiveNode n) {
    return n.startString() + n.contentString() + n.endString();
  }

  /** Converts the Node to a String. 
   *	Note that a Node is quite capable of doing this using the 
   *	standard defaults; passing it off to the Handler means that
   *	we can give the same Document different physical representations
   *	if necessary.
   */
  public String convertToString(ActiveNode n, int syntax) {
    return n.startString() + n.contentString() + n.endString();
  }

  /************************************************************************
  ** Convenience Functions:
  ************************************************************************/

  /** Convenience function: return a NodeList. */
  protected void putList(Output out, NodeList nl) {
    if (nl != null) Copy.copyNodes(nl, out);
  }

  /** Convenience function: return a NodeList with optional separator. */
  protected void putList(Output out, NodeList nl, String sep) {
    if (nl == null) return;
    if (sep == null || "".equals(sep)) {
      Copy.copyNodes(nl, out);
    } else {
      long len = nl.getLength();
      for (long i = 0; i < len; i++) {
	try { out.putNode(nl.item(i)); } catch (Exception e) {}
	if (i < (len-1)) {
	  out.putNode(new ParseTreeText(sep));
	}
      }
    }
  }

  /** Convenience function: return an enumeration. */
  protected void putEnum(Output out, java.util.Enumeration enum) {
    while (enum.hasMoreElements()) {
      out.putNode((Node)enum.nextElement());
    }
  }

  /** Convenience function: return a string. */
  protected void putText(Output out, Context cxt, String s) {
    out.putNode(newText(cxt, s));
  }

  /** Convenience function: create a new Element. 
   *	Unlike createElement, this makes no assumptions about the handler,
   *	so we have to go through the Tagset.
   */
  protected ActiveElement newElement(Context cxt, String tag) {
    Tagset ts = cxt.getTopContext().getTagset();
    return ts.createActiveElement(tag, null, false);
  }

  /** Convenience function: create a Text node.
   *	We don't know anything about the document, so we have to go through
   *	the Tagset to create it.
   */
  protected ActiveText newText(Context cxt, String data) {
    Tagset ts = cxt.getTopContext().getTagset();
    return ts.createActiveText(data);
  }

  /************************************************************************
  ** Messaging Operations:
  ************************************************************************/

  /** per-document notification flag. */
  protected boolean notified = false;

  /** Notify the user about a problem. 
   *	Such notice is only given once per tag.
   */
  protected void notify(Input in, Context cxt, String message) {
    if (!notified) {
      cxt.message(0, message + " in " + Log.node(in.getNode()), 0, true);
      notified = true;
    }
  }

  protected void unimplemented (Input in, Context cxt) {
    // Kludge: the space here ^ keeps grep from noticing.
    cxt.message(-1, "Unimplemented handler " + getClass().getName()
		+ " in " + Log.node(in.getNode()),
		0, true);
  }

  protected void unimplemented (Input in, Context cxt, String s) {
    // Kludge: the space here ^ keeps grep from noticing.
    cxt.message(-1, "Unimplemented feature: " + s + " " 
		+ getClass().getName() + " in " + Log.node(in.getNode()),
		0, true);
  }

  protected void reportError(Input in, Context cxt, String msg) {
    cxt.message(-2, msg + " in " + Log.node(in.getNode()), 0, true);
  }
}
