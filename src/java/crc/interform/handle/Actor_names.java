////// Actor_names.java:  Handler for <actor-names>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/* Syntax:
 *	<actor-names [tag=id]>
 * Dscr:
 *	Return a list of the current actor names.  Optionally as an element 
 *	with the given TAG.
 */

/** Handler class for &lt;actor-names&gt tag. */
public class Actor_names extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    if (it.hasAttr("tag")) {
      ii.replaceIt(new Token(it.attrString(tag),
			     ii.tagset().actorNames()));
    } else {
      ii.replaceIt(new Tokens(ii.tagset().actorNames().elements(), " "));
    }
    ii.unimplemented(ia);
  }
}
