////// Agent_criteria.java:  Handler for <agent-criteria>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.sgml.SGML;
import crc.sgml.Token;
import crc.sgml.Tokens;
import crc.sgml.Text;

/** Handler class for &lt;agent-criteria&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#agent-criteria">Manual Entry</a> 
 *	for syntax and description.
 */
public class Agent_criteria extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<agent-criteria [agent=ident]>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Return the match-criteria list for AGENT or the current agent.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String aname = Util.getString(it, "agent", Run.getAgentName(ii));
    Run env = Run.environment(ii);
    crc.pia.Agent a = env.getAgent(aname);

    ii.replaceIt(new Text(a.criteria().toString()));
  }

  /** Legacy action. */
  public boolean action(crc.dps.Context aContext, crc.dps.Output out,
			String tag, crc.dps.active.ActiveAttrList atts,
			crc.dom.NodeList content, String cstring) {

    String aname = atts.getAttributeString("agent");
    crc.dps.process.ActiveDoc env = getInterFormContext(aContext);
    if (env == null) 
      return legacyError(aContext, tag, "PIA not running: no agent");
    crc.pia.Agent a = env.getAgent(aname);
    return putText(out, a.criteria().toString());
  }

}
