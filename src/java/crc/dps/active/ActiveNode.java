////// ActiveNode.java: Interface for Nodes with actions.
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.active;

import crc.dps.Active;
import crc.dps.Action;
import crc.dps.Handler;
import crc.dps.Syntax;
import crc.dps.Namespace;

import crc.dom.Node;
import crc.dom.NodeList;

/**
 * Interface for parse tree Nodes. <p>
 *
 *	By convention, an interface that extends <code>ActiveNode</code>
 *	has the name <code>Active<em>Xxxx</em></code>.   Classes that
 *	<em>implement</em> <code>ActiveNode</code> have names of the form
 *	<code>ParseTree<em>Xxxx</em></code>.  <p>
 *
 *	This interface contains strongly-typed convenience functions for
 *	conversion, navigation, and construction that are made significantly
 *	more efficient than the equivalent DOM functions by the fact that
 *	they do not need to perform run-time type casting. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dom.Node
 * @see crc.dps.Action
 * @see crc.dps.Active
 * @see crc.dps.Context
 * @see crc.dps.Processor
 */

public interface ActiveNode extends Active, Node {

  /************************************************************************
  ** Navigation:
  ************************************************************************/

  public ActiveNode getPrevActive();
  public ActiveNode getNextActive();
  public ActiveNode getActiveParent();
  public ActiveNode getFirstActive();
  public ActiveNode getLastActive();

  /************************************************************************
  ** Syntax:
  ************************************************************************/

  /** Returns the syntactic handler for this Node.
   */
  public Syntax getSyntax();

  /** Allows syntactic and semantic handlers to be set simultaneously. */
  public void setHandler(Handler newHandler);

  /************************************************************************
  ** Conversion convenience functions:
  ************************************************************************/

  /** Return the node typed as an ActiveElement, or <code>null</code> if it is
   *	not an Element. */
  public ActiveElement asElement();

  /** Return the node typed as an ActiveText, or <code>null</code> if it is
   *	not a Text. */
  public ActiveText asText();

  /** Return the node typed as an ActiveAttribute, or <code>null</code> if it
   *	is not an Attribute. */
  public ActiveAttribute asAttribute();

  /** Return the node typed as an ActiveEntity, or <code>null</code> if it is
   *	not an Entity. */
  public ActiveEntity asEntity();

  /** Return the node typed as an ActiveDocument, or <code>null</code> if it
   *	is not a Document. */
  public ActiveDocument asDocument();

  /** Return the node or its content typed as a Namespace. */
  public Namespace asNamespace();

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Token.  Attributes are copied, but 
   *	children are not. 
   */
  public ActiveNode shallowCopy();

  /** Return a deep copy of this Token.  Attributes and children are copied.
   */
  public ActiveNode deepCopy();

  /************************************************************************
  ** Construction Convenience Functions:
  ************************************************************************/

  /** Append a new child.
   *	Can be more efficient than <code>insertBefore()</code>
   */
  public void addChild(ActiveNode newChild);

  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Node's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString();

  /** Return the String equivalent of the Node's content or
   *	<code>data()</code>.  Entities are substituted for characters
   *	with special significance, such as ampersand.
   */
  public String contentString();

  /** Return the String equivalent of the Node's end tag (for an element)
   *	or the part that comes after the <code>data()</code>.
   */
  public String endString();

}
