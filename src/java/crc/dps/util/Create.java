////// Create.java: Utilities for Creating nodes.
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.aux;

import crc.dom.Node;
import crc.dom.Element;
import crc.dom.NodeList;
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
 * Node Creation utilities (static methods) for a Document Processor. 
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 */

public class Create {

  /************************************************************************
  ** NodeList Construction:
  ************************************************************************/

  /** Create a singleton NodeList containing a given node. */
  public static NodeList createNodeList(Node aNode) {
    return new ArrayNodeList(aNode);
  }

  /** Create a singleton NodeList containing a given String. */
  public static NodeList createNodeList(String aString) {
    return new ArrayNodeList(new ParseTreeText(aString));
  }

  /** Create a NodeList by splitting a string on whitespace. */
  public static NodeList split(String aString) {
    return createNodeList(new java.util.StringTokenizer(aString), " ");
  }

  /** Create a NodeList from an enumeration of elements, with an optional 
   *	separator.  The separator is made ignorable if it is whitespace.
   */
  public static NodeList createNodeList(Enumeration enum, String sep) {
    boolean iws = (sep != null) && Test.isWhitespace(sep);
    ArrayNodeList nl = new ArrayNodeList();
    while (enum.hasMoreElements()) {
      Object o = enum.nextElement();
      if (o instanceof Node) { nl.append((Node)o); }
      else { nl.append(new ParseTreeText(o.toString())); }
      if (sep != null && enum.hasMoreElements()) {
	nl.append(new ParseTreeText(sep, iws));
      }
    }
    return nl;
  }

  /************************************************************************
  ** Node Construction:
  ************************************************************************/

  /** Create an arbitrary ActiveNode with optional name and data. */
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
    case NodeType.DECLARATION:
      return new ParseTreeDecl(name, null, data);
    default:
      return new ParseTreeComment("Undefined type " + nodeType
				  + " name=" + name + " data=" + data);
      //return null;
    }
  }

}
