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
import crc.interform.SecureAttrs;
import crc.pia.Transaction;
import crc.ds.Index;

/** Handler class for &lt;set.trans&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;set.trans name="name" [copy] [feature|header]&gt;...&lt;/set.trans&gt;
 * <dt>Dscr:<dd>
 *	Set NAME to CONTENT in a transaction.  Optionally set a FEATURE
 *	or HEADER.  Optionally COPY content as result.
 *  </dl>
 */
public class Set_trans extends Set {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<set.trans name=\"name\" [copy] [feature|header]>...</set.trans>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Set NAME to CONTENT in a transaction.  Optionally set a FEATURE\n" +
    "or HEADER.  Optionally COPY content as result.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    // name (with  dots) may be used for setting headers or features
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;

    Index index = getIndex(it);
    if(index == null){
      ii.error(ia, " name attribute missing or null");
      return;
    }
    
    SGML value = getValue(it);

    Run env = Run.environment(ii);
    Transaction trans = env.transaction;

    if (it.hasAttr("request")) trans = trans.requestTran();
    if (it.hasAttr("feature")) {
      trans.assert(name, value);
    } else if (it.hasAttr("headers")) {
      trans.setHeader(name, value.toString());
    } else {
      if(isComplex(index,it)){
	doComplexSet(index,new SecureAttrs(trans, ii), value, ia,it,ii);
      }else {
	trans.attr(name, value);
      }
    }

    doFinish(it,value,ii);

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
