////// Agent_install.java:  Handler for <agent-install>
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
 *	<agent-install name=ident [type=ident]>...</agent-install>
 * Dscr:
 *	Install an agent with given NAME and TYPE.  CONTENT is options form.
 *	Returns the agent's name.
 */

/** Handler class for &lt;agent-install&gt tag */
public class Agent_install extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;

    String type = Util.getString(it, "type", name);
    Run env = Run.environment(ii);

    try {
      crc.pia.agent.Agency agency = (crc.pia.agent.Agency) env.agent;
      agency.install(env.transaction.getParameters()); 
    } catch (Exception e) {
      ii.error(ia, "only works in the Agency agent");
    }

    ii.replaceIt(name);
  }
}

