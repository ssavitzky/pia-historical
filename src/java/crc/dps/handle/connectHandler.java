////// connectHandler.java: <connect> Handler implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;

import crc.dom.NodeList;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;

import java.io.*;

/**
 * Handler for &lt;connect&gt;....&lt;/&gt;  <p>
 *
 * <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class connectHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;connect&gt; node. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    TopContext top  = cxt.getTopContext();
    String src   = atts.getAttributeString("src");
    String ename = atts.getAttributeString("entity");
    String  tsname  = atts.getAttributeString("tagset");

    Tagset      ts  = top.loadTagset(tsname);	// correctly handles null
    TopContext proc = null;
    InputStream stm = null;

    ParseTreeExternal ent = null;
    if (ename != null) ent = new ParseTreeExternal(ename, src, null);

    // Actually do the work. 
    unimplemented(in, cxt);	// <connect> ===
  }

  /** This does the parse-time dispatching. <p>
   *
   *	Action is dispatched (delegated) to a subclass if the string
   *	being passed to <code>dispatch</code> is either the name of an
   *	attribute or a period-separated suffix of the tagname. <p>
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "")) 	 return connect_.handle(e);
    return this;
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public connectHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  connectHandler(ActiveElement e) {
    this();
    // customize for element.
  }
}

class connect_ extends connectHandler {
  public void action(Input in, Context aContext, Output out,
  		     ActiveAttrList atts, NodeList content) {
    // do the work
  }
  public connect_(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new connect_(e); }
}
