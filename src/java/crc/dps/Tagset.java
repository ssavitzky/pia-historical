////// Tagset.java: Node Handler Lookup Table interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.AttributeList;
import crc.dom.DocumentType;
import crc.dom.DOMFactory;

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

public interface Tagset extends DOMFactory {

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

  /** Returns a DOMFactory equivalent to the one the Tagset is using.
   *	It is sufficient to simply return <code>this</code>, but if
   *	the Tagset is delegating its factory, it is much more efficient
   *	to return that.
   */
  public DOMFactory getFactory();

  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Called during parsing to return a suitable Handler for a given
   *	tagname.
   */
  public Handler getHandlerForTag(String tagname);

  public void setHandlerForTag(String tagname, Handler newHandler);

  /** Called during parsing to return a suitable Handler for a new Text
   *	node.  It is up to the Parser to determine whether the text consists
   *	only of whitespace.
   */
  public Handler getHandlerForText(boolean isWhitespace);

  /** Called during parsing to return a suitable Handler for a new
   *	entity reference.
   */
  public Handler getHandlerForEntity(String entityName);

  /** Called during parsing to return a suitable Token for a generic
   *	Node, given the Node's type.
   */
  public Handler getHandlerForType(int nodeType);

  public void setHandlerForType(int nodeType, Handler newHandler);

  /** Test whether the Tagset is ``locked.''
   *
   *	A locked Tagset must be extended by creating a new Tagset with
   *	the locked Tagset as its context.
   */
  public boolean isLocked();

  /** Change the lock status. */
  public void setIsLocked(boolean value);

  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/

  /** Return a Parser suitable for parsing a character stream
   *	according to the Tagset.  The Parser may (and probably will)
   *	know the Tagset's actual implementation class, so it can use
   *	specialized operations not described in the Tagset interface.
   */
  public Parser createParser();

  /** Called during parsing to check for the presence of an implicit 
   *	end tag before an end tag.
   *
   * @param t the Token for which the nesting is being checked.  It is safe 
   *	for the Tagset to assume that the Token's handler came from the
   *	Tagset being called.
   * @param c the Context (parse stack) to check.
   * @return a lower bound on the number of elements that need to be ended
   * 	 before <code>t</code> can be ended.  
   */
  public int checkEndNesting(Token t, Context c);

  /** Called during parsing to check for the presence of an implicit 
   *	end tag before a start tag or complete element.
   *
   * @param t the Token for which this is the handler, and for which the
   *	nesting is being checked.
   * @param c the Context (parse stack) to check.
   * @return a lower bound on the number of elements that need to be ended
   * 	 before <code>t</code> can be started.
   */
  public int checkElementNesting(Token t, Context c);


  /** Called during parsing to return a suitable start tag Token for the
   *	given tagname and attribute list. 
   */
  public Token createStartToken(String tagname,
				AttributeList attributes, Parser p);

  /** Called during parsing to return an end tag Token. 
   */
  public Token createEndToken(String tagname, Parser p);

  /** Called during parsing to return a suitable Token for a generic
   *	Node with String data. 
   */
  public Token createToken(int nodeType, String data, Parser p);

  /** Called during parsing to return a suitable Token for a generic
   *	Node with String content. 
   */
  public Token createToken(int nodeType, String name, String data, Parser p);

  /** Called during parsing to return a suitable Token for a new Text.
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
