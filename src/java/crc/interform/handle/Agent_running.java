////// Agent_running.java:  Handler for <agent-running>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;

import crc.interform.Run;
import crc.pia.Agent;


/** Handler class for &lt;agent-running&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;agent-running name="agent-name"&gt;
 * <dt>Dscr:<dd>
 *	Tests whether the agent with the given NAME is running.
 *  </dl>
 */
public class Agent_running extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<agent-running name=\"agent-name\">\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Tests whether the agent with the given NAME is running.\n" +
"";
 
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
