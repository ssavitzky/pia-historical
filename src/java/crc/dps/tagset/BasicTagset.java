////// BasicTagset.java: Node Handler Lookup Table interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.tagset;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.AttributeList;
import crc.dom.DocumentType;
import crc.dom.BasicElement;
import crc.dom.DOMFactory;
import crc.dom.BasicDOMFactory;

import java.util.Enumeration;

import crc.dps.NodeType;
import crc.dps.Tagset;
import crc.dps.Token;
import crc.dps.Handler;
import crc.dps.Parser;
import crc.dps.Context;
import crc.dps.BasicToken;
import crc.dps.Util;

import crc.dps.handle.BasicHandler;
import crc.dps.handle.GenericHandler;

import crc.util.NameUtils;

import crc.ds.Table;
import crc.ds.List;

/**
 * The basic implementation for a Tagset -- a lookup table for Handlers. <p>
 *
 *	In addition to the basic interface implementation, this class
 *	includes a number of constants and convenience functions useful
 *	when initializing a Tagset.  These are usually called from the 
 *	constructor. <p>
 *
 *	BasicTagset extends BasicElement, and so can be treated as a
 *	Node.  This means that it is easily stored in and retrieved from
 *	XML documents. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Context
 * @see crc.dps.Token
 * @see crc.dps.Input 
 * @see crc.dom.Node */

public class BasicTagset extends BasicElement implements Tagset {

  /************************************************************************
  ** Data:
  ************************************************************************/

  protected Handler defaultElementHandler;

  protected Handler defaultTextHandler;

  protected Handler defaultEntityHandler;

  protected Table handlersByTag = new Table();
  protected List  handlerNames = new List();
  protected List  allNames = null;

  protected int  MAX_TYPE = 10;
  protected int  MIN_TYPE = -2;
  protected Handler handlersByType[] = new Handler[MAX_TYPE - MIN_TYPE];

  protected boolean locked = false;

  /** Syntax for an empty element. */
  public final static int EMPTY   = -1;
  /** Syntax for a normal element.  The contents are expanded. */
  public final static int NORMAL  =  1;
  /** Syntax for a quoted element:  contents are parsed but not expanded. */
  public final static int QUOTED  =  2;

  /** Syntax flag (to be or'ed in) to suppress expansion. */
  public final static int NO_EXPAND	= 2;
  /** Syntax flag (to be or'ed in) to suppress parsing of entities. */
  public final static int NO_ENTITIES	= 4;
  /** Syntax flag (to be or'ed in) to suppress parsing of elements. */
  public final static int NO_ELEMENTS	= 8;

  /** Syntax for a literal: elements and entities are not recognized. */
  public final static int LITERAL =  NO_ENTITIES | NO_ELEMENTS | NO_EXPAND;

  /************************************************************************
  ** DOM Factory:
  ************************************************************************/

  /** The DOMFactory to which all construction requests are delegated. */
  protected DOMFactory factory= null;

  public DOMFactory getFactory() { return factory; }

  protected void setFactory(DOMFactory f) { factory = f; }


  public crc.dom.Document createDocument() {
    return factory.createDocument();
  }

  public crc.dom.DocumentContext createDocumentContext() { 
    return factory.createDocumentContext();
  }

  public crc.dom.Element createElement(String tagName,
				       crc.dom.AttributeList attrs) {
    return factory.createElement(tagName, attrs);
  }

  public crc.dom.Text createTextNode(String data) {
    return factory.createTextNode(data);
  }

  public crc.dom.Comment createComment(String data) {
    return factory.createComment( data );
  }

  public crc.dom.PI createPI(String name, String data) {
    return factory.createPI( name, data );
  }

  public crc.dom.Attribute createAttribute(String name, NodeList value){
    return factory.createAttribute( name, value );
  }

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

  /** Cache for the most-recently-looked-up element tag. */
  protected String tagNameCache = null;

  /** Cache for the most-recently-found element Handler.
   *	This can greatly speed up lookup in lists. 
   */
  protected Handler tagHandlerCache;

  public Handler getHandlerForTag(String tagname) {
    if (tagNameCache != null && tagname.equals(tagNameCache)) 
      return tagHandlerCache;

    tagNameCache = tagname;
    tagHandlerCache = (Handler) handlersByTag.at(tagname);
    if (tagHandlerCache != null) return tagHandlerCache;
    else if (context != null) {
      tagHandlerCache = context.getHandlerForTag(tagname);
      return tagHandlerCache;
    }

    if (defaultElementHandler == null) 
      defaultElementHandler = (Handler) getHandlerForType(NodeType.ELEMENT);
    tagHandlerCache = defaultElementHandler;
    return defaultElementHandler;
  }

