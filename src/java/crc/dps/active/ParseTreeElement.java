////// ParseTreeElement.java -- implementation of ActiveElement
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.active;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.ArrayNodeList;
import crc.dom.NodeEnumerator;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.DOMFactory;
import crc.dom.Element;
import crc.dom.BasicElement;
import crc.dom.ElementDefinition;
import crc.dom.Text;
import crc.dom.Comment;
import crc.dom.PI;

import crc.dps.*;

/**
 * An implementation of the ActiveElement interface, suitable for use in 
 *	DPS parse.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dom.Node
 * @see crc.dps.Context
 * @see crc.dps.Processor
 */
public class ParseTreeElement extends BasicElement implements ActiveElement {

  /************************************************************************
  ** Instance Variables:
  ************************************************************************/

  protected Handler handler = null;
  protected Action  action  = null;

  protected ElementDefinition definition = null;

  /** flag for presence of closing semicolon on an Entity. */
  protected boolean hasClosingDelim = true;


  /************************************************************************
  ** ActiveNode interface:
  ************************************************************************/

  public Syntax getSyntax() 		{ return handler; }
  public Action getAction() 		{ return action; }
  public Handler getHandler() 		{ return handler; }

  public void setAction(Action newAction) { action = newAction; }

  public void setHandler(Handler newHandler) {
    handler = newHandler;
    // === used to call newHandler.getHandlerForToken
    action  = handler;
    isEmptyElement = handler.isEmptyElement(this);
  }

 
  // Exactly one of the following will return <code>this</code>:

  public ActiveElement	 asElement() 	{ return this; }
  public ActiveText 	 asText()	{ return null; }
  public ActiveAttribute asAttribute() 	{ return null; }
  public ActiveEntity 	 asEntity() 	{ return null; }
  public ActiveDocument  asDocument() 	{ return null; }

  /** Append a new child.
   *	Can be more efficient than <code>insertBefore()</code>
   */
  public void addChild(ActiveNode newChild) {
    insertAtEnd((crc.dom.AbstractNode)newChild);
  }


  /************************************************************************
  ** ActiveElement interface:
  ************************************************************************/

  /** Append a new attribute.
   *	Can be more efficient than setAttribute.
   */
  public void addAttribute(String aname, NodeList value) {
    crc.dom.Attribute attr = new ParseTreeAttribute(aname, value);
    attr.setSpecified(value != null);
    setAttribute(attr);
    //System.err.println("***Added attribute " + attr.toString() 
    //			 + " value= " + attr.getChildren());
  }

  public void setAttribute(String name, NodeList value) {
    Attribute attr = getAttribute(name);
    if (attr != null) attr.setValue(value);
    else setAttribute(new ParseTreeAttribute(name, value));
  }

  public void setAttribute(String name, Node value) {
    setAttribute(name, new ArrayNodeList(value));
  }

