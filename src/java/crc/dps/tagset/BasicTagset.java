////// BasicTagset.java: Node Handler Lookup Table interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.tagset;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.AttributeList;
import crc.dom.DocumentType;
import crc.dom.DOMFactory;

import java.util.Enumeration;
import java.io.PrintStream;

import crc.dps.Syntax;
import crc.dps.NodeType;
import crc.dps.Tagset;
import crc.dps.Handler;
import crc.dps.Parser;
import crc.dps.Context;
import crc.dps.EntityTable;
import crc.dps.Namespace;

import crc.dps.active.*;
import crc.dps.util.*;
import crc.dps.handle.*;

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
 *	BasicTagset implements ActiveElement, and so can be treated as a
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

public class BasicTagset extends ParseTreeGeneric implements Tagset {

  /************************************************************************
  ** Syntax Flags: (for convenience in setting up tables):
  ************************************************************************/

  /** Syntax for an empty element.  (From Syntax) */
  public final static int EMPTY   = Syntax.EMPTY;
  /** Syntax for a normal element.  The contents are expanded. */
  public final static int NORMAL  = Syntax.NORMAL;
  /** Syntax for a quoted element:  contents are parsed but not expanded. */
  public final static int QUOTED  = Syntax.QUOTED;
  /** Syntax for a literal element: no parsing in context. */
  public final static int LITERAL = Syntax.LITERAL;

  /************************************************************************
  ** Data:
  ************************************************************************/

  protected Handler defaultElementHandler;

  protected Handler defaultTextHandler;

  protected Handler defaultEntityHandler;

  protected EntityTable entities = null;

  protected Table handlersByTag 	= new Table();
  protected Table handlersByAttr 	= new Table();
  protected List  handlerNames 		= new List();
  protected List  contextHandlerNames 	= null;

  protected int  MAX_TYPE = NodeType.MAX_TYPE;
  protected int  MIN_TYPE = NodeType.MIN_TYPE;
  protected Handler handlersByType[] = new Handler[MAX_TYPE - MIN_TYPE + 1];

  protected boolean locked = false;

  protected String paragraphElementTag = null;

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

  /** Include definitions from a given tagset. 
   */
  public void include(Tagset ts) {
    BasicTagset bts = (BasicTagset)ts; // === include is VERY kludgy. ===
    if (verbosity > 0) {
      message(1, getName() + " including " + bts.getName(), 2, true);
    }
    Table t = bts.handlersByTag;
    Enumeration e = t.keys();
    while (e.hasMoreElements()) {
      String tag = e.nextElement().toString();
      AbstractHandler h = (AbstractHandler)t.at(tag);
      setHandlerForTag(tag, h);
    }
  }

  /************************************************************************
  ** Entity Bindings:
  ************************************************************************/

  /** Return a namespace with a given name.  Returns the entity table 
   *	associated with the tagset (possibly in the context chain) having 
   *	the given name.
   */
  public Namespace getNamespace(String name) {
    if (name == null || name.equals(getName())) return entities;
    else if (name != null && context != null) return context.getNamespace(name);
    else return null;
  }

  public EntityTable getEntities() { return entities; }
  public void setEntities(EntityTable bindings) { entities = bindings; }

  /** Get the value of an entity, given its name. 
   * @return <code>null</code> if the entity is undefined.
   */
  public NodeList getEntityValue(String name) {
    ActiveEntity binding = getEntityBinding(name);
    return (binding != null)? binding.getValue() :  null;
  }

  /** Set the value of an entity. 
   */
  public void setEntityValue(String name, NodeList value) {
    ActiveEntity binding = getEntityBinding(name);
    if (binding != null) {
      binding.setValue(value);
    } else {
      if (entities == null) 
	entities = new BasicEntityTable();
      getEntities().setValue(name, value);
    } 
  }

  /** Get the binding (Entity node) of an entity, given its name. 
   * @return <code>null</code> if the entity is undefined.
   */
  public ActiveEntity getEntityBinding(String name) {
    return (entities == null) ? null : entities.getEntityBinding(name);
  }


