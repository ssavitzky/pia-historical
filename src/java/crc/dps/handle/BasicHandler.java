////// BasicHandler.java: Node Handler basic implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.DOMFactory;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.Copy;

import crc.ds.Table;

/**
 * Basic implementation for a Node Handler. <p>
 *
 *	This is a Handler that does only the minimum, and nothing more.
 *	It is capable of representing any kind of purely passive node,
 *	but is not particularly efficient because it contains no specialized
 *	information about the node itself.  It is, however, extensible,
 *	making it a good base class for more specialized versions. 
 *	<p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.handle.GenericHandler
 * @see crc.dps.Processor
 * @see crc.dps.Tagset
 * @see crc.dps.BasicTagset
 * @see crc.dps.Input 
 * @see crc.dps.Output
 * @see crc.dom.Node
 */

public class BasicHandler extends AbstractHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Blythely assume that any active entities have EntityHandler as their
   *	handler. 
   */
  public int actionCode(Input in, Processor p) {
    // There is no need to check for entities here; they use EntityHandler
    return (in.hasActiveChildren() || in.hasActiveAttributes())
      ? Action.EXPAND_NODE: Action.COPY_NODE;
  }

  /** This sort of action has no choice but to do the whole job.
   */
  public void action(Input in, Context aContext, Output out) {
    if (in.hasActiveChildren() || in.hasActiveAttributes()) {
      aContext.subProcess(in, out).expandCurrentNode();
    } else {
      Copy.copyNode(in.getNode(), in, out);
    }
  }

  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/

  /** Set what the Handler knows about a Token's syntax.
   *
   * @see crc.dps.Syntax
   */
  public void setSyntaxCode(int syntax) {
    syntaxCode = syntax;
    if (syntax != 0) {
      parseElementsInContent = (syntax & Syntax.NO_ELEMENTS) == 0;
      parseEntitiesInContent = (syntax & Syntax.NO_ENTITIES) == 0;
    }
  }
  
  /** If <code>true</code>, Element tags are recognized in content. */
  protected boolean parseElementsInContent = true;

  /** If <code>true</code>, Entity references are recognized in content. */
  protected boolean parseEntitiesInContent = true;

  /** If <code>true</code>, Entity references are recognized in content. */
  public boolean parseEntitiesInContent() { return parseEntitiesInContent; }

  /** If <code>true</code>, Element tags are recognized in content. */
  public boolean parseElementsInContent() { return parseElementsInContent; }

  /** If <code>true</code>, Entity references are recognized in content. */
  public void setParseEntitiesInContent(boolean value) {
    parseEntitiesInContent = value;
  }

  /** If <code>true</code>, Element tags are recognized in content. */
  public void setParseElementsInContent(boolean value) {
    parseElementsInContent = value;
  }

  /** Set both parsing flags. */
  public BasicHandler setParseFlags(boolean parseEntities, 
				    boolean parseElements) {
    parseEntitiesInContent = parseEntities;
    parseElementsInContent = parseElements;
    return this;
  }
    

  /** Set of elements inside which this tag is not permitted. */
  Table implicitlyEnds = null;

  /** Return true if this kind of token implicitly ends the given one. */
  public boolean implicitlyEnds(String tag) {
    return implicitlyEnds != null && tag != null && implicitlyEnds.has(tag);
  }

  /** Insert a tag into the implicitlyEnds table. */
  public void setImplicitlyEnds(String tag) {
    if (implicitlyEnds == null) implicitlyEnds = new Table();
    implicitlyEnds.at(tag, tag);
  }

  /** Set of elements which are permissible parents for this one. */
  Table parents = null;

  /** Return true if this kind of token implicitly ends the given one. */
  public boolean isChildOf(String tag) {
    return parents == null || tag != null && parents.has(tag);
  }

  /** Insert a tag into the parents table. */
  public void setIsChildOf(String tag) {
    if (parents == null) parents = new Table();
    parents.at(tag, tag);
  }

  protected boolean mayContainText = true;

  /** Return <code>true</code> if Text nodes are permitted in the content.
   */
  public boolean mayContainText() { return mayContainText; }

  public void setMayContainText(boolean value) { mayContainText = value; }

  protected boolean mayContainParagraphs = true;

  /** Return <code>true</code> if paragraph elements are permitted in the
   *	content.  If this is <code>true</code> and <code>mayContainText</code>
   *	is false, whitespace is made ignorable and non-whitespace is dropped.
   */
  public boolean mayContainParagraphs() { return mayContainParagraphs; }

  public void setMayContainParagraphs(boolean value) {
    mayContainParagraphs = value;
  }


  /************************************************************************
  ** Presentation Operations:
  ************************************************************************/

  /** Converts the Token to a String according to the given syntax. 
   */
  public String convertToString(ActiveNode n, int syntax) {
    if (syntax < 0) return n.startString();
    else if (syntax == 0) return n.contentString();
    else return n.endString();
  }

  /** Converts the Token to a String. 
   *	Note that a Token is quite capable of doing this using the 
   *	standard defaults; passing it off to the Handler means that
   *	we can give the same Document different physical representations
   *	if necessary.
   */
  public String convertToString(ActiveNode n) {
    return convertToString(n, -1) +
	convertToString(n, 0) + convertToString(n, 1); 
  }

  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public BasicHandler() {}

  /** Construct a BasicHandler for a passive element. 
   *
   * @param syntax see codes in <a href="crc.dps.Syntax.html">Syntax</a>
   * @see #getSyntaxCode
   */
  public BasicHandler(int syntax) {
    syntaxCode = syntax;
    if (syntax != 0) {
      parseElementsInContent = (syntax & Syntax.NO_ELEMENTS) == 0;
      parseEntitiesInContent = (syntax & Syntax.NO_ENTITIES) == 0;
    }
  }

  /** Construct a BasicHandler for a passive element. 
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
  public BasicHandler(boolean empty, boolean parseElts, boolean parseEnts) {
    syntaxCode = empty? Syntax.EMPTY : Syntax.NORMAL;
    parseElementsInContent = parseElts;
    parseEntitiesInContent = parseEnts;
  }

}
