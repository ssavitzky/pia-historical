////// Pad.java:  Handler for <pad>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Tokens;


/** Handler class for &lt;pad&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;pad width=N [align=[left|right|center]] [spaces]&gt;...&lt;/pad&gt;
 * <dt>Dscr:<dd>
 *	Pad CONTENT to a given WIDTH with given ALIGNment
 *	(left/center/right).  Optionally just generate the SPACES.  
 *	Ignores markup.
 *  </dl>
 */
public class Pad extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<pad width=N [align=[left|right|center]] [spaces]>...</pad>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Pad CONTENT to a given WIDTH with given ALIGNment\n" +
    "(left/center/right).  Optionally just generate the SPACES.  \n" +
    "Ignores markup.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String text = it.contentString();
    String align = Util.getString(it, "align", "left");
    align = align.toLowerCase();
    int width = Util.getInt(it, "width", 8);
    boolean spaces = it.hasAttr("spaces");

    int pad = width - text.length();
    String left="", right="";

    while (pad-- > 0) {
      if (align.equals("left")
	  || (align.equals("center") && (pad & 1)==1)) {
	left += " ";
      } else {
	right += " ";
      }
    }

    if (it.isText() || spaces) {
      if (spaces) text = "";
      ii.replaceIt(right + text + left);
    } else {
      ii.replaceIt(new Tokens().append(right).append(it).append(left));
    }
  }
}

