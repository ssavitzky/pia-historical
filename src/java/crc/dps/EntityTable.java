////// EntityTable.java: Entity Lookup Table interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.
package crc.dps;

import crc.dps.active.ActiveEntity;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.NodeEnumerator;
import crc.dom.Attribute;
import crc.dom.AttributeList;

import java.util.Enumeration;

/**
 * The interface for a EntityTable -- a lookup table for values. 
 *
 *	Entities are named values that can be substituted into a document.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dom.Entity
 * @see crc.dps.active.ActiveEntity
 * @see crc.dps.aux.BasicEntityTable
 */

public interface EntityTable extends Namespace {

  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Look up a name and return the corresponding Entity. */
  public ActiveEntity getEntityBinding(String name);

  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/

  /** Returns an Enumeration of the entity names defined in this table. 
   */
  public Enumeration entityNames();

}
