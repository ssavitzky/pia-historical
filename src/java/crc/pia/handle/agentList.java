////// agentList.java:  Handler for <agent-home>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.pia.handle;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.process.ActiveDoc;

import crc.dom.NodeList;
import crc.pia.Agent;
import crc.pia.Resolver;

import java.util.Enumeration;
import crc.ds.List;

/** Handler class for &lt;agent-list&gt tag 
 */
public class agentList extends crc.dps.handle.GenericHandler {

  public void action(Input in, Context aContext, Output out,
		     ActiveAttrList atts, NodeList content) {
    ActiveDoc env = ActiveDoc.getInterFormContext(aContext);
    if (env == null) {
      reportError(in, aContext, "PIA not running.");
      return;
    }

    String type = atts.getAttributeString("type");
    boolean subs = atts.hasTrueAttribute("subs");

    Resolver resolver = env.getResolver();

    ParseNodeArray list = new ParseNodeArray();
    list.setSep(" ");

    Enumeration names = resolver.agentNames();
    while (names.hasMoreElements()) {
      String name = names.nextElement().toString();
      Agent agent = resolver.agent(name);
      if (subs && name.equals(agent.type())) continue;
      if (type == null || type.equals(agent.type()))
	list.append(new ParseTreeText(name));
    }

    // === should really return list of agent names as a list ===
    putText(out, aContext, list.toString());
  }
}
