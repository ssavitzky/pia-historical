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
 *  <p> See <a href="../../InterForm/tag_man.html#agent-install">Manual
 *	Entry</a> for syntax and description.
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

    // === at this point should get form parameters from content ===

    if (form == null) {
      ii.error(ia, "no form in transaction");
      ii.deleteIt();
      return;
    }

    String name = form.has("agent")? form.at("agent").toString() : null;
    if (name == null) 
      name = form.has("name")? form.at("name").toString() : null;

    if (name == null) {
      ii.error(ia, "name or agent attribute must be supplied");
      ii.deleteIt();
      return;
    }
    form.at("name", name);
    form.at("agent", name);

    crc.pia.agent.Agency agency = null;
    try {
      agency = (crc.pia.agent.Agency) env.agent;
    } catch (Exception e) {
      ii.error(ia, "only works in the Agency agent");
      ii.deleteIt();
      return;
    }
    try {
      agency.install(form); 
    } catch (crc.pia.agent.AgentInstallException e) {
      ii.error(ia, "Install exception: " + e.getMessage());
      ii.deleteIt();
    } 
    ii.message("Agent "+name+" installed.");
    ii.replaceIt(name);
  }
  /** Legacy action. */
  public boolean action(crc.dps.Context aContext, crc.dps.Output out,
			String tag, crc.dps.active.ActiveAttrList atts,
			crc.dom.NodeList content, String cstring) {
    crc.dps.process.ActiveDoc env = getInterFormContext(aContext);
    if (env == null) return legacyError(aContext, tag, "PIA not running.");

    // === buggy -- should get form from content === 
    if (content != null) 
      notify(aContext, tag, atts,
	     "Bug: agent options in content not implemented.");

    crc.ds.Table form = env.getTransaction().getParameters();

    String name = form.has("agent")? form.at("agent").toString() : null;
    if (name == null) 
      name = form.has("name")? form.at("name").toString() : null;

    crc.pia.agent.Agency agency = null;
    try {
      agency = (crc.pia.agent.Agency) env.getAgent();
    } catch (Exception e) {
      reportError(aContext, tag, atts, "only works in the Agency agent");
      return true;
    }
    try {
      agency.install(form); 
    } catch (crc.pia.agent.AgentInstallException e) {
      reportError(aContext, tag, atts, "Install exception: " + e.getMessage());
      return true;
    } 
    aContext.message(0, "Agent "+name+" installed.", 0, true);    
    return putText(out, name);
  }
}

