////// Quotient.java:  Handler for <quotient>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Tokens;


/** Handler class for &lt;quotient&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;quotient&gt;n1 n2 ... &lt;/quotient&gt;
 * <dt>Dscr:<dd>
 *	Return difference (n1/n2/...) of numbers in CONTENT.
 *  </dl>
 */
public class Quotient extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<quotient>n1 n2 ... </quotient>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Return difference (n1/n2/...) of numbers in CONTENT.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    Tokens list = Util.listItems(it);
    double result = Util.numValue((SGML)list.shift());
    double n;
    
    while (list.nItems() > 0) {
      result /= Util.numValue((SGML)list.shift());
    }    
    ii.replaceIt(java.lang.Double.toString(result));
  }
}
