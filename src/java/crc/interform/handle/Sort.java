////// Sort.java:  Handler for <sort>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Tokens;


/** Handler class for &lt;sort&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;sort [case][text][numeric][reverse]&gt;item, ...&lt;/sort&gt;
 * <dt>Dscr:<dd>
 *	Sort items in CONTENT.  Optionally 
 *	CASE (sensitive), TEXT, NUMERIC, REVERSE.
 *  </dl>
 */
public class Sort extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<sort [case][text][numeric][reverse]>item, ...</sort>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Sort items in CONTENT.  Optionally \n" +
    "CASE (sensitive), TEXT, NUMERIC, REVERSE.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    boolean reverse = it.hasAttr("reverse");
    boolean numeric = it.hasAttr("numeric");
    boolean casesens= it.hasAttr("case");
    boolean text    = it.hasAttr("text");

    Tokens list = Util.listItems(it);

    list = list.sort(reverse, numeric, casesens, text);

    ii.replaceIt(Util.listResult(it, list.elements()));
  }
}
