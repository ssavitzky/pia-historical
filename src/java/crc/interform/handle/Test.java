////// Test.java:  Handler for <test>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Text;


/** Handler class for &lt;test&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;test [iftrue="value"] [iffalse="value"] [not] [text|link]
 *	      [zero|positive|negative|null| match="pattern" [exact] [case] ]
 * <dt>Dscr:<dd>
 *	Test CONTENT; return null or IFFALSE if false, else '1' or
 *	IFTRUE.  <dt>Tests:<dd> default (non-whitespace), ZERO, POSITIVE,
 *	NEGATIVE, NULL, MATCH='pattern'.  <dt>Modifiers:<dd> NOT, CASE
 *	(sensitive), TEXT, LINK, EXACT (match).
 * </dl>
 */
public class Test extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<test [iftrue=\"value\"] [iffalse=\"value\"] [not] [text|link]\n" +
    "[zero|positive|negative|null| match=\"pattern\" [exact] [case] ]\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Test CONTENT; return null or IFFALSE if false, else '1' or\n" +
    "IFTRUE.  Tests: default (non-whitespace), ZERO, POSITIVE,\n" +
    "NEGATIVE, NULL, MATCH='pattern'.  Modifiers: NOT, CASE\n" +
    "(sensitive), TEXT, LINK, EXACT (match).\n" +
"";

  public void handle(Actor ia, SGML it, Interp ii) {
    boolean result = false;
    SGML test = Util.removeSpaces(it.content());

    if (it.hasAttr("link")) {
      ii.error(ia, "link attr unimplemented.");
    } else if (it.hasAttr("text")) {
      test = test.contentText();
    } 

    if (it.hasAttr("zero")) {
      result = Util.numValue(test) == 0;
    } else if (it.hasAttr("positive")) {
      result = Util.numValue(test) > 0;
    } else if (it.hasAttr("negative")) {
      result = Util.numValue(test) < 0;
    } else if (it.hasAttr("match")) {
      String match = it.attrString("match");
      if (match == null) match = "";
      boolean exact = it.hasAttr("exact");
      boolean csens = it.hasAttr("case");
      if (exact) result = csens? match.equals(test.toString())
		               : match.equalsIgnoreCase(test.toString());
      else {
	ii.error(ia, "regexp match unimplemented");
      }
    } else {
      result = ! test.isEmpty();
    } 

    if (result) {
      ii.replaceIt(it.hasAttr("iftrue")? it.attr("iftrue") : new Text("1"));
    } else {
      ii.replaceIt(it.hasAttr("iffalse")? it.attr("iffalse") : null);
    }
  }
}
