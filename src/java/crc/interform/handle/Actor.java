////// Actor.java:  Handler for <actor>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.sgml.SGML;

/** Note: 
 *	There is special hackery in this file because there the class
 *	crc.interform.Tagset exists, so we can't just import it.  */

/* === should have skipped as well === */


/** Handler class for &lt;actor&gt tag. 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;actor [quoted|literal|empty] [parsed|passed]
 *	[name=ident] [tag=ident] [not-inside="tag list"]&gt; content &lt;/actor&gt;
 * <dt>Dscr:<dd>
 *	define an InterForm actor.
 *  </dl>
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
}
