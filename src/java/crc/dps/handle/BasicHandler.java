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

  /** The default action is simply to return the Token. */
  public Node startAction(Token t, Context p) {
    return t;
  }

  /** The default action is simply to return the Token. */
  public Node endAction(Token t, Context p) {
    return t;
  }

  /** The default action is simply to return the Token. */
  public Node nodeAction(Token t, Context p) {
    return t;
  }

  /** Returns the result of ``expanding'' the given Token. 
   * @return a (possibly empty) NodeList of results.
   */
  public NodeList expand(Token t, Context c) {
    return t.expand(c);
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
  public Node createNode(Token t) {
    // Since we don't know what factory to use, just clone the node.
    return t.shallowCopy();
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
