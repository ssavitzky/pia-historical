////// Copy.java: Utilities for Copying nodes.
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
import crc.dom.Element;
import crc.dom.NodeList;
import crc.dom.NodeEnumerator;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Entity;

import crc.dps.NodeType;
import crc.dps.active.*;
import crc.dps.output.*;
import crc.dps.*;

/**
 * Node-copying utilities (static methods) for a Document Processor. 
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.util.Expand
 * @see crc.dps.util.Process
 */

public class Copy {

  /************************************************************************
  ** Copying to an Output
  ************************************************************************/

  /** Copy the current Node from the given Input to the given Output 
   *
   * === It should be possible to do this non-recursively by keeping track
   * === of depth.  Note that we're using the Input's stack for state.
   */
  public static final void copyNode(Node n, Input in, Output out) {
    if (n == null) n = in.getNode();
    if (in.hasChildren() && ! n.hasChildren()) {
      // Copy recursively only if the node hasn't been fully parsed yet.
      out.startNode(n);
      copyChildren(in, out);
      out.endNode();
    } else {
      out.putNode(n);
    }
  }

  /** Copy the children of the input's current Node
   *
   * === It should be possible to do this non-recursively by keeping track
   * === of depth.  Note that we're using the Input's stack for state.
   */
  public static final void copyChildren(Input in, Output out) {
    for (Node n = in.toFirstChild(); n != null; n = in.toNextSibling()) {
      copyNode(n, in, out);
    }
    in.toParent();
  }

  /** Copy nodes from an Input to an Output. */
  public static final void copyNodes(Input in, Output out) {
    for (Node n = in.getNode(); n != null; n = in.toNextSibling()) {
      copyNode(n, in, out);
    }
  }

  /** Copy the content of a NodeList to an Output.
   */
  public static void copyNodes(NodeList aNodeList, Output out) {
    if (aNodeList == null) return;
    NodeEnumerator e = aNodeList.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      out.putNode(node);
    }
  }

  /************************************************************************
  ** Copying from a Node to the return value:
  ************************************************************************/

  public static ActiveAttrList copyAttrs(AttributeList atts) {
    ToAttributeList dst = new ToAttributeList();
    copyNodes(atts, dst);
    return dst.getList();
  }

  public static ActiveNode copyNodeAsActive(Node node) {
    if (node instanceof ActiveNode) 
      return ((ActiveNode)node).shallowCopy();
    int nodeType = node.getNodeType();
    switch (nodeType) {
    case NodeType.ELEMENT: 
      crc.dom.Element e = (crc.dom.Element)node;
      return new ParseTreeElement(e.getTagName(), e.getAttributes());

    case NodeType.TEXT:
      crc.dom.Text t = (crc.dom.Text)node;
      return new ParseTreeText(t.getData(), t.getIsIgnorableWhitespace());

    case NodeType.COMMENT: 
      crc.dom.Comment c = (crc.dom.Comment)node;
      return new ParseTreeComment(c.getData());

    case NodeType.PI:
      crc.dom.PI pi = (crc.dom.PI)node;
      return new ParseTreePI(pi.getName(), pi.getData());

    case NodeType.ATTRIBUTE: 
      crc.dom.Attribute attr = (crc.dom.Attribute)node;
      return new ParseTreeAttribute(attr.getName(), attr.getValue());

    default: 
      return null;		// node.shallowCopy();
    }
  }

  /************************************************************************
  ** Appending (copying into children):
  ************************************************************************/


  /** Append a Node to a given parent.
   *	If the new Node is already a child of <code>parentNode</code>, nothing
   *	happens.  If its parent is non-<code>null</code> and
   *	<em>different</em> from the <code>parentNode</code>, it will be
   *	reparented.  If the Node's type is NODELIST, its children will be
   *	appended (spliced in). <p>
   *
   * @param aNode the node to be appended.
   * @param parentNode the node to be appended to.
   * @return <code>parentNode</code>.
   * @see crc.dps.NodeType */
  public static Node appendNode(Node aNode, Node parentNode) {
    // No node to append to: do nothing.
    if (parentNode == null) return null;
    // Current node is the parent: already taken care of.
    if (aNode.getParentNode() == parentNode) return parentNode;
    // A NodeList in disguise -- append its children:
    if (aNode.getNodeType() == NodeType.NODELIST) {
      if (aNode.hasChildren()) appendNodes(aNode.getChildren(), parentNode);
      return parentNode;
    }
    try {
      // Different parent: deep-copy it.
      if (aNode.getParentNode() != null) { 
	aNode = ((ActiveNode)aNode).deepCopy();
      }
      parentNode.insertBefore(aNode, null);
    } catch (crc.dom.NotMyChildException e) {
      System.err.println("Cloning failed: ");
      e.printStackTrace(System.err);
      // === not clear what to do here...  shouldn't happen. ===
    }
    return parentNode;
  }

  /** Append the nodes in a NodeList to a given parent.
   *
   * @return parentNode.
   * @see #appendNode
   */
  public static Node appendNodes(NodeList aNodeList, Node parentNode) {
    if (aNodeList == null) return parentNode;
    NodeEnumerator e = aNodeList.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      appendNode(node, parentNode);
    }
    return parentNode;
  }

  /** Append the nodes in a NodeList to a given Active parent.
   *
   * @return parentNode.
   * @see #appendNode
   */
  public static ActiveNode appendNodes(NodeList aNodeList,
				       ActiveNode parentNode) {
    if (aNodeList == null) return parentNode;
    NodeEnumerator e = aNodeList.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      parentNode.addChild((ActiveNode)node);
    }
    return parentNode;
  }

}
