////// Get_form.java:  Handler for <get.form>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;
import crc.interform.Tokens;
import crc.interform.Text;
import crc.interform.Util;
import crc.interform.Run;

import crc.ds.Table;

/* Syntax:
 *	<get.form [name="name"]>
 * Dscr:
 *	Get value of NAME, in the FORM context.  
 *	With no name, returns entire form. 
 */

/** Handler class for &lt;get-form&gt tag */
public class Get_form extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);

    Run env = Run.environment(ii);
    Table form = null; ii.unimplemented(ia); // === env.transaction.form();
    if (form == null) {
      ii.deleteIt();
      return;
    } else if (name == null || "".equals(name)) {
      // return the whole form
      ii.replaceIt(new Token("dl", form)); 
    }
    ii.replaceIt(Util.toSGML(form.at(name)));
  }
}
