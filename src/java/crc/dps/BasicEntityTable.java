////// BasicEntityTable.java: Node Handler Lookup Table
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.
package crc.dps;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.BasicAttribute;
import crc.dom.BasicElement;
import java.util.Enumeration;

/**
 * The basic implementation for a EntityTable -- a lookup table for syntax. 
 *
 *	This implementation is represented as an Element; the bindings
 *	are kept in its attribute list.  <p>
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

public class BasicEntityTable extends BasicElement implements EntityTable {

  /************************************************************************
  ** Context:
  ************************************************************************/

  protected EntityTable context = null;

  /** Returns an EntityTable which will handle defaults. 
   *	Note that it may or may not be used by the various lookup
   *	operations; it will usually be more efficient to duplicate the
   *	entries of the context.  However, lightweight implementations
   *	that define only a small number of tags may use it.
   */
  public EntityTable getContext() { return context; }

  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Return the value for a given name.  Performs recursive lookup in the
   *	context if necessary. 
   */
  public NodeList getValueForEntity(String name, boolean local) {
    Attribute binding = getBinding(name);
    if (binding != null) return binding.getValue();
    return (local || context == null) ? null
      				      : context.getValueForEntity(name, local);
  }

  /** Set the value for a given name.
   */
  public void setValueForEntity(String name, NodeList value, boolean local) {
    Attribute binding = getBinding(name);
    if (binding != null) {
      binding.setValue(value);
    } else if (local || context == null) {
      setBinding(name, value);
    } else {
      context.setValueForEntity(name, value,local);
    }
  }

  /** Look up a name and get a (local) binding. */
  public Attribute getBinding(String name) {
    return getAttributes().getAttribute(name);
  }

  /** Add a new binding or replace an existing one. */
  public void setBinding(Attribute binding) {
    setAttribute(binding);
  }

  /** Construct a new binding or replace the value in an existing one. */
  public void setBinding(String name, NodeList value) {
    setAttribute (new BasicAttribute(name, value));
  }

  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/

  /** Returns the bindings defined in this table. */
  public AttributeList getBindings() {
    return getAttributes();
  }

  /** Returns an Enumeration of the entity names defined in this table. 
   */
  public Enumeration entityNames() { 
    return null;		// === need to be able to enumerate attrs! ===
  }

  /** Returns an Enumeration of the entity names defined in this table and
   *	its context. */
  public Enumeration allNames() { 
    return null;		// ===
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public BasicEntityTable() {}
  public BasicEntityTable(EntityTable parent) {
    context = parent;
  }

}
