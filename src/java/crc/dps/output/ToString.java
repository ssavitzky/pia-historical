////// ToWriter.java: Token output Stream to Writer
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.output;

import crc.dps.*;
import crc.dps.aux.*;
import crc.dom.*;
import crc.dps.NodeType;
import crc.dps.active.ActiveEntity;

import java.util.NoSuchElementException;

/**
 * Output a Token stream to a String. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Output
 * @see crc.dps.Processor
 */

public class ToString extends CursorStack implements Output {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected String destination = "";
  protected boolean expandEntities = false;
  protected EntityTable entityTable = null;

  public final String getString() { return destination; }

  /************************************************************************
  ** Internal utilities:
  ************************************************************************/

  protected final void write(String s) {
    destination += s;
  }

  protected String encode(String s) {
    return s;			// === encode
  }

  /************************************************************************
  ** Operations:
  ************************************************************************/

  public void putNode(Node aNode) { 
    if (aNode.getNodeType() == NodeType.ENTITY && expandEntities) {
      ActiveEntity e = (ActiveEntity)aNode;
      // === Should really check value in the entity itself as well ===
      NodeList value = entityTable.getEntityValue(e.getName(), false);
      if (value != null) write(value.toString());
      else write(aNode.toString());
    } else {
      write(aNode.toString());
    }
  }
  public void startNode(Node aNode) { 
    pushInPlace();
    setNode(aNode);
    if (active != null) {
      write(active.startString());
    } else if (node instanceof AbstractNode) {
      AbstractNode n = (AbstractNode) node;
      write(n.startString());
    } else {
      // === punt -- should never happen.
    }
  }

  public boolean endNode() {
    if (active != null) {
      write(active.endString());
    } else if (node == null) {
      // null node indicates nothing to do.
    } else if (node instanceof AbstractNode) {
      AbstractNode n = (AbstractNode) node;
      write(n.endString());
    }  else {
      // === punt -- should never happen.
    }   
    return popInPlace();
  }

  public void startElement(Element anElement) {
    startNode(anElement);
  }

  public boolean endElement(boolean optional) {
    if (optional) {
      return popInPlace();
    } else {
      return endNode();
    }
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct an Output. */
  public ToString() {}

  /** Construct an Output, specifying an entity table for expansion. */
  public ToString(EntityTable ents) {
    entityTable = ents;
    expandEntities = true;
  }
}
