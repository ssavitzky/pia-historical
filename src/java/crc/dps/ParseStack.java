////// ParseStack.java: Parse stack frame
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Element;
import crc.dom.NodeEnumerator;
import crc.dom.ArrayNodeList;
import crc.dom.Attribute;

/**
 * The implementation of a Processor's parse stack, using a linked list.
 *
 *	Unlike the Input stack, in which each InputStack object has a
 *	pointer to the corresponding Input object, ParseStack objects
 *	contain all of the necessary state information, which saves a
 *	level of indirection. <p>
 *
 *	The implementation is a little twisted because a Processor
 *	has additional state beyond a simple ParseStack, so it wants
 *	to stay on top of the stack.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * 
 * @see crc.dps.Token
 * @see crc.dps.Processor
 * @see java.util.Enumeration
 * @see java.util.NoSuchElementException
 */

public class ParseStack extends StackFrame implements Context {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Node node;
  protected String tagName;

  protected Token token;

  protected boolean parsing;
  protected boolean expanding;
  protected boolean passing;

  /************************************************************************
  ** State Accessors:
  ************************************************************************/

  public final Node getNode() 		{ return node; }

  public void  setNode(Node aNode) {
    node = aNode;
  }

  public void setNode(Node aNode, String aTagName) {
    node = aNode;
    tagName = aTagName;
  }

  public final Token getToken() 	{ return token; }
  public void  setToken(Token aToken) 	{ token = aToken; }

  /** Returns the tag name of the corresponding element.
   */
  public final String getTagName() {
    return tagName;
  }


  /** Is the Processor currently constructing a parse tree? */
  public final boolean isParsing() {
    return parsing;
  }

  /** Is the Processor currently executing handlers? */
  public final boolean isExpanding() {
    return expanding;
  }

  /** Is the Processor currently passing start tags? */
  public final boolean isPassing() {
    return passing;
  }

  /** Set the flag that determines whether a parse tree is being constructed. 
   *	Applies to this Node in the parse tree, and its children.
   */
  public void setParsing(boolean value) { parsing = value; }

  /** Set the flag that determines whether a parse tree is being constructed. 
   *	Applies to this Node in the parse tree, and its children.
   */
  public void setPassing(boolean value) { passing = value; }

  /** Set the flag that determines whether handlers are being called.
   *	Applies to this Node in the parse tree, and its children.
   */
  public void setExpanding(boolean value) { expanding = value; }


  /************************************************************************
  ** Name Spaces:
  ************************************************************************/

  protected EntityTable entities;
  protected EntityTable localEntities;
  protected ParseStack entityContext;

  protected Tagset handlers;
  protected Tagset localHandlers;
  protected ParseStack handlerContext;

  /************************************************************************
  ** Name Space Accessors:
  ************************************************************************/

  /** Returns the current EntityTable. 
   *	Goes up the context stack if no entities are defined at this level.  
   */
  public final EntityTable getEntities() {
    return entities;
  }

  public final void setEntities(EntityTable bindings) {
    localEntities = bindings;
    entities = bindings;
  }

  /** Returns the current local EntityTable. 
   *	Returns null if no entities are defined at this level.  
   */
  public final EntityTable getLocalEntities() {
    return localEntities;
  }

  /** Returns the next stack frame in which entities are defined. 
   *	Using <code>entityContext</code> instead of <code>parseStack</code>
   *	allows the search for an entity's value to skip over unproductive
   *	regions of the stack.
   */
  public final ParseStack getEntityContext() {
    return entityContext;
  }

  /** Returns the current Tagset. 
   *	Goes up the context stack if no handlers are defined at this level.  
   */
  public final Tagset getHandlers() {
    if (handlers == null) {
      localHandlers = new crc.dps.tagset.BasicTagset();
      handlers = localHandlers;
    }
    return handlers;
  }

  public final void setHandlers(Tagset bindings) { 
    handlers = bindings;
    localHandlers = bindings;
  }

  /** Returns the current local Tagset. 
   *	Returns null if no handlers are defined at this level.  
   */
  public final Tagset getLocalHandlers() {
    return localHandlers;
  }

  /** Returns the next stack frame in which entities are defined. 
   *	Using <code>handlerContext</code> instead of <code>parseStack</code>
   *	allows the search for an handler's value to skip over unproductive
   *	regions of the stack.
   */
  public final ParseStack getHandlerContext() {
    return handlerContext;
  }

  /************************************************************************
  ** Information Needed for Parsing:
  ************************************************************************/

  public final boolean insideElement(String tag, boolean ignoreCase,
				     int stopDepth) {
    if (ignoreCase) tag = tag.toLowerCase();
    for (ParseStack s = parseStack;
	 s != null && s.depth >= stopDepth;
	 s = s.parseStack) {
      String etag = s.getTagName();
      if (etag == null) continue;
      if (ignoreCase) etag = etag.toLowerCase();
      if (tag.equals(etag)) return true;
    } 
    return false;
  }

  public final String elementTag() {
    return getTagName();
  }


  /************************************************************************
  ** Parse Tree Construction:
  ************************************************************************/

  /** Append a Node to the current parent.
   *	If the new Node's parent is the current Node, nothing happens.
   *	If its parent is non-<code>null</code> and <em>different</em>
   *	from the current Node, it will be reparented.  If the Node's
   *	type is NODELIST, its children will be appended (spliced in). <p>
   *
   * @see #getNode
   * @see #setNode
   * @see crc.dps.NodeType
   */
  public void appendNode(Node aNode) { Util.appendNode(aNode, node); }

