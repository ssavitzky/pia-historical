////// parseHandler.java: <parse> Handler implementation
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

import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;

/**
 * Handler for &lt;parse&gt;....&lt;/&gt;  <p>
 *
 *	
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class parseHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;parse&gt; node. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    TopContext top  = cxt.getTopContext();
    String  tsname  = atts.getAttributeString("tagset");
    Tagset      ts  = top.loadTagset(tsname);	// correctly handles null
    String cstring  = content.toString(); // may not be external form!

    parse(in, cxt, out, ts, cstring);
  }

  /** Parse a string using a given tagset. */
  public void parse(Input in, Context cxt, Output out, 
  		    Tagset ts, String cstring) {
    // Create a Parser and TopProcessor to process the string.  
    Parser p  = ts.createParser();
    p.setReader(new StringReader(cstring));
    TopContext proc = cxt.getTopContext().subDocument(p, cxt, out, ts);
    // Crank away.
    proc.run();
  }

  /** This does the parse-time dispatching. <p>
   *
   *	Action is dispatched (delegated) to a subclass if the string
   *	being passed to <code>dispatch</code> is either the name of an
   *	attribute or a period-separated suffix of the tagname. <p>
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "usenet")) 	 return parse_usenet.handle(e);
    return this;
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public parseHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  parseHandler(ActiveElement e) {
    this();
    // customize for element.
  }
}

class parse_usenet extends parseHandler {
  public void action(Input in, Context cxt, Output out,
  		     ActiveAttrList atts, NodeList content) {
    TopContext top  = cxt.getTopContext();
    String  tsname  = atts.getAttributeString("tagset");
    Tagset      ts  = top.loadTagset(tsname);	// correctly handles null
    String cstring  = content.toString(); // may not be external form!

    cstring = TextUtil.addMarkup(cstring);
    parse(in, cxt, out, ts, cstring);
  }
  public parse_usenet(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new parse_usenet(e); }
}
