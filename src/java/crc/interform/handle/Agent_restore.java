////// Agent_restore.java:  Handler for <agent-restore>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import java.io.File;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.pia.Agent;

import crc.sgml.SGML;
import crc.sgml.Element;
import crc.sgml.Tokens;
import crc.sgml.Text;

import crc.ds.List;

/** Handler class for &lt;agent-restore&gt tag 
 * <p>See <a href="../../InterForm/tag_man.html#agent-restore">Manual Entry</a> 
 *	for syntax and description.
 */
public class Agent_restore extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<agent.restore file=\"name\" [interform [agent=\"agentName\"]] [quiet]\n" +
    "[base=\"path\"] >\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Input from FILE, with optional BASE path.  FILE may be looked\n" +
    "up as an INTERFORM in current or other AGENT.  Optionally be  \n" +
    "QUIET if file does not exist. \n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    boolean quiet= it.hasAttr("quiet");

    String name = Util.getFileName(it, ii, false);
    if (name == null) {
      // might be either non-existent InterForm, or missing attribute
      String file = Util.getString(it, "file", it.attrString("name"));
      if (file == null)
	ii.error(ia, "Must have non-null file attribute.");
      else if (!quiet)
	ii.error(ia, "File '"+file+"' does not exist.");
      ii.deleteIt();
      return;
    }

    /* stat the file. */
    java.io.File file = new java.io.File(name);

    boolean exists = file.exists();
    boolean isdir  = file.isDirectory();

    String result = "";

    if (! file.exists()) {
      if (!quiet) ii.error(ia, "File '"+name+"' does not exist.");
      ii.deleteIt();
      return;
    }

    if (! file.canRead()) {
      ii.error(ia, "File '"+name+"' cannot be read.");
      ii.deleteIt();
      return;
    }

    if (isdir) {
      ii.error(ia, "File '"+name+"' is a directory.");
      ii.deleteIt();
      return;
    }

    try {
      List list = crc.util.Utilities.readObjectsFrom(name);
      for (int i = 0; i < list.nItems(); ++i) {
	if (list.at(i) instanceof Agent) {
	  result += ((Agent)list.at(i)).name() + " ";
	}
      }
    } catch (Exception e) {
      result = null;
    }    

    ii.replaceIt(result);
  }

  /** Legacy action. */
  public boolean action(crc.dps.Context aContext, crc.dps.Output out,
			String tag, crc.dps.active.ActiveAttrList atts,
			crc.dom.NodeList content, String cstring) {
    crc.dps.process.ActiveDoc env = getInterFormContext(aContext);
    if (env == null) return legacyError(aContext, tag, "PIA not running.");

    String name = env.getAgentName(atts.getAttributeString("file"));
    if (name == null) {
      return legacyError(aContext, tag, "File attribute missing.");
    }
    java.io.File file = env.locateSystemResource(name, false);

    boolean exists = file.exists();
    boolean isdir  = file.isDirectory();

    String result = "";

    if (! file.exists()) {
      return legacyError(aContext, tag, "File '"+name+"' does not exist.");
    }

    if (! file.canRead()) {
      return legacyError(aContext, tag, "File '"+name+"' cannot be read.");
    }

    if (isdir) {
      return legacyError(aContext, tag, "File '"+name+"' is a directory.");
    }

    try {
      java.io.FileInputStream stm = new java.io.FileInputStream(file);
      List list = crc.util.Utilities.readObjectsFrom(stm);
      for (int i = 0; i < list.nItems(); ++i) {
	if (list.at(i) instanceof Agent) {
	  putText(out, ((Agent)list.at(i)).name());
	}
      }
    } catch (Exception e) {
      // exception reported in readObjectsFrom
    }    

    return true;
  }
}

