////// setHandler.java: <set> Handler implementation
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

/**
 * Handler for &lt;set&gt;  <p>
 *
 *	This is an approximation to the legacy &gt;set&gt;; it lacks many
 *	of the old extraction modifiers, which have moved to &lt;find&gt;. <p>
 *
 *	It is permissible for the <code>name</code> attribute to be missing,
 *	in which case the entire namespace will be returned.  The 
 *	<code>keys</code>, <code>values</code>, and <code>bindings</code>
 *	attributes are supported.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class setHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** This will normally be the only thing to customize. */
  public void action(Input in, Context aContext, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    // Actually do the work. 
    String name = atts.getAttributeString("name");
    aContext.setEntityValue(name, content, false);
  }

  /** This does the parse-time dispatching. <p>
   *
   *	Action is dispatched (delegated) to a subclass if the string
   *	being passed to <code>dispatch</code> is either the name of an
   *	attribute or a period-separated suffix of the tagname. <p>
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    //if (dispatch(e, "element"))	 return set_element.handle(e);
    //if (dispatch(e, "local"))	 return set_local.handle(e);
    //if (dispatch(e, "global")) return set_global.handle(e);
    //if (dispatch(e, "index"))	 return set_index.handle(e);

    //if (dispatch(e, "pia"))	 return set_pia.handle(e);
    //if (dispatch(e, "env"))	 return set_env.handle(e);
    //if (dispatch(e, "agent"))	 return set_agent.handle(e);
    //if (dispatch(e, "trans"))	 return set_trans.handle(e);
    //if (dispatch(e, "form"))	 return set_form.handle(e);

    return this;
  }
   
  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public setHandler() {
    /* Expansion control: */
    stringContent = false;	// true 	want content as string?
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?
    noCopyNeeded = true;	// false 	don't copy parse tree?
    passElement = false;	// true 	pass while expanding?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL; 		// EMPTY, QUOTED, 0 (check)
  }
}
