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

import crc.dps.NodeType;
import crc.dps.Handler;
import crc.dps.Processor;
import crc.dps.Context;
import crc.dps.Util;

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
 
  /*
  public Token computeResult(Token t, Context c, BasicElement elt) {
    String match = elt.getAttributeString("match");
    //if (ii.missing(ia, "match", match)) return;

    String repl = elt.getAttributeString("result");
    String text = elt.contentString();

    //System.err.print(t.toString() + " content: " +text + " match: " + match
    //	       		+" result: " + repl + " -> ");
    try {
      RegExp re = new RegExp(match);
      text = re.substitute(text + (char)0, repl, true);
      text = text.substring(0, text.length()-1);
    } catch (Exception e) {
      //ii.error(ia, "Exception in regexp: "+e.toString());
    }

    //System.err.println("result: " +text);
    return c.putResult(c.getHandlers().createTextNode(text));
  }
 */
}
