////// BasicTagset.java: Node Handler Lookup Table interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.tagset;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.DocumentType;
import crc.dom.BasicElement;
import java.util.Enumeration;

import crc.dps.Tagset;
import crc.dps.Token;
import crc.dps.Handler;
import crc.dps.Parser;
import crc.dps.Processor;
import crc.dps.BasicToken;

/**
 * The basic implementation for a Tagset -- a lookup table for Handlers.
 *	<p>
 *
 *	BasicTagset extends BasicElement, and so can be treated as a
 *	Node.  This means that it is easily stored in and retrieved from
 *	XML documents. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Processor
 * @see crc.dps.Token
 * @see crc.dps.Input 
 * @see crc.dom.Node */

public class BasicTagset extends BasicElement implements Tagset {

  /************************************************************************
  ** Context:
  ************************************************************************/

  protected Tagset context;

  /** Returns a Tagset which will handle defaults. 
   *	Note that it may or may not be used by the various lookup
   *	operations; it will usually be more efficient to duplicate the
   *	entries of the context.  However, lightweight implementations
   *	that define only a small number of tags may use it.
   */
  public Tagset getContext() { return context; }

  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Called during parsing to return a suitable Handler for a given
   *	tagname.
   */
  public Handler handlerForTag(String tagname) {
    return null;		// ===
  }

  /** Called during parsing to return a suitable Handler for a new Text
   *	node.  It is up to the Parser to determine whether the text consists
   *	only of whitespace.
   */
  public Handler handlerForText(boolean isWhitespace) {
    return null;		// ===
  }

  /** Called during parsing to return a suitable Token for a generic
   *	Node, given the Node's type.
   */
  public Handler handlerForType(int nodeType) {
    return null;		// ===
  }

  /** Called during parsing to return a suitable Handler for a new
   *	entity reference.
   */
  public Handler handlerForEntity(String entityName) {
    return null;		// ===
  }


  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/

  /** Called to obtain a suitable Parser for this Tagset. 
   *	The Parser will have had its <code>setTagset</code> method
   *	called with <code>this</code>.
   */
  public Parser getParser() {
    Parser p = new crc.dps.parse.BasicParser();
    p.setTagset(this);
    return p;
  }

  /** Called during parsing to check for the presence of an implicit 
   *	end tag before an end tag.
   * @param t the Token for which the nesting is being checked.
   * @param p the Parser.
   * @return the number of elements that need to be ended before
   * 	<code>t</code> can be ended.
   */
  public int checkEndNesting(Token t, Processor p) {
    return 0;			// ===
  }

  /** Called during parsing to check for the presence of an implicit 
   *	end tag before a start tag or complete element.
   *
   * @param t the Token for which the nesting is being checked.
   * @param p the Parser.
   * @return the number of elements that need to be ended before
   * 	<code>t</code> can be started.
   */
  public int checkElementNesting(Token t, Processor p) {
    return 0;			// ===
  }

  /** Called during parsing to return a suitable start tag or complete
   *	element Token.  The new Token's handler will normally be
   *	<code>this</code>, although the Handler may select an alternative
   *	based on the attributes.  Typically the Parser will have found the
   *	handler in a Tagset.
   */
  public Token createStartToken(String tagname, NodeList attributes, Parser p){
    return null;		// ===
  }

  /** Called during parsing to return an end tag Token.  The new
   *	Token's handler will be <code>this</code>.
   */
  public Token createEndToken(String tagname, Parser p) {
    return new BasicToken(tagname, 1);
  }

  /** Called during parsing to return a suitable Token for a generic
   *	Node with String content.  The new Token's handler will be
   *	<code>this</code>.
   */
  public Token createToken(int nodeType, String data, Parser p){
    return new BasicToken(nodeType, data);
  }

  /** Called during parsing to return a suitable Token for a new Text.
   *	The new Token's handler will be <code>this</code>.
   */
  public Token createTextToken(String text, Parser p){
    return new BasicToken(text);
  }


  /************************************************************************
  ** Syntactic Information:
  ************************************************************************/

  protected boolean caseFoldTagnames;
  protected boolean caseFoldAttributes;

  /** Does this Tagset treat uppercase and lowercase tagnames the same?
   */
  public boolean caseFoldTagnames() {
    return caseFoldTagnames;
  }

  /** Convert a tagname to the cannonical case. */
  public String cannonizeTagname(String name) {
    return caseFoldTagnames? name.toLowerCase() : name;
  }

  /** Does this Tagset treat uppercase and lowercase attribute names 
   *	the same?
   */
  public boolean caseFoldAttributes() {
    return caseFoldAttributes;
  }

  /** Convert an attribute name to the cannonical case. */
  public String cannonizeAttribute(String name) {
    return caseFoldAttributes? name.toLowerCase() : name;
  }

  /** Return a Parser suitable for parsing a character stream
   *	according to the Tagset.  The Parser may (and probably will)
   *	know the Tagset's actual implementation class, so it can use
   *	specialized operations not described in the Tagset interface.
   */
  public Parser createParser() {
    return null;		// ===
  }

  /** Return the Tagset's DTD.  In some implementations this may be
   *	the Tagset itself.
   */
  public DocumentType getDocumentType() {
    return null;		// ===
  }


  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/

  /** Returns an Enumeration of the element names defined in this
   *	table.  Note that there is no good way to get the handlers for
   *	Node types other than Element unless the implementation gives
   *	them distinctive, generated names.
   */
  public Enumeration elementNames() {
    return null;		// ===
  }

  /** Returns an Enumeration of the element names defined in this table and
   *	its context. */
  public Enumeration allNames() {
    return null;		// ===
  }


}
