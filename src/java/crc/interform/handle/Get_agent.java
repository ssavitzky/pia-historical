////// Get_agent.java:  Handler for <get.agent>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;
import crc.interform.Tokens;
import crc.interform.Text;
import crc.interform.Util;
import crc.interform.Run;

/* Syntax:
 *	<get-agent [agent="agent-name"] name="name">
 * Dscr:
 *	Get value of NAME, in the AGENT context (i.e. as an option).
 */

/** Handler class for &lt;get-agent&gt tag */
public class Get_agent extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    String aname= Util.getString(it, "agent", Run.getAgentName(ii));
      if (name == null || "".equals(name)) {
	ii.error(ia, "name attribute required");
	return;
      }
    SGML result = null;
    Run env = Run.environment(ii);

    crc.pia.Agent a = env.getAgent(aname);
    if (a != null) {
      ii.replaceIt(a.optionAsString(name));
    } else {
      ii.error(ia, "agent " + aname + " not running");
    }
  }
}
