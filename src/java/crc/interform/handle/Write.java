////// Write.java:  Handler for <write>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;


/** Handler class for &lt;write&gt tag.  Dispatches to write-file or
 *	write.href as needed. 
 * <dl> </dl>
 */
public class Write extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<write [file=\"name\" [interform] [append] | href=\"url\" [post]] \n" +
    "[base=\"path\"] [trim] [line]\n" +
    "[copy [protect [markup]]] >content</write>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Output CONTENT to FILE or HREF, with optional BASE path.  FILE\n" +
    "may be looked up as an INTERFORM.  BASE directory is created\n" +
    "if necessary.  Optionally APPEND or POST.  Optionally TRIM\n" +
    "leading and trailing whitespace. Optionally end LINE.\n" +
    "Optionally COPY content to InterForm.\n" +
"";
 
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
