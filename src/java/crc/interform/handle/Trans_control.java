////// Trans_control.java:  Handler for <trans-control>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.sgml.SGML;

/* Syntax:
 *	<trans-control>...</trans-control>
 * Dscr:
 *	Add CONTENT as a control to the current response transaction.
 */

/** Handler class for &lt;trans-control&gt tag */
public class Trans_control extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    Run env = Run.environment(ii);
    env.transaction.addControl(it.contentString());
    ii.deleteIt();
  }
}