  /************************************************************************
  ** Adding a Handler:
  ************************************************************************/

  /** Add the handler to the content of the tagset Node. */
  protected void addHandler(String name, int type, Handler newHandler) {
    ActiveNode h = (ActiveNode)newHandler;
    // === handlers can't deepcopy yet! === 
    if (h.getParentNode() == null) addChild(h);
    if (verbosity > 0) {
      message(1, getName() + " defining " + NodeType.getName(type)
	      + " " + name + ": " + h.getClass().getName(), 2, true);
    }
  }

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
    addHandler(tagname, NodeType.ELEMENT, newHandler);
  }

  public Handler getHandlerForAttr(String name) {
    return (Handler) handlersByAttr.at(name);
  }

  public void setHandlerForAttr(String name, Handler newHandler) {
    handlersByAttr.at(name, newHandler);
    String xname = "~attr~" + name;
    handlerNames.push(xname);
    addHandler(xname, NodeType.ELEMENT, newHandler);
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
    addHandler("~type~" + NodeType.getName(nodeType), nodeType, newHandler);
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

  /**Creates an ActiveElement; otherwise identical to CreateElement. */
  public ActiveElement createActiveElement(String tagname,
					   AttributeList attributes, 
					   boolean hasEmptyDelim){
    Handler h = getHandlerForTag(tagname);
    return h.createElement(tagname, attributes, hasEmptyDelim);
  }

  /** Creates an ActiveNode of arbitrary type with (optional) data. */
  public ActiveNode createActiveNode(int nodeType, String data) {
    return createActiveNode(nodeType, null, data);
  }

  /** Creates an ActiveNode of arbitrary type with name and (optional) data. */
  public ActiveNode createActiveNode(int nodeType, String name, String data) {
    Handler h = getHandlerForType(nodeType);
    return h.createNode(nodeType, name, data);
  }

  /** Creates an ActivePI node with name and data.
   */
  public ActivePI createActivePI(String name, String data) {
    Handler h = getHandlerForType(NodeType.PI);
    return (ActivePI) h.createNode(NodeType.PI, name, data);
  }

  /** Creates an ActiveAttribute node with name and value.
   */
  public ActiveAttribute createActiveAttribute(String name, NodeList value) {
    Handler h = getHandlerForType(NodeType.ATTRIBUTE);
    return new ParseTreeAttribute(name, value, h);
  }

  /** Creates an ActiveEntity node with name and value.
   */
  public ActiveEntity createActiveEntity(String name, NodeList value) {
    Handler h = getHandlerForType(NodeType.ENTITY);
    return (ActiveEntity) h.createNode(NodeType.ENTITY, name, value);
  }

  /** Creates an ActivePI node.
   */
  public ActiveComment createActiveComment(String data) {
    Handler h = getHandlerForType(NodeType.COMMENT);
    return new ParseTreeComment(data, h);
  }

  /** Creates an ActiveText node.  Otherwise identical to createText.
   */
  public ActiveText createActiveText(String text) {
    return createActiveText(text, false);
  }

  /** Creates an ActiveText node.  Otherwise identical to createText. */
  public ActiveText createActiveText(String text, boolean isIgnorable) {
    return createActiveText(text, isIgnorable, Test.isWhitespace(text));
  }

  /** Creates an ActiveText node.  Includes the <code>isWhitespace</code>
   *	flag, which would otherwise have to be tested for.
   */
  public ActiveText createActiveText(String text, boolean isIgnorable,
				     boolean isWhitespace) {
    Handler h = getHandlerForText(isWhitespace);
    ActiveText t = new ParseTreeText(text, h);
    t.setIsWhitespace(isWhitespace);
    t.setIsIgnorableWhitespace(isIgnorable);
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

  public String paragraphElementTag() { return paragraphElementTag; }
  public void setParagraphElementTag(String tag) {
    paragraphElementTag = tag;
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
  public Enumeration handlerNames() {
    return handlerNames.elements();
  }

  /** Returns an Enumeration of the element names defined in this table and
   *	its context, in order of definition (most recent last). */
  public Enumeration allHandlerNames() {
    if (context == null) return handlerNames();
    if (contextHandlerNames == null) {
      contextHandlerNames = new List(context.allHandlerNames());
    }
    List allNames = new List(contextHandlerNames);
    allNames.append(handlerNames());
    return allNames.elements();
  }

  /************************************************************************
  ** Convenience Functions:
  ************************************************************************/

  /** Convenience function to define a tag with a given syntax. */
  public Handler defTag(String tag, String notIn, String parents, int syntax,
			String cname, NodeList content) {
    BasicHandler h = null;
    if (cname == null && content == null) 
      h = defTag(tag, notIn, syntax);
    else
      h = defTag(tag, notIn, syntax, cname);
    if (content != null) Copy.appendNodes(content, h);
    if (parents != null) {
      Enumeration nt = new java.util.StringTokenizer(parents);
      while (nt.hasMoreElements()) {
	h.setIsChildOf(nt.nextElement().toString());
      }
    }
    return h;
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
   * @param syntax see codes in <a href="crc.dps.Syntax.html">Syntax</a>
   * @return the Handler in case more setup needs to be done.
   */
  protected BasicHandler defTag(String tag, String notIn, int syntax) {
    BasicHandler h = (BasicHandler) handlersByTag.at(tag);
    if (h == null) h = new BasicHandler(syntax);
    else if (syntax != 0) h.setSyntaxCode(syntax);
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
   * @param syntax see codes in <a href="crc.dps.Syntax.html">Syntax</a>
   * @param cname the name of the handler's class.  If <code>""</code>,
   *	the tag is used.  If null, GenericHandler is used.
   * @return the Handler in case more setup needs to be done.
   */
  protected GenericHandler defTag(String tag, String notIn, int syntax,
				  String cname) {
    GenericHandler h =
      (GenericHandler) crc.dps.handle.Loader.loadHandler(tag, cname, 
							 syntax, false);
    if (h == null) { h = new GenericHandler(syntax); }
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
   * @param syntax see codes in <a href="crc.dps.Syntax.html">Syntax</a>
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
   * @param syntax see codes in <a href="crc.dps.Syntax.html">Syntax</a>
   */
  protected void defActive(String tags, String notIn, int syntax) {
    Enumeration e  = new java.util.StringTokenizer(tags);
    while (e.hasMoreElements()) {
      String tag = e.nextElement().toString();
      defTag(tag, notIn, syntax, "");
    }
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public BasicTagset() {
    setHandlerForType(NodeType.ENTITY, EntityHandler.DEFAULT);
    setHandlerForType(NodeType.TEXT, TextHandler.DEFAULT);
  }

  public BasicTagset(String name) {
    this();
    this.name = name;
    // === set name attribute of element.
  }

  public BasicTagset(Tagset previousContext) {
    this();
    context = previousContext;
  }

  /************************************************************************
  ** Debugging:
  **	This is a subset of crc.util.Report.
  ************************************************************************/

  protected int verbosity = 0;
  protected static PrintStream log = null;

  public int 	getVerbosity() 		{ return verbosity; }
  public void 	setVerbosity(int value) { verbosity = value; }
  public PrintStream getLog() 		{ return log; }
  public void 	setLog(PrintStream stream) { log = stream; }

  public void message(int level, String text, int indent, boolean endline) {
    if (verbosity < level) return;
    String s = "";
    for (int i = 0; i < indent; ++i) s += " ";
    s += text;
    if (endline) log.println(s); else log.print(s);
  }

  public final void debug(String message) {
    if (verbosity >= 2) log.print(message);
  }

  public final void debug(String message, int indent) {
    if (verbosity < 2) return;
    String s = "";
    for (int i = 0; i < indent; ++i) s += " ";
    s += message;
    log.print(s);
  }

  public String logString(String s) { return Log.string(s); }

}
