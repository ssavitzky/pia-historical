////// Read_file.java:  Handler for <read.file>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Element;
import crc.sgml.Tokens;
import crc.sgml.Text;

import java.util.Date;

import crc.gnu.regexp.RegExp;
import crc.gnu.regexp.MatchInfo;


/** Handler class for &lt;read-file&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;read.file file="name" [interform [agent="agentName"]] [quiet]
 *            [info|head|directory [links] [tag=tag] [all|match="regexp"]] 
 *	      [base="path"] [process [tagset="name"] skip] &gt;
 * <dt>Dscr:<dd>
 *	Input from FILE, with optional BASE path.  FILE may be looked
 *	up as an INTERFORM in current or other AGENT.  Optionally read
 *	only INFO or HEAD.  For DIRECTORY, read names or LINKS, and
 *	return TAG or ul.  DIRECTORY can read ALL names or those that
 *	MATCH; default is all but backups.  Optionally PROCESS with
 *	optional TAGSET and optionally SKIP results.  Optionally be
 *	QUIET if file does not exist.
 *  </dl>
 */
public class Read_file extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<read.file file=\"name\" [interform [agent=\"agentName\"]] [quiet]\n" +
    "[info|head|directory [links] [tag=tag] [all|match=\"regexp\"]] \n" +
    "[base=\"path\"] [process [tagset=\"name\"]] >\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Input from FILE, with optional BASE path.  FILE may be looked\n" +
    "up as an INTERFORM in current or other AGENT.  Optionally read\n" +
    "only INFO or HEAD.  For DIRECTORY, read names or LINKS, and\n" +
    "return TAG or ul.  DIRECTORY can read ALL names or those that\n" +
    "MATCH; default is all but backups.  Optionally PROCESS with\n" +
    "optional TAGSET and optionally SKIP results.  Optionally be  \n" +
    "QUIET if file does not exist. \n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    boolean info = it.hasAttr("info");
    boolean quiet= it.hasAttr("quiet");

    String name = Util.getFileName(it, ii, false);
    if (name == null) {
      // might be either non-existent InterForm, or missing attribute
      String file = Util.getString(it, "file", it.attrString("name"));
      if (file == null)
	ii.error(ia, "Must have non-null name or file attribute.");
      else if (!info && !quiet)
	ii.error(ia, "File '"+file+"' does not exist.");
      ii.deleteIt();
      return;
    }

    /* stat the file. */
    java.io.File file = new java.io.File(name);

    boolean exists = file.exists();
    boolean isdir  = file.isDirectory();
    boolean dir  = it.hasAttr("directory");

    SGML result = null;

    if (! file.exists()) {
      if (!info) ii.error(ia, "File '"+name+"' does not exist.");
      ii.deleteIt();
      return;
    }

    if (! info && ! file.canRead()) {
      ii.error(ia, "File '"+name+"' cannot be read.");
      ii.deleteIt();
      return;
    }

    if (dir && !isdir) {
      if (!info) ii.error(ia, "File '"+name+"' is not a directory.");
      ii.deleteIt();
      return;
    }

    if (info) {
      String what = it.attrString("info").toLowerCase();
      String content;
      boolean r = file.canRead();
      boolean w = file.canWrite();
      boolean x = false /* === file.canExecute() === */;


      if (what.startsWith("d")) content = isdir? "d" : "";
      else if (what.startsWith("r")) content = r? "r" : "";
      else if (what.startsWith("w")) content = w? "w" : "";
      else if (what.startsWith("x")) content = x? "x" : "";
      else if (what.startsWith("p")) content = file.getAbsolutePath();
      else if (what.startsWith("m")) {
	content = new Date(file.lastModified()).toLocaleString();
      } else if (what.startsWith("s")) {
	content = String.valueOf(file.length());
      } else {
	content = isdir? "d" : "-";
	content += r? "r" : "-";
	content += w? "w" : "-";
	content += x? "x" : "-";
	content += " " + String.valueOf(file.length());
	content += "	" + file;
      }
      result = new Text(content);
    } else if (isdir) {
      String [] list = file.list();
      Tokens names = new Tokens();
      boolean all = it.hasAttr("all");
      String match = it.attrString("match");
      RegExp re = null;
      MatchInfo mi = null;

      if (match != null && ! match.equals("")) {
	try {re = new RegExp(match);} catch (Exception e) {};
      }

      for (int i = 0; i < list.length; ++i) {
	String fn = list[i];
	if (!all && crc.pia.FileAccess.ignoreFile(fn, name)) continue;
	if (re != null) {
	  mi = re.match(fn);
	  if (mi != null) continue;
	}
	names.push(new Text(fn));
      }

      String tag  = it.attrString("tag");
      if (tag != null) tag = tag.toLowerCase();
      String itag = (tag != null && tag.equals("dl"))? "dt" : "li";

      if (it.hasAttr("links")) {
	Element t = new Element(tag);
	for (int i = 0; i < names.nItems(); ++i) {
	  Element link = new Element("a");
	  link.addItem(names.itemAt(i));
	  t.attr("href", "file:" + file+names.itemAt(i).toString());
	}
	result = t;
      } else if (tag != null) {
	Element t = new Element(tag);
	result = t;
	for (int i = 0; i < names.nItems(); ++i) 
	  t.addItem(new Element(itag).addItem(names.itemAt(i)));
      } else {
	result = Text.join(" ", names);
      }
      
    } else if (it.hasAttr("process")) {
      String tsname = it.attrString("tagset");
      if (tsname != null) 
	ii.useTagset(tsname);
      if (it.hasAttr("skip")) ii.setSkipping();
      try {
	java.io.FileInputStream in = new java.io.FileInputStream(name);
	ii.pushInput(new crc.interform.Parser(in, null));
      } catch (Exception e) {
	ii.error(ia, "Cannot open input stream on '"+name+"'");
      }

    } else {
      try {
	byte [] bytes = crc.util.Utilities.readFrom(name);
	result = new Text(new String(bytes, 0));
      } catch (Exception e) {
	result = null;
      }    
    }

    ii.replaceIt(result);
  }
}

