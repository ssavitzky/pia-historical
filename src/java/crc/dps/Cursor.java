////// Cursor.java: Shared interface for a ``current node''
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;

import crc.dom.Node;
import crc.dom.Attribute;
import crc.dom.Element;

import crc.dps.active.*;

/**
 * The shared interface for classes that maintain a ``current'' node.
 *
 *	This interface provides convenience functions that permit general DOM
 *	trees and parse trees to be operated on with minimal (none, for parse
 *	trees) run-time type checking.  This makes the most common operations
 *	in the DPS significantly more efficient. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * 
 * @see crc.dps.Input
 * @see crc.dps.Output
 * @see crc.dps.Active
 * @see crc.dps.Processor
 */

public interface Cursor {

  /************************************************************************
  ** Current Node:
  ************************************************************************/

  /** Returns the current Node. <p>
   */
  public Node getNode();

  /** Returns the current Node as an Element. <p>
   *
   * @return  <code>null</code> if the current Node is not an Element. 
   */
  public Element getElement();

  /** Returns the current Node as an Attribute. <p>
   *
   *	This operation exists because it is very common to iterate through
   *	the attributes of an Element being processed. <p>
   *
   * @return  <code>null</code> if the current Node is not an Attribute. 
   */
  public Attribute getAttribute();

  /** Returns the current Node as an ActiveNode. <p>
   *
   * @return <code>null</code> if the current Node is not Active. 
   */
  public ActiveNode getActive();

  /************************************************************************
  ** Information:
  ************************************************************************/

  /** Returns the action (semantic) handler, if known, for the current node. 
   */
  public Action getAction();

  /** Returns the syntax handler, if known, for the current node. 
   */
  public Syntax getSyntax();

  /** Returns the current nesting depth. 
   *	Note that this is not necessarily the same as the total distance
   *	of the current Node from the top level of its containing Document;
   *	the Cursor may be iterating over a subtree.
   */
  public int getDepth();


  /** Test whether the current node has attributes.
   *
   * @return <code>true</code> if the current node has attributes.
   */
  public boolean hasAttributes();

  /** Return the current node's tagName if it is an element. */
  public String getTagName();

  /** Returns <code>true</code> if and only if <code>toParent</code> will
   *	return <code>null</code>. <p>
   *
   *	Note that <code>atTop</code> may be <code>true</code> even if the
   *	current node actually has a parent, if the Input is iterating over
   *	a subtree or fragment of a document. <p>
   */
  public boolean atTop();

  /************************************************************************
  ** Navigation:
  ************************************************************************/

  /** Returns the parent of the current Node.
   * @return <code>null</code> if it is not possible to return to the 
   *	parent of the current Node.
   */
  public Node toParent();

  /** Returns the parent Element of the current attribute list or children.
   */
  public Element toParentElement();
 
  /************************************************************************
  ** Nesting:
  ************************************************************************/

  /** Return true if we are currently nested inside an element with
   *	the given tag.
   *
   *	Note that this may give incorrect results if different regions
   *	of the Document have different case sensitivities for tagnames!
   *
   * @param tag the tag to check for
   * @param ignoreCase <code>true</code> if tag comparison ignores case
   * @param stopDepth do not compare nodes below this depth.
   */
  public boolean insideElement(String tag, boolean ignoreCase);

  /** Return the tagname of the element at the given level.
   *
   * @return <code>null</code> if the node at that level is not an
   *	Element, or if the level does not exist.
   */
  public String getTagName(int level);
 
  /** Return the Node at the given level.
   *
   *	Note that the Node's children are not guaranteed to be valid;
   *	the node might be under construction or its children might be
   *	being dynamically enumerated. 
   *
   * @return <code>null</code> if the level does not exist.
   */
  public Node getNode(int level);
 
}
