////// Agent_remove.java:  Handler for <agent-remove>
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


/** Handler class for &lt;agent-remove&gt tag 
 * <p> See <a href="../../InterForm/tag_man.html#agent-remove">Manual Entry</a> 
 *	for syntax and description.
 */
public class Agent_remove extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<agent-remove name=\"agent-name\">\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Remove (uninstall) the agent with the given NAME.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) {
      ii.deleteIt();
    }
    Run.getResolver(ii).unRegisterAgent( name );
    ii.replaceIt(name);
  }
}
