////// Get_pia.java:  Handler for <get.pia>
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
import crc.interform.Run;

/* Syntax:
 *	<get.pia [name="name"]>
 * Dscr:
 *	Get value of NAME, in the PIA context (i.e. as a pia property).
 */

/** Handler class for &lt;get-pia&gt tag */
public class Get_pia extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = getName(ia, it, ii);
    if (name == null) return;
    Run env = Run.environment(ii);

    ii.replaceIt(crc.pia.Pia.instance().properties().getProperty(name));
  }
}
