////// Create.java: Utilities for Creating nodes.
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
  ** Node Construction:
  ************************************************************************/

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

}
