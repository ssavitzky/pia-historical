////// Agent_home.java:  Handler for <agent_home>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.sgml.SGML;
import crc.sgml.Element;
import crc.sgml.Text;


/** Handler class for &lt;agent-home&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;agent-home name=ident [link]&gt;
 * <dt>Dscr:<dd>
 *	Return path to a pia agent's home InterForm.  Agent NAME defaults
 *	to the name of the current agent.
 *	Optionally make a LINK.  Very kludgy.
 *  </dl>
 */
public class Agent_home extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<agent-home name=ident [link]>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Return path to a pia agent's home InterForm.  Agent NAME defaults\n" +
    "to the name of the current agent.\n" +
    "Optionally make a LINK.  Very kludgy.\n" +
"";
 
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
      Element t = new Element("a");
      t.attr("href", "/" + home + "/home.if");
      t.append(home);
      ii.replaceIt(t);
    } else {
      ii.replaceIt(home);
    }
  }
}
