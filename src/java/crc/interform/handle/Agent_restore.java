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
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;agent.restore file="name" [interform [agent="agentName"]] [quiet]
 *	      [base="path"] &gt;
 * <dt>Dscr:<dd>
 *	Input from FILE, with optional BASE path.  FILE may be looked
 *	up as an INTERFORM in current or other AGENT.  Optionally be
 *	QUIET if file does not exist.
 *  </dl>
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
}

