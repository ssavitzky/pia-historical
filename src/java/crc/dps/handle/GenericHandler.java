////// GenericHandler.java: Node Handler generic implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.BasicElement;
import crc.dom.Element;
import crc.dom.NodeList;
import crc.dom.DOMFactory;

import crc.dps.*;
import crc.dps.active.*;

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

  /** If <code>true</code>, the content is delivered as a string. */
  protected boolean stringContent = false;

  /** If <code>true</code>, the content is expanded. */
  public boolean stringContent() { return stringContent; }

  /** If <code>true</code>, the content is expanded. */
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
    return 0;
  }

  /** This routine does the setup.  It obtains the expanded attribute list
   *	and content; it will rarely have to be overridden.  
   *	It will, however, die horribly if the current node is not an 
   *	ActiveElement. 
   */
  public void action(Input in, Context aContext, Output out) {
    AttributeList atts = getExpandedAttrs(in, aContext);
    if (atts != null) aContext.debug("   atts: " + atts.toString() + "\n");
    ParseNodeList content = null;
    String cstring = null;
    if (!in.hasChildren()) {
      aContext.debug("   no children...\n");
    } else if (stringContent) {
      aContext.debug("   getting content as string\n");
      cstring = expandContent? getProcessedContentString(in, aContext)
	: getContentString(in, aContext);
      aContext.debug("     -> '" + cstring + "'\n");
    } else {
      aContext.debug("   getting content as parse tree\n");
      content = expandContent
	? getProcessedContent(in, aContext)
	: getContent(in, aContext);
      aContext.debug("     -> " + content.getLength() + " nodes\n");
    }
    ActiveElement e = in.getActive().asElement();
    action(e, aContext, out, e.getTagName(), atts, content, cstring);
  }

  /** This routine does the work.
   *
   *	Note that the element we construct (in order to bind &amp;ELEMENT;) is
   *	empty, and the expanded content is kept in a separate NodeList, unless
   *	noCopyNeeded is <code>true</code>.  This means that unexpanded nodes
   *	don't have to be reparented.
   */
  public void action(ActiveElement e, Context aContext, Output out, String tag, 
  		     AttributeList atts, NodeList content, String cstring) {
    ParseTreeElement element = new ParseTreeElement(e);
    element.setAttributes(atts);
    if (!noCopyNeeded) Util.appendNodes(content, element);
    if (hasChildren()) {
      // Create a suitable sub-context:
      EntityTable ents = new BasicEntityTable(aContext.getEntities());
      ents.setValueForEntity("CONTENT", content, true);
      ents.setValueForEntity("ELEMENT", new ParseNodeList(element), true);
      Input in = new crc.dps.input.FromParseTree(this);
      BasicProcessor p = new BasicProcessor(in, aContext, out, ents);
      // expand children in the sub-context.
      p.processChildren();
    } else if (content == null) {
      out.putNode(element);
    } else {
      out.startElement(e);
      if (atts != null) {
	for (int i = 0; i < atts.getLength(); i++) { 
	  try {
	    out.putNode(atts.item(i));
	  } catch (crc.dom.NoSuchNodeException ex) {}
	}
      }
      Util.copyNodes(content, out);
      out.endElement(element.isEmptyElement() || element.implicitEnd());
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
