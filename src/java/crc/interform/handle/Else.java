////// Else.java:  Handler for <else>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Text;


/** Handler class for &lt;else&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;else&gt;content&lt;/else&gt;
 * <dt>Dscr:<dd>
 *	Quote content; pass whole tag.
 *  </dl>
 */
public class Else extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<else>content</else>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Quote content.  Pass whole tag.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
  }
}
