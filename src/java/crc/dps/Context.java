////// Context.java: Document processing context interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Element;

/**
 * The interface for a document parsing or processing context stack. <p>
 *
 *	A Context amounts to a parse stack.  It maintains a parse tree
 *	being constructed or traversed, and any additional state that
 *	applies at each level.  It need not be a stack or linked list,
 *	although this is usually the simplest implementation. <p>
 *
 *	At any given level in the Context stack there is a current set of
 *	bindings for entities and handlers, and a current Node being 
 *	operated on, normally by appending to it.  There is also a current
 *	Token.  Immediately before starting (pushing) a new Context,
 *	the current Token should correspond to the Element being pushed.
 *	That way, immediately after popping, it will have any handlers that
 *	need to be called to finalize the operation. <p>
 *
 *	Note that the current Node does <em>not</em> have to be a child
 *	of the current Node one level up in the stack.  It is perfectly
 *	legitimate to build a parentless parse tree, manipulate it in 
 *	some way, and <em>then</em> append it.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Processor
 * @see crc.dps.Token
 */

public interface Context {

  /************************************************************************
  ** Bindings:
  ************************************************************************/

  /** Obtain the current Handler bindings. */
  public Tagset getHandlers();

  /** Set the current Handler bindings. */
  public void setHandlers(Tagset bindings);

  /** Obtain the current Entity bindings. */
  public EntityTable getEntities();

  /** Set the current Entity bindings. */
  public void setEntities(EntityTable bindings);


  /************************************************************************
  ** Parse State:
  ************************************************************************/

  /** Test whether a parse tree is under construction. */
  public boolean isParsing();

  /** Test whether the output is receiving separate tokens for start tags,
   *	content, and end tags. 
   */
  public boolean isPassing();

  /** Test whether handler actions associated with tokens are being called.
   */
  public boolean isExpanding();

  /** Set the flag that determines whether a parse tree is being constructed. 
   *	Applies to this Node in the parse tree, and its children.
   */
  public void setParsing(boolean value);

  /** Set the flag that determines whether a parse tree is being constructed. 
   *	Applies to this Node in the parse tree, and its children.
   */
  public void setPassing(boolean value);

  /** Set the flag that determines whether handlers are being called.
   *	Applies to this Node in the parse tree, and its children.
   */
  public void setExpanding(boolean value);

  /************************************************************************
  ** Information Needed for Parsing:
  ************************************************************************/

  /** Return true if we are currently nested inside an element with
   *	the given tag.
   *
   *	Note that this may give incorrect results if different regions
   *	of the Document have different case sensitivities for tagnames!
   *
   * @param tag the tag to check for
   * @param ignoreCase <code>true</code> if tag comparison ignores case
   * @param stopDepth do not compare nodes below this depth.
   */
  public boolean insideElement(String tag, boolean ignoreCase, int stopDepth);

  /** Return the tag of the immediately-surrounding Element, that is, the Node
   *	being appended to or traversed.  
   */
  public String elementTag();

  /** Return the current depth of nesting in the parse stack.  It is usual
   *	for the Node at depth 0 to be a Document.
   *
   * @see crc.dom.Document
   */
  public int getDepth();

  /************************************************************************
  ** Current State:
  ************************************************************************/

  /** Get current Token. */
  public Token getToken();

  /** Set current Token. */
  public void setToken(Token aToken);

  /** Get the current <em>parent</em> Node.  This is the node which will 
   *	normally become the parent of the current Token or a Node derived
   *	from it.  <p>
   *
   * @see #getToken
   * @see #appendNode
   */
  public Node getNode();

  /** Set the current <em>parent</em> Node.  This is the node which will 
   *	normally become the parent of the current Token or a Node derived
   *	from it.  <p>
   *
   * @see #getToken
   * @see #appendNode
   */
  public void setNode(Node aNode);

  /** Set the current <em>parent</em> Node (presumably an Element) and
   *	its corresponding <code>tagName</code>.  This is the node which will 
   *	normally become the parent of the current Token or a Node derived
   *	from it.  If the Node is known to be an Element, this is more
   *	efficient than casting it in order to get the tagname. <p>
   *
   * @param aNode the new current Node.  It may be null if no Node is
   *	actually being constructed; this allows the current element nesting
   *	to be well-defined even in the absence of an actual node.
   * @param aTagName should be <code>null</code> if <code>aNode</code> is
   *	not an Element; otherwise it should be equal to 
   *	<code>((Element)aNode).getTagName()</code>.
   * @see crc.dom.Element
   * @see #setNode
   */
  public void setNode(Node aNode, String aTagName);

  /************************************************************************
  ** Parse Tree Construction:
  ************************************************************************/

  /** Append a Node to the current parent.
   *	If the new Node's parent is the current Node, nothing happens.
   *	If its parent is non-<code>null</code> and <em>different</em>
   *	from the current Node, it will be re-parented.  <p>
   *
   * @see #getNode
   * @see #setNode
   * @see crc.dps.NodeType
   */
  public void appendNode(Node aNode);

  /** Append the nodes in a NodeList to the current parent.
   *	It is usual for a Handler to return a NodeList rather than a Node
   *	when computing a value.  <p>
   *
   * @see #getNode
   * @see #setNode
   * @see crc.dps.Handler
   */
  public void appendNodes(NodeList aNodeList);

  /************************************************************************
  ** Context Stack construction:
  ************************************************************************/

  /** Construct a new Context linked to this one.  <p>
   *
   *	Recursive operations like <code>expand</code> use this to create a new
   *	evaluation stack frame when descending into an Element.
   */
  public Context newContext(Node aNode, String aTagName);

  /************************************************************************
  ** Expansion:
  ************************************************************************/

  /** Expand a node. 
   *	Ordinary nodes are deep-copied.  Entities are replaced by their
   *	values, if they have any.  Tokens know how to expand themselves.
   *	Entities and tokens in the values of Attributes are also expanded.
   */
  public Token expand(Node aNode);

  /** Expand all the nodes in a NodeList. */
  public Token expand(NodeList aNodeList);

  /** Called by an expansion Handler in order to return <code>aNode</code>
   *	as its result.  The return value is <code>null</code>, allowing
   *	the Handler to end with <br>
   *	<code>return result(<em>aNode</em>);</code><p>
   *
   * @param aNode a Node to be appended to the parse tree under construction,
   *	and/or passed to the Output.
   * @return <code>null</code>.
   */
  public Token result(Node aNode);

  /** Called by an expansion Handler in order to return <code>aNode</code>
   *	as its result.  The return value is <code>null</code>, allowing
   *	the Handler to end with <br>
   *	<code>return result(<em>aNode</em>);</code><p>
   *
   * @param aNode a Node to be appended to the parse tree under construction.
   * @param aToken a Token to be passed to the output.
   * @return <code>null</code>.
   */
  public Token result(Node aNode, Token aToken);

  /** Called by an expansion Handler in order to return <code>aNodeList</code>
   *	as its result.  The return value is <code>null</code>, allowing
   *	the Handler to end with <br>
   *	<code>return results(<em>aNodeList</em>);</code><p>
   *
   * @param aNodeList a NodeList, the contents of which are to be appended to
   *	the parse tree under construction, and/or passed to the Output.
   * @return <code>null</code>.
   */
  public Token results(NodeList aNodeList);
}