  public void setAttribute(String name, String value) {
    setAttribute(name, new ParseTreeText(value));
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

  public boolean isEmptyElement() { return isEmptyElement; }
  public void setIsEmptyElement(boolean value) { isEmptyElement = value; }

  public boolean hasEmptyDelimiter() { return hasEmptyDelim; }
  public void setHasEmptyDelimiter(boolean value) { hasEmptyDelim = value; }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a ParseTreeElement with all fields to be filled in later. */
  public ParseTreeElement() {
  }

  public ParseTreeElement(ActiveElement e) {
    super((BasicElement)e);
    setTagName(e.getTagName());
    handler = (Handler)e.getSyntax();
    action = e.getAction();
    isEmptyElement = e.isEmptyElement();
    hasEmptyDelim  = e.hasEmptyDelimiter();
    implicitEnd = e.implicitEnd();
  }

  public ParseTreeElement(ActiveElement e, AttributeList atts) {
    super((BasicElement)e, atts);
    setTagName(e.getTagName());
    handler = (Handler)e.getSyntax();
    action = e.getAction();
    isEmptyElement = e.isEmptyElement();
    hasEmptyDelim  = e.hasEmptyDelimiter();
    implicitEnd = e.implicitEnd();
  }

  public ParseTreeElement(Element e) {
    super(e);
    setTagName(e.getTagName());
    if (e instanceof ParseTreeElement) {
      ParseTreeElement ee = (ParseTreeElement)e;
      handler = ee.handler;
      action = ee.action;
      isEmptyElement = ee.isEmptyElement;
      hasEmptyDelim  = ee.hasEmptyDelim;
      implicitEnd = ee.implicitEnd;
    }
  }

  /** Construct a ParseTreeElement with given tagname and syntax. 
   * @see crc.dom.Element
   */
  public ParseTreeElement(String tagname, AttributeList attrs) {
    setTagName(tagname);
    if (attrs != null) setAttributes( new crc.dom.AttrList( attrs ) );
  }

  /** Construct a ParseTreeElement with given tagname and syntax,
   *	and a given implicitEnd flag (almost invariably <code>true</code>).
   * @see crc.dom.Element
   */
  public ParseTreeElement(String tagname, AttributeList attrs,
			  boolean implicit)
  {
    setTagName(tagname);
    implicitEnd = implicit;
    if (attrs != null) setAttributes( new crc.dom.AttrList( attrs ) );
  }

  /** Construct a ParseTreeElement with given tagname, syntax,
   *	and Handler.
   * @see crc.dom.Element
   */
  public ParseTreeElement(String tagname, AttributeList attrs, Handler handler)
  {
    setTagName(tagname);
    if (attrs != null) setAttributes( new crc.dom.AttrList( attrs ) );
    setHandler(handler);
  }


  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Token's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString() {
    String s = "<" + (tagName == null ? "" : tagName);
    AttributeList attrs = getAttributes();
    if (attrs != null && attrs.getLength() > 0) {
      s += " " + attrs.toString();
    }
    return s + (hasEmptyDelimiter() ? "/" : "") + ">";
  }

  /** Return the String equivalent of the Token's content or
   *	<code>data()</code>.  Entities are substituted for characters
   *	with special significance, such as ampersand.
   */
  public String contentString() {
    return (getChildren() == null)? "" : getChildren().toString();
  }

  /** Return the String equivalent of the Token's end tag (for an element)
   *	or the part that comes after the <code>data()</code>.
   */
  public String endString() {
    if (implicitEnd() || isEmptyElement()) return "";
    else return "</" + (tagName == null ? "" : tagName) + ">";
  }


  /** Convert the Token to a String using the Handler's
   *	<code>convertToString</code> method, if there is one.
   *	Otherwise it uses  <code>basicToString</code>.
   */
  public String toString() {
    return (handler != null)
      ? handler.convertToString(this)
      : startString() + contentString() + endString(); 
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Token.  Attributes, if any, are
   *	copied, but children are not.
   */
  public ActiveNode shallowCopy() {
    ParseTreeElement e = new ParseTreeElement(this);
    return e;
  }

  /** Return a deep copy of this Token.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    ActiveNode node = shallowCopy();
    for (Node child = getFirstChild();
	 child != null;
	 child = child.getNextSibling()) {
      ActiveNode newChild = ((ActiveNode)child).deepCopy();
      Util.appendNode(newChild, node);
    }
    return node;
  }

  /** Return new node corresponding to this Token, made using the given 
   *	DOMFactory.  Children <em>are not</em> copied.
   *
   * === Worry about document, attribute, entity-ref ===
   */
  public Node createNode(DOMFactory f) {
    Element e = f.createElement(getTagName(), getAttributes()); 
    if (e instanceof BasicElement) {
      BasicElement be = (BasicElement)e;
      be.setHasEmptyDelimiter(hasEmptyDelimiter());
      be.setIsEmptyElement(isEmptyElement());
      be.setImplicitEnd(implicitEnd());
    }
    return e;
  }

}
