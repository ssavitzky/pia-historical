////// FromParseNodes.java: Input from NodeList
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.input;

import crc.dps.*;
import crc.dps.aux.*;
import crc.dps.active.*;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.NodeEnumerator;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Input from a NodeList containing Active nodes.<p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Fromken
 * @see crc.dps.Input
 * @see crc.dps.Processor
 */

public class FromParseNodes extends ActiveInput implements Input {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected NodeList list;
  protected NodeEnumerator enum;

  /************************************************************************
  ** Overridden Methods:
  ************************************************************************/

  public Node toNextSibling() {
    if (depth > 0) return super.toNextSibling();
    setNode(enum.getNext());
    return active;
  }

  /************************************************************************
  ** Local Methods:
  ************************************************************************/

  public void toFirstNode() {
    enum = list.getEnumerator();
    setNode(enum.getFirst());
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public FromParseNodes(NodeList nodes) {
    list = nodes;
    enum = list.getEnumerator();
    setNode(enum.getFirst());
  }
}