  public void setHandlerForTag(String tagname, Handler newHandler) {
    handlersByTag.at(tagname, newHandler);
    handlerNames.push(tagname);
  }

  /** Called during parsing to return a suitable Handler for a new Text
   *	node.  It is up to the Parser to determine whether the text consists
   *	only of whitespace.
   */
  public Handler getHandlerForText(boolean isWhitespace) {
    if (defaultTextHandler == null) 
      defaultTextHandler = getHandlerForType(NodeType.TEXT);
    return defaultTextHandler;
  }

  /** Called during parsing to return a suitable Handler for a new
   *	entity reference.
   */
  public Handler getHandlerForEntity(String entityName) {
    if (defaultEntityHandler == null) 
      defaultEntityHandler = getHandlerForType(NodeType.ENTITY);
    return defaultEntityHandler;
  }

  /** Called during parsing to return a suitable Token for a generic
   *	Node, given the Node's type.
   */
  public Handler getHandlerForType(int nodeType) {
    Handler h =  (Handler) handlersByType[nodeType - MIN_TYPE];
    if (h != null) return h;
    else if (context != null) return context.getHandlerForType(nodeType);

    setHandlerForType(nodeType, new BasicHandler());
    return (Handler) handlersByType[nodeType - MIN_TYPE];
  }

  public void setHandlerForType(int nodeType, Handler newHandler) {
    if (nodeType < MIN_TYPE || nodeType >= MAX_TYPE) {
      // === At this point we need to resize handlersByType
    }
    handlersByType[nodeType - MIN_TYPE] = newHandler;
  }

  public boolean isLocked() { return locked; }

  public void setIsLocked(boolean value) { locked = value; }


  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/

  /** Return a Parser suitable for parsing a character stream
   *	according to the Tagset.  The Parser may (and probably will)
   *	know the Tagset's actual implementation class, so it can use
   *	specialized operations not described in the Tagset interface.
   */
  public Parser createParser() {
    Parser p = new crc.dps.parse.BasicParser();
    p.setTagset(this);
    return p;
  }

  /** === does not appear to be needed === */
  public int checkEndNesting(Token t, Context c) {
    return 0;			// ===
  }

  /** === does not appear to be needed === */
  public int checkElementNesting(Token t, Context c) {
    // === checkElementNesting needs to use t's handler.  It's a start
    //	   tag from this tagset, so it should have one.

    return 0;			// ===
  }

  /** Called during parsing to return a suitable start tag Token.
   */
  public Token createStartToken(String tagname,
				AttributeList attributes,
				Parser p){
    Handler h = getHandlerForTag(tagname);
    return new BasicToken(tagname, -1, attributes, h);
  }

  /** Called during parsing to return an end tag Token.  The Token will not
   *	have a handler.
   */
  public Token createEndToken(String tagname, Parser p) {
    return new BasicToken(tagname, 1);
  }

  /** Called during parsing to return a suitable Token for a generic
   *	Node with String content. 
   */
  public Token createToken(int nodeType, String data, Parser p){
    Handler h = getHandlerForType(nodeType);
    return new BasicToken(nodeType, data, h);
  }

  /** Called during parsing to return a suitable Token for a generic
   *	Node with a name, and String content.
   */
  public Token createToken(int nodeType, String name, String data, Parser p){
    Handler h = getHandlerForType(nodeType);
    return new BasicToken(nodeType, name, data, h);
  }

  /** Called during parsing to return a suitable Token for a new Text.
   */
  public Token createTextToken(String text, Parser p){
    boolean w = Util.isWhiteSpace(text);
    Handler h = getHandlerForText(w);
    Token t = new BasicToken(text, h);
    t.setIsWhitespace(w);
    return t;
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
    return handlerNames.elements();
  }

  /** Returns an Enumeration of the element names defined in this table and
   *	its context. */
  public Enumeration allNames() {
    if (allNames == null) {
      allNames = new List(elementNames());
      if (context != null) allNames.append(context.allNames());
    }
    return allNames.elements();
  }

  /************************************************************************
  ** Initialization:
  ************************************************************************/

