////// agentRestore.java:  Handler for <agent-restore>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.pia.agent;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.process.ActiveDoc;

import crc.dom.NodeList;
import crc.pia.Agent;
import crc.ds.List;

import java.io.File;
import java.io.FileInputStream;

/** Handler class for &lt;agent-restore&gt tag 
 * <p>See <a href="../../InterForm/tag_man.html#agent-restore">Manual Entry</a> 
 *	for syntax and description.
 */
public class agentRestore extends crc.dps.handle.GenericHandler {

  public void action(Input in, Context aContext, Output out,
		     ActiveAttrList atts, NodeList content) {
    ActiveDoc env = ActiveDoc.getInterFormContext(aContext);
    if (env == null) {
      reportError(in, aContext, "PIA not running.");
      return;
    }

    String fn = atts.getAttributeString("file");
    if (fn == null) {
      reportError(in, aContext, "File attribute missing.");
      return;
    }
    File file = env.locateSystemResource(fn, true);

    boolean exists = file.exists();
    boolean isdir  = file.isDirectory();

    String result = "";

    if (! file.exists()) {
      reportError(in, aContext, "File '"+name+"' does not exist.");
      return;
    }

    if (! file.canRead()) {
      reportError(in, aContext, "File '"+name+"' cannot be read.");
      return;
    }

    if (isdir) {
      reportError(in, aContext, "File '"+name+"' is a directory.");
      return;
    }

    try {
      FileInputStream stm = new FileInputStream(file);
      List list = crc.util.Utilities.readObjectsFrom(stm);
      for (int i = 0; i < list.nItems(); ++i) {
	if (list.at(i) instanceof Agent) {
	  putText(out, aContext, ((Agent)list.at(i)).name());
	}
      }
    } catch (Exception e) {
      // exception reported in readObjectsFrom
    }    
  }
}

