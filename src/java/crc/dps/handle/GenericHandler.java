////// GenericHandler.java: Node Handler generic implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
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
  protected ActiveNode expandAction = null;
  

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  public int action(Input in, Context aContext, Output out) {
    return 1;
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
