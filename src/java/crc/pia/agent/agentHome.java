////// agentHome.java:  Handler for <agent-home>
//	$Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


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
