////// Protect.java:  Handler for <protect>
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
 *	<protect [markup]>content</protect>
 * Dscr:
 *	Protect CONTENT from expansion.  Optionally protect
 *	MARKUP by converting special characters to entities.
 */

/* Syntax:
 *	<protect-result [markup]>content</protect-result>
 * Dscr:
 *	Expand CONTENT and protect the result from further expansion.
 *	Optionally protect MARKUP by converting special characters to
 *	entities.
 */


/** Handler class for &lt;protect&gt tag */
public class Protect extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    if (it.hasAttr("markup")) {
      ii.replaceIt(new Text(Util.protectMarkup(it.contentString())));
    } else {
      ii.replaceIt(it.content());
    }
  }
}
