////// AbstractHandler.java: Node Handler abstract base class
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.Element;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.DOMFactory;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.aux.*;

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

public abstract class AbstractHandler extends ParseTreeGeneric
implements Handler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** The default action is to return the code that tells the Processor
   *	to process the node in the ``usual'' way.
   */
  public int actionCode(Input in, Processor p) {
    return (in.hasActiveChildren() || in.hasActiveAttributes()
	    || in.getNode().getNodeType() == NodeType.ENTITY)
      ? Action.EXPAND_NODE: Action.COPY_NODE;
  }

  /** This sort of action has no choice but to do the whole job.
   */
  public void action(Input in, Context aContext, Output out) {
    Node n = in.getNode();
    if ((in.hasActiveChildren() || in.hasActiveAttributes()
	 || n.getNodeType() == NodeType.ENTITY)) {
      aContext.subProcess(in, out).expandCurrentNode();
    } else {
      Copy.copyNode(n, in, out);
    }
  }

  /** It's unlikely that this will be called, but allow for the possibility. */
  public void action(Input in, Context aContext, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    Element e = in.getElement();
    if (e == null) {
      Node node = in.getNode();
      if (content == null) {
	out.putNode(node);
      } else {
	out.startNode(node);
	Copy.copyNodes(content, out);
	out.endNode();
      }
    } else {
      ParseTreeElement element = new ParseTreeElement(e, atts);
      if (content == null) out.putNode(element);
      else {
	out.startElement(e);
	Copy.copyNodes(content, out);
	out.endElement(element.isEmptyElement() || element.implicitEnd());
      }
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
  public Action getActionForNode(ActiveNode n) {
    return this;
  }

  /** Called from <code>getActionForNode</code> to determine whether
   *	dispatching should be done.  It returns true if the given Element
   *	has the given <code>name</code> as either an attribute name or
   *	a period-separated suffix of its tagname.
   */
  public boolean dispatch(ActiveElement e, String name) {
    return e.hasTrueAttribute(name)
      || e.getTagName().endsWith("."+name);
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

  /** Return <code>true</code> if Text nodes are permitted in the content.
   */
  public boolean mayContainText() { return true; }

  /** Return <code>true</code> if paragraph elements are permitted in the
   *	content.  If this is <code>true</code> and <code>mayContainText</code>
   *	is false, whitespace is made ignorable and non-whitespace is 
   *	commented out.
   */
  public boolean mayContainParagraphs() { return true; }

  /** Return true if this kind of token implicitly ends the given one. 
   *	This is not as powerful a test as using the DTD, but it will work
   *	in most cases and permits a simpler parser.
   */
  public boolean implicitlyEnds(String tag) { return false; }


  /************************************************************************
  ** Presentation Operations:
  ************************************************************************/

  /** Converts the Node to a String. 
   *	Note that a Node is quite capable of doing this using the 
   *	standard defaults; passing it off to the Handler means that
   *	we can give the same Document different physical representations
   *	if necessary.
   */
  public String convertToString(ActiveNode n) {
    return n.startString() + n.contentString() + n.endString();
  }

  /** Converts the Node to a String. 
   *	Note that a Node is quite capable of doing this using the 
   *	standard defaults; passing it off to the Handler means that
   *	we can give the same Document different physical representations
   *	if necessary.
   */
  public String convertToString(ActiveNode n, int syntax) {
    return n.startString() + n.contentString() + n.endString();
  }


}
