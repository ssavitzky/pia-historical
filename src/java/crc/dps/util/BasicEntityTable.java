////// BasicEntityTable.java: Node Handler Lookup Table
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.
package crc.dps.aux;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.NodeEnumerator;
import crc.dom.Attribute;
import crc.dom.AttributeList;

import crc.dps.active.*;
import crc.dps.*;

import java.util.Enumeration;

import crc.ds.List;
import crc.ds.Table;

/**
 * The basic implementation for a EntityTable -- a lookup table for syntax. 
 *
 *	This implementation is represented as an Element; the bindings
 *	are kept in its attribute list.  <p>
 *
 * ===	The implementation is crude, and will probably want to be revisited. ===
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

public class BasicEntityTable extends ParseTreeGeneric implements EntityTable {

  /************************************************************************
  ** Data:
  ************************************************************************/

  protected Table entitiesByName 	= new Table();
  protected List  entityNames 		= new List();
  protected List  contextEntityNames 	= null;

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
  public NodeList getEntityValue(String name, boolean local) {
    ActiveEntity binding = getBinding(name, local);
    return (binding != null)? binding.getValue() :  null;
  }

  /** Set the value for a given name.
   */
  public void setEntityValue(String name, NodeList value, boolean local) {
    ActiveEntity binding = getBinding(name, local);
    if (binding != null) {
      binding.setValue(value);
    } else if (local || context == null) {
      newBinding(name, value);
    } 
  }

  /** Look up a name and get a (local) binding. */
  public ActiveEntity getBinding(String name, boolean local) {
    ActiveEntity binding = (ActiveEntity)entitiesByName.at(name);
    return (local || binding != null || context == null)
      ? binding
      : context.getBinding(name, local);    
  }

  /** Add a new local binding or replace an existing one. */
  public void setBinding(ActiveEntity binding) {
    String name = binding.getName();
    ActiveEntity old = getBinding(name, true);
    if (old == null) addBinding(name, binding);
    else try {
      entitiesByName.at(name, binding);
      replaceChild(old, binding);
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
    }
  }

  /** Construct a new local binding. */
  protected void newBinding(String name, NodeList value) {
    addBinding(name, new ParseTreeEntity(name, value));
  }

  protected void addBinding(String name, ActiveEntity binding) {
    entitiesByName.at(name, binding);
    entityNames.push(name);
    addChild(binding);
  }

  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/

  /** Returns the bindings defined in this table. */
  public NodeEnumerator getBindings() {
    return (hasChildren())? getChildren().getEnumerator() : null;
  }

  /** Returns an Enumeration of the entity names defined in this table. 
   */
  public Enumeration entityNames() { 
    return entityNames.elements();
  }

  /** Returns an Enumeration of the entity names defined in this table and
   *	its context, in order of definition (most recent last). */
  public Enumeration allEntityNames() { 
    if (contextEntityNames == null) {
      contextEntityNames = new List(context.allEntityNames());
    }
    List allNames = new List(contextEntityNames);
    allNames.append(entityNames());
    return allNames.elements();
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public BasicEntityTable() { super("EntityTable"); }
  public BasicEntityTable(EntityTable parent) {
    super("EntityTable"); 
    context = parent;
  }

}
