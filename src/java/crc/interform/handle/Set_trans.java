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
import crc.sgml.AttrWrap;

import crc.interform.SecureAttrs;
import crc.pia.Transaction;
import crc.ds.Index;

/** Handler class for &lt;set.trans&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#set.trans">Manual
 *	Entry</a> for syntax and description.
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
    String name = getName(it);
    Index index = getIndex(it);
    if(index == null && name==null){
      ii.error(ia, " name or index attribute missing or null");
      return;
    }

    String key = (name == null) ? index.shift() : name;
    
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
	doComplexSet(key,index,new AttrWrap(new SecureAttrs(trans, ii)), value, ia,it,ii);
      }else {
	trans.put(key, value);
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
