////// Foreach.java:  Handler for <... foreach>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Token;
import crc.sgml.Tokens;
import crc.sgml.Text;

/* Syntax:
 *	<... foreach list="list" [entity=ident]>element</>
 * Dscr:
 *	Repeat ELEMENT for each ENTITY (default &amp;li;) in LIST of words.
 */

/** Handler class for &lt;... foreach list=...&gt */
public class Foreach extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    Tokens list = Util.listItems(it.attr("list"));
    String entity = Util.getString(it, "entity", "li");

    // re-push "it" with empty content and no foreach or list attr.
    Token t = new Token(it.tag());
    Token itt = it.toToken();
    for (int i = 0; i < itt.nAttrs(); ++i) {
      String name = itt.attrNameAt(i);
      if (name.equals("list") || name.equals("foreach")
	  || name.equals("entity")) continue;
      t.addAttr(name, itt.attrValueAt(i));
    }

    ii.pushInput(Token.endTagFor(it.tag()));
    ii.pushForeach(it.content(), entity, list);
    ii.hoistParseFlags();
    ii.stackToken(t);
    ii.replaceIt(t);

    /* === the old way involved faking a repeat.
    itt.tag("repeat");
    t.addItem(itt);
    ii.pushInto(t);
    ii.deleteIt();
    */
  }
}

