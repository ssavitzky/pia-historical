////// Util.java: Document Processor utilities
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;

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

import crc.ds.Table;

import java.util.Enumeration;

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

  /************************************************************************
  ** Node Construction:
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

  /** Create an arbitrary ActiveNode with optional name and data */
  public static ActiveNode createActiveNode(int nodeType,
					    String name, String data) {
    switch (nodeType) {
    case NodeType.COMMENT:
      return new ParseTreeComment(data);
    case NodeType.PI:
      return new ParseTreePI(name, data);
    case NodeType.ATTRIBUTE:
      return new ParseTreeAttribute(name, (NodeList)null);
    case NodeType.ENTITY:
      return new ParseTreeEntity(name, (NodeList)null);
    case NodeType.ELEMENT:
      return new ParseTreeElement(name, null);
    default:
      return null;
    }
  }

  /************************************************************************
  ** Node Copying and Processing:
  ************************************************************************/

  /** Copy the next Node from the given Input to the given Output 
   *
   * === It should be possible to do this non-recursively by keeping track
   * === of depth.  Note that we're using the Input's stack for state.
   */
  public static void copyNode(Node n, Input in, Output out) {
    if (n == null) n = in.toNextNode();
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
    for (Node n = in.toFirstChild(); n != null; n = in.toNextNode()) {
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

  /************************************************************************
  ** Copying Parse Trees:
  ************************************************************************/



  /************************************************************************
  ** Expansion:
  ************************************************************************/

  public static AttributeList expandAttrs(Context c, AttributeList nl) {
    ToAttributeList dst = new ToAttributeList();
    expandNodes(c, nl, dst);
    return dst.getList();
  }

  public static NodeList expandNodes(Context c, NodeList nl) {
    ToNodeList dst = new ToNodeList();
    expandNodes(c, nl, dst);
    return dst.getList();
  }

  public static void expandNodes(Context c, NodeList nl, Output dst) {
    for (int i = 0; i < nl.getLength(); i++) { 
      try {
	Node n = nl.item(i);
	if (n.getNodeType() == NodeType.ENTITY) {
	  expandEntity(c, (Entity) n, dst);
	} else {
	  dst.putNode(n);
	}
      } catch (crc.dom.NoSuchNodeException ex) {}
    }
  }

  /** Expand a single entity. */
  public static void expandEntity(Context c, Entity n, Output dst) {
    String name = n.getName();
    NodeList value = null;
    if (name.indexOf('.') >= 0) value = c.getIndexValue(name);
    else 
      value = c.getEntityValue(name);
    if (value == null) {
      dst.putNode(n);
    } else {
      Util.copyNodes(value, dst);
    }
  }

  /************************************************************************
  ** Tests:
  ************************************************************************/

  /** Determine whether a Node should be considered <code>true</code> as
   *	a boolean.  Essentially whitespace, comments, and unbound entities
   *	are considered false; everything else is true.
   */
  public static boolean trueValue(Node aNode) {
    int nodeType = aNode.getNodeType();
    switch (nodeType) {
    case NodeType.ELEMENT: 
      return true;

    case NodeType.TEXT:
      crc.dom.Text t = (crc.dom.Text)aNode;
      if (t.getIsIgnorableWhitespace()) return false;
      String s = t.getData();
      for (int i = 0; i < s.length(); ++i) 
	if (Character.isWhitespace(s.charAt(i))) return true;
      return false;

    case NodeType.COMMENT: 
      return false;

    case NodeType.PI:
      return true;

    case NodeType.ATTRIBUTE: 
      crc.dom.Attribute attr = (crc.dom.Attribute)aNode;
      return orValues(attr.getValue());

    case NodeType.NODELIST: 
      return orValues(aNode.getChildren());

    default: 
      return true;
    }
  }

  /** Determine whether a Node should be considered <code>true</code> as
   *	a boolean.  Essentially whitespace, comments, and unbound entities
   *	are considered false; everything else is true.
   */
  public static boolean isWhiteSpace(String s) {
    for (int i = 0; i < s.length(); ++i) 
      if (Character.isWhitespace(s.charAt(i))) return true;
    return false;
  }

  /** Determine whether <em>all</em> the items in a nodeList are true.
   *	An empty list is considered <em>true</em>, because all of
   *	its elements are true.
   */
  public static boolean andValues(NodeList aNodeList) {
    NodeEnumerator e = aNodeList.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      if (! trueValue(node)) return false;
    }
    return true;
  }

  /** Determine whether <em>any</em> of the items in a nodeList are true. 
   *	An empty list is considered <em>false</em> because it contains
   *	no true elements.
   */
  public static boolean orValues(NodeList aNodeList) {
    NodeEnumerator e = aNodeList.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      if (trueValue(node)) return true;
    }
    return false;
  }


  /************************************************************************
  ** Loading: === should eventually go into Load
  ************************************************************************/
  /** Table of all globally-defined tagsets, by name. */
  static Table tagsets = new Table();


  /** Return a Tagset with a given name.
   *	If it doesn't exist or the name is null, null is returned.
   *	If a tagset can be loaded (i.e. as a subclass) it is locked.
   */
  public static Tagset require(String name) {
    if (name == null) return null;
    Tagset t = (Tagset)tagsets.at(name);
    if (t == null) { 
      t = loadTagset(name);
      if (t != null) {
	tagsets.at(name, t);
      }
    }
    return t;
  }

  /** Return a Tagset with a given name.
   *	If it doesn't exist or the name is null, a new Tagset is created.
   *	If a tagset can be loaded (i.e. as a subclass) it is locked.
   */
  public static Tagset getTagset(String name) {
    if (name == null) return new crc.dps.tagset.BasicTagset();
    Tagset t = (Tagset)tagsets.at(name);
    if (t == null) { 
      t = loadTagset(name);
      if (t != null) {
	tagsets.at(name, t);
	t.setIsLocked(true);
      }
    }
    return t;
  }

  /** test for the presence of a Tagset with a given name.
   */
  public static boolean tagsetExists(String name) {
    if (name == null) return false;
    return tagsets.at(name) != null;
  }

  /** Load a Tagset implementation class and create an instance of it.  */
  protected static Tagset loadTagsetSubclass(String name) {
    try {
      name = crc.util.NameUtils.javaName(name);
      // javaName turns the ".ts" file extension into a suffix if "_ts".
      Class c = crc.util.NameUtils.loadClass(name, "crc.dps.tagset.");
      return (c != null)? (Tagset)c.newInstance() : null;
    } catch (Exception e) { 
      return null;
    }
  }

  /** Load a Tagset from a file. */
  protected static Tagset loadTagsetFile(String name) {
    return null;		// ===
  }

  /** Load a named Tagset.  First tries to load a file with a ".ts"
   *	extension.  If that fails, tries to load a class, which had
   *	better be a subclass of Tagset, and create an instance of it
   *	(which had better have the right name). */
  protected static Tagset loadTagset(String name) {
    name += ".ts";
    Tagset ts = loadTagsetFile(name);
    return ts != null? ts : loadTagsetSubclass(name);
  }

}
