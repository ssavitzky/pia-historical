////// Expand.java: Utilities for Expanding nodes.
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

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.output.*;
import crc.dps.input.FromParseNodes;
import crc.dps.input.FromParseTree;

/**
 * Node-expansion utilities (static methods) for a Document Processor. 
 *
 *	These utilities are primarily used in handlers for obtaining
 *	processed content, expanding entities, and so on.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class Expand {

  /************************************************************************
  ** Input Processing:
  ************************************************************************/

  /** Get the processed content of the current node. */
  public static ParseNodeList getProcessedContent(Input in, Context c) {
    ToNodeList out = new ToNodeList();
    c.subProcess(in, out).processChildren();
    return out.getList();
  }

  /** Get the processed content of the current node as a string. */
  public static String getProcessedContentString(Input in, Context c) {
    ToString out = new ToString();
    c.subProcess(in, out).processChildren();
    return out.getString();
  }

  /** Get the unprocessed content of the current node. */
  public static ParseNodeList getContent(Input in, Context c) {
    ToNodeList out = new ToNodeList();
    Copy.copyChildren(in, out);
    return out.getList();
  }

  /** Get the unprocessed content of the current node as a string. */
  public static String getContentString(Input in, Context c) {
    ToString out = new ToString();
    Copy.copyChildren(in, out);
    return out.getString();
  }

  /** Get the processed content of the current node. */
  public static ParseNodeList getProcessedText(Input in, Context c) {
    ToNodeList out = new ToNodeList();
    c.subProcess(in, new FilterText(out)).processChildren();
    return out.getList();
  }

  /** Get the processed content of the current node as a string. */
  public static String getProcessedTextString(Input in, Context c) {
    ToString out = new ToString();
    c.subProcess(in, new FilterText(out)).processChildren();
    return out.getString();
  }

  /** Get the unprocessed content of the current node. */
  public static ParseNodeList getText(Input in, Context c) {
    ToNodeList out = new ToNodeList();
    Copy.copyChildren(in, new FilterText(out));
    return out.getList();
  }

  /** Get the unprocessed content of the current node as a string. */
  public static String getTextString(Input in, Context c) {
    ToString out = new ToString();
    Copy.copyChildren(in, new FilterText(out));
    return out.getString();
  }

  /************************************************************************
  ** NodeList Processing:
  ************************************************************************/

  /** Process a node list and return the result. */
  public static ParseNodeList processNodes(NodeList nl, Context c) {
    Input in = new FromParseNodes(nl);
    ToNodeList out = new ToNodeList();
    c.subProcess(in, out).run();
    return out.getList();
  }

  /** Process the children of a Node and return the result. */
  public static ParseNodeList processChildren(ActiveNode aNode, Context c) {
    Input in = new FromParseTree(aNode);
    ToNodeList out = new ToNodeList();
    c.subProcess(in, out).processChildren();
    return out.getList();
  }

  /** Process the children of a Node and return the result. */
  public static void processChildren(ActiveNode aNode, Context c, Output out) {
    Input in = new crc.dps.input.FromParseTree(aNode);
    c.subProcess(in, out).processChildren();
  }


  /************************************************************************
  ** Expansion from an Input:
  ************************************************************************/

  /** Get the expanded attribute list of the current node. 
   *	The list is not expanded if it doesn't have to be. 
   */
  public static ActiveAttrList getExpandedAttrs(Input in, Context c) {
    if (in.hasActiveAttributes()) {
      return expandAttrs(c, in.getElement().getAttributes());
    } else if (in.hasAttributes()) {
      return Copy.copyAttrs(in.getElement().getAttributes());
    } else {
      return null;
    }
  }

  /************************************************************************
  ** Expansion:
  ************************************************************************/

  public static ActiveAttrList expandAttrs(Context c, AttributeList atts) {
    ToAttributeList dst = new ToAttributeList();
    expandAttrs(c, atts, dst);
    return dst.getList();
  }

  public static ActiveAttrList copyAttrs(AttributeList atts) {
    ToAttributeList dst = new ToAttributeList();
    Copy.copyNodes(atts, dst);
    return dst.getList();
  }

  public static void expandAttrs(Context c, AttributeList atts, Output dst) {
    for (int i = 0; i < atts.getLength(); i++) { 
      try {
	expandAttribute(c, (Attribute) atts.item(i), dst);
      } catch (crc.dom.NoSuchNodeException ex) {}
    }
  }

  public static void expandAttribute(Context c, Attribute att,  Output dst) {
    dst.putNode(new ParseTreeAttribute(att.getName(),
				       expandNodes(c, att.getValue())));
  }

  public static NodeList expandNodes(Context c, NodeList nl) {
    if (nl == null) return null;
    ToNodeList dst = new ToNodeList();
    expandNodes(c, nl, dst);
    return dst.getList();
  }

  public static void expandNodes(Context c, NodeList nl, Output dst) {
    NodeEnumerator e = nl.getEnumerator();
    for (Node n = e.getFirst(); n != null; n = e.getNext()) {
      if (n.getNodeType() == NodeType.ENTITY) {
	expandEntity(c, (Entity) n, dst);
      } else {
	dst.putNode(n);
      }
    }
  }

  /** Expand a single entity. */
  public static void expandEntity(Context c, Entity n, Output dst) {
    String name = n.getName();
    NodeList value = (name.indexOf('.') >= 0)
      ? Index.getIndexValue(c, name)
      : c.getEntityValue(name, false);
    if (value == null) {
      dst.putNode(n);
    } else {
      Copy.copyNodes(value, dst);
    }
  }


}
