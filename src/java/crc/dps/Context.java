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
 *	A Context amounts to a parse stack or execution stack.  It maintains a
 *	parse tree being constructed or traversed, and any additional state
 *	that applies at each level.  It need not be a stack or linked list,
 *	although this will usually be the simplest implementation. <p>
 *
 *	At any given level in the Context stack there is a current set of
 *	bindings for entities and handlers, and a current Node being operated
 *	on, normally by appending to it.  There is also a current Token being
 *	``expanded'' or processed.  Immediately before starting (pushing) a
 *	new Context, the current Token should correspond to the Element being
 *	pushed.  That way, immediately after popping, it will have the
 *	Handler that needs to be called to finalize the operation.  A Handler
 *	need not be associated with an end tag, which saves the Parser the
 *	trouble of looking it up. <p>
 *
 *	Note that the current Node does <em>not</em> have to be a child
 *	of the current Node one level up in the stack.  It is perfectly
 *	legitimate to build a parentless parse tree, manipulate it in 
 *	some way, and <em>then</em> append it. <p>
 *
 *	Handlers append their results to the parse tree under construction
 *	using the Context operations <code>putResult(<em>node</em>)</code> and 
 *	<code>putResults(<em>nodeList</em>)</code>.  This has several
 *	advantages:
 *   	<ul>
 *	    <li> Handlers can easily return multiple results without
 *		 constructing a tempory NodeList which the calling Context
 *		 would then have to take apart.
 *	    <li> A Processor (an extension of Context) can perform the
 *		 optimization of passing the results directly to an 
 *		 Output without constructing a tree at all.
 *	    <li> Handler operations are then free to return a Token which
 *		 represents a ``continuation.''
 *	</ul>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Handler
 * @see crc.dps.Output
 * @see crc.dps.Processor
 * @see crc.dps.Token */

public interface Context {

  /************************************************************************
  ** State accessors:
  ***********************************************************************/

  /** Obtain the current Entity bindings. */
  public EntityTable getEntities();

  /** Set the current Entity bindings. */
  public void setEntities(EntityTable bindings);

  /** Obtain the current input. */
  public Input getInput();

  /** Set the current input. */
  public void setInput(Input in);

  /** Obtain the current output. */
  public Output getOutput();

  /** Set the current output. */
  public void setOutput(Output out);

  /************************************************************************
  ** Context Stack:
  ************************************************************************/

  /** Return the depth of the process stack. */
  public int getDepth();

  /** Construct a new Context linked to this one.  <p>
   *
   *	Recursive operations like <code>expand</code> use this to create a new
   *	evaluation stack frame when descending into an Element.
   */
  public Context newContext();

  /** Construct a new Context linked to this one.  <p>
   *
   *	Recursive operations like <code>expand</code> use this to create a new
   *	evaluation stack frame when descending into an Element.
   */
  public Context newContext(Input in, Output out);

}
