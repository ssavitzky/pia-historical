////// Element.java:  Handler for <element>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.sgml.SGML;



/** Handler class for &lt;element&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;element tag=ident [empty] [not-inside="list of tags"]&gt;
 * <dt>Dscr:<dd>
 *	Define the syntax for an SGML element.  Optionally EMPTY.
 *	Optionally NOT-INSIDE a list of tags which it implicitly ends.
 *  </dl>
 */
public class Element extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<element tag=ident [empty] [not-inside=\"list of tags\"]>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Define the syntax for an SGML element.  Optionally EMPTY.\n" +
    "Optionally NOT-INSIDE a list of tags which it implicitly ends.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    ii.defineActor(new Actor(it, it.attrString("syntax")));
    ii.deleteIt();
  }

  /** Return an instance of the corresponding actor, for bootstrapping. */
  public static Actor bootstrap() {
    return new Actor("element", "element", "empty", "Element"); 
  }
}
