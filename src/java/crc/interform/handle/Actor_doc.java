////// Actor_doc.java:  Handler for <actor-doc>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.sgml.SGML;


/** Handler class for &lt;actor-doc&gt tag. 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;actor-doc name="name"&gt;
 * <dt>Dscr:<dd>
 *	get an actor's DOC attribute in documentation format.
 *  </dl>
 */
public class Actor_doc extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<actor-doc name=\"name\">\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "get an actor's DOC attribute in documentation format.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;
    
    Actor actor = ii.tagset().forName(name);
    ii.replaceIt((actor == null)? null : actor.doc());
  }
}


