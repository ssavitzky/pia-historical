////// AbstractHandler.java: Node Handler abstract base class
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.Element;
import crc.dom.BasicElement;
import crc.dom.NodeList;
import crc.dom.DOMFactory;

import crc.dps.*;

/**
 * An abstract base class for a Node Handler. <p>
 *
 *	This implementation is also an Element, which ensures that 
 *	handlers can easily be stored in and retrieved from XML documents. 
 *	Note that handlers are normally contained in Tagsets, and that
 *	BasicTagset is also an Element.
 *	<p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Context
 * @see crc.dps.Tagset
 * @see crc.dps.BasicTagset
 * @see crc.dps.Input 
 * @see crc.dom.Node
 */

public abstract class AbstractHandler extends BasicElement implements Handler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  public void action(Input in, Context aContext, Output out) {

  }


  /** The default start action is simply to create a Node and return it. */
  public Action startAction(Element e, Processor p) {
    return null;
  }

  /** The default end action is simply to pass the previously-constructed Node
   *	to the output via <code>p.result</code>, and return a null
   *	continuation.
   */
  public Action endAction(Element e, Processor p) {
    return null;
  }

  /** The default node action is to create a new Node and expand the children
   *	of the Token, if any. <p>
   *
   *	If the Token came from a Parser it won't have any children; if
   *	it came from an unprocessed parse tree, expansion will do the
   *	right thing at this point.  <p>
   *
   *	<strong>Handlers for active nodes must override this method.</strong>
   */
  public Action elementAction(Element e, Processor p) {
    return null;
  }


  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/

  /** Called to determine whether the given Token (for which this is
   *	the Handler) is an empty element, or whether content is expected.
   *	It is assumed that <code>this</code> is the result of the Tagset
   *	method <code>handlerForTag</code>.
   *
   * @param t the Token for which this is the handler, and for which the
   *	ssyntax is being checked.
   * @return <code>true</code> if the Token is an empty Element.
   * @see crc.dps.Tagset
   */
  public boolean isEmptyElement(Node n) {
    return false;
  }

  /** Called to determine the correct Handler for a given Token.
   *	The default action is to return <code>this</code>.
   */
  public Action getActionForNode(Node n) {
    return this;
  }

  /** If <code>true</code>, the content is expanded.
   *	The default is to return <code>true</code> -- content is expanded.
   */
  public boolean expandContent() { return true; }

  /** If <code>true</code>, begin constructing a parse tree even if the
   *	parent is not building a parse tree.
   *	The default is to return <code>! passElement()</code>.
   */
  public boolean parseContent() { return ! passElement(); }

  /** If <code>true</code>, Element tags are recognized in content.
   *	The default is to return <code>true</code>.
   */
  public boolean parseElementsInContent() { return true; }

  /** If <code>true</code>, Entity references are recognized in content.
   *	The default is to return <code>true</code>.
   */
  public boolean parseEntitiesInContent() { return true; }

  /** If <code>true</code>, the element is passed to the output while being
   *	processed.  The default is to return <code>true</code> -- the element
   *	is passed in its entirety.
   */
  public boolean passElement() { return true; }


  /************************************************************************
  ** Presentation Operations:
  ************************************************************************/

  /** Converts the Node to a String. 
   *	Note that a Node is quite capable of doing this using the 
   *	standard defaults; passing it off to the Handler means that
   *	we can give the same Document different physical representations
   *	if necessary.
   */
  public String convertToString(Node n) {
    crc.dom.AbstractNode nn = (crc.dom.AbstractNode)n;
    return nn.startString() + nn.contentString() + nn.endString();
  }

  /** Converts the Node to a String. 
   *	Note that a Node is quite capable of doing this using the 
   *	standard defaults; passing it off to the Handler means that
   *	we can give the same Document different physical representations
   *	if necessary.
   */
  public String convertToString(Node n, int syntax) {
    crc.dom.AbstractNode nn = (crc.dom.AbstractNode)n;
    return nn.startString() + nn.contentString() + nn.endString();
  }


}
