////// AbstractHandler.java: Node Handler abstract base class
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import crc.dom.Node;
import crc.dom.BasicElement;
import crc.dom.NodeList;
import crc.dom.DOMFactory;

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
 * @see crc.dps.Processor
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

  /** Performs actions associated with an Element's start tag Token.  
   *	Normally returns the original Token, but it may replace it with
   *	a new one or return <code>null</code>.  Called only for a Token
   *	that represents the start tag of an Element with content.
   *	Called only if the Processor is expanding.
   *
   * @param t the Token for which actions are being performed.
   * @param p the Processor operating on this Token.
   * @return the original Token, a replacement, or  <code>null</code>.
   */
  public Token startAction(Token t, Processor p) {
    return t;
  }

  /** Performs actions associated with an Element's end tag Token.
   *	Normally returns the original Token, but it may replace it
   *	with a new one or return <code>null</code>.  Called only for a
   *	Token that represents the end tag of an Element with content.
   *	Called only if the Processor is expanding.
   *
   * @param t the Token for which actions are being performed.
   * @param p the Processor operating on this Token.
   * @return the original Token, a replacement, or <code>null</code>.  */
  public Token endAction(Token t, Processor p) {
    return t;
  }

  /** Performs actions associated with an complete Node's Token.
   *	Normally returns the original Token, but it may replace it
   *	with a new one or return <code>null</code>.  Called for empty
   *	Elements, Text, Comments, PI's, and so on.  It is not called
   *	by the Processor for parse trees associated with elements
   *	having content, although <code>endAction</code> may call it in
   *	that case.  Called only if the Processor is expanding.
   *
   * @param t the Token for which actions are being performed.
   * @param p the Processor operating on this Token.
   * @return the original Token, a replacement, or <code>null</code>.  */
  public Token nodeAction(Token t, Processor p) {
    return t;
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
  public abstract Node createNode(Token t);

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
