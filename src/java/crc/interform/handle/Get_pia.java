////// Get_pia.java:  Handler for <get.pia>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.sgml.SGML;


/** Handler class for &lt;get-pia&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;get.pia [name="name"]&gt;
 * <dt>Dscr:<dd>
 *	Get value of NAME, in the PIA context (i.e. as a pia property).
 *  </dl>
 */
public class Get_pia extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<get.pia [name=\"name\"]>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Get value of NAME, in the PIA context (i.e. as a pia property).\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = getName(ia, it, ii);
    if (name == null) return;
    Run env = Run.environment(ii);
    crc.pia.Pia pia = crc.pia.Pia.instance();
    if (pia == null) {
      ii.deleteIt();
    } else {
      ii.replaceIt(pia.properties().getProperty(name));
    }
  }
}
