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


/** Handler class for &lt;get-trans&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;get-trans [name="name"]&gt;
 * <dt>Dscr:<dd>
 *	Get value of NAME, in the TRANSaction context.  Optionally get
 *	HEADERS (optionally from REQUEST) or a FEATURE.
 *  </dl>
 */
public class Get_trans extends Get {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<get-trans [name=\"name\"]>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Get value of NAME, in the TRANSaction context.  Optionally get\n" +
    "HEADERS (optionally from REQUEST) or a FEATURE.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);


    Run env = Run.environment(ii);
    Transaction trans = env.transaction;
    if (trans == null) {
      ii.deleteIt();
      return;
    }
    if (it.hasAttr("request")) trans = trans.requestTran();
    if (trans == null) {
      ii.deleteIt();
      return;
    }
    if (it.hasAttr("feature")) {
      ii.replaceIt(Util.toSGML(trans.getFeature(name)));
    } else if (it.hasAttr("headers")) {
      if (name == null) 
	ii.replaceIt(trans.headersAsString());
      else
	ii.replaceIt(trans.header(name));
    } else {
      SGML result = env.transaction.attr(name);
      result = processResult(result, it);
      ii.replaceIt(result);
    }
  }
}
