////// Namespace.java: Node Handler Lookup Table interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.
package crc.dps;

import crc.dps.active.ActiveNode;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.NodeEnumerator;
import crc.dom.Attribute;
import crc.dom.AttributeList;

import java.util.Enumeration;

/**
 * The interface for a Namespace -- a lookup table for named nodes.
 *
 *	Note that a Namespace might be either a Node (e.g. BasicEntityTable)
 *	or a NodeList (e.g. AttributeList), or something else entirely.  As
 *	long as it maps names to values, we don't care.  Each value is
 *	contained in the value and/or children of some Node, called its
 *	<em>binding</em>.
 *
 *	A Namespace is normally accessed through a name that ends in a 
 *	colon character.  It is not required to ``know'' its own name,
 *	however; it may simply be the value of that name in another 
 *	Namespace, or even <em>contained in</em> some name's value.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Processor
 * @see crc.dps.Input 
 * @see crc.dom.Node 
 * @see crc.dom.Attribute
 */

public interface Namespace {

  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  // NamedNodeMap uses:
  //	 Node                      getNamedItem(in wstring name);
  //     void                      setNamedItem(in Node arg);
  //     Node                      removeNamedItem(in wstring name);
  //     Node                      item(in unsigned long index);

  /** Look up a name and get a binding (node). */
  public ActiveNode getBinding(String name);

  /** Look up a name and get a value (nodelist). */
  public NodeList getValue(String name);

  /** Add a new binding or replace an existing one.  Returns the old binding,
   *	if any.  Removes existing binding if the new binding is
   *	<code>null</code>
   */
  public ActiveNode setBinding(String name, ActiveNode binding);

  /** Associate a new value with a name.  Construct a new binding of the
   *	appropriate type if necessary.
   */
  public void setValue(String name, NodeList value);

  /************************************************************************
  ** Information Operations:
  ************************************************************************/

  /** Returns the name of this namespace. */
  public String getName();

  /** Returns the bindings defined in this table, in the same order as the 
   *	names returned by <code>getNames</code>. */
  public NodeEnumerator getBindings();

  /** Returns an Enumeration of the names defined in this table, in the same 
   *	order as the bindings returned by <code>getBindings</code>. 
   */
  public Enumeration getNames();

  /** Returns <code>true</code> if the Namespace is case-sensitive. */
  public boolean isCaseSensitive();

  /** Convert a name to cannonical case. */
  public String cannonizeName(String name);

  /** Returns <code>true</code> if any of the bindings in the Namespace 
   *	implement the Namespace interface themselves. */
  public boolean containsNamespaces();
}
