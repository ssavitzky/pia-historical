////// BasicToken.java: Token implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.DOMFactory;
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
 *	Node extensions that Token has to implement. <p>
 *
 * ===	Tokens should be constructed using a TokenFactory rather than by
 * ===	calling BasicToken's constructors directly.
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

  protected Node originalNode = null;
  protected Handler handler = null;
  protected ElementDefinition definition = null;

  /** Values are -1 for start tag, 1 for end tag, 0 for whole node. */
  protected int syntax = 0;

  protected boolean isEmpty = false;
  protected boolean hasEmptyDelim = false;
  protected boolean hasClosingDelim = true;
  protected boolean implicitEnd = false;

  protected int nodeType;

  protected String data = null;
  protected boolean isIgnorableWhitespace = false;
  protected boolean isWhitespace = false;

  protected String name = null;

  /************************************************************************
  ** Semantics:
  ************************************************************************/

  /** Returns the corresponding original Node, if any. */
  public Node getOriginalNode() {
    return originalNode;
  }

  /** Returns the Handler for this Token.  Note that the Handler determines
   *	both the syntax and the semantics for the Node.  getHandler should 
   *	never return null; there will always be a generic default handler that
   *	applies.
   */
  public Handler getHandler() {
    return handler;
  }

  /** Sets the Handler for this Token. 
   *	<code>newHandler.getHandlerForToken</code> is called, in case
   *	the Handler needs to do more specific dispatching.
   */
  public void setHandler(Handler newHandler) {
    handler = (newHandler == null)
      ? null
      : newHandler.getHandlerForToken(this);
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

  public void setSyntax(int value) { syntax = value; }

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

  public boolean isEmptyElement() { return isEmpty; }
  public void setIsEmptyElement(boolean value) { isEmpty = value; }

  public boolean hasEmptyDelimiter() { return hasEmptyDelim; }
  public void setHasEmptyDelimiter(boolean value) { hasEmptyDelim = value; }

  public boolean hasClosingDelimiter() { return hasClosingDelim; }
  public void setHasClosingDelimiter(boolean value) { hasClosingDelim = value; }

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

  public boolean getIsWhitespace() { return isWhitespace; }
  public void setIsWhitespace(boolean value) {
    isWhitespace = value;
  }

  public boolean getIsIgnorableWhitespace() { return isIgnorableWhitespace; }
  public void setIsIgnorableWhitespace(boolean value) {
    isIgnorableWhitespace = value;
  }

  public String getName() 		{ return name; }
  public void setName(String newName) 	{ name = newName; }
  
  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a BasicToken with all fields to be filled in later. */
  public BasicToken() {
  }

  /** Construct a BasicToken with a given nodeType.
   * @see crc.dps.NodeType
   * @see crc.dom.NodeType
   */
  public BasicToken(int nodeType) {
    this.nodeType = nodeType;
  }

  /** Construct a BasicToken with a given nodeType and data. 
   * @see crc.dps.NodeType
   * @see crc.dom.NodeType
   */
  public BasicToken(int nodeType, String data) {
    this.nodeType = nodeType;
    setData(data);
  }

  /** Construct a BasicToken with a given nodeType, name, and data. 
   * @see crc.dps.NodeType
   * @see crc.dom.NodeType
   */
  public BasicToken(int nodeType, String name, String data) {
    this.nodeType = nodeType;
    setName(name);
    setData(data);
  }

  /** Construct a BasicToken with a given nodeType, name, data, and handler. 
   * @see crc.dps.NodeType
   * @see crc.dom.NodeType
   */
  public BasicToken(int nodeType, String name, String data, Handler handler) {
    this.nodeType = nodeType;
    this.handler = handler;
    if (handler != null && handler.isEmptyElement(this)) isEmpty = true;
    setName(name);
    setData(data);
  }

  /** Construct a BasicToken with a given nodeType, data, and Handler. 
   * @see crc.dps.NodeType
   * @see crc.dom.NodeType
   */
  public BasicToken(int nodeType, String data, Handler handler) {
    this.nodeType = nodeType;
    this.handler = handler;
    setData(data);
  }

  /** Construct a TEXT BasicToken with given data. 
   * @see crc.dom.Text
   */
  public BasicToken(String data) {
    this.nodeType = NodeType.TEXT;
    setData(data);
  }

  /** Construct a TEXT BasicToken with given data and Handler. 
   * @see crc.dom.Text
   */
  public BasicToken(String data, Handler handler) {
    this.nodeType = NodeType.TEXT;
    this.handler = handler;
    setData(data);
  }

  /** Construct a TEXT BasicToken with given data and ignorableWhitespace flag. 
   * @see crc.dom.Text
   */
  public BasicToken(String data, boolean ignorableWhitespace) {
    this.nodeType = NodeType.TEXT;
    this.handler = handler;
    isIgnorableWhitespace = ignorableWhitespace;
    setData(data);
  }

  /** Construct an ELEMENT BasicToken with given tagname and syntax. 
   * @see crc.dom.Element
   */
  public BasicToken(String tagname, int syntax) {
    setTagName(tagname);
    this.nodeType = NodeType.ELEMENT;
    this.syntax = syntax;
  }

  /** Construct an ELEMENT BasicToken with given tagname and syntax,
   *	and a given implicitEnd flag (almost invariably <code>true</code>).
   * @see crc.dom.Element
   */
  public BasicToken(String tagname, int syntax, boolean implicit) {
    setTagName(tagname);
    this.nodeType = NodeType.ELEMENT;
    this.syntax = syntax;
    implicitEnd = implicit;
  }

  /** Construct an ELEMENT BasicToken with given tagname, syntax,
   *	and Handler.
   * @see crc.dom.Element
   */
  public BasicToken(String tagname, int syntax,
		    AttributeList attrs, Handler handler) {
    setTagName(tagname);
    this.nodeType = NodeType.ELEMENT;
    this.syntax = syntax;
    if (attrs != null) setAttributes( new crc.dom.AttrList( attrs ) );
    setHandler(handler);
  }

  /** Construct a BasicToken from an original Node. */
  public BasicToken(Node node) {
    this(node, 0);
  }

  /** Construct an ELEMENT BasicToken from an original node and syntax. */
  public BasicToken(Node node, int syntax) {
    originalNode = node;
    this.syntax = syntax;
    nodeType = node.getNodeType();
    switch (nodeType) {
    case NodeType.ELEMENT: 
      crc.dom.Element e = (crc.dom.Element)node;
      setTagName(e.getTagName());
      if (syntax <= 0) setAttributes( e.getAttributes() );
      break;

    case NodeType.TEXT:
      crc.dom.Text t = (crc.dom.Text)node;
      data = t.getData();
      isIgnorableWhitespace = t.getIsIgnorableWhitespace();
      break;

    case NodeType.COMMENT: 
      crc.dom.Comment c = (crc.dom.Comment)node;
      data = c.getData();
      break;

    case NodeType.PI:
      crc.dom.PI pi = (crc.dom.PI)node;
      name = pi.getName();
      data = pi.getData();
      break;

    case NodeType.ATTRIBUTE: 
      crc.dom.Attribute attr = (crc.dom.Attribute)node;
      name = attr.getName();
      break;
    }
  }

  /** Construct a BasicToken from an original Token. 
   *	The children are not copied; that has to be done explicitly.  */
  public BasicToken(Token t) {
    originalNode	  = t.getOriginalNode();
    syntax 		  = t.getSyntax();
    nodeType 		  = t.getNodeType();
    tagName 		  = t.getTagName();
    data 		  = t.getData();
    name 		  = t.getName();
    isWhitespace 	  = t.getIsWhitespace();
    isIgnorableWhitespace = t.getIsIgnorableWhitespace();

    handler		  = t.getHandler();

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
      return copyIfToken? new BasicToken((Token)original) : (Token)original;
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
      String s = "<" +(tagName == null ? "" : tagName);
      crc.dom.AttributeList attrs = getAttributes();
      if (attrs != null && attrs.getLength() > 0) {
	s += " " + attrs.toString();
      }
      return s + (hasEmptyDelimiter() ? "/" : "") + ">";

    case NodeType.TEXT:
      return "";

    case NodeType.COMMENT:
      return "<!--";
     
    case NodeType.PI:
      return "<?" + (name == null? "" : name);
			 
    case NodeType.ENTITY:
      return "&";
			 
    default:
      // if (originalNode != null) return originalNode.startString();
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
			 
    case NodeType.ENTITY:
      return name;
			 
    default:
      // if (originalNode != null) return originalNode.contentString();
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
			 
    case NodeType.ENTITY:
      return hasClosingDelimiter()? ";" : "";
			 
    default:
      // if (originalNode != null) return originalNode.endString();
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
      return startString() + contentString() + endString(); 
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

  /** Return a deep copy of this Token.  Attributes and children are copied.
   */
  public Token deepCopy() {
    Token node = shallowCopy();
    for (Node child = getFirstChild();
	 child != null;
	 child = child.getNextSibling()) {
      Node newChild = createToken(child, true);
      Util.appendNode(newChild, node);
    }
    return node;
  }

  /** Expand the Token in the given Context. */
  public Token expand(Context c) {
    if (handler != null) return c.putResult(handler.expandAction(this, c));
    Node node = Util.expandAttrs(this, c.getHandlers(), c.getEntities());
    Context cc = c.newContext(node, getTagName());
    for (Node child = getFirstChild();
	 child != null;
	 child = child.getNextSibling()) {
      if (child instanceof Token) cc.expand((Token)child);
      else			  cc.putResult(child);
    }
    return c.putResult(node);
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

  /** Return a copy of the Token, including its children, unless the Token
   *	is not already part of a parse tree.  If the Token is a start tag
   *	it is known not to be part of a tree, so it is simply returned with
   *	its syntax changed to Node.
   */
  public Token copyTokenIfNecessary() {
    if (syntax < 0) {
      syntax = 0;
      return this;
    } else {
      return deepCopy();
    }
  }


  /************************************************************************
  ** Convenience Functions:
  ************************************************************************/

  /** Append a new child.
   *	Can be more efficient than <code>insertBefore()</code>
   */
  public void append(Token newChild) {
    try {
      insertBefore(newChild, null);
    } catch (crc.dom.NotMyChildException e) {
	  // === not clear what to do here...  shouldn't happen. ===
    }
  }
  /** Append a new attribute.
   *	Can be more efficient than <code>insertBefore()</code>
   */
  public void addAttr(String aname, crc.dom.NodeList value) {
    crc.dom.Attribute attr = new crc.dom.BasicAttribute(aname, value);
    attr.setSpecified(value != null);
    setAttribute(attr);
  }

  /** Append the Token (or a copy if necessary) to the given parent. */
  public Token copyTokenUnder(Node parent) {
    Token node = copyTokenIfNecessary();
    if (parent != null) try {
      parent.insertBefore(node, null);
    } catch (crc.dom.NotMyChildException e) {
	  // === not clear what to do here...  shouldn't happen. ===
    }
    return node;
  }

}
