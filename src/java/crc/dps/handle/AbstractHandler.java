////// AbstractHandler.java: Node Handler abstract base class
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.Element;
import crc.dom.BasicElement;
import crc.dom.NodeList;
import crc.dom.AttributeList;
import crc.dom.DOMFactory;

import crc.dps.*;
import crc.dps.active.*;

/**
 * An abstract base class for a Node Handler. <p>
 *
 *	This implementation is also an Element, which ensures that 
 *	handlers can easily be stored in and retrieved from XML documents. 
 *	Note that handlers are normally contained in Tagsets, and that
 *	BasicTagset is also an Element.
 *	<p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Context
 * @see crc.dps.Tagset
 * @see crc.dps.BasicTagset
 * @see crc.dps.Input 
 * @see crc.dom.Node
 */

public abstract class AbstractHandler extends ParseTreeElement
implements Handler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** The default action is to return the code that tells the Processor
   *	to process the node in the ``usual'' way.
   */
  public int action(Input in, Processor p) {
    return (in.hasActiveChildren() || in.hasActiveAttributes())
      ? 1 : -1;
  }

  /** This sort of action has no choice but to do the whole job.
   */
  public void action(Input in, Context aContext, Output out) {
    BasicProcessor p = new BasicProcessor(in, aContext, out);
    p.defaultProcessNode();
  }

  /** It's unlikely that this will be called, but allow for the possibility. */
  public void action(ActiveElement e, Context aContext, Output out, String tag, 
  		     AttributeList atts, NodeList content, String cstring) {
    ParseTreeElement element = new ParseTreeElement(e);
    element.setAttributes(atts);
    if (content == null) out.putNode(element);
    else {
      out.startElement(e);
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
  public boolean isEmptyElement(Node n) {
    return false;
  }

  /** Called to determine the correct Action for a given Token.
   *	The default action is to return <code>this</code>, but it is
   *	possible to do additional dispatching based on the Node's 
   *	attributes or other information.
   */
  public Action getActionForNode(Node n) {
    return this;
  }

  /** If <code>true</code>, the content is expanded.
   *	The default is to return <code>true</code> -- content is expanded.
   */
  public boolean expandContent() { return true; }

  /** If <code>true</code>, begin constructing a parse tree even if the
   *	parent is not building a parse tree.
   *	The default is to return <code>! passElement()</code>.
   */
  public boolean stringContent() { return false; }

  /** If <code>true</code>, Element tags are recognized in content.
   *	The default is to return <code>true</code>.
   */
  public boolean parseElementsInContent() { return true; }

  /** If <code>true</code>, Entity references are recognized in content.
   *	The default is to return <code>true</code>.
   */
  public boolean parseEntitiesInContent() { return true; }

  /** If <code>true</code>, the element is passed to the output while being
   *	processed.  The default is to return <code>true</code> -- the element
   *	is passed in its entirety.
   */
  public boolean passElement() { return true; }


  /************************************************************************
  ** Presentation Operations:
  ************************************************************************/

  /** Converts the Node to a String. 
   *	Note that a Node is quite capable of doing this using the 
   *	standard defaults; passing it off to the Handler means that
   *	we can give the same Document different physical representations
   *	if necessary.
   */
  public String convertToString(Node n) {
    crc.dom.AbstractNode nn = (crc.dom.AbstractNode)n;
    return nn.startString() + nn.contentString() + nn.endString();
  }

  /** Converts the Node to a String. 
   *	Note that a Node is quite capable of doing this using the 
   *	standard defaults; passing it off to the Handler means that
   *	we can give the same Document different physical representations
   *	if necessary.
   */
  public String convertToString(Node n, int syntax) {
    crc.dom.AbstractNode nn = (crc.dom.AbstractNode)n;
    return nn.startString() + nn.contentString() + nn.endString();
  }


  /************************************************************************
  ** Utilities:
  ************************************************************************/

  /** Get the expanded attribute list of the current node. 
   *	The list is not expanded if it doesn't have to be. 
   */
  public AttributeList getExpandedAttrs(Input in, Context c) {
    if (in.hasActiveAttributes()) {
      return Util.expandAttrs(c, in.getElement().getAttributes());
    } else if (in.hasAttributes()) {
      return in.getElement().getAttributes();
    } else {
      return null;
    }
  }

  /** Get the processed content of the current node. */
  public ParseNodeList getProcessedContent(Input in, Context c) {
    crc.dps.output.ToNodeList out = new crc.dps.output.ToNodeList();
    new BasicProcessor(in, c, out).processChildren();
    return out.getList();
  }

  /** Get the processed content of the current node as a string. */
  public String getProcessedContentString(Input in, Context c) {
    crc.dps.output.ToString out = new crc.dps.output.ToString();
    new BasicProcessor(in, c, out).processChildren();
    return out.getString();
  }

  /** Get the unprocessed content of the current node. */
  public ParseNodeList getContent(Input in, Context c) {
    crc.dps.output.ToNodeList out = new crc.dps.output.ToNodeList();
    Util.copyChildren(in, out);
    return out.getList();
  }

  /** Get the unprocessed content of the current node as a string. */
  public String getContentString(Input in, Context c) {
    crc.dps.output.ToString out = new crc.dps.output.ToString();
    Util.copyChildren(in, out);
    return out.getString();
  }


}
