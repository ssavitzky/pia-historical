////// agentHome.java:  Handler for <agent-home>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.pia.agent;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.process.ActiveDoc;

import crc.dom.NodeList;
import crc.pia.Agent;
import crc.ds.List;

/** Handler class for &lt;agent-home&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#agent-home">Manual Entry</a> 
 *	for syntax and description.
 */
public class agentHome extends crc.dps.handle.GenericHandler {

  public void action(Input in, Context aContext, Output out,
		     ActiveAttrList atts, NodeList content) {
    ActiveDoc env = ActiveDoc.getInterFormContext(aContext);
    if (env == null) {
      reportError(in, aContext, "PIA not running.");
      return;
    }

    String name = env.getAgentName(atts.getAttributeString("agent"));
    String type = env.getAgentType(name);
    boolean link = atts.hasTrueAttribute("link");

    String home = (type.equals(name))? name : type + "/" + name;
    if (link) {
      ActiveElement t = new ParseTreeElement("a");
      t.setAttributeValue("href", "/" + home + "/home");
      t.addChild(new ParseTreeText(home));
      out.putNode(t);
    } else {
      putText(out, aContext, home);
    }
  }
}
