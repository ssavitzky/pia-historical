////// Get.java:  Handler for <get>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;


/** Handler class for &lt;get&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;get [name="name"] 
 *	     [pia|agent|form|trans|env|element[tag=tag]|local|global
 *	     | [file="filename"|href="url"|[file|href] name="string" ] &gt;
 * <dt>Dscr:<dd>
 *	Get value of NAME, optionally in PIA, ENV, AGENT, FORM, 
 *	ELEMENT, TRANSaction, or LOCAL or GLOBAL entity context.
 *      Default is to start with the local entity table and move up the
 *      stack until name is found.  Returns "" if name does not exist in
 *      specified context.  Elements of complex data structures can be accessed
 *      using a dotted notation "foo.bar" returns the bar element of foo.
 *	If FILE or HREF specified, functions as &lt;read&gt;.
 *  </dl>
 */
public class Get extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<get [name=\"name\"] \n" +
    "[pia|agent|form|trans|env|element[tag=tag]|local|global\n" +
    "| [file=\"filename\"|href=\"url\"|[file|href] name=\"string\" ] >\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Get value of NAME, optionally in PIA, ENV, AGENT, FORM, \n" +
    "ELEMENT, TRANSaction, or LOCAL or GLOBAL entity context.\n" +
    "Default is to start with the local entity table and move up the\n" +
     "stack until name is found.  Returns \"\" if name does not exist in\n" +
     "specified context.  Elements of complex data structures can be accessed\n" +
     "using a dotted notation \"foo.bar\" returns the bar element of foo.\n" +
    "Default is the generic lookup that includes paths.\n" +
    "If FILE or HREF specified, functions as <read>.\n" +
"";
 
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
	result = ii.getvar(name);  // look only in local table
      } else if (it.hasAttr("global")) {
	result = ii.getGlobal(name); // look only in global table
      } else {
	result = ii.getEntity(name); // start in local table and move up
      }
      ii.replaceIt(result);
    }
  }
}
