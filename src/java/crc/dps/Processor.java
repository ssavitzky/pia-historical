////// Processor.java: Document Processor interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

/**
 * The interface for a document Processor. 
 *	A Processor maintains two stacks: 
 *	<ol><li> an `input stack' of Input objects that are a source of Token
 *		 objects to be processed.
 *	    <li> a `parse stack' consisting of a parse tree under construction
 *		 along with additional processor state.
 *	</ol>
 *
 *	There are two ways in which an object can interface to a 
 *	Processor:
 *
 *	<ol>
 *	    <li> ``Pull mode'' -- the object requests each individual
 *		 Token.  This treats the Processor as an Input.  Nothing
 *		 special has to be done in this case.
 *
 *	    <li> ``Push mode'' -- an Output registers itself with the
 *		 Processor, which then feeds (pushes) Token objects
 *		 to the Output as they become available.  In this case
 *		 the Processor runs ``to completion,'' which is somewhat
 *		 more efficient.
 *	</ol>
 *
 *	A Processor will normally ensure that any element started inside
 *	an Input is ended when that Input is popped off the stack.<p>
 *
 * === NOTE: Both Parser and Processor need DTD and parse stack info. ===
 * === it's up to the Parser to associate Handler, etc. with Token. ===
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Token
 * @see crc.dps.Input */

package crc.dps;
import crc.dom.Node;
import crc.dom.NodeList;

public interface Processor extends Input {

  /************************************************************************
  ** Pushing Output from the Processor:
  ************************************************************************/

  /** Registers an Output object for the Processor.  The Processor 
   *	will call the Output's <code>nextToken</code> method with
   *	each Token as it becomes available, and finally call the 
   *	Output's <code>endOutput</code> function.
   */
  public void setOutput(Output anOutput);

  /** Run the Processor, pushing a stream of Token objects at its
   *	registered Output, until the Output's <code>nextToken</code>
   *	method returns <code>false</code>.
   */
  public void run();


  /************************************************************************
  ** Context Operations:
  ************************************************************************/

  /** Obtain the Handler for a given tag. */
  public Handler getHandlerForTag(String tag);

  /** Obtain the Handler for a given Node. */
  public Handler getHandlerForNode(Node aNode);

  /** Obtain the value associated with a given entity. */
  public NodeList getEntityValue(String name);


  /************************************************************************
  ** Input Stack Operations:
  ************************************************************************/

  /** Return the input stack */
  public InputStack getInputStack();

  /** Push an Input onto the input stack. */
  public void pushInput(Input anInput);

  /** Push an InputStackFrame (specialized Input) onto the stack. */
  public void pushFrame(InputStackFrame aFrame);

  /** Push a Token onto the input stack.
   *	This is a convenience function, included in the Processor interface 
   *	mainly for increased efficiency.
   */
  public void pushInput(Token aToken);

  /** Push a Token onto the input stack to be expanded as a start tag,
   *	content, and end tag.
   */
  public void pushInto(Token aToken);

  /** Push a Node onto the input stack.
   *	This is a convenience function, included in the Processor
   *	interface mainly for increased efficiency.  The Node is
   *	converted to a Token using the Processor's current Tagset.
   */
  public void pushInput(Node aNode);

  /** Push a Node onto the input stack to be expanded as a start tag,
   *	content, and end tag.  Token conversion is done using the 
   *	Processor's current Tagset.
   */
  public void pushInto(Node aNode);

  /** Push a NodeList onto the input stack.
   *	This is a convenience function, included in the Processor
   *	interface mainly for increased efficiency.  The Nodes are
   *	converted to Tokens using the Processor's current Tagset.
   */
  public void pushInput(NodeList aNodeList);


  /************************************************************************
  ** Parse Stack Operations:
  ************************************************************************/

  /** Push a Node onto the parse stack. */
  public void pushNode(Node aNode);

  /** Push a Token onto the parse stack. */
  public void pushToken(Token aToken);

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
  ** Control Operations:
  ************************************************************************/

  /************************************************************************
  ** Operations Used by Handlers:
  ************************************************************************/


}
