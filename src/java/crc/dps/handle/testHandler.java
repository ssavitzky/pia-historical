////// testHandler.java: <test> handler.
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Element;

import crc.ds.Association;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.aux.*;

import crc.gnu.regexp.RegExp;
import crc.gnu.regexp.MatchInfo;

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
    returnBoolean(Test.orValues(content), out, atts);
  }

  /** This does the parse-time dispatching. */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();

    if (dispatch(e, "zero")) 	 return new test_zero(e);
    if (dispatch(e, "positive")) return new test_positive(e);
    if (dispatch(e, "negative")) return new test_negative(e);
    if (dispatch(e, "match")) 	 return new test_match(e);
    if (dispatch(e, "null")) 	 return new test_null(e);

    if (e.getAttributes() == null || e.getAttributes().getLength() == 0)
      return this;
    else return new testHandler(e);
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
    if (atts == NO_ATTRS) {
      if (value) { out.putNode(new ParseTreeText("1")); }
      return;
    }
    NodeList rv;
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
    //c.debug("<test> returning " + value + " " + getClass().getName() + "\n");
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
  public testHandler(boolean string, boolean text, boolean invert,
		     NodeList iftrue, NodeList iffalse) {
    inverted 	= invert;
    trueValue 	= iftrue;
    falseValue 	= iffalse;

    /* Expansion control: */
    stringContent = string;	//  	want content as string?
    textContent = text;		//	want only text in content?

    expandContent = true;	// false	Expand content?
    passElement = false;	// true 	pass while expanding?
    noCopyNeeded = true;	// false 	don't copy parse tree?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    elementSyntax = -1;			// -1: non-empty 1: empty 0: check
  }

  public testHandler(ActiveElement e) {
    this(false, e.hasTrueAttribute("text"),
	 e.hasTrueAttribute("not"),
	 e.getAttributeValue("iftrue"),
	 e.getAttributeValue("iffalse"));
  }
  public testHandler(ActiveElement e, boolean text, boolean string) {
    this(string, text,
	 e.hasTrueAttribute("not"),
	 e.getAttributeValue("iftrue"),
	 e.getAttributeValue("iffalse"));
  }
}

/*	=== unimplemented stuff from legacy implementation ===
    boolean result = false;
    SGML test = Util.removeSpaces(it.content());

    if (it.hasAttr("link")) {
      ii.error(ia, "link attr unimplemented.");
    } else if (it.hasAttr("text")) {
      test = test.contentText();
    } 

    } else if (it.hasAttr("markup")) {
      result = ! test.isText();
*/

/* ***********************************************************************
 * Subclasses:
 *
 *	These subclasses cannot be used as stand-alone handlers; they
 *	really only work as <code>action</code> handlers because they
 *	assume that <code>trueValue</code>, <code>falseValue</code>, etc. 
 *	are set up properly. 
 *
 *	The correct thing is to have dispatching look at the tag as well
 *	as the attributes of the element being dispatched on; this is
 *	done by the <code>dispatch(<em>e, name</em>)</code> function 
 *	in the most common case where <em>name</em> is either the name
 *	of an attribute or a suffix of the element's tagname.
 *
 ************************************************************************/


class test_zero extends testHandler {
  public void action(Input in, Context aContext, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    Association a = Association.associateNumeric(null, cstring);
    returnBoolean(a.isNumeric() && a.doubleValue() == 0.0, aContext, out);
  }
  public test_zero(ActiveElement e) { super(e, true, true); }
}

class test_positive extends testHandler {
  public void action(Input in, Context aContext, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    Association a = Association.associateNumeric(null, cstring);
    returnBoolean(a.doubleValue() > 0.0, aContext, out);
  }
  public test_positive(ActiveElement e) { super(e, true, true); }
}

class test_negative extends testHandler {
  public void action(Input in, Context aContext, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    Association a = Association.associateNumeric(null, cstring);
    returnBoolean(a.doubleValue() < 0.0, aContext, out);
  }
  public test_negative(ActiveElement e) { super(e, true, true); }
}

class test_match extends testHandler {
  boolean exactMatch = false;
  boolean caseSens   = false;
  public void action(Input in, Context aContext, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    String match = atts.getAttributeString("match");
    if (match == null) match = "";
    boolean result = false;
    if (exactMatch) {
      result = caseSens? match.equals(cstring)
		       : match.equalsIgnoreCase(cstring);
    } else {
      if (! caseSens) {
	cstring = cstring.toLowerCase();
	match   = match.toLowerCase();
      }
      try {
	RegExp re = new RegExp(match);
	MatchInfo mi = re.match(cstring);
	result = (mi != null && mi.end() >= 0);
      } catch (Exception ex) {
	// === ii.error(ia, "Exception in regexp: "+ex.toString());
      }
    }
    returnBoolean(result, aContext, out);
  }
  public test_match(ActiveElement e) {
    super(e);
    stringContent = true;
    exactMatch = e.hasTrueAttribute("exact");
    caseSens   = e.hasTrueAttribute("case");
  }
}

class test_null extends testHandler {
  public void action(Input in, Context aContext, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    returnBoolean(content == null || content.getLength() == 0, aContext, out);
  }
  public test_null(ActiveElement e) { super(e); }
}

