////// Actor_doc.java:  Handler for <actor-doc>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.sgml.SGML;


/** Handler class for &lt;actor-doc&gt tag. 
 *  <p> See <a href="../../InterForm/tag_man.html#actor-doc">Manual Entry</a> 
 *	for syntax and description.
 */
public class Actor_doc extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<actor-doc name=\"name\">\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "get an actor's DOC attribute in documentation format.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;
    
    Actor actor = ii.tagset().forName(name);
    ii.replaceIt((actor == null)? null : actor.doc());
  }

  /** Legacy action. */
  public boolean action(crc.dps.Context aContext, crc.dps.Output out,
			String tag, crc.dps.active.ActiveAttrList atts,
			crc.dom.NodeList content, String cstring) {
    return omittedLegacyAction(aContext, tag, atts);
  }
}


