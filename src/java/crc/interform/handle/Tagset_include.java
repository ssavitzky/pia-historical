////// Tagset_include.java:  Handler for <tagset-include>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;

/* Syntax:
 *	<tagset-include name=tagset-name>
 * Dscr:
 *	Include (merge) an InterForm tagset called NAME 
 *	into the current tagset.
 */

/** Handler class for &lt;tagset-include&gt tag */
public class Tagset_include extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;

    ii.tagset().include(crc.interform.Tagset.tagset(name));
    ii.replaceIt("&lt;tagset-include name=" + name + "&gt;");
  }
}
