////// ParseStack.java: Parse stack frame
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import crc.dom.Node;
import crc.dom.NodeList;

/**
 * The implementation of a Processor's parse stack, using a linked list.
 *
 *	Unlike the Input stack, in which each InputStack object has a
 *	pointer to the corresponding Input object, ParseStack objects
 *	contain all of the necessary state information, which saves a
 *	level of indirection. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * 
 * @see crc.dps.Token
 * @see crc.dps.Processor
 * @see java.util.Enumeration
 * @see java.util.NoSuchElementException
 */

public class ParseStack extends StackFrame {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Node node;
  protected Token token;

  protected boolean parsing;
  protected boolean expanding;
  protected boolean passing;

  /************************************************************************
  ** State Accessors:
  ************************************************************************/

  /** Returns the current Node of a parse tree under construction. 
   *	Note that if we are constructing a parse tree and the current
   *	Token is null (indicating a sequence of Tokens balanced for start
   *	and end tags), getNode returns the next non-null Node up the stack.
   */
  public final Node getNode() {
    return node;
  }

  /** Returns the start tag Token corresponding to the current Node. 
   *	Note that the current Token can be null, indicating a sequence of
   *	Nodes which may be part of a larger NodeList but within which every
   *	start tag must have a corresponding end tag.
   */
  public final Token getToken() {
    return token;
  }


  /** Returns the tag name of the corresponding element.
   */
  public final String getTagName() {
    return token == null? null : token.getTagName();
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
  protected ParseStack entityContext;

  protected Tagset handlers;
  protected ParseStack handlerContext;

  /************************************************************************
  ** Name Space Accessors:
  ************************************************************************/

  /** Returns the current EntityTable. 
   *	Goes up the context stack if no entities are defined at this level.  
   */
  public final EntityTable getEntities() {
    if (entities != null) return entities;
    else if (handlerContext == null) return null;
    else return handlerContext.getEntities();
  }

  /** Returns the current local EntityTable. 
   *	Returns null if no entities are defined at this level.  
   */
  public final EntityTable getLocalEntities() {
    return entities;
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
    if (handlers != null) return handlers;
    else if (handlerContext == null) return null;
    else return handlerContext.getHandlers();
  }

  /** Returns the current local Tagset. 
   *	Returns null if no handlers are defined at this level.  
   */
  public final Tagset getLocalHandlers() {
    return handlers;
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

    parseStack = new ParseStack(parseStack);

    depth++;
    parseStack.node 		= node;
    parseStack.token		= token;
    parseStack.parsing		= parsing;
    parseStack.expanding	= expanding;
    parseStack.passing		= passing;
    parseStack.entities		= entities;
    parseStack.entityContext	= entityContext;
    parseStack.handlers		= handlers;
    parseStack.handlerContext	= handlerContext;
  }

  /** Push a Token onto the parse stack. */
  public void pushToken(Token aToken){
    push();
    node = null;
    token = aToken;
  }

  /** Pop the parse stack. 
   * @return <code>false</code> if the parse stack is empty
   */
  public boolean popParseStack() {
    if (parseStack == null) return false;

    depth--;
    node 		= parseStack.node;
    token 		= parseStack.token;
    parsing 		= parseStack.parsing;
    expanding		= parseStack.expanding;
    passing 		= parseStack.passing;
    entities 		= parseStack.entities;
    entityContext 	= parseStack.entityContext;
    handlers 		= parseStack.handlers;
    handlerContext 	= parseStack.handlerContext;
    parseStack 		= parseStack.parseStack;

    return true;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public ParseStack() {
    super(0);
  }

  public ParseStack(ParseStack next) {
    super((next == null)? 0 : next.depth + 1);
    parseStack = next;
  }

}