  /** Define a tag for a non-active element.
   *
   *	This assumes that all element handlers are subclasses of BasicHandler. 
   *	It's a safe assumption as long as <code>defTags</code> is used to
   *	initialize the entire table. <p>
   *
   * @param tag the name of the Element to define
   * @param notIn a String of blank-separated tag names representing the
   *	elements that will be ended if the tag being defined is found
   *	inside them.
   * @param syntax 1 for ordinary elements, -1 for empty elements.
   * @return the Handler in case more setup needs to be done.
   */
  protected BasicHandler defTag(String tag, String notIn, int syntax) {
    BasicHandler h = (BasicHandler) handlersByTag.at(tag);
    boolean parseEnts = syntax > 0 && (syntax & NO_ENTITIES) == 0;
    boolean parseElts = syntax > 0 && (syntax & NO_ELEMENTS) == 0;
    if (h == null) h = new BasicHandler(syntax, parseElts, parseEnts);
    if (notIn != null) {
      Enumeration nt = new java.util.StringTokenizer(notIn);
      while (nt.hasMoreElements()) {
	h.setImplicitlyEnds(nt.nextElement().toString());
      }
    }
    setHandlerForTag(tag, h);
    return h;
  }

  /** Define a tag with a named handler class.
   *
   *	This assumes that all element handlers are subclasses of BasicHandler. 
   *	It's a safe assumption as long as <code>defTags</code> is used to
   *	initialize the entire table. <p>
   *
   * @param tag the name of the Element to define
   * @param notIn a String of blank-separated tag names representing the
   *	elements that will be ended if the tag being defined is found
   *	inside them.
   * @param syntax 1 for ordinary elements, -1 for empty elements.
   * @param cname the name of the handler's class.  If <code>null</code>,
   *	the tag is used.  GenericHandler is used if the specified class 
   *	does not exist.
   * @return the Handler in case more setup needs to be done.
   */
  protected GenericHandler defTag(String tag, String notIn, int syntax,
				  String cname) {
    GenericHandler h = null;
    boolean parseEnts = syntax > 0 && (syntax & NO_ENTITIES) == 0;
    boolean parseElts = syntax > 0 && (syntax & NO_ELEMENTS) == 0;
    boolean expand    = syntax > 0 && (syntax & NO_EXPAND)   == 0;
    try {
      String name = (cname == null)? tag : cname;
      Class c = NameUtils.loadClass(name, "crc.dps.handle.");
      if (c == null) {
	c = NameUtils.loadClass(name+"Handler", "crc.dps.handle.");
      }
      if (c != null) h = (GenericHandler)c.newInstance();
      h.setElementSyntax(syntax);
      h.setParseEntitiesInContent(parseEnts);
      h.setParseElementsInContent(parseElts);
    } catch (Exception e) { 
    }
    if (h == null) {
      h = new GenericHandler(syntax, parseElts, parseEnts);
      if (expand) h.setExpandContent(true);
    }
    if (notIn != null) {
      Enumeration nt = new java.util.StringTokenizer(notIn);
      while (nt.hasMoreElements()) {
	h.setImplicitlyEnds(nt.nextElement().toString());
      }
    }
    setHandlerForTag(tag, h);
    return h;
  }

  /** Define a set of syntax tags with a specified implicitlyEnds table.
   *	If the tags are already defined (e.g. they are actors or empty),
   *	simply append to the implicitlyEnds table. <p>
   *
   *	This assumes that all element handlers are subclasses of BasicHandler. 
   *	It's a safe assumption as long as <code>defTags</code> is used to
   *	initialize the entire table. <p>
   *
   * @param tags a String of blank-separated tag names.
   * @param notIn a String of blank-separated tag names representing the
   *	elements that will be ended if the tag being defined is found
   *	inside them.
   * @param syntax 1 for ordinary elements, -1 for empty elements.
   */
  protected void defTags(String tags, String notIn, int syntax) {
    Enumeration e  = new java.util.StringTokenizer(tags);
    while (e.hasMoreElements()) {
      String tag = e.nextElement().toString();
      defTag(tag, notIn, syntax);
    }
  }

  /** Define a set of active tags with a specified syntax.  The tag names
   *	are mapped into handler class names.
   *
   * @param tags a String of blank-separated tag names.
   * @param notIn a String of blank-separated tag names representing the
   *	elements that will be ended if the tag being defined is found
   *	inside them.
   * @param syntax 11 for ordinary elements, -1 for empty elements.
   */
  protected void defActive(String tags, String notIn, int syntax) {
    Enumeration e  = new java.util.StringTokenizer(tags);
    while (e.hasMoreElements()) {
      String tag = e.nextElement().toString();
      defTag(tag, notIn, syntax, (String)null);
    }
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public BasicTagset() {
    factory = new BasicDOMFactory();
  }

  public BasicTagset(String name) {
    factory = new BasicDOMFactory();
    // set name attribute of element.
  }

  public BasicTagset(Tagset previousContext) {
    factory = previousContext.getFactory();
    context = previousContext;
  }

  public BasicTagset(DOMFactory f) {
    factory = f;
  }
}
