////// agentSave.java:  Handler for <agent-save>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.pia.agent;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;
import crc.dps.process.ActiveDoc;

import crc.dom.NodeList;
import crc.pia.Agent;
import crc.ds.List;

import java.io.File;
import java.io.FileInputStream;


/** Handler class for &lt;agent-save&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#agent-save">Manual Entry</a> 
 *	for syntax and description.
 */
public class agentSave extends crc.dps.handle.GenericHandler {

  public void action(Input in, Context aContext, Output out,
		     ActiveAttrList atts, NodeList content) {
    ActiveDoc env = ActiveDoc.getInterFormContext(aContext);
    if (env == null) {
      reportError(in, aContext, "PIA not running.");
      return;
    }

    List list = new List(ListUtil.getListItems(atts.getAttributeValue("list")));

    if (list.nItems() == 0) {
      String name = atts.getAttributeString("agent");
      if (name == null) name = env.getAgentName();
      if (name != null) list.push(new ParseTreeText(name));
    }

    List agents = new List();
    for (int i = 0; i < list.nItems(); ++i) {
      Agent agent = env.getAgent(list.at(i).toString());
      if (agent != null) {
	agents.push(agent);
      }
    }

    if (agents.nItems() == 0) {
      reportError(in, aContext,
		  "No agents specified or specified agents not found");
      return;
    }

    String fn = atts.getAttributeString("file");
    if (fn == null) {
      reportError(in, aContext, "Must have non-null file attribute.");
      return;
    }
    File file = env.locateSystemResource(fn, true);
    if (file == null) {
      reportError(in, aContext, "Cannot locate " + fn);
      return;
    }
    File parent = (file.getParent()!=null)? new File(file.getParent()) : null;

    String errmsg = null;
    try {
      if (parent != null && ! parent.exists()) {
	if (! parent.mkdirs())
	  errmsg = "Cannot make parent directory for " + file.getPath();
      }
      if (atts.hasTrueAttribute("append")) {
	crc.util.Utilities.appendObjectTo(file.getPath(), agents);
      } else {
	crc.util.Utilities.writeObjectTo(file.getPath(), agents);
      }
      putText(out, aContext, file.getPath());
    } catch (Exception e) {
      System.out.println("exception: " + e.getMessage());
      e.printStackTrace();
      errmsg = "Write failed on " + file.getPath();
    }

    if (errmsg != null) {
      reportError(in, aContext, errmsg);
      putText(out, aContext, errmsg);
    }
  }
}
