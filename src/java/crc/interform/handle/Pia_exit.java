////// Pia_exit.java:  Handler for <pia-exit>
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
 *	<pia-exit [status=N]>message</pia-exit>
 * Dscr:
 *	Exit from the pia, after printing CONTENT.  Optional STATUS 
 *	(default 1).
 */

/** Handler class for &lt;pia-exit&gt tag */
public class Pia_exit extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    //=== should set a flag to allow the resolver to quit cleanly

    ii.message(it.contentString());
    System.exit(Util.getInt(it, "status", 1));
  }
}

