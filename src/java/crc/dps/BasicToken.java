////// BasicToken.java: Token implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import crc.dom.Node;
import crc.dom.DOMFactory;
import crc.dom.NodeType;
import crc.dom.Element;
import crc.dom.ElementDefinition;
import crc.dom.AttributeList;
import crc.dom.Text;
import crc.dom.Comment;
import crc.dom.PI;

import crc.dom.BasicElement;

import java.util.Enumeration;
import java.util.NoSuchElementException;

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

public class BasicToken extends BasicElement implements Token, Comment, PI {

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
  public Node getOriginalNode() {
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

  /** Returns a negative number is <code>isStartTag</code>, a positive
   *	number if <code>isEndTag</code>, and zero if <code>isNode</code>.
   */
  public int getSyntax() {
    return syntax;
  }

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

  public BasicToken(int nodeType, String data) {
    this.nodeType = nodeType;
    setData(data);
  }

  public BasicToken(int nodeType, String data, Handler handler) {
    this.nodeType = nodeType;
    this.handler = handler;
    setData(data);
  }

  public BasicToken(String data) {
    this.nodeType = NodeType.TEXT;
    setData(data);
  }

  public BasicToken(String data, Handler handler) {
    this.nodeType = NodeType.TEXT;
    this.handler = handler;
    setData(data);
  }

  public BasicToken(String tagname, int syntax) {
    setTagName(tagname);
    this.syntax = syntax;
  }

  public BasicToken(String tagname, int syntax,
		    AttributeList attrs, Handler handler) {
    setTagName(tagname);
    this.syntax = syntax;
    this.handler = handler;
    if (attrs != null) setAttributes( new crc.dom.AttrList( attrs ) );
  }

  /** Construct a BasicToken from an original Node. */
  public BasicToken(Node original) {
    originalNode = original;
    setNode();				// say it's a node.
    nodeType = original.getNodeType();
  }

  /** Construct a BasicToken from an original Token. */
  public BasicToken(Token t) {
    originalNode = t.getOriginalNode();
    syntax = t.getSyntax();
    nodeType = t.getNodeType();
    tagName = t.getTagName();
    data = t.getData();
    if (t.getAttributes() != null)
      setAttributes( new crc.dom.AttrList( t.getAttributes() ) );
  }

  /** Construct a BasicToken from an original Node or Token.
   *  @param original the original Node
   *  @param copyIfToken if <code>true</code>, <code>original</code> is
   *	copied if it is a Token.  Otherwise, it is simply returned.
   */
  public static Token createToken(Node original, boolean copyIfToken) {
    if (original instanceof Token) {
      return copyIfToken? new BasicToken(original) : (Token)original;
    } else {
      return new BasicToken(original);
    }
  }

  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Convert the Token to a String using the standard SGML/XML defaults. 
   *	This may be called by the Handler's <code>convertToString</code>
   *	method, which in turn is called by the Token's <code>toString</code>.
   */
  public String basicToString(int syntax) {
    if      (syntax <  0) return startString();
    else if (syntax == 0) return contentString();
    else 		  return endString();
  }

  /** Return the String equivalent of the Token's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString() {
    switch (nodeType) {
    case NodeType.ELEMENT:
      return "<" +
	(tagName == null ? "" : tagName) +
	(getAttributes() == null ? "" : " " + getAttributes().toString()) +
	(hasEmptyDelimiter() ? "/" : "") + ">";

    case NodeType.TEXT:
      return "";

    case NodeType.COMMENT:
      return "<!--";
     
    case NodeType.PI:
      return "<?" + (name == null? "" : name);
			 
    default:
      return "<!-- nodeType=" + getNodeType() + " -->";
    }
  }

  /** Return the String equivalent of the Token's content or
   *	<code>data()</code>.  Entities are substituted for characters
   *	with special significance, such as ampersand.
   */
  public String contentString() {
    switch (nodeType) {
    case NodeType.ELEMENT:
      return (getChildren() == null)? "" : getChildren().toString();

    case NodeType.TEXT:
      return data;		// === need to do entity substitution.

    case NodeType.COMMENT:
      return data;
			 
    case NodeType.PI:
      return (data == null? "" : " " + data);
			 
    default:
      return "";
    }
  }

  /** Return the String equivalent of the Token's end tag (for an element)
   *	or the part that comes after the <code>data()</code>.
   */
  public String endString() {
    switch (nodeType) {
    case NodeType.ELEMENT:
      if (implicitEnd()) return "";
      else return "</" + (tagName == null ? "" : tagName) + ">";

    case NodeType.TEXT:
      return "";

    case NodeType.COMMENT:
      return "-->";
			 
    case NodeType.PI:
      return ">";
			 
    default:
      return "";
    }
  }


  /** Convert the Token to a String using the Handler's
   *	<code>convertToString</code> method, if there is one.
   *	Otherwise it uses  <code>basicToString</code>.
   */
  public String toString() {
    if (handler != null) return handler.convertToString(this);
    else if (syntax == 0) {
      return basicToString(-1) + basicToString(0) + basicToString(1); 
    } else {
      return basicToString(syntax);
    }
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Token.  Attributes, if any, are
   *	copied, but children are not.
   */
  public Token shallowCopy() {
    return new BasicToken(this);
  }

  /** Return a new start-tag Token for this Token.
   *	If the Token is already a start tag, it is simply returned. 
   *	If the Token is not an element, null is returned.
   */
  public Token startToken() {
    if (isStartTag()) return this;
    else if (! isElement()) return null;
    else {
      return new BasicToken(getTagName(), -1, getAttributes(), getHandler());
    }
  }

  /** Return a new end-tag Token for this Token.
   *	If the Token is already an end tag, it is simply returned. 
   *	If the Token is not an element, null is returned.
   */
  public Token endToken() {
    if (isEndTag()) return this;
    else if (! isElement()) return null;
    else {
      return new BasicToken(getTagName(), 1);
    }
  }

  /** Return new node corresponding to this Token, made using the given 
   *	DOMFactory.  Children <em>are not</em> copied.
   *
   * === Worry about document, attribute, entity-ref ===
   */
  public Node createNode(DOMFactory f) {
    switch (getNodeType()) {
    case NodeType.ELEMENT:
      return f.createElement(getTagName(), getAttributes()); 
    case NodeType.TEXT:
      return f.createTextNode(getData());
    case NodeType.COMMENT:
      return f.createComment(getData());
    case NodeType.PI:
      return f.createPI(getName(), getData());
    default:
      return null;		// bad.
    }
  }

  /** Return new node corresponding to this Token, made using the given 
   *	DOMFactory.  Children <em>are</em> copied recursively if
   *	<code>isElement</code> and <code>isNode</code>.
   */
  public Node createTree(DOMFactory f) {
    if (isElement() && isNode()) {
      Node node = createNode(f);

      for (Node child = getFirstChild();
	   child != null;
	   child = child.getNextSibling()) {
	Node newChild = (child instanceof Token
			 ? ((Token)child).createNode(f)
			 : null); // Node has to be Clonable...
				  //     (Node)child.clone()
	try {
	  node.insertBefore(newChild, null);
	} catch (crc.dom.NotMyChildException e) {
	  // === not clear what to do here...  shouldn't happen. ===
	}
      }
      return node;
    } else {
      return createNode(f);
    }
  }

}
