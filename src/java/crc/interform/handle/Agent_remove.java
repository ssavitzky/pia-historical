////// Agent_remove.java:  Handler for <agent-remove>
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
 *	<agent-remove name="agent-name">
 * Dscr:
 *	Remove (uninstall) the agent with the given NAME.
 */

/** Handler class for &lt;agent-remove&gt tag */
public class Agent_remove extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;
    Run.getResolver(ii).unRegisterAgent( name );
    ii.replaceIt(name);
  }
}
