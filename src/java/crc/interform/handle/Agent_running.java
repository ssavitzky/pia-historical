////// Agent_running.java:  Handler for <agent-running>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;

import crc.interform.Run;
import crc.pia.Agent;


/** Handler class for &lt;agent-running&gt tag 
 *<p>See <a href="../../InterForm/tag_man.html#agent-running">Manual Entry</a> 
 *	for syntax and description.
 */
public class Agent_running extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<agent-running name=\"agent-name\">\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Tests whether the agent with the given NAME is running.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;

    if (Run.getAgent(ii, name) == null) {
      ii.deleteIt();
    } else {
      ii.replaceIt(name);
    }
  }

  /** Legacy action. */
  public boolean action(crc.dps.Context aContext, crc.dps.Output out,
			String tag, crc.dps.active.ActiveAttrList atts,
			crc.dom.NodeList content, String cstring) {
    crc.dps.InterFormProcessor env = getInterFormContext(aContext);
    if (env == null) return legacyError(aContext, tag, "PIA not running");

    String name = atts.getAttributeString("name");
    if (name == null)
      return legacyError(aContext, tag, "NAME attribute missing");
    if (env.getAgent(name) != null) putText(out, name);
    return true;
  }
}
