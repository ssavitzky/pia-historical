////// Get_env.java:  Handler for <get.env>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Token;
import crc.sgml.Tokens;
import crc.sgml.Text;

/* Syntax:
 *	<get.env [name="name"]>
 * Dscr:
 *	Get value of NAME from the ENVironment.
 */

/** Handler class for &lt;get.env&gt tag */
public class Get_env extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;

    SGML result = null;

    // === The system properties are not the environment, but they are
    //	   as close as we get in Java. ===

    ii.replaceIt(System.getProperty(name));
  }
}
