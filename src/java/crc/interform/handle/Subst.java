////// Subst.java:  Handler for <subst>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Text;

import crc.gnu.regexp.RegExp;


/** Handler class for &lt;subst&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;subst match="pattern" result="string"&gt;text&lt;/subst&gt;
 * <dt>Dscr:<dd>
 *	Substitute RESULT string for MATCH pattern in CONTENT.
 *  </dl>
 */
public class Subst extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<subst match=\"pattern\" result=\"string\">text</subst>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Substitute RESULT string for MATCH pattern in CONTENT.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String match = it.attrString("match");
    if (ii.missing(ia, "match", match)) return;

    String repl = it.attrString("result");

    String text = it.contentString();
    try {
      RegExp re = new RegExp(match);
      text = re.substitute(text, repl, true);
    } catch (Exception e) {
      ii.error(ia, "Exception in regexp: "+e.toString());
    }
    ii.replaceIt(text);
  }
}
