////// Tagset_load.java:  Handler for <tagset-load>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Tagset;

import crc.sgml.SGML;


/** Handler class for &lt;tagset-load&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#tagset-load">Manual
 *	Entry</a> for syntax and description.
 */
public class Tagset_load extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<tagset-load name=tagset-name [file=file-name]>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Load (i.e. use) a Tagset called NAME, optionally from a given FILE. \n" +
    "If the tagset is already loaded, its TIMESTAMP attribute is compared \n" +
    "with the file's last-modified date. \n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) {
      ii.deleteIt();
    }
    Tagset ts = Tagset.require(name);
    long timestamp = Util.getLong(ts, "timestamp", 0);
    if (it.hasAttr("file")) {
      String file = Util.getFileName(it, ii, false);
      timestamp = Util.loadFile(ii, file, timestamp, null);
      ts = Tagset.require(name);
      if (ts != null) {
	ts.attr("timestamp", Long.toString(timestamp));
      }
    }
    ii.useTagset(ts);
    ii.deleteIt();
  }
}
