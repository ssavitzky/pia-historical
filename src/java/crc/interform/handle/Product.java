////// Product.java:  Handler for <product>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Text;
import crc.sgml.Token;
import crc.sgml.Tokens;

/* Syntax:
 *	<product>n1 n2 ... </product>
 * Dscr:
 *	Return product of numbers in CONTENT
 */

/** Handler class for &lt;product&gt tag */
public class Product extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    Tokens list = Util.listItems(it);
    double result = Util.numValue((SGML)list.shift());
    double n;
    
    while (list.nItems() > 0) {
      result *= Util.numValue((SGML)list.shift());
    }    
    ii.replaceIt(new Text(java.lang.Double.toString(result)));
  }
}
