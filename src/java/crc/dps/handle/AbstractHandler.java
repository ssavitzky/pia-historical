////// AbstractHandler.java: Node Handler abstract base class
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.BasicElement;
import crc.dom.NodeList;
import crc.dom.DOMFactory;

import crc.dps.NodeType;
import crc.dps.EntityTable;
import crc.dps.Token;
import crc.dps.Context;
import crc.dps.Processor;
import crc.dps.Handler;
import crc.dps.Util;

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
 * @see crc.dps.Token
 * @see crc.dps.Input 
 * @see crc.dom.Node
 */

public abstract class AbstractHandler extends BasicElement implements Handler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** The default start action is simply to create a Node and return it. */
  public Node startAction(Token t, Processor p) {
    p.setExpanding(expandContent());
    if (! p.isParsing()) p.setParsing(parseContent());
    p.setPassing(passElement());
    return createNode(t, p);
  }

  /** The default end action is simply to pass the previously-constructed Node
   *	to the output via <code>p.result</code>, and return a null
   *	continuation.
   */
  public Token endAction(Token t, Processor p, Node n) {
    return p.putResult(n);
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
  public Token nodeAction(Token t, Processor p) {
    if (t.getNodeType() == NodeType.ENTITY) {
      EntityTable ents = p.getEntities();
      NodeList v = (ents == null)? null
				 : ents.getValueForEntity(t.getName(), false);
      if (v != null) {
	return p.putResults(v);
	// p.pushInput(v) would be incorrect: it would expand t twice.
      }
    } 
    // XXX nodeAction for an unexpanded Element could call p.pushInto,
    // XXX but it's an open question which is more efficient.
    
    Node node = createNode(t, p);
    if (t.hasChildren())
      Util.expandChildren(node, t.getFirstChild(), t.getTagName(), p);
    return p.putResult(node);
  }

  /** Computes the result of ``expanding'' the given Token.  <p>
   *
   *	<strong>Handlers for active nodes must override this method.</strong>
   *
   * @return a Token representing a continuation.
   */
  public Token expandAction(Token t, Context c) {
    if (t.getNodeType() == NodeType.ENTITY) {
      EntityTable ents = c.getEntities();
      NodeList v = (ents == null)? null
				 : ents.getValueForEntity(t.getName(), false);
      if (v != null) {
	return c.putResults(v);
      }
    }
    Node node = createNode(t, c);
    if (t.hasChildren())
      Util.expandChildren(node, t.getFirstChild(), t.getTagName(), c);
    return c.putResult(node);
  }

  /** Returns a new, clean Node corresponding to the given Token.
   *	The new Node is suitable for incorporating into a new
   *	document. <p>
   *
   *	Note that this is not used when creating a parse tree of an
   *	existing document -- such a parse tree is made out of Token
   *	objects, which preserves the syntactic and semantic
   *	information (e.g. handlers).
   */
  public Node createNode(Token t, Context c) {
    return t.createNode(c.getHandlers());
    //return Util.expandAttrs(this, c.getHandlers(), c.getEntities());
  }

  /** Returns a new, clean Node corresponding to the given Token,
   *	created using the given DOMFactory. <p>
   */
  public Node createNode(Token t, DOMFactory f) {
    return t.createNode(f);
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
  public boolean isEmptyElement(Token t) {
    return t.hasEmptyDelimiter();
  }

  /** Called to determine the correct Handler for a given Token.
   *	The default action is to return <code>this</code>.
   */
  public Handler getHandlerForToken(Token t) {
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

  /** Converts the Token to a String. 
   *	Note that a Token is quite capable of doing this using the 
   *	standard defaults; passing it off to the Handler means that
   *	we can give the same Document different physical representations
   *	if necessary.
   */
  public String convertToString(Token t) {
    return t.startString() + t.contentString() + t.endString();
  }


}
