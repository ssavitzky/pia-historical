////// Actor.java:  Handler for <actor>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.sgml.SGML;

import crc.dps.Syntax;

/** Handler class for &lt;actor&gt tag. 
 *  Note: 
 *	There is special hackery in this file because the class
 *	crc.interform.Actor exists, so we can't just import it.
 *  <p> See <a href="../../InterForm/tag_man.html#actor">Manual Entry</a> 
 *	for syntax and description.
 */
public class Actor extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<actor [quoted|literal|empty] [parsed|passed]\n" +
    "[name=ident] [tag=ident] [not-inside=\"tag list\"]> content </actor>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "define an InterForm actor.\n" +
"";
 
  public void handle(crc.interform.Actor ia, SGML it, Interp ii) {
    ii.defineActor(new crc.interform.Actor(it));
    ii.deleteIt();
  }
  /** Return an instance of the corresponding actor, for bootstrapping. */
  public static crc.interform.Actor bootstrap() {
    return new crc.interform.Actor("actor", "actor", "quoted", "actor");
  }

  /** Legacy action. */
  public boolean action(crc.dps.Context aContext, crc.dps.Output out,
			String tag, crc.dps.active.ActiveAttrList atts,
			crc.dom.NodeList content, String cstring) {
    crc.dps.TopContext top = aContext.getTopContext();
    if (top == null) return legacyError(aContext, tag, "No top context!");

    boolean quoted = atts.hasTrueAttribute("quoted");
    boolean empty = atts.hasTrueAttribute("empty");
    String tagname = atts.getAttributeString("tag");
    String notIn = (atts.hasTrueAttribute("not-inside")
			? atts.getAttributeString("not-inside")
			: null);
    String handle = (atts.hasTrueAttribute("handle")
		     ? atts.getAttributeString("handle")
		     : null);
    // === literal, passed, name not handled ===

    int syntax = empty? Syntax.EMPTY : quoted? Syntax.QUOTED : Syntax.NORMAL;

    top.getTagset().defTag(tagname, notIn, syntax, handle, content);
    
    return true;
  }
}
