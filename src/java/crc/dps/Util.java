////// Util.java: Document Processor utilities
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;

import crc.dom.Node;
import crc.dom.Element;
import crc.dom.NodeList;
import crc.dom.ArrayNodeList;
import crc.dom.Attribute;
import crc.dom.DOMFactory;

/**
 * Utilities (static methods) for a Document Processor. 
 *
 *	This class contains the static methods that apply generally to
 *	objects that implement interfaces in the Document Processor
 *	package.  This way we avoid cluttering up implementations with
 *	duplicate methods.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Token
 * @see crc.dps.Input
 */

public class Util {

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
      // Different parent: re-parent it.
      if (aNode.getParentNode() != null) { 
	aNode.getParentNode().removeChild(aNode);
      }
      parentNode.insertBefore(aNode, null);
    } catch (crc.dom.NotMyChildException e) {
      // === not clear what to do here...  shouldn't happen. ===
    }
    return parentNode;
  }

  /** Create a singleton NodeList containing a given node */
  public static NodeList createNodeList(Node aNode) {
    return new ArrayNodeList(aNode);
  }

  /** Create a shallow copy of a given Node using a given DOMFactory. */
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

  /** Append the nodes in a NodeList to a given parent.
   *
   * @return parentNode.
   * @see #appendNode
   */
  public static Node appendNodes(NodeList aNodeList, Node parentNode) {
    if (aNodeList == null) return parentNode;
    crc.dom.NodeEnumerator e = aNodeList.getEnumerator();
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
    crc.dom.NodeEnumerator e = aNodeList.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      appendNode(copyNode(node, f), parent);
    }
  }

  /** Make a deep copy of a Node, expanding defined entities. 
   *	The results are appended to a Node, which is returned.
   *
   * @param aNode the node to be expanded, then appended.
   * @param parentNode the node to be appended to.
   * @return <code>parentNode</code>.
   */
  public static Node expandEntities(Node aNode, Node parentNode, 
				    DOMFactory fac, EntityTable ents) {
    switch (aNode.getNodeType()) {
    case NodeType.ELEMENT:
      Element elt = (Element)aNode;
      Element ne = expandAttrs(elt, fac, ents);
      appendNode(expandEntities(elt.getChildren(), ne, fac, ents), ne);
      return appendNode(ne, parentNode);

    case NodeType.NODELIST:
      if (aNode.hasChildren())
	return expandEntities(aNode.getChildren(), parentNode, fac, ents);
      return parentNode;

    case NodeType.ENTITY:
      crc.dom.Entity ent = (crc.dom.Entity)aNode;
      NodeList v = (ents == null)? null 
	: ents.getValueForEntity(ent.getName(), false);
      if (v != null) return expandEntities(v, parentNode, fac, ents);
      // if unbound, fall through to copy...

    default:
      Node nn = copyNode(aNode, fac);
      if (nn.hasChildren()) expandEntities(aNode.getChildren(), nn, fac, ents);
      return appendNode(nn, parentNode);
    }
  }

  /** Make a deep copy of a Node, expanding defined entities. 
   *	The results are appended to an ArrayNodeList.
   *
   * @param aNode the node to be expanded, then appended.
   * @param aList the list to be appended to.
   */
  public static void expandEntities(Node aNode, ArrayNodeList aList, 
				    DOMFactory fac, EntityTable ents) {
    switch (aNode.getNodeType()) {
    case NodeType.ELEMENT:
      Element elt = (Element)aNode;
      Element ne = expandAttrs(elt, fac, ents);
      appendNode(expandEntities(elt.getChildren(), ne, fac, ents), ne);
      aList.append(ne);
      return;

    case NodeType.NODELIST:
      if (aNode.hasChildren())
	expandEntities(aNode.getChildren(), aList, fac, ents);
      return;

    case NodeType.ENTITY:
      crc.dom.Entity ent = (crc.dom.Entity)aNode;
      NodeList v = (ents == null)? null 
	: ents.getValueForEntity(ent.getName(), false);
      if (v != null) expandEntities(v, aList, fac, ents);
      // if unbound, fall through to copy...

    default:
      Node nn = copyNode(aNode, fac);
      if (nn.hasChildren()) expandEntities(aNode.getChildren(), nn, fac, ents);
      aList.append(nn);
    }
  }

  /** Copy a NodeList, expanding entities wherever they occur, and
   *	appending the resulting expansions to the children of a Node. */
  public static Node expandEntities(NodeList aNodeList, Node parent,
					DOMFactory fac, EntityTable ents) {
    if (aNodeList == null) return parent;
    crc.dom.NodeEnumerator e = aNodeList.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      expandEntities(node, parent, fac, ents);
    }
    return parent;
  }

  /** Expand entities in a NodeList and append them to an ArrayNodeList. */
  public static NodeList expandEntities(NodeList aNodeList, ArrayNodeList nl,
					DOMFactory fac, EntityTable ents) {
    if (nl == null) nl = new ArrayNodeList();
    if (aNodeList == null) return nl;
    crc.dom.NodeEnumerator e = aNodeList.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      expandEntities(node, nl, fac, ents);
    }
    return nl;
  }

  /** Copy a NodeList, expanding entities wherever they occur. */
  public static NodeList expandEntities(NodeList aNodeList, 
					DOMFactory fac, EntityTable ents) {
    return expandEntities(aNodeList, new ArrayNodeList(), fac, ents);
  }

  /** Make a shallow copy of an element, expanding attributes if it has any. */
  public static Element expandAttrs(Element e, 
				    DOMFactory fac, EntityTable ents) {
    String tag = e.getTagName();
    Element ne = fac.createElement(tag, null);
    crc.dom.AttributeList attrs = e.getAttributes();
    for (long i = 0; i < attrs.getLength(); ++i) try {
      Attribute attr = (Attribute)attrs.item( i );
      ne.setAttribute(fac.createAttribute(attr.getName(),
					  expandEntities(attr.getValue(),
							 fac, ents)));
    } catch (crc.dom.NoSuchNodeException ex){}
    return ne;
  }

  /** Make a deep copy of an element, expanding attributes if it has any. */
  public static Element expandEntities(Element e, 
				       DOMFactory fac, EntityTable ents) {
    return (Element) expandEntities(e.getChildren(),
				    expandAttrs(e, fac, ents),
				    fac, ents);
  }

}
