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
 *  <p> See <a href="../../InterForm/tag_man.html#set.pia">Manual
 *	Entry</a> for syntax and description.
 */
public class Set_pia extends Set {
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

    SGML value = getValue(it);

    // properties are not SGML objects so treat differently than most sets
    crc.pia.Pia.instance().properties().put(name, value.toString());
    // === almost certainly hae to run something to notify about prop. chg.

    doFinish(it, value, ii);
  }
}

