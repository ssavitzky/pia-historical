////// Tagset_exists.java:  Handler for <tagset-exists>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Tagset;

import crc.sgml.SGML;


/** Handler class for &lt;tagset-exists&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#tagset-exists">Manual
 *	Entry</a> for syntax and description.
 */
public class Tagset_exists extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<tagset-exists name=tagset-name>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Test whether a Tagset called NAME exists. \n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) {
      ii.deleteIt();
    }
    if (Tagset.tagsetExists(name)) {
      ii.replaceIt(name);
    } else {
      ii.deleteIt();
    }
  }
}
