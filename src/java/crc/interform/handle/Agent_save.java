////// Agent_save.java:  Handler for <agent-save>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import java.io.File;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;

import crc.interform.Run;
import crc.pia.Agent;


/** Handler class for &lt;agent-save&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;agent-save [name="agent-name"] file="name" [interform] [append]
 *	       [base="path"]&gt;
 * <dt>Dscr:<dd>
 *	Save the current or NAMEd agent's state in FILE, with optional 
 *	BASE path.  FILE may be looked up as an INTERFORM.  
 *	BASE directory is created if necessary.  Optionally APPEND. 
 *  </dl>
 */
public class Agent_save extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<agent-save [name=\"agent-name\"] file=\"name\" [interform] [append]\n" +
    "[base=\"path\"]>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Save the current or NAMEd agent's state in FILE, with optional\n" + 
    "BASE path.  FILE may be looked up as an INTERFORM.  \n" +
    "BASE directory is created if necessary.  Optionally APPEND. \n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", Run.getAgentName(ii));
    String errmsg = null;

    Agent agent = Run.getAgent(ii, name);

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
	crc.util.Utilities.appendObjectTo(fn, agent);
      } else {
	crc.util.Utilities.writeObjectTo(fn, agent);
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
