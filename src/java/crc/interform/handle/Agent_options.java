////// Agent_options.java:  Handler for <agent-options>
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


/** Handler class for &lt;agent-options&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#agent-options">Manual
 *	Entry</a> for syntax and description.
 */
public class Agent_options extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<agent-options [name=\"agent-name\"]>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Returns list of option names for agent NAME.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "agent",
				 Util.getString(it, "name",
						Run.getAgentName(ii)));
    if (ii.missing(ia, "name or agent attribute", name)) return;

    Run env = Run.environment(ii);
    crc.pia.Agent a = env.getAgent(name);

    ii.replaceIt(Util.listResult(it, a.attrs()));
  }
}
