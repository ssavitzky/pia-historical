////// Handler.java: Node Handler interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

/**
 * The interface for a Node Handler. 
 *
 *	A Node's Handler provides all of the necessary syntactic and
 *	semantic information required for parsing, processing, and
 *	presenting a Node and its start tag and end tag Token.  (A
 *	Handler does <em>not</em> include the additional traversal
 *	information that distinguishes a Token: an Input will
 *	associate the same Handler with a Node's start tag and end
 *	tag). <p>
 *
 *	Note that this interface says little about the implementation.
 *	It is expected, however, that any practical implementation of
 *	Handler will also be a Node, so that sets of Handlers (also
 *	called <em>tagsets</em>) can be read and stored as documents
 *	or (better) DTD's.   <p>
 *
 *	(We may eventually make Handler an extension of Node in order
 *	to enforce this.) <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Processor
 * @see crc.dps.Token
 * @see crc.dps.Input 
 * @see crc.dom.Node
 */

package crc.dps;
import crc.dom.Node;

public interface Handler {

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
  public Token startAction(Token t, Processor p);

  /** Performs actions associated with an Element's end tag Token.
   *	Normally returns the original Token, but it may replace it
   *	with a new one or return <code>null</code>.  Called only for a
   *	Token that represents the end tag of an Element with content.
   *	Called only if the Processor is expanding.
   *
   * @param t the Token for which actions are being performed.
   * @param p the Processor operating on this Token.
   * @return the original Token, a replacement, or <code>null</code>.  */
  public Token endAction(Token t, Processor p);

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
  public Token nodeAction(Token t, Processor p);

  /** Returns a new, clean Node corresponding to the given Token.
   *	The new Node is suitable for incorporating into a new
   *	document. <p>
   *
   *	Note that this is not used when creating a parse tree of an
   *	existing document -- such a parse tree is made out of Token
   *	objects, which preserves the syntactic and semantic
   *	information (e.g. handlers).
   */
  public Node createNode(Token t, Processor p);


  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/

  /** Called during parsing to return a suitable start tag or complete
   *	element Token.  The new Token's handler will be
   *	<code>this</code>.  Typically the Parser will have found the
   *	handler in a Tagset.
   */
  public Token createStartToken(String tagname, NodeList attributes, Parser p);

  /** Called during parsing to return an end tag Token.  The new
   *	Token's handler will be <code>this</code>.
   */
  public Token createEndToken(String tagname, Parser p);

  /** Called during parsing to return a suitable Token for a generic
   *	Node with String content.  The new Token's handler will be
   *	<code>this</code>.
   */
  public Token createToken(int nodeType, String content, Parser p);

  /** Called during parsing to return a suitable Token for a new Text.
   *	The new Token's handler will be <code>this</code>.
   */
  public Token createTextToken(String text, Parser p);

  /** Called during parsing to check for the presence of an implicit 
   *	end tag before an end tag.
   * @param t the Token for which this is the handler, and for which the
   *	nesting is being checked.
   * @param p the Parser.
   * @return a list of generated end-tag tokens, innermost first, to be
   *	returned from the parser ahead of <code>t</code>. 
   */
  public TokenList checkEndNesting(Token t, Parser p);

  /** Called during parsing to check for the presence of an implicit 
   *	end tag before a start tag or complete element.
   *
   * @param t the Token for which this is the handler, and for which the
   *	nesting is being checked.
   * @param p the Parser.
   * @return a list of generated end-tag tokens, innermost first, to be
   *	returned from the parser ahead of <code>t</code>.
   */
  public TokenList checkElementNesting(Token t, Parser p);

  /** Returns the Tagset from which this Handler came. */
  public Tagset getTagset();

  /************************************************************************
  ** Presentation Operations:
  ************************************************************************/

  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/


}
