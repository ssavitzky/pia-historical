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

/* Syntax:
 *	<difference>n1 n2 ... </difference>
 * Dscr:
 *	Return difference of numbers in CONTENT
 */

/** Handler class for &lt;difference&gt tag */
public class Difference extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    Tokens list = Util.listItems(it);
    double result = Util.numValue((SGML)list.shift());
    double n;
    
    while (list.nItems() > 0) {
      result -= Util.numValue((SGML)list.shift());
    }    
    ii.replaceIt(new crc.sgml.Text(java.lang.Double.toString(result)));
  }
}

