////// Actor_attrs.java:  Handler for <actor-attrs>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.sgml.SGML;
import crc.sgml.Token;

/* Syntax:
 *	<actor-attrs name="name">
 * Dscr:
 *	get an actor's attributes in a format suitable for documentation.
 */

/** Handler class for &lt;actor-dscr&gt tag. */
public class Actor_attrs extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;
    
    Actor actor = ii.tagset().forName(name);

    ii.replaceIt(Util.attrsResult(it, actor));
  }
}
