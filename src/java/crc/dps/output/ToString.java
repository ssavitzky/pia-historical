////// ToWriter.java: Token output Stream to Writer
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.output;

import crc.dps.*;
import crc.dps.util.*;
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

public class ToString extends ToExternalForm {

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

  /************************************************************************
  ** Operations:
  ************************************************************************/

  public void putNode(Node aNode) { 
    if (aNode.getNodeType() == NodeType.ENTITY && expandEntities) {
      ActiveEntity e = (ActiveEntity)aNode;
      // === Should really check value in the entity itself as well ===
      NodeList value = entityTable.getValue(e.getName());
      if (value != null) write(value.toString());
      else write(aNode.toString());
    } else {
      write(aNode.toString());
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
