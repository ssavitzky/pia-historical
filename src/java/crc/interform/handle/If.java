////// If.java:  Handler for <if>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Tokens;


/** Handler class for &lt;if&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;if&gt;&lt;test&gt;condition&lt;/test&gt;&lt;then&gt;...&lt;/then&gt;&lt;else&gt;...&lt;/else&gt;&lt;/if&gt;
 * <dt>Dscr:<dd>
 *	If TEST non-null, expand THEN, else ELSE.
 *  </dl>
 */
public class If extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<if><test>condition</test><then>...</then><else>...</else></if>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "If TEST non-null, expand THEN, else ELSE.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    Tokens from = it.content();
    if (from == null) {
      ii.deleteIt(); 
      return;
    }
    Tokens list = new Tokens();
    Tokens thenDo = null;
    Tokens elseDo = null;

    // === should stop appending to test list after finding <then> or <else>
    for (int i = 0; i < from.nItems(); ++i) {
      SGML s = from.itemAt(i);
      if (! s.isText()) {
	String t = s.tag();
	if ("then".equals(t)) thenDo = s.content();
	else if ("else".equals(t)) elseDo = s.content();
	else if (! "!".equals(t) && ! "!--".equals(t)) list.append(s);
    } else {
	String ss = s.toString().trim();
	if (ss != null && ! "".equals(ss)) list.append(ss);
      }
    }

    if(list.nItems() > 0) {
      if (thenDo != null) ii.pushInto(thenDo);
    } else {
      if (elseDo != null) ii.pushInto(elseDo);
    }
    ii.deleteIt();
  }
}
