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
 * @see crc.dps.Token
 * @see crc.dps.Input 
 * @see crc.dom.Node 
 * @see crc.dom.Attribute
 */

public interface Namespace {

  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Return the value for a given name.  Performs recursive lookup in the
   *	context if necessary.
   */
  public NodeList getValue(String name);

  /** Set the value for a given name.
   */
  public void setValue(String name, NodeList value);

  // NamedNodeMap uses:
  //	 Node                      getNamedItem(in wstring name);
  //     void                      setNamedItem(in Node arg);
  //     Node                      removeNamedItem(in wstring name);
  //     Node                      item(in unsigned long index);

  /** Look up a name and get a binding (node). */
  public ActiveNode getNode(String name);

  /** Add a new binding or replace an existing one. */
  public void setNode(ActiveNode binding);

  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/

  /** Returns the bindings defined in this table. */
  public NodeEnumerator getEnumerator();

  /** Returns an Enumeration of the entity names defined in this table. 
   */
  public Enumeration getNames();

}
