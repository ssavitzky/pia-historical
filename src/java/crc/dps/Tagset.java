////// Tagset.java: Node Handler Lookup Table interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.DocumentType;
import java.util.Enumeration;

/**
 * The interface for a Tagset -- a lookup table for syntax. 
 *
 *	A Node's Handler provides all of the necessary syntactic and
 *	semantic information required for parsing, processing, and
 *	presenting a Node and its start tag and end tag Token.  A
 *	Tagset can be regarded as either a lookup table for syntactic
 *	information, or as a Handler factory. <p>
 *
 *	Note that this interface says little about the implementation.
 *	It is expected, however, that any practical implementation of
 *	Tagset will also be a Node, so that tagsets can be read and
 *	stored as documents or (better) DTD's.  <p>
 *
 *	(We may eventually make Tagset an extension of Node in order
 *	to enforce this.) <p>
 *
 * === 	need encoders/decoders for character entities, URLs, etc.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Processor
 * @see crc.dps.Token
 * @see crc.dps.Input 
 * @see crc.dom.Node */

public interface Tagset {

  /************************************************************************
  ** Context:
  ************************************************************************/

  /** Returns a Tagset which will handle defaults. 
   *	Note that it may or may not be used by the various lookup
   *	operations; it will usually be more efficient to duplicate the
   *	entries of the context.  However, lightweight implementations
   *	that define only a small number of tags may use it.
   */
  public Tagset getContext();

  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Called during parsing to return a suitable Handler for a given
   *	tagname.
   */
  public Handler handlerForTag(String tagname);

  /** Called during parsing to return a suitable Handler for a new Text
   *	node.  It is up to the Parser to determine whether the text consists
   *	only of whitespace.
   */
  public Handler handlerForText(boolean isWhitespace);

  /** Called during parsing to return a suitable Token for a generic
   *	Node, given the Node's type.
   */
  public Handler handlerForType(int nodeType);

  /** Called during parsing to return a suitable Handler for a new
   *	entity reference.
   */
  public Handler handlerForEntity(String entityName);


  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/

  /** Called to obtain a suitable Parser for this Tagset. 
   *	The Parser will have had its <code>setTagset</code> method
   *	called with <code>this</code>.
   */
  public Parser getParser();

  /** Called during parsing to check for the presence of an implicit 
   *	end tag before an end tag.
   * @param t the Token for which this is the handler, and for which the
   *	nesting is being checked.
   * @param p the Parser.
   * @return the number of elements that need to be ended before
   * 	<code>t</code> can be ended.
   */
  public int checkEndNesting(Token t, Processor p);

  /** Called during parsing to check for the presence of an implicit 
   *	end tag before a start tag or complete element.
   *
   * @param t the Token for which this is the handler, and for which the
   *	nesting is being checked.
   * @param p the Parser.
   * @return the number of elements that need to be ended before
   * 	<code>t</code> can be started.
   */
  public int checkElementNesting(Token t, Processor p);

  /** Called during parsing to return a suitable start tag or complete
   *	element Token.  The new Token's handler will normally be
   *	<code>this</code>, although the Handler may select an alternative
   *	based on the attributes.  Typically the Parser will have found the
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
  public DocumentType getDocumentType();


  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/

  /** Returns an Enumeration of the element names defined in this
   *	table.  Note that there is no good way to get the handlers for
   *	Node types other than Element unless the implementation gives
   *	them distinctive, generated names.
   */
  public Enumeration elementNames();

  /** Returns an Enumeration of the element names defined in this table and
   *	its context. */
  public Enumeration allNames();


}
