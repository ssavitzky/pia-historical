////// hideHandler.java: <hide> Handler implementation
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
import crc.dps.output.*;

/**
 * Handler for &lt;hide&gt;....&lt;/&gt;  <p>
 *
 *	
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class hideHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** We're assuming that this is an <em>active</em> node, so call
   *	the three-input <code>action</code> routine to do the work.
   */
  public int actionCode(Input in, Processor p) {
    action(in, p, p.getOutput());
    return Action.COMPLETED;
  }

  /** This routine does the work, such as it is.  */
  public void action(Input in, Context aContext, Output out) {
    if (in.hasActiveChildren()) {
      aContext.subProcess(in, new DiscardOutput()).processChildren();
    }
  }

  /** This does the parse-time dispatching. <p>
   *
   *	Action is dispatched (delegated) to a subclass if the string
   *	being passed to <code>dispatch</code> is either the name of an
   *	attribute or a period-separated suffix of the tagname. <p>
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "markup")) 	 return hide_markup.handle(e);
    if (dispatch(e, "text")) 	 return hide_text.handle(e);
    return this;
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public hideHandler() {
    /* Expansion control: */
    stringContent = false;	// true 	want content as string?
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  hideHandler(ActiveElement e) {
    this();
    // customize for element.
  }
}

class hide_markup extends hideHandler {
  /** This routine does the work, such as it is.  */
  public void action(Input in, Context aContext, Output out) {
    if (in.hasActiveChildren()) {
      aContext.subProcess(in, new FilterText(out)).processChildren();
    } else {
      Copy.copyChildren(in, new FilterText(out));
    }
  }
  public hide_markup(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new hide_markup(e); }
}

class hide_text extends hideHandler {
  /** This routine does the work, such as it is.  */
  public void action(Input in, Context aContext, Output out) {
    if (in.hasActiveChildren()) {
      aContext.subProcess(in, new FilterMarkup(out)).processChildren();
    } else {
      Copy.copyChildren(in, new FilterMarkup(out));
    }
  }
  public hide_text(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new hide_text(e); }
}

