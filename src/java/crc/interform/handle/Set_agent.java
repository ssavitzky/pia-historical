////// Set.agent.java:  Handler for <set.agent>
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
 *	<set.agent name="name" [hook] [copy]>...</set.agent>
 * Dscr:
 *	set NAME to CONTENT in AGENT.  May set a HOOK (parsed InterForm) 
 *	or string value.  Optionally COPY content as result.  
 */

/** Handler class for &lt;set.agent&gt tag */
public class Set_agent extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;
    String aname= Util.getString(it, "agent", Run.getAgentName(ii));

    Run env = Run.environment(ii);
    SGML value = it.content().simplify();

    crc.pia.Agent a = env.getAgent(aname);

    if (a == null) {
      ii.error(ia, "agent " + aname + " not running");
    } else if (it.hasAttr("hook")) {
      ii.error(ia, "hook unimplemented"); // === a.option("name", value);
    } else {
      a.option("name", value.toString());
    }

    if (it.hasAttr("copy")) {
      ii.replaceIt(value);
    } else {
      ii.deleteIt();
    }
  }
}

