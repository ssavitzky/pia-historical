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
   *
   * @param t the Token for which actions are being performed.
   * @param p the Context in which to perform the actions.
   * @return a Node to which content Nodes will be added.
   */
  public Node startAction(Token t, Context p);

  /** Performs actions associated with an Element's end tag Token.
   *	Called only for a Token that represents the start tag of an
   *	Element with content, and only if the Processor is expanding.
   * <p>
   *	The return value is a Node for efficiency.  In order to append
   *	multiple nodes to the parse tree, the simplest thing is to 
   *	return a Token with <code>nodeType</code> = NodeType.NODELIST.
   *	The alternative is to call <code>p.appendNodes</code> and return
   *	<code>null</code>.
   *
   * @param t the Token for which actions are being performed.
   * @param p the Context in which to perform the actions.
   * @return a Node to be added to the parse tree being constructed.  */
  public Node endAction(Token t, Context p);

  /** Performs actions associated with an complete Node's Token.
   *	Normally returns the original Node,   Called for empty
   *	Elements, Text, Comments, PI's, and so on.  It is not called
   *	by the Context for parse trees associated with elements
   *	having content, although <code>endAction</code> may call it in
   *	that case.  Called only if the Context is expanding.
   * <p>
   *	The return value is a Node for efficiency.  In order to append
   *	multiple nodes to the parse tree, the simplest thing is to 
   *	return a Token with <code>nodeType</code> = NodeType.NODELIST.
   *
   * @param t the Token for which actions are being performed.
   * @param p the Context in which to perform the actions.
   * @return a Node to be added to the parse tree being constructed.  */
  public Node nodeAction(Token t, Context p);

  /** Returns a new, clean Node corresponding to the given Token.
   *	The new Node is suitable for incorporating into a new
   *	document. <p>
   *
   *	Note that this is not used when creating a parse tree of an
   *	active document -- such a parse tree is made out of Token
   *	objects, which preserves the syntactic and semantic
   *	information (e.g. handlers).  <p>
   *
   *	This might be implemented by cloning a prototype Node, for example.
   */
  public Node createNode(Token t);

  /** Returns a new, clean Node corresponding to the given Token,
   *	created using the given DOMFactory. <p>
   */
  public Node createNode(Token t, DOMFactory f);

  /** Returns the result of ``expanding'' the given Token. 
   * @return a (possibly empty) NodeList of results.
   */
  public NodeList expand(Token t, Context c);


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
