////// Get.java:  Handler for <get>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;

/* Syntax:
 *	<get [name="name"] 
 *	     [pia|agent|form|trans|env|element[tag=tag]|local|entity
 *	     | [file="filename"|href="url"|[file|href] name="string" ] >
 * Dscr:
 *	Get value of NAME, optionally in PIA, ENV, AGENT, FORM, 
 *	ELEMENT, TRANSaction, or LOCAL or global ENTITY context.
 *	Default is the generic lookup that includes paths.
 *	If FILE or HREF specified, functions as <read>.
 */

/** Handler class for &lt;get&gt tag */
public class Get extends crc.interform.Handler {
  /** Handle for &lt;get&gt.  The dispatching really should be in 
   *	actOn; we're faking it for now. === */
  public void handle(Actor ia, SGML it, Interp ii) {
    if (it.hasAttr("pia")) dispatch("get.pia", ia, it, ii);
    else if (it.hasAttr("agent")) dispatch("get.agent", ia, it, ii);
    else if (it.hasAttr("form")) dispatch("get.form", ia, it, ii);
    else if (it.hasAttr("trans")) dispatch("get.trans", ia, it, ii);
    else if (it.hasAttr("env")) dispatch("get.env", ia, it, ii);
    else if (it.hasAttr("href")) dispatch("read.href", ia, it, ii);
    else if (it.hasAttr("file")) dispatch("read.file", ia, it, ii);
    else {
      /* The following are all in the Basic tagset,
       *     so it's cheaper not to dispatch on them.
       */
      String name = Util.getString(it, "name", null);
      if (ii.missing(ia, "name", name)) return;

      SGML result = null;

      if (it.hasAttr("element")) {
	result = ii.getAttr(name, it.attr("tag").toString());
      } else if (it.hasAttr("local")) {
	result = ii.getvar(name);
      } else if (it.hasAttr("entity")) {
	result = ii.getGlobal(name);
      } else {
	result = ii.getEntity(name);
      }
      ii.replaceIt(result);
    }
  }
}
