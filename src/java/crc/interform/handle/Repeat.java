////// Repeat.java:  Handler for <repeat>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Tokens;


/** Handler class for &lt;repeat&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#repeat">Manual
 *	Entry</a> for syntax and description.
 */
public class Repeat extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<repeat list=\"...\" | start=start stop=stop [step=step] [entity=\"name\"]>...</repeat>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Repeat CONTENT with ENTITY (default &amp;li;) in LIST.\n" +
    "START and STOP specify a numeric list from START to STOP with STEP stepsize (default +/-1).\n" +
    "Return the repeated CONTENT \n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    Tokens list;
    if(!it.hasAttr("list") && it.hasAttr("start")){
       it.attr("list",Util.listItems(it.attr("start"),it.attr("stop"),it.attr("step")));
    }

    list = Util.listItems(it.attr("list"));
    String entity = Util.getString(it, "entity", "li");
    
    ii.pushForeach(it.content(), entity, list);
    //ii.hoistParseFlags();
    ii.deleteIt();
  }

  /** Legacy action.  Should never get here. */
  public boolean action(crc.dps.Context aContext, crc.dps.Output out,
			String tag, crc.dps.active.ActiveAttrList atts,
			crc.dom.NodeList content, String cstring) {
    return bogusLegacyAction(aContext, tag);
  }
}
