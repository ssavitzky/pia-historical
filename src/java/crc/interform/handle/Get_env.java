////// Get_env.java:  Handler for <get.env>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;


/** Handler class for &lt;get.env&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;get.env [name="name"]&gt;
 * <dt>Dscr:<dd>
 *	Get value of NAME from the ENVironment.
 *  </dl>
 */
public class Get_env extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<get.env [name=\"name\"]>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Get value of NAME from the ENVironment.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;

    // === The system properties are not the environment, but they are
    //	   as close as we get in Java. ===

    ii.replaceIt(System.getProperty(name));
  }
}
