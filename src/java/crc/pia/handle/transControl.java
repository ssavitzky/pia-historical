////// transControl.java:  Handler for <trans-control>
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

/** Handler class for &lt;trans-control&gt tag 
 */
public class transControl extends crc.dps.handle.GenericHandler {

  public void action(Input in, Context aContext, Output out,
		     ActiveAttrList atts, NodeList content) {
    ActiveDoc env = ActiveDoc.getInterFormContext(aContext);
    if (env == null) {
      reportError(in, aContext, "PIA not running.");
      return;
    }

    env.getTransaction().addControl(content.toString());
  }
}
