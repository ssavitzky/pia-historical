////// valueHandler.java: <value> Handler implementation
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
 * Handler for &lt;value&gt;....&lt;/&gt;  <p>
 *
 *	This is a sub-element of &lt;define&gt;.  It actually performs no
 *	actions; we just need to make sure a corresponding node ends up in
 *	the output where <code>defineHandler</code> can find it.
 *
 *	The handler's class is used to recognize the corresponding element.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class valueHandler extends GenericHandler {

  /** The default is to expand the content at the point where it's defined. */
  static valueHandler DEFAULT = new valueHandler();

  /** QUOTED_VALUE is not expanded at the point where it is defined. */
  static valueHandler QUOTED_VALUE  = new valueHandler(QUOTED);

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;value&gt; node. */
  public void action(Input in, Context cxt, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    // Actually do the work. 
    ActiveElement e = in.getActive().asElement();
    ActiveElement element = e.editedCopy(atts, null);
    // === should be able to skip expanding the attrs altogether for <value>
    out.startElement(element);
    Copy.copyNodes(content, out);
    out.endElement(e.isEmptyElement() || e.implicitEnd());
  }

  /** This does the parse-time dispatching. <p>
   *
   *	Action is dispatched (delegated) to a subclass if the string
   *	being passed to <code>dispatch</code> is either the name of an
   *	attribute or a period-separated suffix of the tagname. <p>
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "quoted")) 	 return QUOTED_VALUE;
    return this;
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public valueHandler() {
    /* Expansion control: */
    stringContent = false;	// true 	want content as string?
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  valueHandler(int syntax) {
    syntaxCode = syntax;
  }

  valueHandler(ActiveElement e) {
    this();
    // customize for element.
  }
}
