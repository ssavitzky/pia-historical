////// BasicNamespace.java: Node Handler Lookup Table
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
 * The basic implementation for a Namespace -- a lookup table for syntax. 
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

public class BasicNamespace extends ParseTreeGeneric implements Namespace {

  /************************************************************************
  ** Data:
  ************************************************************************/

  protected Table   itemsByName	   = new Table();
  protected List    itemNames 	   = new List();
  protected boolean caseSensitive  = true;
  protected boolean lowerCase	   = true; // ignored if caseSensitive
  protected int     namespaceItems = 0;

  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Look up a name and get a (local) binding. */
  public ActiveNode getBinding(String name) {
    if (caseSensitive) name = cannonizeName(name);
    return (ActiveNode)itemsByName.at(name);
  }

  public NodeList getValue(String name) {
    ActiveNode binding = getBinding(name);
    if (binding == null) {
      return null;
    } else if (binding instanceof ParseTreeNamed) {
      return ((ParseTreeNamed)binding).getValue();
    } else if (binding.hasChildren()) {
      return binding.getChildren();
    } else {
      return new ParseNodeList(binding);
    }
  }


  /** Add a new local binding or replace an existing one. */
  public ActiveNode setBinding(String name, ActiveNode binding) {
    if (caseSensitive) name = cannonizeName(name);
    ActiveNode old = getBinding(name);
    if (binding == null) {	// We are removing an old binding
      if (old == null) {	// ... but there wasn't one.  Nothing to do.
      } else try {		// ... so remove from:
	itemsByName.remove(name); // ... hash table
	itemNames.remove(name);	  // ... name list
	removeChild(old);	  // ... children
      } catch (Exception ex) {
	ex.printStackTrace(System.err);
      } 
    } else if (old == null) {	// We are adding a new binding.  Easy.
      addBinding(name, binding);
    } else try {		// We are replacing an old binding.
      itemsByName.at(name, binding); // ... in hash table.  Name stays.
      replaceChild(old, binding);    // ... in children
      // adjust namespaceItems if necessary:
      if (old instanceof Namespace) {
	if (binding == null || !(binding instanceof Namespace))
	  namespaceItems --;
      } else {
	if (binding != null && binding instanceof Namespace) 
	  namespaceItems ++;
      }
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
    }
    return old;
  }

  public void setValue(String name, NodeList value) {
    ActiveNode binding = getBinding(name);
    if (binding == null) {
      addBinding(name, new ParseTreeEntity(name, value));
    } else if (binding instanceof ParseTreeNamed) {
      ((ParseTreeNamed)binding).setValue(value);
    } else {
      // === we're out of luck.
      setBinding(name, new ParseTreeEntity(name, value));
    }
  }


  /** Add a new local binding.  Assumes that the name has already been
   *	cannonized if necessary.   Can be useful for initialization.
   */
  protected final void addBinding(String name, ActiveNode binding) {
    itemsByName.at(name, binding);
    itemNames.push(name);
    addChild(binding);
    if (binding instanceof Namespace) namespaceItems ++;
  }

  /************************************************************************
  ** Information Operations:
  ************************************************************************/

  /** Returns the bindings defined in this table. */
  public NodeEnumerator getBindings() {
    return (hasChildren())? getChildren().getEnumerator() : null;
  }

  /** Returns an Enumeration of the entity names defined in this table. 
   */
  public Enumeration getNames() { 
    return itemNames.elements();
  }

  /** Returns <code>true</code> if names are case-sensitive. */
  public boolean isCaseSensitive() {
    return caseSensitive;
  }

  /** Convert a name to cannonical case. */
  public String cannonizeName(String name) {
    return (!caseSensitive)? name
      : lowerCase? name.toLowerCase() : name.toUpperCase();
  }

  /** Returns <code>true</code> if any of the bindings in the Namespace 
   *	implement the Namespace interface themselves. */
  public boolean containsNamespaces() {
    return namespaceItems > 0;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public BasicNamespace() { super("Namespace"); }
  public BasicNamespace(String name) {
    super("Namespace"); 
    setName(name);
  }

}