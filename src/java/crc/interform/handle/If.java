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

/* Syntax:
 *	<if><test>condition</test><then>...</then><else>...</else></if>
 * Dscr:
 *	If TEST non-null, expand THEN, else ELSE.
 */

/** Handler class for &lt;if&gt tag */
public class If extends crc.interform.Handler {
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
