////// Add_markup.java:  Handler for <add-markup>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Tokens;
import crc.sgml.Text;


/** Handler class for &lt;add-markup&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#add-markup">Manual Entry</a> 
 *	for syntax and description.
 */
public class Add_markup extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<add-markup>text</add-markup>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Add markup to text CONTENT using common conventions.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    Tokens content = it.content();
    Tokens result = new Tokens();
    SGML item;
    while (content != null && content.nItems() > 0) {
      item = (SGML)content.shift();
      if (item.isText()) {
	result.append(Util.addMarkup(item.toString()));
      } else {
	result.append(item);
      }
    }
    ii.replaceIt(result);
  }
}
