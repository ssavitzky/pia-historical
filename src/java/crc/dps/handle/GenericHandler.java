////// GenericHandler.java: Node Handler generic implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Element;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.aux.*;

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

  protected static final ParseTreeAttrs NO_ATTRS = new ParseTreeAttrs();

  /************************************************************************
  ** State Used for Syntax:
  ************************************************************************/

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
   * @see #action
   */
  protected boolean noCopyNeeded = true;

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

  /** If <code>true</code>, only Text in the content is retained. */
  protected boolean textContent = false;

  /** If <code>true</code>, only Text in the content is retained. */
  public boolean textContent() { return textContent; }

  /** If <code>true</code>, only Text in the content is retained. */
  public void setTextContent(boolean value) { textContent = value; }

  /** If <code>true</code>, the content is delivered as a string. */
  protected boolean stringContent = false;

  /** If <code>true</code>, the content is delivered as a string. */
  public boolean stringContent() { return stringContent; }

  /** If <code>true</code>, the content is delivered as a string. */
  public void setStringContent(boolean value) { stringContent = value; }

 
  /************************************************************************
  ** State Used for Semantics:
  ************************************************************************/

  /** The class name of the nodes to construct. */
  protected String nodeClassName = null;

  /** The parse tree of the action to perform at expansion time. */
  protected ActiveNode expandAction = null;
  

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** We're assuming that this is an <em>active</em> node, so call
   *	the three-input <code>action</code> routine to do the work.
   */
  public int action(Input in, Processor p) {
    action(in, p, p.getOutput());
    return Action.COMPLETED;
  }

  /** This routine does the setup for the ``7-argument'' action routine. 
   *
   *	It obtains the expanded attribute list and content, and will rarely
   *	have to be overridden.  At some point it may be copied into an
   *	implementation of Processor.
   */
  public void action(Input in, Context aContext, Output out) {
    ActiveAttrList atts = Expand.getExpandedAttrs(in, aContext);
    if (atts != null) aContext.debug("   atts: " + atts.toString() + "\n");
    else atts = NO_ATTRS;
    ParseNodeList content = null;
    String cstring = null;
    if (!in.hasChildren()) {
      aContext.debug("   no children...\n");
    } else if (stringContent) {
      aContext.debug("   getting content as " + (textContent? "text in " : "")
		     + (expandContent? "" : "un") + "expanded string\n");
      if (textContent) {
	cstring = expandContent
	  ? Expand.getProcessedTextString(in, aContext)
	  : Expand.getTextString(in, aContext);
      } else {
	cstring = expandContent
	  ? Expand.getProcessedContentString(in, aContext)
	  : Expand.getContentString(in, aContext);
      }
      aContext.debug("     -> '" + cstring + "'\n");
    } else {
      aContext.debug("   getting content as " + (textContent? "text in " : "")
		     + (expandContent? "" : "un") + "expanded parse tree\n");
      if (textContent) {
	content = expandContent
	  ? Expand.getProcessedText(in, aContext)
	  : Expand.getText(in, aContext);
      } else {
	content = expandContent
	  ? Expand.getProcessedContent(in, aContext)
	  : Expand.getContent(in, aContext);
      }
      aContext.debug("     -> '" + content.toString() + "' "
		     + content.getLength() + " nodes\n");
    }
    String tag = in.getTagName();
    aContext.debug("   Performing action for <" + tag + ">\n");
    action(in, aContext, out, tag, atts, content, cstring);
    aContext.debug("   Completed action for <" + tag + ">\n");
  }

  /** This routine does the work; it should be overridden in specialized
   *	subclasses, and subclasses in which the current node is not an Element.
   *
   *	Note that the element we construct (in order to bind &amp;ELEMENT;) is
   *	empty, and the expanded content is kept in a separate NodeList, unless
   *	noCopyNeeded is <code>false</code>.  This means that unexpanded nodes
   *	don't have to be reparented in the usual case. <p>
   *
   *	If the handler has no children, we simply copy the newly-constructed
   *	Element to the Output.  This should be equivalent to the default
   *	action obtained by returning Action.EXPAND_NODE as an action code.
   */
  public void action(Input in, Context aContext, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    aContext.debug("in action for " + in.getNode());
    ActiveElement e = in.getActive().asElement();

    // === We shouldn't ever have to copy the children here.
    // === Instead, make a special EntityTable that can construct the element
    // === if a value of ELEMENT is requested, or (better) construct a 
    // === pseudo-Element that behaves like one but doesn't have up-links.
    // === Supporting pseudo-Elements requires special hackery in the
    // === ParseListIterator, with a potential nodelist at each level.

    ActiveElement element = e.editedCopy(atts, null);
    if (!noCopyNeeded) Copy.appendNodes(content, element);
    if (hasChildren()) {
      // Create a suitable sub-context:
      aContext.debug("expanding definition in sub-context\n");
      EntityTable ents = new BasicEntityTable(aContext.getEntities());
      ents.setEntityValue("CONTENT", content, true);
      ents.setEntityValue("ELEMENT", new ParseNodeList(element), true);
      // ... in which to expand this Actor's definition
      Input def = new crc.dps.input.FromParseTree(this);
      Processor p = aContext.subProcess(def, out, ents);
      // ... Expand the definition in the sub-context
      p.processChildren();
    } else if (content == null) {
      // No content: just put the new element. 
      out.putNode(element);
    } else {
      // Content. 
      out.startElement(element);
      Copy.copyNodes(content, out);
      out.endElement(e.isEmptyElement() || e.implicitEnd());
    }
  }

  public NodeList getValue(Node aNode, Context aContext) {
    return null;
  }

  public NodeList getValue(String aName, Node aNode, Context aContext) {
    return null;
  }

  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public GenericHandler() {}

  /** Construct a GenericHandler for a passive element. 
   *
   * @param syntax 
   *	<dl compact>
   *	    <dt> -1 <dd> known to be empty.
   *	    <dt>  0 <dd> unknown
   *	    <dt>  1 <dd> known to be non-empty.
   *	</dl>
   * @param parseElts if <code>true</code> (default), recognize elements in
   *	the content.
   * @param parseEnts if <code>true</code> (default), recognize entities in
   *	the content.
   * @see #getElementSyntax
   */
  public GenericHandler(int syntax, boolean parseElts, boolean parseEnts) {
    super(syntax, parseElts, parseEnts);
  }
  /** Construct a GenericHandler for a passive element. 
   *
   * @param empty     if <code>true</code>, the element has no content
   *	and expects no end tag.  If <code>false</code>, the element
   *	<em>must</em> have an end tag.
   * @param parseElts if <code>true</code> (default), recognize elements in
   *	the content.
   * @param parseEnts if <code>true</code> (default), recognize entities in
   *	the content.
   * @see #getElementSyntax
   */
  public GenericHandler(boolean empty, boolean parseElts, boolean parseEnts) {
    super(empty, parseElts, parseEnts);
  }

}
