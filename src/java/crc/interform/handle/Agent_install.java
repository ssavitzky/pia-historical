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


/** Handler class for &lt;agent-install&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;agent-install name=ident [type=ident]&gt;...&lt;/agent-install&gt;
 * <dt>Dscr:<dd>
 *	Install an agent with given NAME and TYPE.  CONTENT is options form.
 *	Returns the agent's name.
 *  </dl>
 */
public class Agent_install extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<agent-install name=ident [type=ident]>...</agent-install>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Install an agent with given NAME and TYPE.  CONTENT is options form.\n" +
    "Returns the agent's name.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    Run env = Run.environment(ii);
    crc.ds.Table form = env.transaction.getParameters();
    if (form == null) {
      ii.error(ia, "No form in transaction");
      ii.replaceIt("No form in transaction");
      return;
    }

    String name = form.has("agent")? form.at("agent").toString() : null;
    if (name == null) 
      name = form.has("name")? form.at("name").toString() : null;

    if (name == null) {
      ii.error(ia, "Name or Agent attribute must be supplied");
      ii.replaceIt("name or agent attribute must be supplied");
      return;
    }
    form.at("name", name);
    form.at("agent", name);
    try {
      crc.pia.agent.Agency agency = (crc.pia.agent.Agency) env.agent;
      agency.install(form); 
    } catch (Exception e) {
      ii.error(ia, "only works in the Agency agent");
    }

    ii.replaceIt(form.at("agent").toString());
  }
}

