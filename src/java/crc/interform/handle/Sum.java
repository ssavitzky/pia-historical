////// Sum.java:  Handler for <sum>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Tokens;


/** Handler class for &lt;sum&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;sum&gt;n1 n2 ... &lt;/sum&gt;
 * <dt>Dscr:<dd>
 *	Return sum of numbers in CONTENT.
 *  </dl>
 */
public class Sum extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<sum [digits=D]>n1 n2 ... </sum>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Return sum of numbers in CONTENT.\n" +
    "Shows D digits after the decimal point.  (Default D=-1 which shows all digits.)" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    Tokens list = Util.listItems(it);
    double result = 0.0;
    double n;
    
    for (int i = 0; i < list.nItems(); ++i) {
      result += Util.numValue(list.itemAt(i));
    }    
// Need a default for digits
    ii.replaceIt(Util.numberToString(result,Util.getInt(it,"digits",-1)));
  }
}

