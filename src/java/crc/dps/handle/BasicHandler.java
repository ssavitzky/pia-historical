////// BasicHandler.java: Node Handler basic implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.BasicElement;
import crc.dom.NodeList;
import crc.dom.NodeType;
import crc.dom.DOMFactory;

import crc.dps.Token;
import crc.dps.Handler;
import crc.dps.Context;
import crc.dps.Processor;

import crc.dps.BasicTokenList;
import crc.dps.ParseStack;
import crc.dps.Util;

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
 * @see crc.dps.Token
 * @see crc.dps.Input 
 * @see crc.dom.Node
 */

public class BasicHandler extends AbstractHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  // All inherited.

  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/

  /** If the handler corresponds to an Element, this determines its syntax.
   *	<dl compact>
   *	    <dt> -1 <dd> known to be non-empty.
   *	    <dt>  0 <dd> unknown: look at the Token.
   *	    <dt>  1 <dd> known to be empty.
   *	</dl>
   */
  protected int elementSyntax = 0;

  /** What the Handler knows about a Token's syntax without looking at it.
   *
   * @return
   *	<dl compact>
   *	    <dt> -1 <dd> known to be non-empty.
   *	    <dt>  0 <dd> unknown
   *	    <dt>  1 <dd> known to be empty.
   *	</dl>
   */
  public int getElementSyntax() { return elementSyntax; }
  public void setElementSyntax(int value) { elementSyntax = value; }
  
  /** Called to determine whether the given Token (for which this is
   *	the Handler) is an empty element, or whether content is expected.
   *	It is assumed that <code>this</code> is the result of the Tagset
   *	method <code>handlerForTag</code>.
   *
   *	If <code>elementSyntax</code> is zero, we look at the Token's 
   *	<code>hasEmptyDelimiter</code> flag.
   *
   * @param t the Token for which this is the handler, and for which the
   *	ssyntax is being checked.
   * @return <code>true</code> if the Token is an empty Element.
   * @see crc.dps.Tagset
   */
  public boolean isEmptyElement(Token t) {
    if (elementSyntax != 0) return elementSyntax > 0;
    else return t.hasEmptyDelimiter();
  }

  /** Called to determine the correct Handler for a given Token.
   *	The default action is to return <code>this</code>.
   */
  public Handler getHandlerForToken(Token t) {
    return this;
  }

  /** If <code>true</code>, Element tags are recognized in content. */
  protected boolean parseElementsInContent = true;

  /** If <code>true</code>, Entity references are recognized in content. */
  protected boolean parseEntitiesInContent = true;

  /** If <code>true</code>, Element tags are recognized in content. */
  public boolean parseElementsInContent() { return parseElementsInContent; }

  /** If <code>true</code>, Entity references are recognized in content. */
  public void setParseEntitiesInContent(boolean value) {
    parseEntitiesInContent = value;
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

  /************************************************************************
  ** Presentation Operations:
  ************************************************************************/

  /** Converts the Token to a String according to the given syntax. 
   */
  public String convertToString(Token t, int syntax) {
    return t.basicToString(syntax);
  }

  /** Converts the Token to a String. 
   *	Note that a Token is quite capable of doing this using the 
   *	standard defaults; passing it off to the Handler means that
   *	we can give the same Document different physical representations
   *	if necessary.
   */
  public String convertToString(Token t) {
    if (t.getSyntax() == 0) {
      return convertToString(t, -1) +
	convertToString(t, 0) + convertToString(t, 1); 
    } else {
      return convertToString(t, t.getSyntax());
    }
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
   * @param syntax 
   *	<dl compact>
   *	    <dt> -1 <dd> known to be non-empty.
   *	    <dt>  0 <dd> unknown
   *	    <dt>  1 <dd> known to be empty.
   *	</dl>
   * @param parseElts if <code>true</code> (default), recognize elements in
   *	the content.
   * @param parseEnts if <code>true</code> (default), recognize entities in
   *	the content.
   * @see #getElementSyntax
   */
  public BasicHandler(int syntax, boolean parseElts, boolean parseEnts) {
    elementSyntax = syntax;
    parseElementsInContent = parseElts;
    parseEntitiesInContent = parseEnts;
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
   * @see #getElementSyntax
   */
  public BasicHandler(boolean empty, boolean parseElts, boolean parseEnts) {
    elementSyntax = empty? 1 : -1;
    parseElementsInContent = parseElts;
    parseEntitiesInContent = parseEnts;
  }

}
