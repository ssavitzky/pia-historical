////// Write_href.java:  Handler for <write.href>
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
 *	<write.href href="url" [post] [base="path"] [trim] [line]
 *	       [copy [protect [markup]]] >content</output>
 * Dscr:
 *	Output CONTENT to HREF, with optional BASE path. 
 *	Optionally POST.  Optionally TRIM
 *	leading and trailing whitespace. Optionally end LINE.
 *	Optionally COPY content to InterForm.
 */

/** Handler class for &lt;write.href&gt tag. */
public class Write_href extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.unimplemented(ia);
  }
}
