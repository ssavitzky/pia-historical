////// ToCharData.java: Token output Stream to Character data
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
 * Output a Token stream to a String in <em>internal</em> form. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Output
 * @see crc.dps.output.ToString
 */

public class ToCharData extends ToExternalForm {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected String destination = "";
  protected boolean expandEntities = true;
  protected EntityTable entityTable = TextUtil.getCharacterEntities();

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
      NodeList value = entityTable.getEntityValue(null, e.getName());
      if (value != null) write(value.toString());
      else write(aNode.toString());
    } else if (aNode.getNodeType() == NodeType.TEXT) {
      write(((Text)aNode).getData());
    } else {
      write(aNode.toString());
    }
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct an Output. */
  public ToCharData() {}

  /** Construct an Output, specifying an entity table for expansion. */
  public ToCharData(EntityTable ents) {
    entityTable = ents;
    expandEntities = true;
  }
}
