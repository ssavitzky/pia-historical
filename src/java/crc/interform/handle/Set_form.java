////// Set_form.java:  Handler for <set.form>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.sgml.SGML;

/* Syntax:
 *	<set.form name="name" [copy]>...</set.form>
 * Dscr:
 *	set NAME to CONTENT in the form table. 
 *	Optionally COPY content as result.
 */

/** Handler class for &lt;set.form&gt tag */
public class Set_form extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;

    SGML value = it.content().simplify();

    ii.unimplemented(ia);
    //crc.pia.Pia.instance().properties().put(name, value.toString());
    // === almost certainly hae to run something to notify about prop. chg.

    if (it.hasAttr("copy")) {
      ii.replaceIt(value);
    } else {
      ii.deleteIt();
    }
  }
}

