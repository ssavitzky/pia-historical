////// defineHandler.java: <define> Handler implementation
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
 * Handler for &lt;define&gt;....&lt;/&gt;  <p>
 *
 *	=== <define [tag=tag|attribute=attr|type=type] syntax>def</> ===
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class defineHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** This will normally be the only thing to customize. */
  public void action(Input in, Context aContext, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    // Actually do the work. 
  }

  /** This does the parse-time dispatching. <p>
   *
   *	Action is dispatched (delegated) to a subclass if the string
   *	being passed to <code>dispatch</code> is either the name of an
   *	attribute or a period-separated suffix of the tagname. <p>
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    //if (dispatch(e, "")) 	 return define_.handle(e);
    return this;
  }
   
  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public defineHandler() {
    /* Expansion control: */
    stringContent = false;	// true  	want content as string?
    expandContent = false;	// true 	Expand content?
    textContent = false;	// true 	extract text from content?
    noCopyNeeded = true;	// false  	don't copy parse tree?
    passElement = false;	// true  	pass while expanding?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }
}
