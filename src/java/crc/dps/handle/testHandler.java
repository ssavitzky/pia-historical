////// testHandler.java: <test> handler.
//	$Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Element;

import crc.ds.Association;
import crc.ds.List;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;

import gnu.regexp.RegExp;
import gnu.regexp.MatchInfo;

import java.util.Enumeration;

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

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** This action routine ought to check <em>all</em> of the attributes, even 
   *	in the presence of parse-time dispatching, because some of the
   *	attributes may have contained entities.
   */
  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    // Default is simply to test for "truth"
    returnBoolean(Test.orValues(content, aContext), out, atts);
  }

  /** This does the parse-time dispatching. */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();

    if (dispatch(e, "zero")) 	 return test_zero.handle(e);
    if (dispatch(e, "positive")) return test_positive.handle(e);
    if (dispatch(e, "negative")) return test_negative.handle(e);
    if (dispatch(e, "match")) 	 return test_match.handle(e);
    if (dispatch(e, "null")) 	 return test_null.handle(e);
    if (dispatch(e, "numeric"))	 return test_numeric.handle(e);
    if (dispatch(e, "sorted"))	 return test_sorted.handle(e);

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
    if (value) { out.putNode(new ParseTreeText("1")); }
    // nothing to do if there's no false return value.
  }

  /** This returns a boolean <code>value</code> possibly modified by
   *	a <code>not</code> attribute already stored in the handler instance.
   *	<p>
   *
   *	This routine takes a Context because the <code>iftrue</code> and
   *	<code>iffalse</code> attributes were acquired at parse time and
   *	may contain entities that have to be expanded.
   */
  public void returnBoolean(boolean value, Context c, Output out) {
    //c.debug("<test> returning " + value + " " + getClass().getName() + "\n");
    if (inverted) value = !value;
    if (value) { out.putNode(new ParseTreeText("1")); }
    // nothing to do if there's no false return value.
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public testHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = EMPTY;
  }

  /** Construct a specialized action. */
  public testHandler(boolean string, boolean text, boolean invert) {
    inverted 	= invert;

    /* Expansion control: */
    textContent = text;		//	want only text in content?

    expandContent = true;	// false	Expand content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = EMPTY;
  }

  public testHandler(ActiveElement e) {
    this(false, e.hasTrueAttribute("text"),
	 e.hasTrueAttribute("not"));
  }
  public testHandler(ActiveElement e, boolean text, boolean string) {
    this(string, text,
	 e.hasTrueAttribute("not"));
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

/** Test for zero.  Whitespace is considered zero, but non-blanks are not. */
class test_zero extends testHandler {
  public void action(Input in, Context aContext, Output out) {
    String cstring = textContent
      ? Expand.getProcessedTextString(in, aContext)
      : Expand.getProcessedContentString(in, aContext);
    Association a = Association.associateNumeric(null, cstring);
    returnBoolean(( (a.isNumeric() && a.doubleValue() == 0.0)
		    || (!a.isNumeric() && Test.isWhitespace(cstring))),
		  aContext, out);
  }
  public test_zero(ActiveElement e) { super(e, true, true); }
  static Action handle(ActiveElement e) { return new test_zero(e); }
}

class test_positive extends testHandler {
  public void action(Input in, Context aContext, Output out) {
    String cstring = textContent
      ? Expand.getProcessedTextString(in, aContext)
      : Expand.getProcessedContentString(in, aContext);
    Association a = Association.associateNumeric(null, cstring);
    returnBoolean(a.doubleValue() > 0.0, aContext, out);
  }
  public test_positive(ActiveElement e) { super(e, true, true); }
  static Action handle(ActiveElement e) { return new test_positive(e); }
}

class test_negative extends testHandler {
  public void action(Input in, Context aContext, Output out) {
    String cstring = textContent
      ? Expand.getProcessedTextString(in, aContext)
      : Expand.getProcessedContentString(in, aContext);
    Association a = Association.associateNumeric(null, cstring);
    returnBoolean(a.doubleValue() < 0.0, aContext, out);
  }
  public test_negative(ActiveElement e) { super(e, true, true); }
  static Action handle(ActiveElement e) { return new test_negative(e); }
}

/** Test for numeric.  Although whitespace is considered equal to zero,
 *	it is not considered numeric. */
class test_numeric extends testHandler {
  public void action(Input in, Context aContext, Output out) {
    String cstring = textContent
      ? Expand.getProcessedTextString(in, aContext)
      : Expand.getProcessedContentString(in, aContext);
    Association a = Association.associateNumeric(null, cstring);
    returnBoolean(a.isNumeric(), aContext, out);
  }
  public test_numeric(ActiveElement e) { super(e, true, true); }
  static Action handle(ActiveElement e) { return new test_numeric(e); }
}

class test_sorted extends testHandler {

  protected boolean reverse = false;
  protected boolean caseSens = false;
  protected boolean text = false;
  boolean result = false;

  public void action(Input in, Context aContext, Output out,
		     ActiveAttrList atts, NodeList content) {

    boolean result = true;

    reverse = atts.hasTrueAttribute("reverse");
    caseSens = atts.hasTrueAttribute("case");
    text = atts.hasTrueAttribute("text");

    if(text) {
      List tl = TextUtil.getTextList(content, caseSens);
      long len = tl.size();
      
      for(int i = 1; i < len; i++) {
	int c = ((Association)tl.at(i)).compareTo((Association)tl.at(i-1));
	if ( (reverse && (c > 0)) || (!reverse && (c < 0)) ) {
	  result = false;
	  break;
	}
      }
    }
    returnBoolean(result, aContext, out);
  }

  public test_sorted(ActiveElement e) { super(e, true, true); }
  static Action handle(ActiveElement e) { return new test_sorted(e); }
}

class test_match extends testHandler {
  boolean exactMatch = false;
  boolean caseSens   = false;
  public void action(Input in, Context aContext, Output out) {
    ActiveAttrList atts = Expand.getExpandedAttrs(in, aContext);
    String cstring = textContent
      ? Expand.getProcessedTextString(in, aContext)
      : Expand.getProcessedContentString(in, aContext);

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
    exactMatch = e.hasTrueAttribute("exact");
    caseSens   = e.hasTrueAttribute("case");
  }
  static Action handle(ActiveElement e) { return new test_match(e); }

}

class test_null extends testHandler {
  public void action(Input in, Context aContext, Output out) {
    ParseNodeList content = textContent
      ? Expand.getProcessedText(in, aContext)
      : Expand.getProcessedContent(in, aContext);

    returnBoolean(content == null || content.getLength() == 0, aContext, out);
  }
  public test_null(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new test_null(e); }
}

