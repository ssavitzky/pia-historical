////// Actor_names.java:  Handler for <actor-names>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.sgml.SGML;
import crc.sgml.Element;
import crc.sgml.Tokens;


/** Handler class for &lt;actor-names&gt tag. 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;actor-names [tag=id]&gt;
 * <dt>Dscr:<dd>
 *	Return a list of the current actor names.  Optionally as an element 
 *	with the given TAG.
 *  </dl>
 */
public class Actor_names extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<actor-names [tag=id]>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Return a list of the current actor names.  Optionally as an element \n" +
    "with the given TAG.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {

    if (it.hasAttr("tag")) {
      ii.replaceIt(new Element(it.attrString("tag"),
			     ii.tagset().actorNames()));
    } else {
      // === toString should not be needed.
      ii.replaceIt(new Tokens(ii.tagset().actorNames().elements(), " ").toString());
    }
  }
}
