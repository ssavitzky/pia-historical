////// BasicHandler.java: Node Handler basic implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.BasicElement;
import crc.dom.NodeList;
import crc.dom.NodeType;
import crc.dom.DOMFactory;

import crc.dps.Token;
import crc.dps.Handler;
import crc.dps.Context;
import crc.dps.Processor;

import crc.dps.BasicTokenList;
import crc.dps.ParseStack;
import crc.dps.Util;

/**
 * Basic implementation for a Node Handler. <p>
 *
 *	This is a Handler that does only the minimum, and nothing more.
 *	It is capable of representing any kind of purely passive node,
 *	but is not particularly efficient because it contains no specialized
 *	information about the node itself.  It is, however, extensible,
 *	making it a good base class for more specialized versions. 
 *	<p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.handle.GenericHandler
 * @see crc.dps.Processor
 * @see crc.dps.Tagset
 * @see crc.dps.BasicTagset
 * @see crc.dps.Token
 * @see crc.dps.Input 
 * @see crc.dom.Node
 */

public class BasicHandler extends AbstractHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** The default action is simply to return the Token as the Node
   *	being constructed.
   */
  public Node startAction(Token t, Processor p) {
    t.setNode();
    return t;
  }

  /** The default action is simply to pass the constructed Node to the output
   *	via <code>p.result</code>, and return null.
   */
  public Token endAction(Token t, Processor p, Node n) {
    return (t.isEndTag())? p.result(n, t) : p.result(n);
  }

  /** The default action is simply to pass the Token to the output via
   *	<code>p.result</code>, and return null.
   */
  public Token nodeAction(Token t, Processor p) {
    // === nodeAction could be more efficient knowing it's in a processor
    return expandAction(t, p);
  }

  /** Returns the result of ``expanding'' the given Token. 
   * @return a (possibly empty) NodeList of results.
   */
  public Token expandAction(Token t, Context c) {
    // create a new, suitable node
    Node node = createNode(t, c);
    if (! node.hasChildren()) return c.result(node);

    // create a new context in which to expand the children.
    Context cc = c.newContext(node, t.getTagName());
    for (Node child = getFirstChild();
	 child != null;
	 child = child.getNextSibling()) {
      if (child instanceof Token) cc.expand((Token)child);
      else			  cc.result(child);
    }
    return c.result(node);
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
    return Util.expandAttrs(this, c.getHandlers(), c.getEntities());
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

  /** If the handler corresponds to an Element, this determines its syntax.
   *	<dl compact>
   *	    <dt> -1 <dd> known to be non-empty.
   *	    <dt>  0 <dd> unknown
   *	    <dt>  1 <dd> known to be empty.
   *	</dl>
   */
  protected int elementSyntax = 0;

  /** What the Handler knows about a Token's syntax without looking at it.
   *
   * @return
   *	<dl compact>
   *	    <dt> -1 <dd> known to be non-empty.
   *	    <dt>  0 <dd> unknown
   *	    <dt>  1 <dd> known to be empty.
   *	</dl>
   */
  public int getElementSyntax() { return elementSyntax; }
  public void setElementSyntax(int value) { elementSyntax = value; }
  
  /** Called to determine whether the given Token (for which this is
   *	the Handler) is an empty element, or whether content is expected.
   *	It is assumed that <code>this</code> is the result of the Tagset
   *	method <code>handlerForTag</code>.
   *
   *	If <code>elementSyntax</code> is zero, we look at the Token's 
   *	<code>hasEmptyDelimiter</code> flag.
   *
   * @param t the Token for which this is the handler, and for which the
   *	ssyntax is being checked.
   * @return <code>true</code> if the Token is an empty Element.
   * @see crc.dps.Tagset
   */
  public boolean isEmptyElement(Token t) {
    if (elementSyntax != 0) return elementSyntax > 0;
    else return t.hasEmptyDelimiter();
  }

  /** Called to determine the correct Handler for a given Token.
   *	The default action is to return <code>this</code>.
   */
  public Handler getHandlerForToken(Token t) {
    return this;
  }

  /************************************************************************
  ** Presentation Operations:
  ************************************************************************/

  /** Converts the Token to a String according to the given syntax. 
   */
  public String convertToString(Token t, int syntax) {
    return t.basicToString(syntax);
  }

  /** Converts the Token to a String. 
   *	Note that a Token is quite capable of doing this using the 
   *	standard defaults; passing it off to the Handler means that
   *	we can give the same Document different physical representations
   *	if necessary.
   */
  public String convertToString(Token t) {
    if (t.getSyntax() == 0) {
      return convertToString(t, -1) +
	convertToString(t, 0) + convertToString(t, 1); 
    } else {
      return convertToString(t, t.getSyntax());
    }
  }

  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/


}
