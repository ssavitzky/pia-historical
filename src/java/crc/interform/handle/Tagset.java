////// Tagset.java:  Handler for <tagset>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Actor;
import crc.sgml.SGML;

/** Handler class for &lt;tagset&gt; 
 *  <p> See <a href="../../InterForm/tag_man.html#tagset">Manual
 *	Entry</a> for syntax and description.
 * Note: 
 *	There is special hackery in this file because the class
 *	crc.interform.Tagset exists, so we can't just import it.
 */
public class Tagset extends Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<tagset name=tagset-name base=tagset-name>\n" +
    "actor definitions </tagset>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Define an InterForm tagset called NAME.   Optionally include BASE.\n" +
    "\n" +
"";

  /** Initialize a new Actor object.  Add this as action handler. */
  public void initializeActor(Actor ia) {
    ia.setAction(this);
  }

  public void actOn(Actor ia, SGML it, Interp ii, byte inc, int quot) {
    if (inc > 0) {
      crc.interform.Tagset ts = new crc.interform.Tagset(it);
      ii.replaceIt(ts);
      ii.useTagset(ts);
    }
    ia.defaultAction(it, ii, inc, quot);
  }
  public void handle(crc.interform.Actor ia, SGML it, Interp ii) {
    ii.deleteIt();
  }
  /** Return an instance of the corresponding actor, for bootstrapping. */
  public static crc.interform.Actor bootstrap() {
    return new crc.interform.Actor("actor", "actor", "quoted", "Actor");
  }
}