  /** Append the nodes in a NodeList to the current parent.
   *	It is usual for a Handler to return a NodeList rather than a Node
   *	when computing a value.  <p>
   *
   * @see #getNode
   * @see #setNode
   * @see crc.dps.Handler
   */
  public void appendNodes(NodeList aNodeList) {
    if (aNodeList == null) return;
    NodeEnumerator e = aNodeList.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      appendNode(node);
    }
  }

  /************************************************************************
  ** StackFrame:
  ************************************************************************/

  /** The pointer to the next ParseStack frame in the stack. */
  protected ParseStack parseStack = null;

  
  /************************************************************************
  ** Basic stack traversal:
  ************************************************************************/

  /** Returns the next ParseStack frame in the linked list.
   */
  public final ParseStack getNext() {
    return parseStack;
  }

  public final StackFrame getNextFrame() {
    return parseStack;
  }


  /************************************************************************
  ** Pushing and Popping:
  ************************************************************************/

  protected void push() {

    parseStack = new ParseStack(this);
    depth++;

    if (localHandlers != null) handlerContext = parseStack;
    localHandlers = null;

    if (localEntities != null) entityContext = parseStack;
    localEntities = null;
  }

  public Context newContext(Node aNode, String aTagName) {
    return new ParseStack(this, aNode, aTagName);
  }

  /** Push a Token onto the parse stack. */
  public void pushToken(Token aToken){
    push();
    node = null;
    token = aToken;
    tagName = aToken.getTagName();
  }

  /** Push a Token onto the parse stack along with its tagname. */
  public void pushToken(Token aToken, String aTagName){
    push();
    node = null;
    token = aToken;
    tagName = aTagName;
  }

  /** Pop the parse stack. 
   * @return <code>false</code> if the parse stack is empty
   */
  public boolean popParseStack() {
    if (parseStack == null) return false;

    depth--;
    copy(parseStack);
    return true;
  }

  public void copy(ParseStack s) {
    node 		= s.node;
    token 		= s.token;
    tagName		= s.tagName;
    parsing 		= s.parsing;
    expanding		= s.expanding;
    passing 		= s.passing;

    entities 		= s.entities;
    localEntities	= s.localEntities;
    entityContext 	= s.entityContext;

    handlers 		= s.handlers;
    localHandlers	= s.localHandlers;
    handlerContext 	= s.handlerContext;
    parseStack 		= s.parseStack;
    depth		= s.depth;
  }

  /************************************************************************
  ** Expansion:
  ************************************************************************/

  /** Expand a node. */
  public Token expand(Node aNode) {
    if (aNode instanceof Token) return ((Token)aNode).expand(this);
    Tagset tagset = getHandlers();
    EntityTable ents = getEntities();

    switch (aNode.getNodeType()) {
    case NodeType.ELEMENT:
      Element elt = (Element)aNode;
      Element ne = Util.expandAttrs(elt, tagset, ents);
      // Note that we have to create a new context to expand the children in.
      String tag = elt.getTagName();
      Context c = newContext(ne, tag);
      c.expand(elt.getChildren());
      return result(ne);

    case NodeType.NODELIST:
    case NodeType.TOKENLIST:
      return expand(aNode.getChildren());

    case NodeType.ENTITY:
      crc.dom.Entity ent = (crc.dom.Entity)aNode;
      NodeList v = (ents == null)? null 
	: entities.getValueForEntity(ent.getName(), false);
      if (v != null) return expand(v);
      // if unbound, fall through to copy...

    default:
      // === Worry about children
      return result(Util.copyNode(aNode, tagset));
    }
  }

  /** Expand all the nodes in a nodelist */
  public Token expand(NodeList aNodeList) {
    if (aNodeList == null) return null;
    ArrayNodeList nl = new ArrayNodeList();
    crc.dom.NodeEnumerator e = aNodeList.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      expand(node);
    }
    return null;
  }

  public Token result(Node aNode) {
    if (parsing) appendNode(aNode);
    return null;
  }

  /** A ParseStack doesn't have an output, so just drop aToken on the floor. */
  public Token result(Node aNode, Token aToken) {
    if (parsing) appendNode(aNode);
    return null;
  }

  public Token results(NodeList aNodeList) {
    if (parsing) appendNodes(aNodeList);
    return null;
  }


  /************************************************************************
  ** Construction:
  ************************************************************************/

  public ParseStack() {
    super(0);
  }

  /** Create an exact copy of the given parseStack node.  In general
   *	this is done by the top node in a parse stack, which puts the
   *	new node in its <code>parseStack</code> link and goes on being
   *	the top node. */
  public ParseStack(ParseStack next) {
    copy(next);
  }

  /** Create a new ParseStack node linked to the given one.  */
  public ParseStack(ParseStack s, Node aNode, String aTagName) {
    node 		= aNode;
    token 		= null;
    tagName		= aTagName;
    parsing 		= s.parsing;
    expanding		= s.expanding;
    passing 		= s.passing;

    entities 		= s.entities;
    localEntities	= null;
    entityContext 	= (s.localEntities == null)? s : s.entityContext;

    handlers 		= s.handlers;
    localHandlers	= null;
    handlerContext 	= (s.localHandlers == null)? s : s.handlerContext;
    parseStack 		= s;
    depth		= s.depth + 1;
  }


}
