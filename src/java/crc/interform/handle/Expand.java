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
 *	&lt;expand [protect [markup]]&gt;content&lt;/expand&gt;
 * <dt>Dscr:<dd>
 *	Expand CONTENT, then either re-expand or PROTECT it.
 *	Optionally protect MARKUP as well.
 *  </dl>
 */
public class Expand extends Protect {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<expand [protect [markup]]>content</expand>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Expand CONTENT, then either re-expand or PROTECT it.\n" +
    "Optionally protect MARKUP as well.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    if (it.hasAttr("protect")) {
      // The following is slightly sleazy, but it works.
      super.handle(ia, it, ii); 
    } else {
      ii.pushInto(it.content());
      ii.deleteIt();
    }
  }
}
