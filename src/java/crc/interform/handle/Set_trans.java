////// Set_trans.java:  Handler for <set.trans>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.sgml.SGML;

import crc.pia.Transaction;

/* Syntax:
 *	<set.trans name="name" [copy] [feature|header]>...</set.trans>
 * Dscr:
 *	Set NAME to CONTENT in a transaction.  Optionally set a FEATURE
 *	or HEADER.  Optionally COPY content as result.
 */

/** Handler class for &lt;set.trans&gt tag */
public class Set_trans extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;
    SGML value = it.content().simplify();

    Run env = Run.environment(ii);
    Transaction trans = env.transaction;

    if (it.hasAttr("request")) trans = trans.requestTran();
    if (it.hasAttr("feature")) {
      trans.assert(name, value);
    } else if (it.hasAttr("headers")) {
      trans.setHeader(name, value.toString());
    } else {
      trans.attr(name, value);
    }

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
