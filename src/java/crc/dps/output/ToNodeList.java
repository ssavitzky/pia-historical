////// ToNodeList.java: Token output Stream to node list
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.output;

import crc.dps.*;
import crc.dps.aux.*;
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
    else 		Util.appendNode(aNode, aParent);
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/
  public ToNodeList() {
  }

}
