////// Read.java:  Handler for <read>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;


/** Handler class for &lt;read&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#read">Manual
 *	Entry</a> for syntax and description.
 */
public class Read extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<read [ file=\"name\" [interform [agent=\"agentName\"]] \n" +
    "[info|head|directory [links] [tag=tag] [all|match=\"regexp\"]] \n" +
    "| href=\"url\" [into=filename] ] \n" +
    "[base=\"path\"] [process [tagset=\"name\"]] >\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Input from FILE or HREF, with optional BASE path.  FILE may be\n" +
    "looked up as an INTERFORM in current or other AGENT.\n" +
    "Optionally read only INFO or HEAD.  For DIRECTORY, read names\n" +
    "or LINKS, and return TAG or ul.  DIRECTORY can read ALL names\n" +
    "or those that MATCH; default is all but backups.  Optionally\n" +
    "PROCESS with optional TAGSET. HREF content can be placed into FILENAME \n" +
    " otherwise non text data will return an IMG tag.\n" + 
"";
  public void handle(Actor ia, SGML it, Interp ii) {
    // === href + resolve should dispatch to read.href.resolve ===
    // === file + interform should dispatch to read.file.interform
    if (it.hasAttr("href") && it.hasAttr("file")) 
      ii.error(ia, "href and file attributes both specified");
    else if (it.hasAttr("href")) dispatch("read.href", ia, it, ii);
    else if (it.hasAttr("file")) dispatch("read.file", ia, it, ii);
    else ii.error(ia, "must have file or href attribute");

    // === could do read.file in place.
  }

  /** Legacy action: default is to flag as unimplemented. */
  public boolean action(crc.dps.Context aContext, crc.dps.Output out,
			String tag, crc.dps.active.ActiveAttrList atts,
			crc.dom.NodeList content, String cstring) {
    return legacyError(aContext, tag, "Shouldn't get here -- dispatched.");
  }

  /** getActionForNode: override to perform parse-time dispatching */
  public crc.dps.Action getActionForNode(crc.dps.active.ActiveNode n,
					 crc.dps.handle.LegacyHandler h) {
    crc.dps.active.ActiveElement e = n.asElement();
    if (h.dispatch(e, "href")) return h.wrap(new Read_href());
    if (h.dispatch(e, "file")) return h.wrap(new Read_file());
    return h;
  }

}

