////// Add_markup.java:  Handler for <add-markup>
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
 *	<add-markup>text</add-markup>
 * Dscr:
 *	Add markup to text CONTENT using common conventions.
 */

/** Handler class for &lt;add-markup&gt tag */
public class Add_markup extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    Tokens content = it.content();
    Tokens result = new Tokens();
    SGML item;
    while (content.nItems() > 0) {
      item = (SGML)content.shift();
      if (item.isText()) {
	result.append(Util.addMarkup(item.toString()));
      } else {
	result.append(item);
      }
    }
    ii.replaceIt(content);
  }
}
