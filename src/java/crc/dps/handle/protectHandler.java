////// protectHandler.java: <protect> Handler implementation
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
import crc.dps.util.*;

/**
 * Handler for &lt;protect&gt;....&lt;/&gt;  <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class protectHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Just return the content. */
  public void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    putList(out, content);
  }

  /** This does the parse-time dispatching. <p>
   *
   *	Action is dispatched (delegated) to a subclass if the string
   *	being passed to <code>dispatch</code> is either the name of an
   *	attribute or a period-separated suffix of the tagname. <p>
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "markup")) 	 return protect_markup.handle(e);
    if (dispatch(e, "result"))	 return protect_result.handle(e);
    return this;
  }
   
  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public protectHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = false;	// true	recognize tags?
    parseEntitiesInContent = false;	// true	recognize entities?
    syntaxCode = QUOTED;  		// EMPTY, QUOTED, 0 (check)
  }

  protectHandler(ActiveElement e) {
    this();
    // customize for element.
    if (e.hasTrueAttribute("result")) {
      syntaxCode=NORMAL;
      parseElementsInContent = true;	// false	recognize tags?
      parseEntitiesInContent = true;	// false	recognize entities?
    }
  }
}

class protect_markup extends protectHandler {
  /** The action for &lt;protect markup&gt; is tricky: it relies on the fact
   *	that Text, when output, replaces markup characters with entities. 
   */
  public void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    putText(out, aContext, content.toString());
  }
  /** The constructor is also tricky: it relies on the fact that the 
   *	superclass's constructor detects the "result" attribute.
   */
  public protect_markup(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new protect_markup(e); }
}

class protect_result extends protectHandler {
  // The inherited action works because syntaxCode=NORMAL in super(e).
  public protect_result(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new protect_result(e); }
}

