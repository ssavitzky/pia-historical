////// GenericHandler.java: Node Handler generic implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.BasicElement;
import crc.dom.Element;
import crc.dom.NodeList;
import crc.dom.DOMFactory;

import crc.dps.NodeType;
import crc.dps.Token;
import crc.dps.Handler;
import crc.dps.Processor;
import crc.dps.Context;
import crc.dps.Util;

/**
 * Generic implementation for an Element Handler. <p>
 *
 *	This is a Handler that contains enough additional state to be
 *	customized, via its attributes and content, to handle any syntax and
 *	semantics that can be specified without the use of primitives.  It is
 *	specialized for Elements.  Specialized subclasses should be based 
 *	on TypicalHandler. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.handle.TypicalHandler
 * @see crc.dps.Processor
 * @see crc.dps.Tagset
 * @see crc.dps.BasicTagset
 * @see crc.dps.Token
 * @see crc.dps.Input 
 * @see crc.dom.Node */

public class GenericHandler extends BasicHandler {

  /************************************************************************
  ** State Used for Syntax:
  ************************************************************************/

  /** If <code>true</code>, a parse tree is built for the content. */
  protected boolean parseContent = true;

  /** If <code>true</code>, a parse tree is built for the content. */
  public boolean parseContent() { return parseContent; }

  /** If <code>true</code>, a parse tree is built for the content. */
  public void setParseContent(boolean value) { parseContent = value; }


  /** If <code>true</code>, the element is passed to the output while
   *	being processed.
   */
  protected boolean passElement = false;

  /** If <code>true</code>, the element is passed to the output while
   *	being processed.
   */
  public boolean passElement() { return passElement; }

  /** If <code>true</code>, the element is passed to the output while
   *	being processed.
   */
  public void setPassElement(boolean value) { passElement = value; }


  /** If <code>true</code>, it is not necessary to copy a parse tree
   *	before calling <code>computeResult</code>.
   *
   * @see #computeResult
   * @see #expandAction
   */
  protected boolean noCopyNeeded = false;

  /** If <code>true</code>, it is not necessary to copy a parse tree
   *	before calling <code>computeResult</code>.
   *
   * @see #computeResult
   * @see #expandAction
   */
  protected boolean noCopyNeeded() { return noCopyNeeded; }
  protected void setNoCopyNeeded(boolean value) {
    noCopyNeeded = value;
  }


  /** If <code>true</code>, the content is expanded. */
  protected boolean expandContent = true;

  /** If <code>true</code>, the content is expanded. */
  public boolean expandContent() { return expandContent; }

  /** If <code>true</code>, the content is expanded. */
  public void setExpandContent(boolean value) { expandContent = value; }

 
  /************************************************************************
  ** State Used for Semantics:
  ************************************************************************/

  /** The class name of the nodes to construct. */
  protected String nodeClassName = null;

  /** The parse tree of the action to perform at expansion time. */
  protected Token expandAction = null;
  

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** At this point, we have to set the processing flags.
   */
  public Node startAction(Token t, Processor p) {
    // set p.setExpanding, etc. flags from instance vbls.
    p.setExpanding(expandContent);
    if (! p.isParsing()) p.setParsing(parseContent);
    p.setPassing(passElement);
    return createNode(t, p);
  }

  /** This assumes that the Token is an Element. */
  public Token endAction(Token t, Processor p, Node n) {
    return computeResult(t, p, (Element)n);
  }

  /** Since we know that the Token is an element, we know that it's
   *	unexpanded at this point.  Expand it.  There are three possible
   *	ways of doing this:
   *	<ol>
   *	   <li>	call expandAction -- usually the most efficient way.
   *	   <li> call p.pushInto(t)
   *	   <li> simply return t and let the processor worry about it!
   *	</ol>
   */
  public Token nodeAction(Token t, Processor p) {
    return expandAction(t, p);
  }

  /** Constructs a new Node just as if the body had just been processed,
   *	then returns computeResult(n).
   */
  public Token expandAction(Token t, Context c) {
    if (noCopyNeeded) return computeResult(t, c, null);

    // create a new, suitable node, with expanded attributes.
    Node node = createNode(t, c);
    if (expandContent) {
      if (t.hasChildren()) 
	Util.expandChildren(node, t.getFirstChild(), t.getTagName(), c);
    } else {
      if (t.hasChildren())
	Util.copyAsTokens(t.getChildren(), node);      
    }
    return computeResult(t, c, (Element)node);
  }

  /** Compute a result, given the unexpanded Token and the constructed
   *	Element.  If the Token does not expand content, the element
   *	will be null and the Token will be a parse tree.
   */
  public Token computeResult(Token t, Context c, Element elt) {
    // === check for children here...
    return c.putResult(elt);
  }

  /** Returns a new, clean Node corresponding to the given Token.
   *	The new Node is suitable for incorporating into a new
   *	document. <p>
   *
   *	Note that this is not used when creating a parse tree of an
   *	existing document -- such a parse tree is made out of Token
   *	objects, which preserves the syntactic and semantic
   *	information (e.g. handlers).
   */
  public Node createNode(Token t, Context c) {
    return Util.expandAttrs(this, c.getHandlers(), c.getEntities());
  }

  /** Returns a new, clean Node corresponding to the given Token,
   *	created using the given DOMFactory. <p>
   */
  public Node createNode(Token t, DOMFactory f) {
    return t.createNode(f);
  }

  /************************************************************************
  ** Presentation Operations:
  ************************************************************************/

  /** Converts the Token to a String. 
   *	Note that a Token is quite capable of doing this using the 
   *	standard defaults; passing it off to the Handler means that
   *	we can give the same Document different physical representations
   *	if necessary.
   */
  public String convertToString(Token t, int syntax) {
    return t.basicToString(syntax);
  }

  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/


}
