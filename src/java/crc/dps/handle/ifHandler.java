////// ifHandler.java: Node Handler generic implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.BasicElement;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.DOMFactory;

import crc.dps.*;
import crc.dps.active.*;

/**
 * Handler for <if>...<then>...<else-if>... <else>...</if>. <p>
 *
 *	This would actually be more efficient if startAction built a
 *	node of type if_node that cached the <code>then</code> and
 *	<code>else</code> children.  Even more if it built an if_token
 *	while parsing.
 *	<p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Processor
 * @see crc.dps.Tagset
 * @see crc.dps.BasicTagset
 * @see crc.dps.Input 
 * @see crc.dom.Node
 */

public class ifHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  public void action(ActiveElement e, Context aContext, Output out, String tag, 
  		     AttributeList atts, NodeList content, String cstring) {
    ParseTreeElement element = new ParseTreeElement(e);
    element.setAttributes(atts);
    if (!noCopyNeeded) Util.appendNodes(content, element);
  }

  /** endAction must assume that the contents have been expanded.
  public Token endAction(Token t, Processor p, Node n) {
    // n is the result of expanding the condition.
    boolean trueCondition = false;

    for (Node child = n.getFirstChild() ;
	 child != null;
	 child = child.getNextSibling()) {
      if (child.getNodeType() == NodeType.ELEMENT
	  && child instanceof Token) {
	Token ct = (Token)child;
	if ("then".equalsIgnoreCase(ct.getTagName())) {
	  if (trueCondition) return ct;
	} else if ("else-if".equalsIgnoreCase(ct.getTagName())) {
	  if (trueCondition) { p.pushInto(ct); return null; }
	} else if ("else".equalsIgnoreCase(ct.getTagName())) {
	  if (!trueCondition) return ct;
	} else if (Util.trueValue(child)) {
	  trueCondition = true;
	}
      } else if (Util.trueValue(child)) {
	trueCondition = true;
      }
    }
    return null;
  }
 */
  /** expandAction must assume that the contents still need expansion.
  public Token expandAction(Token t, Context c) {

    // We have to expand the children at this point.  We do it
    // carefully so as to avoid expanding the then and else clauses.
    // We can't use short-circuit expansion, though, because if we did it
    // wouldn't match what happens in a Processor.

    boolean trueCondition = false;
    Context cc = c.newContext(new BasicToken(NodeType.NODELIST), "if");
    for (Node child = t.getFirstChild() ;
	 child != null;
	 child = child.getNextSibling()) {
      if (child.getNodeType() == NodeType.ELEMENT
	  && child instanceof Token) {
	Token ct = (Token)child;
	// See whether any children expanded to true
	if (Util.trueValue(cc.getNode())) trueCondition = true;
	if ("then".equalsIgnoreCase(ct.getTagName())) {
	  if (trueCondition) return ct;
	} else if ("else-if".equalsIgnoreCase(ct.getTagName())) {
	  if (trueCondition) return ct;
	} else if ("else".equalsIgnoreCase(ct.getTagName())) {
	  if (!trueCondition) return ct;
	} else {
	  cc.expand(ct);
	}
      } else if (child instanceof Token) {
	cc.expand((Token)child);
      } else if (Util.trueValue(child)) {
	trueCondition = true;
      }
    }
    return null;
  }
 */
  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public ifHandler() {
    stringContent = false;	// true 	want content as string?
    expandContent = true;
  }
}
