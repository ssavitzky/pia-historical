////// Repeat.java:  Handler for <repeat>
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
 *	<repeat list="..." [entity="name"]>...</repeat>
 * Dscr:
 *	Repeat CONTENT with ENTITY (default &amp;li; in LIST of words.
 */

/** Handler class for &lt;repeat&gt tag */
public class Repeat extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    Tokens list = Util.listItems(it.attr("list"));
    String entity = Util.getString(it, "entity", "li");

    ii.pushForeach(it.content(), entity, list);
    ii.hoistParseFlags();
    ii.deleteIt();
  }
}
