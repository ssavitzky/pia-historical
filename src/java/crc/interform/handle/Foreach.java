////// Foreach.java:  Handler for <foreach>
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


/** Handler class for &lt;foreach list=...&gt 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;foreach list="list" [entity=ident]&gt;element&lt;/&gt;
 * <dt>Dscr:<dd>
 *	Repeat CONTENT for each ENTITY (default &amp;amp;li;) in LIST.
 *	Return the repeated CONTENT.
 *  </dl>
 */
public class Foreach extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<foreach list=\"list\" [entity=ident]>element</>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Repeat CONTENT for each ENTITY (default &amp;li;) in LIST.\n" +
    "Return the repeated CONTENT.  \n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    Tokens list = Util.listItems(it.attr("list"));
    String entity = Util.getString(it, "entity", "li");

    ii.pushForeach(it.content(), entity, list);
    //ii.hoistParseFlags();
    ii.deleteIt();
  }
}

