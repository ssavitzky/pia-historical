////// Trans_control.java:  Handler for <trans-control>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.sgml.SGML;


/** Handler class for &lt;trans-control&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#trans-control">Manual
 *	Entry</a> for syntax and description.
 */
public class Trans_control extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<trans-control>...</trans-control>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Add CONTENT as a control to the current response transaction.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {

    Run env = Run.environment(ii);
    env.transaction.addControl(it.contentString());
    ii.deleteIt();
  }

  /** Legacy action. */
  public boolean action(crc.dps.Context aContext, crc.dps.Output out,
			String tag, crc.dps.active.ActiveAttrList atts,
			crc.dom.NodeList content, String cstring) {
    crc.dps.InterFormProcessor env = getInterFormContext(aContext);
    if (env == null) return legacyError(aContext, tag, "PIA not running");
    env.getTransaction().addControl(cstring);
    return true;
  }
}
