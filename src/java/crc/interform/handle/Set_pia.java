////// Set_pia.java:  Handler for <set.pia>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.sgml.SGML;


/** Handler class for &lt;set.pia&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;set.pia name="name" [copy]&gt;...&lt;/set.pia&gt;
 * <dt>Dscr:<dd>
 *	set NAME to CONTENT in the pia global properties. 
 *	Optionally COPY content as result.
 *  </dl>
 */
public class Set_pia extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<set.pia name=\"name\" [copy]>...</set.pia>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "set NAME to CONTENT in the pia global properties. \n" +
    "Optionally COPY content as result.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;

    SGML value = it.content().simplify();

    crc.pia.Pia.instance().properties().put(name, value.toString());
    // === almost certainly hae to run something to notify about prop. chg.

    if (it.hasAttr("copy")) {
      ii.replaceIt(value);
    } else {
      ii.deleteIt();
    }
  }
}

