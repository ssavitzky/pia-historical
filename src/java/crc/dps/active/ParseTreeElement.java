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
import crc.dom.ElementDefinition;
import crc.dom.NoSuchNodeException;
import crc.dom.NotMyChildException;

import crc.dps.NodeType;
import crc.dps.Handler;

/**
 * An implementation of the ActiveElement interface, suitable for use in 
 *	DPS parse trees.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dom.Node
 * @see crc.dom.Element
 * @see crc.dps.Context
 * @see crc.dps.Processor
 */
public class ParseTreeElement extends ParseTreeNode implements ActiveElement
{

  /************************************************************************
  ** Element interface:
  ************************************************************************/

  public int getNodeType() { return NodeType.ELEMENT; }

  public String getTagName(){ return tagName; }
  public void setTagName(String tagName){ this.tagName = tagName; }  

  public AttributeList getAttributes(){ return attrList; }
  public void setAttributes(AttributeList attrs)  { attrList = attrs; }
  
  public void setAttribute(Attribute newAttr)  {
    if( newAttr == null ) return;
    if (attrList == null) attrList = new ParseTreeAttrs();
    attrList.setAttribute( newAttr.getName(), newAttr );
  }

  /** 
   * Produces an enumerator which iterates over all of the Element nodes that
   * are descendants of the current node whose tagName matches the given
   * name. The iteration order is a depth first enumeration of the elements as
   * they occurred in the original document.
   */
  public NodeEnumerator getElementsByTagName(String name)
  {
    ArrayNodeList result = new ArrayNodeList();
    findAll( name, this, result );
    return result.getEnumerator();
  }

  /************************************************************************
  ** ActiveNode interface:
  ************************************************************************/

  // Exactly one of the following will return <code>this</code>:

  public ActiveElement	 asElement() 	{ return this; }


  /************************************************************************
  ** ActiveElement interface:
  ************************************************************************/

  /** Make a copy with a different attribute list and content. */
  public ActiveElement editedCopy(AttributeList atts, NodeList content) {
    return new ParseTreeElement(this, atts, content);
  }

  /** Convenience function: get an Attribute by name. */
  public Attribute getAttribute(String name) {
    if (attrList == null || name == null) return null;
    return attrList.getAttribute(name);
  }

  /** Convenience function: get an Attribute by name and return its value. */
  public NodeList getAttributeValue(String name) {
    Attribute attr = getAttribute(name);
    return (attr == null)? null : attr.getValue();
  }

  /** Convenience function: get an Attribute by name and return its value
   *	as a String.
   */
  public String getAttributeString(String name) {
    NodeList v = getAttributeValue(name);
    return (v == null) ? null : v.toString();
  }

  public boolean hasTrueAttribute(String name) {
    return crc.dps.util.Test.trueValue(getAttribute(name));
  }

  public void setAttributeValue(String name, NodeList value) {
    setAttribute(new ParseTreeAttribute(name, value));
  }

  public void setAttributeValue(String name, Node value) {
    setAttributeValue(name, new ArrayNodeList(value));
  }

  public void setAttributeValue(String name, String value) {
    setAttributeValue(name, new ParseTreeText(value));
  }

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


  /************************************************************************
  ** Additional Syntactic Operations:
  ************************************************************************/

  protected boolean isEmptyElement = false;
  protected boolean hasEmptyDelim = false;
  protected boolean implicitEnd = false;

  /** Returns <code>true</code> if the Element has no content. 
   *
   *	This flag is redundant given a valid DTD; it exists to take care
   *	of the common case where the DTD is unknown or incomplete.  Also
   *	it can greatly speed up many operations that would otherwise require
   *	knowledge of the DTD.
   */
  public boolean isEmptyElement() {
    //System.err.println("<" + getTagName() + "> is " + 
    //	       (isEmptyElement? "empty" : "non-empty"));
    return isEmptyElement;
  }

  /** Sets the internal flag corresponding to isEmptyElement. */
  public void setIsEmptyElement(boolean value) { 
    isEmptyElement = value;
    //System.err.println("<" + getTagName() + "> defined as " + 
    //	       (isEmptyElement? "empty" : "non-empty"));
  }

  /** Returns <code>true</code> if the Element has an XML-style 
   *	``<code>/</code>'' denoting an empty element.
   *
   *	This flag is redundant given a valid DTD; it exists to take care of
   *	the common case where the DTD is unknown or incomplete; for example,
   *	where XML extensions are mixed in with HTML.  Also it can greatly
   *	speed up many operations that would otherwise require knowledge of the
   *	DTD.
   */
  public boolean hasEmptyDelimiter() { return hasEmptyDelim; }

  /** Sets the internal flag corresponding to hasEmptyDelim. */
  public void setHasEmptyDelimiter(boolean value) {
    hasEmptyDelim = value;
    if (value) isEmptyElement = true;
  }

  /** Returns true if the Token corresponds to an Element which has content
   *	but no end tag, because the end tag can be deduced from context.
   *
   *	This flag is redundant given a valid DTD; it exists to take care
   *	of the common case where the DTD is unknown or incomplete, or where
   *	an effort needs to be made to preserve exact input formatting in
   *	the parse tree. <p>
   *
   * === Strictly speaking it has to be turned off if a Text node is inserted
   *	 following this Element.
   */
  public boolean implicitEnd() 		   { return implicitEnd; }

