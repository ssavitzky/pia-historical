////// ToNodeList.java: Token output Stream to node list
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.output;

import crc.dps.*;
import crc.dps.util.*;
import crc.dps.active.*;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.Element;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Output to an (active) NodeList.<p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Token
 * @see crc.dps.Input
 * @see crc.dps.Processor
 */

public class ToNodeList extends ActiveOutput implements Output {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected ParseNodeList list = new ParseNodeList();

  /************************************************************************
  ** Methods:
  ************************************************************************/

  public ParseNodeList getList() { return list; }
  public void clearList() { list = new ParseNodeList(); }

  public void putNode(Node aNode) {
    if (depth == 0) {
      list.append(aNode);
    } else {
      super.putNode(aNode);
    }
  }

  public void startNode(Node aNode) {
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

  protected void appendNode(Node aNode, Node aParent) {
    if (depth == 0)  	list.append(aNode); 
    else 		Copy.appendNode(aNode, aParent);
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/
  public ToNodeList() {
  }

}
