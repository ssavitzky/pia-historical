////// Trim.java:  Handler for <trim>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;


/** Handler class for &lt;trim&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#trim">Manual
 *	Entry</a> for syntax and description.
 */
public class Trim extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<trim [all]>content</trim>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Eliminate leading and trailing (optionally ALL) whitespace \n" +
    "from CONTENT.  Whitespace inside markup is not affected.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    if (it.hasAttr("all")) {
      ii.replaceIt(Util.removeSpaces(it));
    } else {
      ii.replaceIt(Util.trimSpaces(it));
    }
  }
}
