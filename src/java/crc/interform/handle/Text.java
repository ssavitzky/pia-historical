////// Text.java:  Handler for <text>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.sgml.SGML;


/** Handler class for &lt;text&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;text&gt;content&lt;/text&gt;
 * <dt>Dscr:<dd>
 *	Eliminate markup from CONTENT.
 *  </dl>
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
}


