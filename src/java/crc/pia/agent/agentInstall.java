////// agentInstall.java:  Handler for <agent-install>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.pia.agent;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.process.ActiveDoc;
import crc.dom.NodeList;

/** Handler class for &lt;agent-install&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#agent-install">Manual
 *	Entry</a> for syntax and description.
 */
public class agentInstall extends crc.dps.handle.GenericHandler {

  public void action(Input in, Context aContext, Output out,
		     ActiveAttrList atts, NodeList content) {
    ActiveDoc env = ActiveDoc.getInterFormContext(aContext);
    if (env == null) {
      reportError(in, aContext, "PIA not running.");
      return;
    }

    // === buggy -- should get form from content === 
    //if (content != null) 
    //  notify(in, aContext, "Bug: agent options in content not implemented.");

    crc.pia.Transaction trans = env.getTransaction();
    crc.pia.Transaction req = trans.requestTran();
    crc.ds.Table form = req.getParameters();
    if (form == null) return;

    String name = form.has("agent")? form.at("agent").toString() : null;
    if (name == null) 
      name = form.has("name")? form.at("name").toString() : null;

    crc.pia.agent.Admin admin = null;
    try {
      admin = (crc.pia.agent.Admin) env.getAgent();
    } catch (Exception e) {
      reportError(in, aContext, "only works in the Admin agent");
      return;
    }
    try {
      admin.install(form); 
    } catch (crc.pia.agent.AgentInstallException e) {
      reportError(in, aContext, "Install exception: " + e.getMessage());
      return;
    } 
    aContext.message(0, "Agent "+name+" installed.", 0, true);    
    putText(out, aContext, name);
  }
}