  /** Sets the internal flag corresponding to implicitEnd. */
  public void setImplicitEnd(boolean flag) { implicitEnd = flag; }


  /************************************************************************
  ** Utilities:
  ************************************************************************/
  
  protected void findAll( String tag, Element elem, ArrayNodeList result){
    Element child = null;
    
    if( elem.hasChildren() ){
      NodeEnumerator enum = elem.getChildren().getEnumerator();
      
      child =  (Element)enum.getFirst();
      while( child != null ) {
	//Report.debug(this, "child name-->"+ child.getTagName());
	if( child.getTagName().equalsIgnoreCase( tag ) ){
	  try{
	    result.insert( result.getLength(), child );
	  }catch(NoSuchNodeException err){
	  }
	}
	findAll( tag, child, result );
	child = (Element)enum.getNext();
      }
      
    }
  }


  /************************************************************************
  ** Instance Variables:
  ************************************************************************/

  /* tag name */
  protected String tagName;

  /* attribute list */
  protected AttributeList attrList = null;

  protected ElementDefinition definition = null;

  /** flag for presence of closing semicolon on an Entity. */
  protected boolean hasClosingDelim = true;


  /************************************************************************
  ** Syntax:  DTD entry:
  ************************************************************************/

  /** Returns the Token's declaration from the Document's DTD. */
  public ElementDefinition getDefinition() {
    return definition;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public ParseTreeElement(){
    setTagName( "" );
  }

  public ParseTreeElement(String tagName){
    setTagName( tagName );
  }

  /** Construct a ParseTreeElement with given tagname, attributes, and content. 
   * @see crc.dom.Element
   */
  public ParseTreeElement( String tag, AttributeList atts, NodeList content ){
    setTagName( tag );
    if (atts != null) 	 setAttributes( new ParseTreeAttrs(atts) );
    if (content != null) copyContent(content);
  }

  public ParseTreeElement( String tag, AttributeList atts, NodeList content,
			   Handler handler){
    setTagName( tag );
    if (atts != null) 	 setAttributes( new ParseTreeAttrs(atts) );
    if (content != null) copyContent(content);
    setHandler(handler);
  }

  public ParseTreeElement(Element e, AttributeList atts, NodeList content) {
    setTagName( e.getTagName() );
    if (e instanceof ActiveElement) copyActiveInfo((ActiveElement)e);
    if (atts != null)       	    setAttributes( new ParseTreeAttrs(atts) );
    if (content != null) 	    copyContent(content);
  }

  protected final void copyContent(NodeList content) {
    if (content != null) {
      NodeEnumerator enum = content.getEnumerator();
      for (Node elem = enum.getFirst(); elem != null; elem = enum.getNext()) {
	try{
	  insertBefore( elem, null );
	}catch(NotMyChildException ex){
	  //Report.debug(ex.toString());
	}
      }
    }
  }

  protected final void copyActiveInfo(ActiveElement e) {
    setTagName(e.getTagName());
    handler = (Handler)e.getSyntax();
    action = e.getAction();
    isEmptyElement = e.isEmptyElement();
    hasEmptyDelim  = e.hasEmptyDelimiter();
    implicitEnd = e.implicitEnd();
  }

  public ParseTreeElement(ActiveElement e,
			  AttributeList atts, NodeList content) {
    setTagName( e.getTagName() );
    copyActiveInfo(e);
    if (atts != null)       	    setAttributes( new ParseTreeAttrs(atts) );
    if (content != null) 	    copyContent(content);
  }

  /** Copy constructor.  Selects deep or shallow copy */
  public ParseTreeElement(ParseTreeElement e, boolean copyChildren) {
    tagName = e.tagName;
    copyActiveInfo(e);
    AttributeList attrs = e.getAttributes();
    if (attrs != null) setAttributes( new ParseTreeAttrs( attrs ) );
    if (copyChildren) copyChildren(e);
  }

  public ParseTreeElement(Element e, AttributeList atts) {
    this(e, e.getAttributes(), null);
  }

  /** Construct a ParseTreeElement with given tagname and attributes. 
   * @see crc.dom.Element
   */
  public ParseTreeElement(String tagname, AttributeList attrs) {
    setTagName(tagname);
    if (attrs != null) setAttributes( new ParseTreeAttrs( attrs ) );
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
    if (attrs != null) setAttributes( new ParseTreeAttrs( attrs ) );
  }

  /** Construct a ParseTreeElement with given tagname, syntax,
   *	and Handler.
   * @see crc.dom.Element
   */
  public ParseTreeElement(String tagname, AttributeList attrs, Handler handler)
  {
    setTagName(tagname);
    if (attrs != null) setAttributes( new ParseTreeAttrs( attrs ) );
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
    return new ParseTreeElement(this, false);
  }

  /** Return a deep copy of this Token.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    return new ParseTreeElement(this, true);
  }

  /** Return new node corresponding to this Token, made using the given 
   *	DOMFactory.  Children <em>are not</em> copied.
   *
   * === Worry about document, attribute, entity-ref ===
   */
  public Node createNode(DOMFactory f) {
    Element e = f.createElement(getTagName(), getAttributes()); 
    if (e instanceof ActiveElement) {
      ActiveElement be = (ActiveElement)e;
      be.setHasEmptyDelimiter(hasEmptyDelimiter());
      be.setIsEmptyElement(isEmptyElement());
      be.setImplicitEnd(implicitEnd());
    }
    return e;
  }

}
