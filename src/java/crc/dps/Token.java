////// Token.java: Token Input interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

/**
 * A wrapper or surrogate for a DOM Node <em>or</em> for an element's start tag
 * 	or end tag, providing 
 *   <ul>
 *	<li> syntactic flags required during parsing, traversal, and rendering
 *	<li> semantic information required for processing
 *	<li> a reference back to the original Node
 *   </ul>
 *
 * 	Since a Token is a Node in its own right, it can be put into a
 * 	parse tree that is separate from the Document that contained
 * 	the original Node.  It is also possible to create a Token
 * 	directly without reference to an original Node, using
 * 	TokenFactory -- this is done when parsing a text file, for
 * 	example.  <p>
 *
 *	The Token interface provides some of the operations provided by 
 *	subclasses of Node (e.g. Text and Element), making it possible to 
 *	operate on a sequence of Tokens without incurring the overhead of
 *	type-casting and exception handling.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.TokenFactory
 * @see crc.dom.Node
 */

package crc.dps;
import crc.dom.Node;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public interface Token extends Node {

  /************************************************************************
  ** Semantics:
  ************************************************************************/

  /** Returns the corresponding original Node, if any. */
  public Node originalNode();

  /** Returns the Handler for this Token.  Note that the Handler determines
   *	both the syntax and the semantics for the Node.  getHandler will 
   *	never return null; there will always be a generic default handler that
   *	applies.
   */
  public Handler getHandler();

  /** Sets the Handler for this Token. */
  public void setHandler(Handler newHandler);

  /************************************************************************
  ** Syntax:  DTD entry:
  ************************************************************************/

  /** Returns the Token's declaration from the Document's DTD. */
  public Node declaration();

  /************************************************************************
  ** Syntax: convenience flags:
  ************************************************************************/

  /** Returns true if the Token corresponds to a start tag: the beginning
   *	of an Element (which will be terminated with a corresponding end tag).
   */
  public boolean isStartTag();

  /** Returns true if the Token corresponds to a end tag: the end
   *	of an Element (which was started with a start tag).
   */
  public boolean isEndTag();

  /** Returns true if the Token corresponds to a complete node.  A Token will
   *	return true from <em>exactly one</em> of <code>isStartTag()</code>,
   *	<code>isEndTag()</code>, or <code>isNode()</code>.
   */
  public boolean isNode();

  /** Returns true if the Token corresponds to a complete element:
   *	either an empty element or a start tag, all of its content,
   *	and an end tag.
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

  /** Returns true if the Token corresponds to an Element which has content
   *	but no end tag, or to an end tag that was omitted from the input or 
   *	that should perhaps be omitted from the output.
   */
  public boolean implicitEnd();

}
