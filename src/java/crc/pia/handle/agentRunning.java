////// agentRunning.java:  Handler for <agent-home>
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

/** Handler class for &lt;agent-running&gt tag 
 */
public class agentRunning extends crc.dps.handle.GenericHandler {

  public void action(Input in, Context aContext, Output out,
		     ActiveAttrList atts, NodeList content) {
    ActiveDoc env = ActiveDoc.getInterFormContext(aContext);
    if (env == null) {
      reportError(in, aContext, "PIA not running.");
      return;
    }

    String name = atts.getAttributeString("name");
    if (name == null)
      reportError(in, aContext, "NAME attribute missing");
    if (env.getAgent(name) != null) putText(out, aContext, name);
  }
}
