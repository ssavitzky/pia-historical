////// ToAttributeList.java: Token output Stream to attribute list
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
