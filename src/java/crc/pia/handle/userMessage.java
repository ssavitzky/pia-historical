////// userMessage.java:  Handler for <user-message>
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

/** Handler class for &lt;user-message&gt tag 
 */
public class userMessage extends crc.dps.handle.GenericHandler {

  public void action(Input in, Context aContext, Output out,
		     ActiveAttrList atts, NodeList content) {
    aContext.message(0, content.toString(), 0, true);
  }
}
