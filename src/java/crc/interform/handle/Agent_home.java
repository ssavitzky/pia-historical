////// Agent_home.java:  Handler for <agent_home>
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

/* Syntax:
 *	<agent-home name=ident [link]>
 * Dscr:
 *	Return path to a pia agent's home InterForm.  Agent NAME defaults
 *	to the name of the current agent.
 *	Optionally make a LINK.  Very kludgy.
 */

/** Handler class for &lt;agent-home&gt tag */
public class Agent_home extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", Run.getAgentName(ii));
    boolean link = it.hasAttr("link");
    String type = Run.getAgentType(ii, name);

    if (name == null || type == null) {
      ii.deleteIt();
      return;
    }
    String home = (type.equals(name))? name : type + "/" + name;
    if (link) {
      Token t = new Token("a");
      t.attr("href", home+"/home.if");
      t.append(home);
      ii.replaceIt(t);
    } else {
      ii.replaceIt(home);
    }
  }
}
