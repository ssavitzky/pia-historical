////// Text.java:  Handler for <text>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.sgml.SGML;


/** Handler class for &lt;text&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#text">Manual
 *	Entry</a> for syntax and description.
 */
public class Text extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<text>content</text>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Eliminate markup from CONTENT.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    ii.replaceIt(it.contentText());
  }

  /** Legacy action. */
  public boolean action(crc.dps.Context aContext, crc.dps.Output out,
			String tag, crc.dps.active.ActiveAttrList atts,
			crc.dom.NodeList content, String cstring) {
    crc.dps.aux.Copy.copyNodes(content, new crc.dps.output.FilterText(out));
    return true;
  }
}


