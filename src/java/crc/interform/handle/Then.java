////// Then.java:  Handler for <then>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Text;


/** Handler class for &lt;then&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;then&gt;content&lt;/then&gt;
 * <dt>Dscr:<dd>
 *	Quote content; pass whole tag.
 *  </dl>
 */
public class Then extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<then>content</then>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Quote content.  Pass whole tag.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
  }
}
