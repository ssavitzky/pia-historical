////// expandHandler.java: <expand> Handler implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Element;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.aux.*;
import crc.dps.output.DiscardOutput;

/**
 * Handler for &lt;expand&gt;  <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class expandHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** This will normally be the only thing to customize. */
  public void action(Input in, Context aContext, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    Expand.processNodes(content, aContext, out);
  }

  /** This does the parse-time dispatching. <p>
   *
   *	Action is dispatched (delegated) to a subclass if the string
   *	being passed to <code>dispatch</code> is either the name of an
   *	attribute or a period-separated suffix of the tagname. <p>
   */
  public Action expandActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "hide")) 	 return expand_hide.handle(e);
    return this;
  }
   
  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public expandHandler() {
    /* Expansion control: */
    stringContent = false;	// true 	want content as string?
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;		// EMPTY, QUOTED, 0 (check)
  }
}

class expand_hide extends expandHandler {
  /** This routine does the work, such as it is.  */
  public void action(Input in, Context aContext, Output out) {
    if (in.hasActiveChildren()) {
      aContext.subProcess(in, new DiscardOutput()).processChildren();
    }
  }
  public expand_hide(ActiveElement e) { super(); }
  static Action handle(ActiveElement e) { return new expand_hide(e); }
}
