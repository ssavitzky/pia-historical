////// Protect.java:  Handler for <protect>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Text;


/** Handler class for &lt;protect&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#protect">Manual
 *	Entry</a> for syntax and description.
 */
public class Protect extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<protect [markup]>content</protect>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Protect CONTENT from expansion.  Optionally protect\n" +
    "MARKUP by converting special characters to entities.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    if (it.hasAttr("markup")) {
      ii.replaceIt(new Text(Util.protectMarkup(it.contentString())));
    } else {
      ii.replaceIt(it.content());
    }
  }

  /** Legacy action. */
  public boolean action(crc.dps.Context aContext, crc.dps.Output out,
			String tag, crc.dps.active.ActiveAttrList atts,
			crc.dom.NodeList content, String cstring) {
    if (atts.hasTrueAttribute("markup")) {
      putText(out, Util.protectMarkup(cstring));
    } else {
      putList(out, content);
    }
    return true;
  }
}
