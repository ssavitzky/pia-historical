////// Get_trans.java:  Handler for <get.trans>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.pia.Transaction;

import crc.sgml.SGML;

/* Syntax:
 *	<get-trans [name="name"]>
 * Dscr:
 *	Get value of NAME, in the TRANSaction context.  Optionally get
 *	HEADERS (optionally from REQUEST) or a FEATURE.
 */

/** Handler class for &lt;get-trans&gt tag */
public class Get_trans extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;
    SGML result = null;
    Run env = Run.environment(ii);
    Transaction trans = env.transaction;

    if (it.hasAttr("request")) trans = trans.requestTran();
    if (it.hasAttr("feature")) {
      ii.replaceIt(Util.toSGML(trans.getFeature(name)));
    } else if (it.hasAttr("headers")) {
      ii.replaceIt(trans.header(name));
    } else {
      ii.replaceIt(env.transaction.attr(name));
    }
  }
}
