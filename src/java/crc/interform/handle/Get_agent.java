////// Get_agent.java:  Handler for <get.agent>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.sgml.SGML;


/** Handler class for &lt;get-agent&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;get-agent [agent="agent-name"] name="name"&gt;
 * <dt>Dscr:<dd>
 *	Get value of NAME, in the AGENT context (i.e. as an option).
 *  </dl>
 */
public class Get_agent extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<get-agent [agent=\"agent-name\"] name=\"name\">\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Get value of NAME, in the AGENT context (i.e. as an option).\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;

    String aname= Util.getString(it, "agent", Run.getAgentName(ii));

    SGML result = null;
    Run env = Run.environment(ii);

    crc.pia.Agent a = env.getAgent(aname);
    if (a != null) {
      ii.replaceIt(a.attr(name));
    } else {
      ii.error(ia, "agent " + aname + " not running");
    }
  }
}
