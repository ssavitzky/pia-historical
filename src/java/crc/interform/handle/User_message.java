////// User_message.java:  Handler for <user-message>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.sgml.SGML;

/* Syntax:
 *	<user-message>content</user-message>
 * Dscr:
 *	Display CONTENT as a message to the user.
 */

/** Handler class for &lt;user-message&gt tag */
public class User_message extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    ii.message(it.contentString());
    ii.deleteIt();
  }
}
