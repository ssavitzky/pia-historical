////// GenericHandler.java: Node Handler generic implementation
//	$Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Element;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;
import crc.dps.output.DiscardOutput;

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
 * @see crc.dps.Input 
 * @see crc.dom.Node */

public class GenericHandler extends BasicHandler {

  protected static final ParseTreeAttrs NO_ATTRS = new ParseTreeAttrs();

  /************************************************************************
  ** State Used for Syntax:
  ************************************************************************/

  /** Override to handle additional syntax codes. */
  public void setSyntaxCode(int syntax) {
    super.setSyntaxCode(syntax);
    if (syntax != 0) {
      expandContent = (syntax & Syntax.NO_EXPAND) == 0;
    }
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


  /************************************************************************
  ** State Used for Semantics:
  ************************************************************************/

  /** The class name of the nodes to construct. */
  protected String nodeClassName = null;

  /** The parse tree of the action to perform at expansion time. */
  protected ActiveNode expandAction = null;

  protected boolean hideExpansion = false;
  protected boolean passTag = false;
  protected boolean passContent = false;

  /** If <code>true</code>, the tag is passed through. */
  public boolean passTag() { return passTag; }
  /** If <code>true</code>, the tag is passed through. */
  public void setPassTag(boolean value) { passTag = value; }
 
  /** If <code>true</code>, the content is passed through. */
  public boolean passContent() { return passContent; }
  /** If <code>true</code>, the content is passed through. */
  public void setPassContent(boolean value) { passContent = value; }
 
  /** If <code>true</code>, the expansion is hidden. */
  public boolean hideExpansion() { return hideExpansion; }
  /** If <code>true</code>, the expansion is hidden. */
  public void setHideExpansion(boolean value) { hideExpansion = value; }
 

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** We're assuming that this is an <em>active</em> node, so call
   *	the three-argument <code>action</code> routine to do the work.
   */
  public int actionCode(Input in, Processor p) {
    action(in, p, p.getOutput());
    return Action.COMPLETED;
  }

  /** The default action for active elements is to obtain the expanded
   *	attribute list and content, then call the ``5-argument'' action
   *	method. 
   */
  public void action(Input in, Context aContext, Output out) {
    defaultAction(in, aContext, out);
  }

  /** This routine does the setup for the ``5-argument'' action routine. 
   *
   * <p> It is provided as a separate method in case the normal three-argument
   *	 action routine is overridden, and we need the normal functionality in
   *	 a subclass.  <code>extractHandler</code> is a good example.
   *
   * @see crc.dps.handle.extractHandler
   */
  protected final void defaultAction(Input in, Context aContext, Output out) {
    ActiveAttrList atts = Expand.getExpandedAttrs(in, aContext);
    if (atts == null) atts = NO_ATTRS;
    if (passTag) {
      ActiveElement e = in.getActive().asElement();
      ActiveElement element = e.editedCopy(atts, null);
      out.startElement(element);
    }
    ParseNodeList content = null;
    boolean empty = false;
    if (!in.hasChildren()) {
      empty = true;
      // aContext.debug("   no children...\n");
    } else if (textContent) {
	content = expandContent
	  ? Expand.getProcessedText(in, aContext)
	  : Expand.getText(in, aContext);
    } else {
	content = expandContent
	  ? Expand.getProcessedContent(in, aContext)
	  : Expand.getContent(in, aContext);
    }
    if (passContent) {
      Copy.copyNodes(content, out);
    }
    action(in, aContext,
	   (hideExpansion? new DiscardOutput() : out),
	   atts, content);
    if (passTag) {
      out.endElement(empty);
    }
  }

  /** This routine does the work; it should be overridden in specialized
   *	subclasses.  The generic action is to expand the handler's children.
   *
   *	<p>Note that the element we construct (in order to bind &amp;element;)
   *	is empty, and the expanded content is kept in a separate NodeList.
   *	This means that unexpanded nodes don't have to be reparented in the
   *	usual case.
   *
   *	<p>If the handler has no children, we simply copy the new Element to
   *	the Output.  This should be equivalent to the default action obtained
   *	by returning Action.EXPAND_NODE as an action code.
   *
   * @param the Input, with the current node being the one to be processed.
   * @param aContext the context in which to look up entity bindings
   * @param out the Output to which to send results
   * @param atts the (processed) attribute list.
   * @param content the (possibly-processed) content.  */
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    //aContext.debug("in action for " + in.getNode());
    ActiveElement e = in.getActive().asElement();
    ActiveElement element = e.editedCopy(atts, null);

    // === We shouldn't ever have to copy the children here.
    // === Instead, make a special EntityTable that can construct the element
    // === if a value of ELEMENT is requested, or (better) construct a 
    // === pseudo-Element that behaves like one but doesn't have up-links.
    // === Supporting pseudo-Elements requires special hackery in the
    // === ParseListIterator, with a potential nodelist at each level.

    if (hasChildren()) {	
      // Children exists, so this is a defined action (macro). 

      //aContext.debug("expanding <"+in.getTagName()+"> "+getChildren()+"\n");

      // Create a suitable sub-context for the expansion:
      //    === not clear if entities should still be lowercase.  For now...
      //    === This may want to go into defaultAction ===

      Tagset ts = aContext.getTopContext().getTagset();
      BasicEntityTable ents = new BasicEntityTable(e.getTagName());
      ents.setEntityValue(aContext, "content", content, ts);
      ents.setEntityValue(aContext, "element", new ParseNodeList(element), ts);
      ents.setEntityValue(aContext, "attributes", atts, ts);
      // ... in which to expand this Actor's definition
      Input def = new crc.dps.input.FromParseTree(this);
      Processor p = aContext.subProcess(def, out, ents);
      // ... Expand the definition in the sub-context
      p.processChildren();

    } else if (content == null) {
      // No content: just put the new element. 
      out.putNode(element);
    } else {
      // Content: output an expanded copy of the original element.
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
  ** Construction:
  ************************************************************************/

  public GenericHandler() {}

  /** Construct a GenericHandler for a passive element. 
   *
   * @param syntax see codes in <a href="crc.dps.Syntax.html">Syntax</a>
   * @see #getSyntaxCode
   */
  public GenericHandler(int syntax) {
    super(syntax);
    if (syntax != 0) {
      expandContent = (syntax & Syntax.NO_EXPAND) == 0;
    }
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
   * @see #getSyntaxCode
   */
  public GenericHandler(boolean empty, boolean parseElts, boolean parseEnts) {
    super(empty, parseElts, parseEnts);
  }

}
