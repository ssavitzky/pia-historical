////// Write_file.java:  Handler for <write.file>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;

import java.io.File;


/** Handler class for &lt;write.file&gt tag. 
 *  <p> See <a href="../../InterForm/tag_man.html#write.file">Manual
 *	Entry</a> for syntax and description.
 */
public class Write_file extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<write.file file=\"name\" [interform] [append]\n" +
    "[base=\"path\"] [trim] [line]\n" +
    "[copy [protect [markup]]] >content</write.file>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Output CONTENT to FILE, with optional BASE path.  FILE\n" +
    "may be looked up as an INTERFORM.  BASE directory is created\n" +
    "if necessary.  Optionally APPEND.  Optionally TRIM\n" +
    "leading and trailing whitespace. Optionally end LINE.\n" +
    "Optionally COPY content to InterForm.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getFileName(it, ii, true);
    if (name == null) {
      ii.error(ia, "Must have non-null name or file attribute.");
      ii.deleteIt();
      return;
    }

    String content = (it.hasAttr("text")) ? it.contentText().toString()
      					  : it.contentString();

    if (it.hasAttr("trim")) { content = content.trim(); }
    if (it.hasAttr("line") && ! content.endsWith(lineSep))
      content += lineSep;

    String errmsg = null;

    /* Make sure all directories in the path exist. */

    File file = new File(name);
    File parent = (file.getParent()!=null)? new File(file.getParent()) : null;

    try {
      if (parent != null && ! parent.exists()) {
	if (! parent.mkdirs()) errmsg = "Cannot make parent directory";
      }
      if (it.hasAttr("directory")) {
	file.mkdirs();
	if (! file.exists() || ! file.isDirectory()) {
	  errmsg = "Could not create directory " + name;
	}
      } else if (it.hasAttr("append")) {
	crc.util.Utilities.appendTo(name, content);
      } else {
	crc.util.Utilities.writeTo(name, content);
      }
    } catch (Exception e) {
      errmsg = "Write failed on " + name;
    }

    if (errmsg != null) {
      ii.error(ia, errmsg);
      ii.replaceIt(errmsg);
      return;
    }

    if (it.hasAttr("copy")) {
      // === [copy [protect [markup]]] unimplemented: replaceIt is protect.
      ii.replaceIt(it.content());
    } else {
      ii.deleteIt();
    }
  }

  private static String lineSep  = System.getProperty("line.separator");

}

