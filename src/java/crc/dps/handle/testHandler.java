////// testHandler.java: <test> handler.
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
 * Handler for <test>  <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class testHandler extends GenericHandler {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected boolean  inverted = false;
  protected NodeList trueValue = null;
  protected NodeList falseValue = null;

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** This action routine ought to check <em>all</em> of the attributes, even 
   *	in the presence of parse-time dispatching, because some of the
   *	attributes may have contained entities.
   */
  public void action(Input in, Context aContext, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    // Default is simply to test for "truth"
    // aContext.message(0, "Testing " + content + " " + Test.orValues(content), 
    //		        0, true);
    returnBoolean(Test.orValues(content), out, atts);
  }

  /** This does the parse-time dispatching. */
  public Action getActionForNode(Node n) {
    ActiveElement e = (ActiveElement)n;

    // === Parse-time dispatching currently unimplemented ===
    // === Should really test for active attributes before calling ===
    return this;
  }

  /** This returns a boolean <code>value</code> according to the 
   *	<code>not</code>, <code>iftrue</code>, and <code>iffalse</code>
   *	attributes.  <p>
   *
   *	The right information could be encoded into a new instace of the
   *	Handler if necessary; this would require more data but would
   *	definitely run faster.  We may want to consider this later.
   */
  public static void returnBoolean(boolean value,
				   Output out, ActiveAttrList atts) {
    NodeList rv;
    if (atts == NO_ATTRS) {
      if (value) { out.putNode(new ParseTreeText("1")); }
    }
    if (atts.hasTrueAttribute("not")) value = !value;
    if (value) {
      rv = atts.getAttributeValue("iftrue");
      if (rv != null) 	{ Copy.copyNodes(rv, out); }
      else 		{ out.putNode(new ParseTreeText("1")); }
    } else {
      rv = atts.getAttributeValue("iffalse");
      if (rv != null) 	{ Copy.copyNodes(rv, out); }
      // nothing to do if there's no false return value.
    }
  }

  /** This returns a boolean <code>value</code> according to the 
   *	<code>not</code>, <code>iftrue</code>, and <code>iffalse</code>
   *	attributes already accumulated in a fresh instance of the
   *	handler. <p>
   *
   *	This routine takes a Context because the <code>iftrue</code> and
   *	<code>iffalse</code> attributes were acquired at parse time and
   *	may contain entities that have to be expanded.
   */
  public void returnBoolean(boolean value, Context c, Output out) {
    NodeList rv;
    if (inverted) value = !value;
    if (value) {
      if (trueValue != null) 	{ Expand.expandNodes(c, trueValue, out); }
      else 			{ out.putNode(new ParseTreeText("1")); }
    } else {
      if (falseValue != null) 	{ Expand.expandNodes(c, falseValue, out); }
      // nothing to do if there's no false return value.
    }
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public testHandler() {
    /* Expansion control: */
    stringContent = false;	// true 	want content as string?
    expandContent = true;	// false	Expand content?
    passElement = false;	// true 	pass while expanding?
    noCopyNeeded = true;	// false 	don't copy parse tree?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    elementSyntax = -1;			// -1: non-empty 1: empty 0: check
  }

  /** Construct a specialized action. */
  public testHandler(boolean text, boolean invert,
		     NodeList iftrue, NodeList iffalse) {
    textContent = text;
    inverted 	= invert;
    trueValue 	= iftrue;
    falseValue 	= iffalse;

    /* Expansion control: */
    stringContent = false;	// true 	want content as string?
    expandContent = true;	// false	Expand content?
    passElement = false;	// true 	pass while expanding?
    noCopyNeeded = false;	// true 	don't copy parse tree?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    elementSyntax = -1;			// -1: non-empty 1: empty 0: check
  }

  public testHandler(ActiveAttrList atts) {
    this(atts.hasTrueAttribute("text"),
	 atts.hasTrueAttribute("not"),
	 atts.getAttributeValue("iftrue"),
	 atts.getAttributeValue("iffalse"));
  }
}

/*
    boolean result = false;
    SGML test = Util.removeSpaces(it.content());

    if (it.hasAttr("link")) {
      ii.error(ia, "link attr unimplemented.");
    } else if (it.hasAttr("text")) {
      test = test.contentText();
    } 

    if (it.hasAttr("zero")) {
      result = Util.numValue(test) == 0;
    } else if (it.hasAttr("positive")) {
      result = Util.numValue(test) > 0;
    } else if (it.hasAttr("negative")) {
      result = Util.numValue(test) < 0;
    } else if (it.hasAttr("markup")) {
      result = ! test.isText();
    } else if (it.hasAttr("match")) {
      String match = it.attrString("match");
      if (match == null) match = "";
      boolean exact = it.hasAttr("exact");
      boolean csens = it.hasAttr("case");
      if (exact) result = csens? match.equals(test.toString())
		               : match.equalsIgnoreCase(test.toString());
      else {
	String str = test.toString();
	if (! csens) {
	  str = str.toLowerCase();
	  match = match.toLowerCase();
	}
	try {
	  RegExp re = new RegExp(match);
          MatchInfo mi = re.match(str);
	  result = (mi != null && mi.end() >= 0);
	} catch (Exception e) {
	  ii.error(ia, "Exception in regexp: "+e.toString());
	}
      }
    } else {
      result = ! test.isEmpty();
    } 

    if (it.hasAttr("not")) result = ! result;

    if (result) {
      ii.replaceIt(it.hasAttr("iftrue")? it.attr("iftrue") : new Text("1"));
    } else {
      ii.replaceIt(it.hasAttr("iffalse")? it.attr("iffalse") : null);
    }
*/
