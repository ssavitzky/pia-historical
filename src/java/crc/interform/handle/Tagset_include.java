////// Tagset_include.java:  Handler for <tagset-include>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;


/** Handler class for &lt;tagset-include&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;tagset-include name=tagset-name&gt;
 * <dt>Dscr:<dd>
 *	Include (merge) an InterForm tagset called NAME 
 *	into the current tagset.
 *  </dl>
 */
public class Tagset_include extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<tagset-include name=tagset-name>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Include (merge) an InterForm tagset called NAME \n" +
    "into the current tagset.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;

    ii.tagset().include(crc.interform.Tagset.tagset(name));
    ii.replaceIt("&lt;tagset-include name=" + name + "&gt;");
  }
}
