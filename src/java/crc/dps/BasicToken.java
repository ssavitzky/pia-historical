////// BasicToken.java: Token implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

/**
 * Basic implementation of the Token interface. <p>
 *
 *	BasicToken is derived from BasicElement rather than from AbstractNode
 *	only because Element has the most complex interface of the various
 *	Node extensions that Token has to implement.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Token
 * @see crc.dom.Node
 * @see crc.dom.AbstractNode
 * @see crc.dom.BasicElement
 */

package crc.dps;
import crc.dom.Node;
import crc.dom.NodeType;
import crc.dom.Element;
import crc.dom.ElementDefinition;
import crc.dom.Text;
import crc.dom.Comment;
import crc.dom.PI;

import crc.dom.BasicElement;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class BasicToken extends BasicElement implements Token {

  /************************************************************************
  ** Instance Variables:
  ************************************************************************/

  protected Node originalNode;
  protected Handler handler;
  protected ElementDefinition definition;

  /** Values are -1 for start tag, 1 for end tag, 0 for whole node. */
  protected int syntax;

  protected boolean isEmpty;
  protected boolean hasEmptyDelim;
  protected boolean implicitEnd;

  protected int nodeType;

  protected String data;
  protected boolean isIgnorableWhitespace;

  protected String name;

  /************************************************************************
  ** Semantics:
  ************************************************************************/

  /** Returns the corresponding original Node, if any. */
  public Node originalNode() {
    return originalNode;
  }

  /** Returns the Handler for this Token.  Note that the Handler determines
   *	both the syntax and the semantics for the Node.  getHandler will 
   *	never return null; there will always be a generic default handler that
   *	applies.
   */
  public Handler getHandler() {
    return handler;
  }

  /** Sets the Handler for this Token. */
  public void setHandler(Handler newHandler) {
    handler = newHandler;
  }

  /************************************************************************
  ** Syntax:  DTD entry:
  ************************************************************************/

  /** Returns the Token's declaration from the Document's DTD. */
  public ElementDefinition getDefinition() {
    return definition;
  }

  /************************************************************************
  ** Syntax: convenience flags:
  ************************************************************************/

  /** Returns true if the Token corresponds to a start tag: the beginning
   *	of an Element (which will be terminated with a corresponding end tag).
   */
  public boolean isStartTag() {
    return syntax < 0;
  }

  /** Sets internal flags such that <code>isStartTag</code> will return true. */
  public void setStartTag() {
    syntax = -1;
  }

  /** Returns true if the Token corresponds to a end tag: the end
   *	of an Element (which was started with a start tag).
   */
  public boolean isEndTag() {
    return syntax > 0;
  }

  /** Sets internal flags such that <code>isEndTag</code> will return true. */
  public void setEndTag() {
    syntax = 1;
  }

  /** Returns true if the Token corresponds to a complete node.  A Token will
   *	return true from <em>exactly one</em> of <code>isStartTag()</code>,
   *	<code>isEndTag()</code>, or <code>isNode()</code>.
   */
  public boolean isNode() {
    return syntax == 0;
  }

  /** Sets internal flags such that <code>isNode</code> will return true. */
  public void setNode() {
    syntax = 0;
  }

  /** Returns true if the Token corresponds to a complete element:
   *	either an empty element or a start tag, all of its content,
   *	and an end tag.
   */
  public boolean isElement() {
    return nodeType == NodeType.ELEMENT;
  }

  /** Returns true if the Token corresponds to an Element that
   *	consists of a start tag with no content or corresponding end
   *	tag.  Note that such an element may return either <code>true</code>
   *	or <code>false</code> from <code>isStartTag()</code>.
   */
  public boolean isEmptyElement() {
    return isEmpty;
  }

  /** Returns true if the Token corresponds to an empty Element and
   *	its (start) tag contains the final ``<code>/</code>'' that marks
   *	an empty element in XML.
   */
  public boolean hasEmptyDelimiter() { return hasEmptyDelim; }
  public void setHasEmptyDelimiter(boolean value) { hasEmptyDelim = value; }

  /** Returns true if the Token corresponds to an Element which has content
   *	but no end tag, or to an end tag that was omitted from the input or 
   *	that should perhaps be omitted from the output.
   */
  public boolean implicitEnd() 		   { return implicitEnd; }

  /** Sets the internal flag corresponding to implicitEnd. */
  public void setImplicitEnd(boolean flag) { implicitEnd = flag; }

  /************************************************************************
  ** Overrides:
  ************************************************************************/

  public int getNodeType() 		{ return nodeType; }
  public void setNodeType(int newType) 	{ nodeType = newType; }

  public String getData() 		{ return data; }
  public void setData(String newData) 	{ data = newData;  }

  public boolean getIsIgnorableWhitespace() { return isIgnorableWhitespace; }
  public void setIsIgnorableWhitespace(boolean value) {
    isIgnorableWhitespace = value;
  }

  public String getName() 		{ return name; }
  public void setName(String newName) 	{ name = newName; }
  
  /************************************************************************
  ** Construction:
  ************************************************************************/

  public BasicToken() {
  }

  public BasicToken(int nodeType) {
    this.nodeType = nodeType;
  }

  /** Construct a BasicToken from an original Node. */
  public BasicToken(Node original) {
    originalNode = original;
    setNode();				// say it's a node.
    nodeType = original.getNodeType();
  }
}
