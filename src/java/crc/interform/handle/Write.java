////// Write.java:  Handler for <write>
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
 *	<write [file="name" [interform] [append] | href="url" [post]] 
 *	       [base="path"] [trim] [line]
 *	       [copy [protect [markup]]] >content</write>
 * Dscr:
 *	Output CONTENT to FILE or HREF, with optional BASE path.  FILE
 *	may be looked up as an INTERFORM.  BASE directory is created
 *	if necessary.  Optionally APPEND or POST.  Optionally TRIM
 *	leading and trailing whitespace. Optionally end LINE.
 *	Optionally COPY content to InterForm.
 */

/** Handler class for &lt;write&gt tag.  Dispatches to write-file or
 *	write.href as needed. */
public class Write extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    // === href + resolve should dispatch to write.href.resolve ===
    // === file + interform should dispatch to write.file.interform
    if (it.hasAttr("href") && it.hasAttr("file")) 
      ii.error(ia, "href and file attributes both specified");
    else if (it.hasAttr("href")) dispatch("write.href", ia, it, ii);
    else if (it.hasAttr("file")) dispatch("write.file", ia, it, ii);
    else ii.error(ia, "must have file or href attribute");

    // === could do write.file in place.
   }
}
