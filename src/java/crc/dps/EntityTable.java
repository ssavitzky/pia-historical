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
 * @see crc.dps.Processor
 * @see crc.dps.Token
 * @see crc.dps.Input 
 * @see crc.dom.Node 
 * @see crc.dom.Attribute
 */

public interface EntityTable {

  /************************************************************************
  ** Context:
  ************************************************************************/

  /** Returns an EntityTable which will handle defaults. 
   *	Note that it may or may not be used by the various lookup
   *	operations; it will usually be more efficient to duplicate the
   *	entries of the context.  However, lightweight implementations
   *	that define only a small number of tags may use it.
   */
  public EntityTable getContext();

  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Return the value for a given name.  Performs recursive lookup in the
   *	context if necessary.
   */
  public NodeList getEntityValue(String name, boolean local);

  /** Set the value for a given name.
   */
  public void setEntityValue(String name, NodeList value, boolean local);

  /** Look up a name and get a binding. */
  public ActiveEntity getBinding(String name, boolean local);

  /** Add a new local binding or replace an existing one. */
  public void setBinding(ActiveEntity binding);

  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/

  /** Returns the bindings defined in this table. */
  public NodeEnumerator getBindings();

  /** Returns an Enumeration of the entity names defined in this table. 
   */
  public Enumeration entityNames();

  /** Returns an Enumeration of the entity names defined in this table and
   *	its context, in order of definition (most recent last). */
  public Enumeration allEntityNames();

}
