////// Test.java: Utilities for testing nodes and strings
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


/**
 * Test utilities (static methods) for a Document Processor. 
 *
 *	This class contains static methods used for computing tests
 *	(booleans) on nodes and strings.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class Test {

  /************************************************************************
  ** Tests on Strings:
  ************************************************************************/

  /** Determine whether a string consists entirely of whitespace.
   */
  public static boolean isWhitespace(String s) {
    if (s == null) return true;
    for (int i = 0; i < s.length(); ++i) 
      if (! Character.isWhitespace(s.charAt(i))) return false;
    return true;
  }

  /************************************************************************
  ** Tests on Nodes:
  ************************************************************************/

  /** Determine whether a Node should be considered <code>true</code> as
   *	a boolean.  Essentially whitespace, comments, and unbound entities
   *	are considered false; everything else is true.
   */
  public static boolean trueValue(Node aNode) {
    if (aNode == null) return false;
    int nodeType = aNode.getNodeType();
    switch (nodeType) {
    case NodeType.ELEMENT: 
      return true;

    case NodeType.ENTITY: 
      return aNode.hasChildren();

    case NodeType.TEXT:
      crc.dom.Text t = (crc.dom.Text)aNode;
      if (t.getIsIgnorableWhitespace()) return false;
      return ! isWhitespace(t.getData());

    case NodeType.COMMENT: 
      return false;

    case NodeType.PI:
      return true;

    case NodeType.ATTRIBUTE: 
      crc.dom.Attribute attr = (crc.dom.Attribute)aNode;
      if (! attr.getSpecified()) return true;
      return orValues(attr.getValue());

    case NodeType.NODELIST: 
      return orValues(aNode.getChildren());

    default: 
      return true;
    }
  }

  /** Determine whether <em>all</em> the items in a nodeList are true.
   *	An empty list is considered <em>true</em>, because all of
   *	its elements are true.
   */
  public static boolean andValues(NodeList aNodeList) {
    if (aNodeList == null) return true;
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
    if (aNodeList == null) return false;
    NodeEnumerator e = aNodeList.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      if (trueValue(node)) return true;
    }
    return false;
  }


}
