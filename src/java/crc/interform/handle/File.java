////// File.java:  Handler for <file>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.AttrTable;

/** Handler class for &lt;file&gt tag. 
 *  <p> See <a href="../../InterForm/tag_man.html#file">Manual Entry</a> 
 *      for syntax and description.
 */
public class File extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<file [name|src]=\"name\" [interform] [remove|copy|rename]\n" +
    "[dst=\"path\"] [subst=entities [beg=\"char\"][end=\"char\"]]\n" +
    "[base=\"path\"] >" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Operate on file with given NAME (or SRC path) and optional BASE path.\n" +
    "File may be looked up as an INTERFORM.  \n" +
    "Operation is REMOVE, COPY or RENAME (to DST path).\n" +
    "COPY may SUBSTitute entities with optional BEGin and END characters.\n" +
"";

  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getFileName(it, ii, it.hasAttr("remove"));
    if (name == null) {
      ii.error(ia, "Must have non-null name or file attribute.");
      ii.deleteIt();
      return;
    }
    
    String errmsg = null;
    String op = null;
    java.io.File file = new java.io.File(name);
    java.io.File dst = null;

    if (! file.exists()) {
      ii.error(ia, "File " + name + " does not exist");
      ii.replaceIt("File " + name + " does not exist");
      return;
    }

    try {
      if (it.hasAttr("remove")) {
	op = "remove";
	file.delete();
      } else if (it.hasAttr("rename")) {
	String newname = it.attrString("dst");
	if (! newname.startsWith(file.separator)) {
	  newname = file.getParent()+file.separator+newname;
	}
	dst = new java.io.File(newname);
	op = "rename to "+newname;
	file.renameTo(dst);
      } else if (it.hasAttr("copy")) {
	String newname = it.attrString("dst");
	if (! newname.startsWith(file.separator)) {
	  newname = file.getParent()+file.separator+newname;
	}
	dst = new java.io.File(newname);
	op = "copy to "+newname;
	if (it.hasAttr("subst")) {
	  AttrTable subst = new AttrTable(Util.getPairs(it.attr("subst"),
							ii, false));
	  String beg = it.attrString("beg");
	  String end = it.attrString("end");
	  if (beg == null) beg = "&";
	  if (end == null) end = ";";
	  crc.util.Utilities.substFile(file, dst, subst,
				       beg.charAt(0), end.charAt(0));
	} else {
	  crc.util.Utilities.copyFile(file, dst);
	}
      } else {
	errmsg = "No operation specified for " + name;
      }
    } catch (java.io.IOException e) {
      errmsg = name + ": cannot " + op + ": " + e.toString();
    } catch (Exception e) {
      errmsg = name + ": cannot " + op + ": " + e.toString();
      e.printStackTrace();
    }

    if (errmsg != null) {
      ii.error(ia, errmsg);
      ii.replaceIt(errmsg);
    } else {
      ii.deleteIt();
    }
  }

  private static String lineSep  = System.getProperty("line.separator");

}

