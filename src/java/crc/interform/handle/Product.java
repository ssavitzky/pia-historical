////// Product.java:  Handler for <product>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Tokens;


/** Handler class for &lt;product&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;product&gt;n1 n2 ... &lt;/product&gt;
 * <dt>Dscr:<dd>
 *	Return product of numbers in CONTENT
 *  </dl>
 */
public class Product extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<product>n1 n2 ... </product>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Return product of numbers in CONTENT\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    Tokens list = Util.listItems(it);
    double result = Util.numValue((SGML)list.shift());
    double n;
    
    while (list.nItems() > 0) {
      result *= Util.numValue((SGML)list.shift());
    }    
    ii.replaceIt(java.lang.Double.toString(result));
  }
}
