////// Element.java:  Handler for <element>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;


/* Syntax:
 *	<element tag=ident [empty] [not-inside="list of tags"]>
 * Dscr:
 *	Define the syntax for an SGML element.  Optionally EMPTY.
 *	Optionally NOT-INSIDE a list of tags which it implicitly ends.
 */

/** Handler class for &lt;element&gt tag */
public class Element extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    Token t = it.toToken();
    ii.tagset().define(new Actor(t, t.attrString("syntax")));
    ii.deleteIt();
  }

  /** Return an instance of the corresponding actor, for bootstrapping. */
  public static Actor bootstrap() {
    return new Actor("element", "element", "empty", "Element"); 
  }
}
