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

import crc.dps.aux.MathUtil;
import java.util.Enumeration;
import crc.ds.Association;

/** Handler class for &lt;quotient&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#quotient">Manual
 *	Entry</a> for syntax and description.
 */
public class Quotient extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<quotient [digits=D]>n1 n2 ... </quotient>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Return difference (n1/n2/...) of numbers in CONTENT.\n" +
    "Shows D digits after the decimal point.  (Default D=-1 which shows all digits.)" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    Tokens list = Util.listItems(it);
    double result = Util.numValue((SGML)list.shift());
    double n;
    
    while (list.nItems() > 0) {
      result /= Util.numValue((SGML)list.shift());
    }    
    ii.replaceIt(Util.numberToString(result,Util.getInt(it,"digits",-1)));
  }

  /** Legacy action. */
  public boolean action(crc.dps.Context aContext, crc.dps.Output out,
			String tag, crc.dps.active.ActiveAttrList atts,
			crc.dom.NodeList content, String cstring) {
    Enumeration args = MathUtil.getNumbers(content);
    double result = 1;
    Association a;
    if (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      result = a.doubleValue();
    }      
    while (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      result /= a.doubleValue();
    }
    return putText(out,
		   Util.numberToString(result,
				       MathUtil.getInt(atts, "digits", -1)));
  }

}
