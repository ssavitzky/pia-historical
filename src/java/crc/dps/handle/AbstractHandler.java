////// AbstractHandler.java: Node Handler abstract base class
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.BasicElement;
import crc.dom.NodeList;
import crc.dom.DOMFactory;

import crc.dps.Token;
import crc.dps.Context;
import crc.dps.Processor;
import crc.dps.Handler;

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

  /** The default action is simply to return the Token. */
  public Node startAction(Token t, Processor p) {
    return t;
  }

  /** The default action is simply to return the Token. */
  public Token endAction(Token t, Processor p, Node n) {
    return p.result(n);
  }

  /** The default action is simply to return the Token. */
  public Token nodeAction(Token t, Processor p) {
    return p.result(t);
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
  public abstract Node createNode(Token t, Context p);

  /** Returns a new, clean Node corresponding to the given Token,
   *	created using the given DOMFactory. <p>
   */
  public abstract Node createNode(Token t, DOMFactory f);


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

  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/


}
