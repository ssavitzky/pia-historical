////// Tagset.java: Node Handler Lookup Table interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

/**
 * The interface for a Tagset -- a lookup table for syntax. 
 *
 *	A Node's Handler provides all of the necessary syntactic and
 *	semantic information required for parsing, processing, and
 *	presenting a Node and its start tag and end tag Token.  A
 *	Tagset can be regarded as either a lookup table for syntactic
 *	information, or as a a Handler factory. <p>
 *
 *	Note that this interface says little about the implementation.
 *	It is expected, however, that any practical implementation of
 *	Tagset will also be a Node, so that tagsets can be read and
 *	stored as documents or (better) DTD's.  <p>
 *
 *	(We may eventually make Tagset an extension of Node in order
 *	to enforce this.) <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Processor
 * @see crc.dps.Token
 * @see crc.dps.Input 
 * @see crc.dom.Node */

package crc.dps;
import crc.dom.Node;

public interface Tagset {

  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/

  /** Called during parsing to return a suitable Handler for a given
   *	tagname.
   */
  public Handler handlerForTag(String tagname);

  /** Called during parsing to return a suitable Handler for a new Text
   *	node.  The string is passed because the Handler might depend on 
   *	whether the text is whitespace or not.
   */
  public Handler handlerForText(String text);

  /** Called during parsing to return a suitable Token for a generic
   *	Node, given the Node's type.
   */
  public Handler handlerForType(int nodeType);

  /** Called during parsing to return a suitable Handler for a new
   *	entity reference.
   */
  public Handler handlerForEntity(String entityName);

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


  /************************************************************************
  ** Syntactic Information:
  ************************************************************************/

  /** Does this Tagset treat uppercase and lowercase tagnames the same?
   */
  public boolean caseFoldTagnames();

  /** Convert a tagname to the cannonical case. */
  public String cannonizeTagname(String name);

  /** Does this Tagset treat uppercase and lowercase attribute names 
   *	the same?
   */
  public boolean caseFoldAttributes();

  /** Convert an attribute name to the cannonical case. */
  public String cannonizeAttribute(String name);

  /** Return a Parser suitable for parsing a character stream
   *	according to the Tagset.  The Parser may (and probably will)
   *	know the Tagset's actual implementation class, so it can use
   *	specialized operations not described in the Tagset interface.
   */
  public Parser createParser();

  /** Return the Tagset's DTD.  In some implementations this may be
   *	the Tagset itself.
   */
  public Node getDTD();


  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/


}
