////// Copy.java: Utilities for Copying nodes.
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.aux;

import crc.dom.Node;
import crc.dom.Element;
import crc.dom.NodeList;
import crc.dom.NodeEnumerator;
import crc.dom.ArrayNodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.DOMFactory;
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
 * @see crc.dps.aux.Expand
 * @see crc.dps.aux.Process
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
  public static void copyNode(Node n, Input in, Output out) {
    if (n == null) n = in.getNode();
    if (in.hasChildren()) {
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
  public static void copyChildren(Input in, Output out) {
    for (Node n = in.toFirstChild(); n != null; n = in.toNextSibling()) {
      copyNode(n, in, out);
    }
    in.toParent();
  }

  /** Copy the content of a NodeList to an Output.
   */
  public static void copyNodes(NodeList aNodeList, Output out) {
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

  /** Create a shallow copy of a given Node using a given DOMFactory. 
   */
  public static Node copyNode(Node node, DOMFactory f) {
    int nodeType = node.getNodeType();
    switch (nodeType) {
    case NodeType.ELEMENT: 
      crc.dom.Element e = (crc.dom.Element)node;
      return f.createElement(e.getTagName(), e.getAttributes());

    case NodeType.TEXT:
      crc.dom.Text t = (crc.dom.Text)node;
      crc.dom.Text nt = f.createTextNode(t.getData());
      nt.setIsIgnorableWhitespace(t.getIsIgnorableWhitespace());
      return nt;

    case NodeType.COMMENT: 
      crc.dom.Comment c = (crc.dom.Comment)node;
      return f.createComment(c.getData());

    case NodeType.PI:
      crc.dom.PI pi = (crc.dom.PI)node;
      return f.createPI(pi.getName(), pi.getData());

    case NodeType.ATTRIBUTE: 
      crc.dom.Attribute attr = (crc.dom.Attribute)node;
      return f.createAttribute(attr.getName(), attr.getValue());

    default: 
      return null;		// node.shallowCopy();
    }
  }

  /** Create a deep copy of a given Node using a given DOMFactory. 
   *	Recursively copy the children.
   */
  public static Node deepCopyNode(Node node, DOMFactory f) {
    Node newNode = copyNode(node, f);
    if (node.hasChildren()) {
      appendCopies(node.getChildren(), newNode, f);
    }
    return newNode;
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

  /** Append copies of the nodes in a NodeList to a given parent.
   *
   * @see #appendNode
   */
  public static void appendCopies(NodeList aNodeList, Node parent,
				  DOMFactory f) {
    if (aNodeList == null) return;
    NodeEnumerator e = aNodeList.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      appendNode(deepCopyNode(node, f), parent);
    }
  }

}
