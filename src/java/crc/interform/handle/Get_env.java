////// Get_env.java:  Handler for <get.env>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;
import crc.interform.Tokens;
import crc.interform.Text;
import crc.interform.Util;

/* Syntax:
 *	<get.env [name="name"]>
 * Dscr:
 *	Get value of NAME from the ENVironment.
 */

/** Handler class for &lt;get.env&gt tag */
public class Get_env extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
      if (name == null || "".equals(name)) {
	ii.error(ia, "name attribute required");
	return;
      }
    SGML result = null;
    ii.replaceIt(System.getProperty(name));
  }
}
