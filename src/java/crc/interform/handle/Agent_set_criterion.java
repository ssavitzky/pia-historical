////// Agent_set_criterion.java:  Handler for <agent-set-criterion>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.sgml.SGML;


/** Handler class for &lt;agent-set-criterion&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;agent-set-criterion name="name" [value="value"] 
 *			     [agent="agent-name"]&gt;
 * <dt>Dscr:<dd>
 *	set match criterion NAME to VALUE (default true), 
 *	optionally in AGENT.  NAME ending in '-' negates test.
 *  </dl>
 */
public class Agent_set_criterion extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<agent-set-criterion name=\"name\" [value=\"value\"] \n" +
    "[agent=\"agent-name\"]>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "set match criterion NAME to VALUE (default true), \n" +
    "optionally in AGENT.  NAME ending in '-' negates test.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;
    String aname= Util.getString(it, "agent", Run.getAgentName(ii));
    String value= Util.getString(it, "value", null);

    Run env = Run.environment(ii);
    crc.pia.Agent a = env.getAgent(aname);

    /* Convert the value (a string) to something acceptable as a match
     *	criterion. === setting from a string would be better === */

    if (value == null) a.matchCriterion(name, true);
    else if (value.equals("")) a.matchCriterion(name, false);
    else if (value.equals("0")) a.matchCriterion(name, false);
    else if (value.equals("1")) a.matchCriterion(name, true);
    else a.matchCriterion(name, value);

    ii.deleteIt();
  }
}
