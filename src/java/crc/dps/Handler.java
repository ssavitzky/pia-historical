////// Handler.java: Node Handler interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.DOMFactory;

/**
 * The interface for a Node Handler. 
 *
 *	A Node's Handler provides all of the necessary syntactic and
 *	semantic information required for parsing, processing, and
 *	presenting a Node and its start tag and end tag Token.  (A
 *	Handler does <em>not</em> include the additional traversal
 *	information that distinguishes a Token: an Input will
 *	associate the same Handler with a Node's start tag and end
 *	tag). <p>
 *
 *	Note that this interface says little about the implementation.
 *	It is expected, however, that any practical implementation of
 *	Handler will also be a Node, so that sets of Handlers (also
 *	called <em>tagsets</em>) can be read and stored as documents
 *	or (better) DTD's.   <p>
 *
 *	(We may eventually make Handler an extension of Node in order
 *	to enforce this.) <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Processor
 * @see crc.dps.Token
 * @see crc.dps.Input 
 * @see crc.dom.Node
 */

public interface Handler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Performs actions associated with an Element's start tag Token.  
   *	Called only for a Token that represents the start tag of an
   *	Element with content, and only if the Processor is expanding.
   *	Called <em>after pushing</em> the Token onto the parse stack. <p>
   *
   * @param t the Token for which actions are being performed.
   * @param p the Processor in which to perform the actions.  The Context
   *	is one in which the Token has already been pushed.
   * @return a Node which will contain the content of the Token's Element.
   */
  public Node startAction(Token t, Processor p);

  /** Performs actions associated with an Element's end tag Token. <p>
   *	Called only for a Token that represents the start tag of an
   *	Element with content, and only if the Processor is expanding.
   *	Called <em>after popping</em> the Token from the parse stack. <p>
   *
   *	Results are returned directly to the calling Context using
   *	<code>result</code>; the Context is expected to know what to do with
   *	them, e.g., append them to a document under construction, or pass them
   *	along to an Output.  The value <em>returned</em> from
   *	<code>expandAction</code> is a Token to be <em>expanded</em> in the
   *	calling Context, that is, a <em>continuation</em> or
   *	<em>thunk</em>.   The default <code>endAction</code> calls
   *	<code>p.result(n)</code> and returns <code>null</code>. <p>
   *
   * @param t the Token for which actions are being performed.
   * @param p the Processor in which to perform the actions.
   * @param n the Node originally returned by the corresponding startAction.
   * @return a Token representing a continuation, which the calling 
   *	context is expected to re-expand.
   * @see #expand
   */
  public Token endAction(Token t, Processor p, Node n);

  /** Performs actions associated with an complete Node's Token. <p>
   *
   *	Normally returns the original Node, Called for Text, Comments, PI's,
   *	and so on.  It is also called for a Token that represents a complete
   *	(possibly-trivial) parse tree; it <em>may</em>, but need not, call
   *	<code>expand</code>.  Called only if the Processor is expanding.  <p>
   *
   *	Results are returned directly to the calling Context using
   *	<code>result</code>; the Context is expected to know what to do with
   *	them, e.g., append them to a document under construction, or pass them
   *	along to an Output.  The value <em>returned</em> from
   *	<code>expandAction</code> is a Token to be <em>expanded</em> in the
   *	calling Context, that is, a <em>continuation</em> or
   *	<em>thunk</em>.   The default <code>endAction</code> calls
   *	<code>p.result(n)</code> and returns <code>null</code>. <p>
   *
   * @param t the Token for which actions are being performed.
   * @param p the Processor in which to perform the actions.
   * @return a Token representing a continuation, which the calling 
   *	context is expected to re-expand.
   * @see #expand
   */
  public Token nodeAction(Token t, Processor p);

  /** Performs the actions necessary to ``expand'' the given Token. <p>
   *
   *	Results are returned directly to the calling Context using
   *	<code>result</code>; the Context is expected to know what to do with
   *	them, e.g., append them to a document under construction, or pass them
   *	along to an Output.  The value <em>returned</em> from
   *	<code>expandAction</code> is a Token to be <em>expanded</em> in the
   *	calling Context, that is, a <em>continuation</em> or
   *	<em>thunk</em>. <p>
   *
   *	The result of calling <code>expandAction(<em>t</em>,
   *	<em>p</em>)</code> with a given Token <code><em>t</em></code> is
   *	supposed to be the same as using the Token as input to a Processor
   *	with: <code><em>p</em>.pushInto(<em>t</em>)</code>.  Note, however,
   *	that <code>expandAction</code> takes a Context as a parameter, while
   *	<code>pushInto</code> is an operation on Processor.  If expansion
   *	actually requires pushing something on the input stack of a Processor
   *	(for example, a file to be parsed and processed), it will be necessary
   *	to construct a new Processor. <p>
   *
   *	As a consequence, a Processor will never need to call
   *	<code>expandAction</code> when processing input; it is only called
   *	when expanding a Token returned as a ``thunk'' by one of the 
   *	action routines. <p>
   *
   *	It would be reasonable for <code>expandAction</code> to assume that
   *	any children of the Token that are not themselves instances of Token,
   *	do not need to be expanded but simply copied.  However, it will
   *	probably continue expanding in order to replace locally-defined
   *	entities with their values. <p>
   *
   * @param t the Token to expand
   * @param c the Context in which to perform the expansion. 
   * @return a Token representing a continuation, which the calling 
   *	context is expected to re-expand.
   * @see #nodeAction
   * @see crc.dps.Processor
   * @see crc.dps.Context */
  public Token expandAction(Token t, Context c);

  /** Returns a new, clean Node corresponding to the given Token.
   *	The new Node is suitable for incorporating into a new document.
   *	Children are not copied, but entities in the values of attributes will
   *	be expanded if they are defined. <p>
   *
   *	Note that this is not used when creating a parse tree for an active
   *	document -- such a parse tree is made out of Token objects, which
   *	preserves the syntactic and semantic information (e.g. handlers).  <p>
   *
   *	This might be implemented by cloning a prototype Node, for example.
   */
  public Node createNode(Token t, Context c);

  /** Returns a new, clean Node corresponding to the given Token,
   *	created using the given DOMFactory. <p>
   */
  public Node createNode(Token t, DOMFactory f);


  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/

  /** Called to determine whether the given Token (for which this is
   *	the Handler) is an empty element, or whether content is expected.
   *	It is assumed that <code>this</code> is the result of the Tagset
   *	method <code>handlerForTag</code>.
   *
   * @param t the Token for which this is the handler, and for which the
   *	ssyntax is being checked.
   * @return <code>true</code> if the Token is an empty Element.
   * @see crc.dps.Tagset
   */
  public boolean isEmptyElement(Token t);

  /** Called to determine the correct Handler for a Token.
   *	It is assumed that <code>this</code> is the result of the
   *	Tagset method <code>handlerForTag</code>.  Normally just
   *	returns <code>this</code>, but a handler may further examine
   *	the Token's attributes and return something more specific. <p>
   *
   *	Note that this replaces the earlier technique of dispatching
   *	to a separate named actor, although this may still be useful
   *	in some cases.
   *
   * @param t the Token for which this is a handler, and for which the
   *	nesting is being checked.
   * @return the correct Handler for the Token.  
   * @see crc.dps.Tagset
   */
  public Handler getHandlerForToken(Token t);

  /************************************************************************
  ** Presentation Operations:
  ************************************************************************/

  /** Converts the Token to a String. 
   *	Note that a Token would be quite capable of doing this using the 
   *	standard defaults; passing it off to the Handler means that
   *	we can give the same Document different physical representations
   *	if necessary.<p>
   *
   *	<b>Implementation Note:</b> It is important that the Handler's
   *	<code>convertToString</code> method <em>not</em> call the
   *	Token's <code>toString</code>, method, since that will
   *	normally call the Handler.  Use <code>basicToString</code>
   *	instead. <p>
   *
   * === Not clear where entity, url encoding and decoding is done. ===
   */
  public String convertToString(Token t);

  /** Converts the Token to a String according to the given syntax. <p>
   *
   *	Note that the <code>syntax</code> code has a different meaning
   *	than it does in the Token itself: <em>in all cases</em> a Node
   *	is converted to a String with:
   *	<pre>
   *	     convertToString(t, -1) + 
   *	     convertToString(t,  0) +
   *	     convertToString(t,  1)
   *	</pre>
   */
  public String convertToString(Token t, int syntax);

  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/


}
