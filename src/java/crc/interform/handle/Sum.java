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
    "<sum>n1 n2 ... </sum>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Return sum of numbers in CONTENT.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    Tokens list = Util.listItems(it);
    double result = Util.numValue((SGML)list.shift());
    double n;
    
    while (list.nItems() > 0) {
      result += Util.numValue((SGML)list.shift());
    }    
    ii.replaceIt(java.lang.Double.toString(result));
  }
}

