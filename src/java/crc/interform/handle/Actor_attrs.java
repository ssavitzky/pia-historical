////// Actor_attrs.java:  Handler for <actor-attrs>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.sgml.SGML;
import crc.sgml.Token;


/** Handler class for &lt;actor-dscr&gt tag. 
 *  <p> See <a href="../../InterForm/tag_man.html#actor-dscr">Manual Entry</a> 
 *	for syntax and description.
 */
public class Actor_attrs extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<actor-attrs name=\"name\">\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "get an actor's attributes in a format suitable for documentation.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;
    
    Actor actor = ii.tagset().forName(name);

    ii.replaceIt(Util.attrsResult(it, actor));
  }
}
