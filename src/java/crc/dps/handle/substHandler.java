////// Subst.java:  Handler for <subst>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.dps.handle;

import crc.dom.Node;
import crc.dom.Element;
import crc.dom.NodeList;
import crc.dom.DOMFactory;
import crc.dom.AttributeList;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.aux.*;

import crc.gnu.regexp.RegExp;


/** Handler class for &lt;subst&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#subst">Manual
 *	Entry</a> for syntax and description.
 */
public class substHandler extends GenericHandler {
 
  public void action(Input in, Context aContext, Output out) {
    ActiveAttrList atts = Expand.getExpandedAttrs(in, aContext);
    String text = Expand.getProcessedContentString(in, aContext);
      
    String match = atts.getAttributeString("match");
    //if (ii.missing(ia, "match", match)) return;

    String repl = atts.getAttributeString("result");
    //System.err.println("*** match = " + match + ", result = " + repl
    //		         + " in " + atts.toString());

    try {
      RegExp re = new RegExp(match);
      text = re.substitute(text + (char)0, repl, true);
      text = text.substring(0, text.length()-1);
    } catch (Exception ex) {
      //ii.error(ia, "Exception in regexp: "+e.toString());
    }

    //System.err.println("result: " +text);
    out.putNode(new ParseTreeText(text));
  }


  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public substHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = QUOTED;  		// EMPTY, QUOTED, 0 (check)
  }

  substHandler(ActiveElement e) {
    this();
    // customize for element.
    if (e.hasTrueAttribute("result")) syntaxCode=NORMAL;
  }
}

