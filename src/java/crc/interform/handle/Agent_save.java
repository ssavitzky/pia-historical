////// Agent_save.java:  Handler for <agent-save>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import java.io.File;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.ds.List;

import crc.sgml.SGML;
import crc.sgml.Text;
import crc.sgml.Tokens;

import crc.pia.Agent;


/** Handler class for &lt;agent-save&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#agent-save">Manual Entry</a> 
 *	for syntax and description.
 */
public class Agent_save extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<agent-save [name=\"agent-name\" | list=\"names\"] file=\"name\"\n" +
    " [interform] [append] [base=\"path\"]>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Save state of the current or NAMEd agent or LIST in FILE, with optional\n" + 
    "BASE path.  FILE may be looked up as an INTERFORM.  \n" +
    "BASE directory is created if necessary.  Optionally APPEND. \n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    Tokens names = new Tokens();
    String name = null;
    String errmsg = null;

    if (it.hasAttr("list")) {
      names = Util.listItems(it.attr("list"));
    } else {
      name = Util.getString(it, "name", Run.getAgentName(ii));
      names.push(new Text(name));
    }

    List agents = new List();
    for (int i = 0; i < names.nItems(); ++i) {
      Agent agent = Run.getAgent(ii, names.itemAt(i).toString());
      if (agent != null) {
	agents.push(agent);
      }
    }

    if (agents.nItems() == 0) {
      ii.error(ia, "No agents specified or specified agents not found");
      ii.deleteIt();
      return;
    }

    String fn = Util.getFileName(it, ii, true);
    if (fn == null) {
      ii.error(ia, "Must have non-null file attribute.");
      ii.deleteIt();
      return;
    }
    File file = new File(fn);
    File parent = (file.getParent()!=null)? new File(file.getParent()) : null;

    try {
      if (parent != null && ! parent.exists()) {
	if (! parent.mkdirs()) errmsg = "Cannot make parent directory";
      }
      if (it.hasAttr("append")) {
	crc.util.Utilities.appendObjectTo(fn, agents);
      } else {
	crc.util.Utilities.writeObjectTo(fn, agents);
      }
    } catch (Exception e) {
      System.out.println("exception: " + e.getMessage());
      e.printStackTrace();
      errmsg = "Write failed on " + name;
    }

    if (errmsg != null) {
      ii.error(ia, errmsg);
      ii.replaceIt(errmsg);
      return;
    }

    ii.deleteIt();
  }
}
