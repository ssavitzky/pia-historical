////// Tagset.java:  Handler for <tagset>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.sgml.SGML;

/** Handler class for &lt;tagset&gt; 
 *  <p> See <a href="../../InterForm/tag_man.html#tagset">Manual
 *	Entry</a> for syntax and description.
 * Note: 
 *	There is special hackery in this file because the class
 *	crc.interform.Tagset exists, so we can't just import it.
 */
public class Tagset extends crc.interform.Handler {
  public void handle(crc.interform.Actor ia, SGML it, Interp ii) {
// Does this do anything? --GJW 7/3/97
    ii.tagset().define(new crc.interform.Actor(it));
    ii.deleteIt();
  }
  /** Return an instance of the corresponding actor, for bootstrapping. */
  public static crc.interform.Actor bootstrap() {
    return new crc.interform.Actor("actor", "actor", "quoted", "Actor");
  }
}
