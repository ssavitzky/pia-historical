////// Trim.java:  Handler for <trim>
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
 *	<trim [all]>content</trim>
 * Dscr:
 *	Eliminate leading and trailing (optionally ALL) whitespace 
 *	from CONTENT.  Whitespace inside markup is not affected.
 */

/** Handler class for &lt;trim&gt tag */
public class Trim extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    if (it.hasAttr("all")) {
      ii.replaceIt(Util.removeSpaces(it));
    } else {
      ii.replaceIt(Util.trimSpaces(it));
    }
  }
}
