////// agentRunning.java:  Handler for <agent-home>
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
