////// Agent_set_options.java:  Handler for <agent-set-options>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.sgml.SGML;
import crc.sgml.Tokens;


/** Handler class for &lt;agent-set-options&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;agent-set-options [name="agent-name"]&gt;options&lt;/agent-set-options&gt;
 * <dt>Dscr:<dd>
 *	Sets CONTENT as options for agent NAME.
 *  </dl>
 */
public class Agent_set_options extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<agent-set-options [name=\"agent-name\"]>options</agent-set-options>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Sets CONTENT as options for agent NAME.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "agent",
				 Util.getString(it, "name",
						Run.getAgentName(ii)));
    if (ii.missing(ia, "name or agent attribute", name)) return;

    Run env = Run.environment(ii);
    crc.pia.Agent a = env.getAgent(name);

    a.addAttrs(Util.getPairs(it, ii, true));

    ii.deleteIt();
  }
}

