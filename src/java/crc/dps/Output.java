////// Output.java: Document Builder
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.Element;

/**
 * The interface for a consumer of Nodes. <p>
 *
 *	An Output receives the Nodes that comprise a Document in depth-first
 *	order.  Output subclasses exist to:
 *
 *	<ul>
 *	    <li> Copy parse trees.
 *	    <li> Construct arbitrary Document trees
 *	    <li> Convert a subtree into a String.
 *	</ul>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Token
 * @see crc.dps.Input
 * @see crc.dps.Processor
 */

public interface Output {

  /** Adds <code>aNode</code> and its children to the document under 
   *	construction as a new child of the current node.  The new node
   *	is copied unless it has no parent and has a type compatible with
   *	the document under construction.  <p>
   *
   *	If the current node is an Element and <code>aNode</code> is an
   *	Attribute, it is added to the attribute list of the curren node.
   */
  public void putNode(Node aNode);

  /** Adds <code>aNode</code> to the document under construction, and
   *	makes it the current node.
   */
  public void startNode(Node aNode);

  /** Ends the current Node and makes its parent current.
   * @return <code>false</code> if the current Node has no parent.
   */
  public boolean endNode();

  /** Adds <code>anElement</code> to the document under construction,
   *	and makes it the current node.  The attribute list is not
   *	copied; instead, putAttribute, etc. are used.  An element
   *	may be ended with either <code>endElement</code> or
   *	<code>endNode</code>. 
   */
  public void startElement(Element anElement);

  /** Ends the current Element.  The end tag may be optional.  
   *	<code>endElement(true)</code> may be used to end an empty element. 
   */
  public boolean endElement(boolean optional);

  /** Adds the attribute and its value to the element under construction.
   *	If the value is <code>null</code> the attribute will be marked as
   *	unspecified.
   */
  public void putAttribute(String name, NodeList value);

  /** Adds a named attribute to the element under construction,
   *	and makes it the current node.  Subsequent calls on
   *	<code>putNode</code> add nodes to the attribute's value.
   *	The attribute is ended with <code>endNode</code>
   */
  public void startAttribute(String name);

}
