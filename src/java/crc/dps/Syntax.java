////// Syntax.java: Node Syntax Handler interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.DOMFactory;

/**
 * The interface for a Node's Syntax handler. 
 *
 *	A Node's Syntax handler provides all of the necessary syntactic
 *	information required for parsing a Node. <p>
 *
 *	Syntax has booleans that correspond to tests that a Parser would
 *	otherwise have to make using information from the DTD.  This permits a
 *	(non-verifying) parser to be built without exposing the gory details
 *	of SGML to the casual programmer. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Processor
 * @see crc.dps.Token
 * @see crc.dps.Input 
 * @see crc.dom.Node */

public interface Syntax {

  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/

  /** Called to determine whether the given Node (for which this is
   *	the Handler) is an empty element, or whether content is expected.
   *	It is assumed that <code>this</code> is the result of the Tagset
   *	method <code>handlerForTag</code>.
   *
   * @param t the Node for which this is the handler, and for which the
   *	ssyntax is being checked.
   * @return <code>true</code> if the Node is an empty Element.
   * @see crc.dps.Tagset
   */
  public boolean isEmptyElement(Node n);

  /** Called to determine the correct Action handler for a Node. 
   *
   *	It is assumed that <code>this</code> is the result of the
   *	Tagset method <code>handlerForTag</code>.  Normally just
   *	returns <code>this</code>, but a handler may further examine
   *	the Token's attributes and return something more specific. 
   *	The Handler is also permitted to <em>modify</em> the Token.
   *	<p>
   *
   *	Note that this replaces the earlier technique of dispatching
   *	to a separate named actor, although that may still be useful
   *	in some cases.  Since <code>getActionForNode</code> is called
   *	at parse time, it preceeds any actual processing of the Node. <p>
   *
   * @param t the Token for which the syntax is being checked.
   * @return the correct Handler for the Token.  
   * @see crc.dps.Tagset
   */
  public Action getActionForNode(Node n);


  /** If <code>true</code>, Element tags are recognized in content.  If 
   *	<code>false</code>, the content will consist only of Text and 
   *	(possibly) entity references.
   */
  public boolean parseElementsInContent();

  /** If <code>true</code>, Entity references are recognized in content. */
  public boolean parseEntitiesInContent();

  /** Return <code>true</code> if Text nodes are permitted in the content.
   */
  public boolean mayContainText();

  /** Return <code>true</code> if paragraph elements are permitted in the
   *	content.  If this is <code>true</code> and <code>mayContainText</code>
   *	is false, whitespace is made ignorable and non-whitespace is 
   *	commented out.
   */
  public boolean mayContainParagraphs();

  /** Return true if this kind of token implicitly ends the given one. 
   *	This is not as powerful a test as using the DTD, but it will work
   *	in most cases and permits a simpler parser.
   */
  public boolean implicitlyEnds(String tag);

}
