////// EntityTable.java: Node Handler Lookup Table interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.
package crc.dps;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import java.util.Enumeration;

/**
 * The interface for a EntityTable -- a lookup table for syntax. 
 *
 *	A Node's Handler provides all of the necessary syntactic and
 *	semantic information required for parsing, processing, and
 *	presenting a Node and its start tag and end tag Token.  A
 *	EntityTable can be regarded as either a lookup table for syntactic
 *	information, or as a a Handler factory. <p>
 *
 *	Name=value bindings are represented using the Attribute interface,
 *	since that has all the necessary properties.  
 *	=== we  may change it later into EntityBinding or some such. === <p>
 *
 *	Note that this interface says little about the implementation.
 *	It is expected, however, that any practical implementation of
 *	EntityTable will also be a Node, so that entityTables can be read and
 *	stored as documents or (better) DTD's.  <p>
 *
 *	(We may eventually make EntityTable an extension of Node in order
 *	to enforce this.) <p>
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
  public NodeList getValueForEntity(String name, boolean local);

  /** Set the value for a given name.
   */
  public void setValueForEntity(String name, NodeList value, boolean local);

  /** Look up a name and get a (local) binding. */
  public Attribute getBinding(String name);

  /** Add a new binding or replace an existing one. */
  public void setBinding(Attribute binding);

  /** Construct a new binding or replace the value in an existing one. */
  public void setBinding(String name, NodeList value);

  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/

  /** Returns the bindings defined in this table. */
  public AttributeList getBindings();

  /** Returns an Enumeration of the entity names defined in this table. 
   */
  public Enumeration entityNames();

  /** Returns an Enumeration of the entity names defined in this table and
   *	its context. */
  public Enumeration allNames();

}
