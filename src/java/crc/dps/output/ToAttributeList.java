////// ToAttributeList.java: Token output Stream to attribute list
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.output;

import crc.dps.*;
import crc.dps.util.*;
import crc.dps.active.*;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Element;
import crc.dom.Attribute;
import crc.dom.AttributeList;

/**
 * Output to an AttributeList.<p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Token
 * @see crc.dps.Input
 * @see crc.dps.Processor
 */

public class ToAttributeList extends ActiveOutput implements Output {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected ActiveAttrList list = new ParseTreeAttrs();

  /************************************************************************
  ** Methods:
  ************************************************************************/

  public ActiveAttrList getList() { return list; }

  public void putNode(Node aNode) {
    if (aNode.getNodeType() == NodeType.ATTRIBUTE && depth == 0) {
      Attribute attr = (Attribute)aNode;
      list.setAttribute(attr.getName(), attr);
    } else {
      super.putNode(aNode);
    }
  }

  public void startNode(Node aNode) {
    if (depth == 0) {
      putNode(aNode);
      descend();
      setNode(aNode);
      return;
    }
    Node p = aNode.getParentNode();
    if (active == p && active != null) {	// already a child.  descend.
      if (p != null) descend();
      setNode(aNode);
      return;
    }
    if (p != null || aNode.hasChildren()) {
      aNode = Copy.copyNodeAsActive(aNode);
    }
    appendNode(aNode, active);
    descend();
    setNode(aNode);
  }

  public Node toParent() {
    if (depth != 1) return super.toParent();
    setNode((Node)null);
    depth--;
    atFirst = false;
    return active;
  }

  public Element toParentElement() {
    if (depth != 1) return super.toParentElement();
    setNode((Node)null);
    depth--;
    atFirst = false;
    return element;
  }


  /************************************************************************
  ** Construction:
  ************************************************************************/
  public ToAttributeList() {
  }
}
