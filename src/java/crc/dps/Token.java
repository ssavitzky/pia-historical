////// Token.java: Token interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import crc.dom.Node;
import crc.dom.DOMFactory;
import crc.dom.Element;
import crc.dom.ElementDefinition;
import crc.dom.Text;
import crc.dom.Comment;
import crc.dom.PI;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * A wrapper or surrogate for a DOM Node <em>or</em> for an element's start tag
 * 	or end tag, providing 
 *   <ul>
 *	<li> syntactic information required during parsing, traversal, 
 *		and rendering
 *	<li> semantic information required for processing
 *	<li> a reference back to the original Node, if any.
 *   </ul>
 *
 * 	Since a Token is a Node in its own right, it can be put into a
 * 	parse tree that is separate from the Document that contained
 * 	the original Node.  It is also possible to create a Token
 * 	directly without reference to an original Node -- this is done
 * 	when parsing a text file, for example.  <p>
 *
 *	The Token interface extends all of the specializations of Node
 *	that are encountered in the body of a Document (e.g. Text,
 *	Element, and so on), making it possible to operate on a
 *	sequence of Tokens without incurring the overhead of
 *	type-casting and exception handling.  Unfortunately, it is
 *	not possible to specify Comment and PI in the <code>extends</code>
 *	list, because Java can't handle the ambiguity. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.TokenFactory
 * @see crc.dom.Node
 */

public interface Token extends Element, Text {

  /************************************************************************
  ** Semantics:
  ************************************************************************/

  /** Returns the corresponding original Node, if any. */
  public Node getOriginalNode();

  /** Allows the nodeType to be set.  Use with caution. */
  public void setNodeType(int newType);

  /** Returns the Handler for this Token.  Note that the Handler determines
   *	both the syntax and the semantics for the Node.  getHandler should 
   *	never return null; there will always be a generic default handler that
   *	applies.
   */
  public Handler getHandler();

  /** Sets the Handler for this Token. */
  public void setHandler(Handler newHandler);

  /************************************************************************
  ** Syntax:  DTD entry:
  ************************************************************************/

  /** Returns the Token's definition from the Document's DTD. */
  public ElementDefinition getDefinition();

  /************************************************************************
  ** Syntax: convenience flags:
  ************************************************************************/

  /** Returns a negative number is <code>isStartTag</code>, a positive
   *	number if <code>isEndTag</code>, and zero if <code>isNode</code>.
   */
  public int getSyntax();

  /** Returns true if the Token corresponds to a start tag: the beginning
   *	of an Element (which will be terminated with a corresponding end tag).
   */
  public boolean isStartTag();

  /** Sets internal flags such that <code>isStartTag</code> will return true. */
  public void setStartTag();

  /** Returns true if the Token corresponds to a end tag: the end
   *	of an Element (which was started with a start tag).
   */
  public boolean isEndTag();

  /** Sets internal flags such that <code>isEndTag</code> will return true. */
  public void setEndTag();

  /** Returns true if the Token corresponds to a complete node.  A Token will
   *	return true from <em>exactly one</em> of <code>isStartTag()</code>,
   *	<code>isEndTag()</code>, or <code>isNode()</code>.
   */
  public boolean isNode();

  /** Sets internal flags such that <code>isNode</code> will return true. */
  public void setNode();

  /** Returns true if the Token corresponds to any part of an Element:
   *	the full node, its start tag, or its end tag.
   */
  public boolean isElement();

  /** Returns true if the Token corresponds to an Element that
   *	consists of a start tag with no content or corresponding end
   *	tag.  Note that such an element may return either <code>true</code>
   *	or <code>false</code> from <code>isStartTag()</code>.
   */
  public boolean isEmptyElement();

  /** Returns true if the Token corresponds to an empty Element and
   *	its (start) tag contains the final ``<code>/</code>'' that marks
   *	an empty element in XML.
   */
  public boolean hasEmptyDelimiter();

  /** Sets the internal flag corresponding to hasEmptyDelimiter. */
  public void setHasEmptyDelimiter(boolean value);

  /** Returns true if the Token corresponds to an Element which has content
   *	but no end tag, or to an end tag that was omitted from the input or 
   *	that should perhaps be omitted from the output.
   */
  public boolean implicitEnd();

  /** Sets the internal flag corresponding to implicitEnd. */
  public void setImplicitEnd(boolean flag);

  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Convert the Token to a String using the standard SGML/XML defaults. 
   *	This may be called by the Handler's <code>convertToString</code>
   *	method, which in turn is called by the Token's <code>toString</code>.
   *	<p>
   *
   *	Note that the <code>syntax</code> code has a different meaning
   *	than it does in the Token itself: <em>in all cases</em> a Token
   *	is converted to a String with:
   *	<pre>basicToString(-1) + basicToString(0) + basicToString(1)</pre>
   *
   * === Not clear where entity, url encoding and decoding is done. ===
   */
  public String basicToString(int syntax);

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Token.  Attributes are copied, but 
   *	children are not. 
   */
  public Token shallowCopy();

  /** Return a new start-tag Token for this Token.
   *	If the Token is already a start tag, it is simply returned. 
   *	If the Token is not an element, null is returned.
   */
  public Token startToken();

  /** Return a new end-tag Token for this Token.
   *	If the Token is already an end tag, it is simply returned. 
   *	If the Token is not an element, null is returned.
   */
  public Token endToken();

  /** Return new node corresponding to this Token, made using the given 
   *	DOMFactory.  Children <em>are not</em> copied.
   */
  public Node createNode(DOMFactory f);

  /** Return new node corresponding to this Token, made using the given 
   *	DOMFactory.  Children <em>are</em> copied recursively.
   */
  public Node createTree(DOMFactory f);

}
