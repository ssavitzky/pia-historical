////// Set.java:  Handler for <set>
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
 *	<set name="name" [copy]
 *	     [ pia | agent [hook] | trans [feature] | env 
 *  	     | [element [tag=ident] | entity [local] ]>...</set>
 * Dscr:
 *	set NAME to CONTENT, optionally in PIA, AGENT, TRANSaction, 
 *	ENVironment, ELEMENT, or ENTITY context.  ENTITY may define
 *	a LOCAL binding.  ELEMENT may have a TAG.  TRANSaction item
 *	may be FEATURE.  AGENT may be a HOOK (parsed InterForm) or string. 
 *	Optionally COPY content as result.
 */

/** Handler class for &lt;set&gt tag */
public class Set extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    if (it.hasAttr("pia")) dispatch("set.pia", ia, it, ii);
    else if (it.hasAttr("agent")) dispatch("set.agent", ia, it, ii);
    //else if (it.hasAttr("form")) dispatch("set.form", ia, it, ii);
    else if (it.hasAttr("trans")) dispatch("set.trans", ia, it, ii);
    //else if (it.hasAttr("env")) dispatch("set.env", ia, it, ii);
    else {
      /* The following are all in the Basic tagset,
       *     so it's cheaper not to dispatch on them.
       */
      String name = Util.getString(it, "name", null);
      if (ii.missing(ia, "name", name)) return;

      SGML value = it.content().simplify();
      if (it.hasAttr("element")) {
	ii.setAttr(name, value, it.attr("tag").toString());
      } else if (it.hasAttr("local")) {
	ii.defvar(name, value);
      } else if (it.hasAttr("entity")) {
	ii.setGlobal(name, value);
      } else {
	ii.setvar(name, value);
      }

      if (it.hasAttr("copy")) {
	ii.replaceIt(value);
      } else {
	ii.deleteIt();
      }
    }
  }
}

