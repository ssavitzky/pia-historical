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

/* Syntax:
 *	<agent-set-criterion name="name" [value="value"] 
 *			     [agent="agent-name"]>
 * Dscr:
 *	set match criterion NAME to VALUE (default true), 
 *	optionally in AGENT.
 */

/** Handler class for &lt;agent-set-criterion&gt tag */
public class Agent_set_criterion extends crc.interform.Handler {
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
