////// agentInstall.java:  Handler for <agent-install>
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

    crc.pia.agent.Admin admin = null;
    try {
      admin = (crc.pia.agent.Admin) env.getAgent();
    } catch (Exception e) {
      reportError(in, aContext, "only works in the Admin agent");
      return;
    }
    env.getResolver().unRegisterAgent( name );
    aContext.message(0, "Agent "+name+" removed.", 0, true);    
    putText(out, aContext, name);
  }


  public agentRemove() { syntaxCode = EMPTY; }

}

