////// Write_href.java:  Handler for <write.href>
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
 *	<write.href href="url" [post] [base="path"] [trim] [line]
 *	       [copy [protect [markup]]] >content</write.href>
 * Dscr:
 *	Output CONTENT to HREF, with optional BASE path. 
 *	Optionally POST.  Optionally TRIM
 *	leading and trailing whitespace. Optionally end LINE.
 *	Optionally COPY content to InterForm.
 */

/** Handler class for &lt;write.href&gt tag. */
public class Write_href extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String url = it.attrString("href");
    if (url == null || "".equals(url)) url = it.attrString("name");
    if (url == null || "".equals(url)) {
      ii.error(ia, "must have non-null name or href attribute");
      return;
    }

    ii.unimplemented(ia);

    if (it.hasAttr("copy")) {
      ii.replaceIt(it.content());
    } else {
      ii.deleteIt();
    }
  }
}
