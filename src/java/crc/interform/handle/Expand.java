////// Expand.java:  Handler for <expand>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;


/** Handler class for &lt;expand&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;expand [skip | protect [markup]]&gt;content&lt;/expand&gt;
 * <dt>Dscr:<dd>
 *	Expand CONTENT, then either re-expand, SKIP, or PROTECT it.
 *	Optionally protect MARKUP as well.
 *  </dl>
 */
public class Expand extends Protect {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<expand [skip | protect [markup]]>content</expand>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Expand CONTENT, then either re-expand, SKIP, or PROTECT it.\n" +
    "Optionally protect MARKUP as well.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    if (it.hasAttr("skip")) {
      // Skipping.  It's already been expanded once.
      ii.deleteIt();
    } else if (it.hasAttr("protect")) {
      // The following is slightly sleazy, but it works.
      super.handle(ia, it, ii); 
    } else {
      ii.pushInto(it.content());
      ii.deleteIt();
    }
  }
}
