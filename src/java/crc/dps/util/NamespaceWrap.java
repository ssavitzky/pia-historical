////// NamespaceWrap.java: Wrap a Tabular as a Namespace
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.
package crc.dps.util;

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
import crc.ds.Tabular;

/**
 * Make a Tabular implementation look like a Namespace.
 *
 * ===	The implementation is crude, and will probably want to be revisited. ===
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Namespace
 * @see crc.ds.Tabular
 */

public class NamespaceWrap extends ParseTreeGeneric implements Namespace {

  /************************************************************************
  ** Data:
  ************************************************************************/

  protected Tabular itemsByName	   = null;
  protected boolean caseSensitive  = true;
  protected boolean lowerCase	   = true; // ignored if caseSensitive
  protected int     namespaceItems = 0;

  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Look up a name and get a (local) binding. */
  public ActiveNode getBinding(String name) {
    if (!caseSensitive) name = cannonizeName(name);
    Object o = itemsByName.get(name);
    if (o instanceof ActiveNode) return (ActiveNode)o;
    else if (o instanceof String) return new ParseTreeText(o.toString());
    else return new ParseTreeText(o.toString()); // === might make a wrapper.
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
    if (!caseSensitive) name = cannonizeName(name);
    ActiveNode old = getBinding(name);
    if (binding == null) {	// We are removing an old binding
      if (old == null) {	// ... but there wasn't one.  Nothing to do.
      } else try {		// ... so remove from:
	itemsByName.put(name, null); // ... hash table
      } catch (Exception ex) {
	ex.printStackTrace(System.err);
      } 
    } else if (old == null) {	// We are adding a new binding.  Easy.
      addBinding(name, binding);
    } else try {		// We are replacing an old binding.
      itemsByName.put(name, binding); // put it into the table.
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
    itemsByName.put(name, binding);
    if (binding instanceof Namespace) namespaceItems ++;
  }

  /************************************************************************
  ** Information Operations:
  ************************************************************************/

  /** Returns the bindings defined in this table.
   *	This is more-or-less meaningless for NamespaceWrap because
   *	the underlying objects are not nodes. 
   */
  public NodeEnumerator getBindings() {
    return null;
  }

  /** Returns an Enumeration of the entity names defined in this table. 
   */
  public Enumeration getNames() { 
    return itemsByName.keys();
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

  public String getName()		{ return name; }
  public void setName(String n) 	{ name = n; }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public NamespaceWrap() { this("Namespace", new Table()); }
  public NamespaceWrap(String name, Tabular t) {
    setName(name);
    itemsByName = t;
  }

}