////// Agent_running.java:  Handler for <agent-running>
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
import crc.pia.Agent;

/* Syntax:
 *	<agent-running name="agent-name">
 * Dscr:
 *	Tests whether the agent with the given NAME is running.
 */

/** Handler class for &lt;agent-running&gt tag */
public class Agent_running extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;

    if (Run.getAgent(ii, name) == null) {
      ii.deleteIt();
    } else {
      ii.replaceIt(name);
    }
  }
}
