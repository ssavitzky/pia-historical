////// Subst.java:  Handler for <subst>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.dps.handle;

import crc.dom.Node;
import crc.dom.BasicElement;
import crc.dom.BasicText;
import crc.dom.Element;
import crc.dom.NodeList;
import crc.dom.DOMFactory;
import crc.dom.AttributeList;

import crc.dps.*;
import crc.dps.active.*;

import crc.gnu.regexp.RegExp;


/** Handler class for &lt;subst&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#subst">Manual
 *	Entry</a> for syntax and description.
 */
public class substHandler extends GenericHandler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<subst match=\"pattern\" result=\"string\">text</subst>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Substitute RESULT string for MATCH pattern in CONTENT.\n" +
"";
 
  public substHandler() {
    /* Expansion control: */
    stringContent = true;	// false 	want content as string?
    expandContent = true;	// false	Expand content?
    passElement = false;	// true 	pass while expanding?
    noCopyNeeded = false;	// true 	don't copy parse tree?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    elementSyntax = -1;			// -1: non-empty 1: empty 0: check
  }

  public void action(ActiveElement e, Context aContext, Output out, String tag, 
  		     AttributeList atts, NodeList content, String cstring) {
    String match = getAttributeString("match", atts);
    //if (ii.missing(ia, "match", match)) return;

    String repl = getAttributeString("result", atts);
    //System.err.println("*** match = " + match + ", result = " + repl
    //		         + " in " + atts.toString());
    String text = cstring;

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

}
