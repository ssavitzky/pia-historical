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
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;agent-criteria [agent=ident]&gt;
 * <dt>Dscr:<dd>
 *	Return the match-criteria list for AGENT or the current agent.
 *  </dl>
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
    String aname= Util.getString(it, "agent", Run.getAgentName(ii));
    Run env = Run.environment(ii);
    crc.pia.Agent a = env.getAgent(aname);

    ii.replaceIt(new Text(a.criteria().toString()));
  }
}
