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
public class agentRemove extends crc.dps.handle.GenericHandler {

  public void action(Input in, Context aContext, Output out,
		     ActiveAttrList atts, NodeList content) {
    ActiveDoc env = ActiveDoc.getInterFormContext(aContext);
    if (env == null) {
      reportError(in, aContext, "PIA not running.");
      return;
    }

    String name = atts.getAttributeString("agent");
    if (name == null || name.equals("")) name = atts.getAttributeString("name");
    if (name == null) {
      reportError(in, aContext, "no agent name given");
      return;
    }

    crc.pia.agent.Agency agency = null;
    try {
      agency = (crc.pia.agent.Agency) env.getAgent();
    } catch (Exception e) {
      reportError(in, aContext, "only works in the Agency agent");
      return;
    }
    env.getResolver().unRegisterAgent( name );
    aContext.message(0, "Agent "+name+" removed.", 0, true);    
    putText(out, aContext, name);
  }


  public agentRemove() { syntaxCode = EMPTY; }

}

