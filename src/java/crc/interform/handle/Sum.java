////// Sum.java:  Handler for <sum>
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
 *	<sum>n1 n2 ... </sum>
 * Dscr:
 *	Return sum of numbers in CONTENT.
 */

/** Handler class for &lt;sum&gt tag */
public class Sum extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    Tokens list = Util.listItems(it);
    double result = Util.numValue((SGML)list.shift());
    double n;
    
    while (list.nItems() > 0) {
      result += Util.numValue((SGML)list.shift());
    }    
    ii.replaceIt(new Text(java.lang.Double.toString(result)));
  }
}

