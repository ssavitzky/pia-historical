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
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;repeat list="..." | start=start stop=stop [entity="name"]&gt;...&lt;/repeat&gt;
 * <dt>Dscr:<dd>
 *	Repeat CONTENT with ENTITY (default &amp;amp;li;) in LIST.
 *      START=1 and STOP=5 will automatically create a list from 1 to 5.
 *	Return the repeated CONTENT.
 *  </dl>
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
}
