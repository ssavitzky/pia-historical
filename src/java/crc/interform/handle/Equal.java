////// Equal.java:  Handler for <equal>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Token;
import crc.sgml.Tokens;
import crc.sgml.Text;

import crc.ds.List;
import crc.ds.Association;

/** Handler class for &lt;equal&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;equal [not] [case] [text] [numeric]&gt;list...&lt;/equal&gt;
 * <dt>Dscr:<dd>
 *	Test list items in CONTENT for equality; 
 *	return null or IFFALSE if false, else '1' or IFTRUE. 
 *	<dt>Modifiers:<dd> NOT, CASE (sensitive), TEXT, NUMERIC.
 *  </dl>
 */
public class Equal extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<equal [not] [case] [text] [numeric]>list...</equal>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Test list items in CONTENT for equality; \n" +
    "return null or IFFALSE if false, else '1' or IFTRUE. \n" +
    "Modifiers: NOT, CASE (sensitive), TEXT, NUMERIC.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {

    boolean numeric = it.hasAttr("numeric");
    boolean casesens= it.hasAttr("case");
    boolean text    = it.hasAttr("text");

    boolean result = true;

    List list = Util.assocItems(it, numeric, casesens, text);
    
    for (int i = 1; i < list.nItems(); ++i) {
      if (! ((Association)list.at(i)).equals(((Association)list.at(i-1)))) {
	result = false;
	break;
      }
    }

    if (result) {
      ii.replaceIt(it.hasAttr("iftrue")? it.attr("iftrue") : new Text("1"));
    } else {
      ii.replaceIt(it.hasAttr("iffalse")? it.attr("iffalse") : null);
    }

  }
}

