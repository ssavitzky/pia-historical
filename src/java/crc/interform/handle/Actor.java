////// Actor.java:  Handler for <actor>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;

/** Note: 
 *	There is special hackery in this file because there the class
 *	crc.interform.Tagset exists, so we can't just import it.  */

/* === should have skipped as well === */

/* Syntax:
 *	<actor [quoted|literal|empty] [parsed|passed]
 *	[name=ident] [tag=ident] [not-inside="tag list"]> content </actor>
 * Dscr:
 *	define an InterForm actor.
 */

/** Handler class for &lt;actor&gt tag. */
public class Actor extends crc.interform.Handler {
  public void handle(crc.interform.Actor ia, SGML it, Interp ii) {
    ii.tagset().define(new crc.interform.Actor(it.toToken()));
    ii.deleteIt();
  }
  /** Return an instance of the corresponding actor, for bootstrapping. */
  public static crc.interform.Actor bootstrap() {
    return new crc.interform.Actor("actor", "actor", "quoted", "actor");
  }
}
