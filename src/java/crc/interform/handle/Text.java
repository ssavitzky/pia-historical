////// Text.java:  Handler for <text>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;

/* Syntax:
 *	<text>content</text>
 * Dscr:
 *	Eliminate markup from CONTENT.
 */

/** Handler class for &lt;text&gt tag */
public class Text extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    ii.replaceIt(it.contentText());
  }
}


