////// Set_trans.java:  Handler for <set.trans>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Token;
import crc.sgml.Tokens;
import crc.sgml.Text;

/* Syntax:
 *	<set.trans name="name" [copy] [feature]>...</set.trans>
 * Dscr:
 *	Set NAME to CONTENT in a transaction.  Optionally set a FEATURE.
 *	Optionally COPY content as result.
 */

/** Handler class for &lt;set.trans&gt tag */
public class Set_trans extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;
    SGML value = it.content().simplify();

    ii.unimplemented(ia);

    if (it.hasAttr("copy")) {
      ii.replaceIt(value);
    } else {
      ii.deleteIt();
    }
  }
}

/* ====================================================================

    } elsif ($it->attr('trans')) {
	local $trans = IF::Run::transaction();
        if ($it->attr('feature')) {
	    $trans->set_feature($name, $value) if defined $trans;
	} else {
	    $trans->attr($name, $value) if defined $trans;
	}

*/
