////// ToAttributeList.java: Token output Stream to attribute list
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.output;

import crc.dps.*;
import crc.dps.aux.*;
import crc.dps.active.*;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Element;
import crc.dom.Attribute;
import crc.dom.AttributeList;

import java.util.Enumeration;
import java.util.NoSuchElementException;

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

  protected AttributeList list = new crc.dom.AttrList();

  /************************************************************************
  ** Methods:
  ************************************************************************/

  public AttributeList getList() { return list; }

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
