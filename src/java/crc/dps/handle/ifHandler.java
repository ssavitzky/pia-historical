////// ifHandler.java: Node Handler generic implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.Element;
import crc.dom.NodeList;
import crc.dom.NodeEnumerator;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.DOMFactory;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.aux.*;

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

  public void action(Input in, Context aContext, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    boolean trueCondition = false;
    NodeEnumerator enum = content.getEnumerator();
    aContext.debug("   Initializing action for <" + tag + ">\n");

    for (Node child = enum.getNext() ;
	 child != null;
	 child = enum.getNext()) {
      if (child.getNodeType() == NodeType.ELEMENT) {
	ActiveElement ct = (ActiveElement)child;
	if ("then".equalsIgnoreCase(ct.getTagName())) {
	  aContext.debug("     <then> with condition " + 
			 (trueCondition? "true" : "false") + "\n");
	  if (trueCondition) {
	    Expand.processChildren(ct, aContext, out);
	    return;
	  }
	} else if ("else-if".equalsIgnoreCase(ct.getTagName())) {
	  aContext.debug("     <else-if> with condition " + 
			 (trueCondition? "true" : "false") + "\n");
	  if (!trueCondition) {
	    // else-if: just delegate to <else-if>'s (expanded) children.
	    content = Expand.processNodes(ct.getChildren(), aContext);
	    action(in, aContext, out, "else-if", null, content, null);
	    return;
	  }
	} else if ("else".equalsIgnoreCase(ct.getTagName())) {
	  aContext.debug("     <else> with condition " + 
			 (trueCondition? "true" : "false") + "\n");
	  if (!trueCondition) {
	    Expand.processChildren(ct, aContext, out);
	    return;
	  }
	} else {
	  trueCondition = true;
	}
      } else if (Test.trueValue(child)) {
	trueCondition = true;
      }
    }
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public ifHandler() {
    stringContent = false;	// true 	want content as string?
    expandContent = true;
  }
}
