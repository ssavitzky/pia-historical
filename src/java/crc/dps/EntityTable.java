////// EntityTable.java: Node Handler Lookup Table interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

/**
 * The interface for a EntityTable -- a lookup table for syntax. 
 *
 *	A Node's Handler provides all of the necessary syntactic and
 *	semantic information required for parsing, processing, and
 *	presenting a Node and its start tag and end tag Token.  A
 *	EntityTable can be regarded as either a lookup table for syntactic
 *	information, or as a a Handler factory. <p>
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
 */

package crc.dps;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.DocumentType;
import java.util.Enumeration;

public interface EntityTable {

  /************************************************************************
  ** Context:
  ************************************************************************/

  /** Returns a EntityTable which will handle defaults. 
   *	Note that it may or may not be used by the various lookup
   *	operations; it will usually be more efficient to duplicate the
   *	entries of the context.  However, lightweight implementations
   *	that define only a small number of tags may use it.
   */
  public EntityTable getContext();

  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Return the value for a given name.
   */
  public NodeList valueForEntity(String name);


  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/

  /** Returns an Enumeration of the entity names defined in this table. 
   */
  public Enumeration entityNames();

  /** Returns an Enumeration of the entity names defined in this table and
   *	its context. */
  public Enumeration allNames();

}
