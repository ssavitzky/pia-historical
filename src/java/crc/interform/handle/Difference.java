////// Difference.java:  Handler for <difference>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Tokens;
import crc.sgml.Text;


/** Handler class for &lt;difference&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;difference&gt;n1 n2 ... &lt;/difference&gt;
 * <dt>Dscr:<dd>
 *	Return difference of numbers in CONTENT
 *  </dl>
 */
public class Difference extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<difference>n1 n2 ... </difference>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Return difference of numbers in CONTENT\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    Tokens list = Util.listItems(it);
    double result = Util.numValue((SGML)list.shift());
    double n;
    
    while (list.nItems() > 0) {
      result -= Util.numValue((SGML)list.shift());
    }    
    ii.replaceIt(new Text(java.lang.Double.toString(result)));
  }
}

