////// Expand.java:  Handler for <expand>
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
 *	<expand [protect [markup]]>content</expand>
 * Dscr:
 *	Expand CONTENT, then either re-expand or PROTECT it.
 *	Optionally protect MARKUP as well.
 */

/** Handler class for &lt;expand&gt tag */
public class Expand extends Protect {
  public void handle(Actor ia, SGML it, Interp ii) {
    if (it.hasAttr("protect")) {
      // The following is slightly sleazy, but it works.
      super.handle(ia, it, ii); 
    } else {
      ii.pushInto(it.content());
      ii.deleteIt();
    }
  }
}
