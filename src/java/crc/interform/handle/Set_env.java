////// Set_env.java:  Handler for <set.env>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.sgml.SGML;

/* Syntax:
 *	<set.env name="name" [copy]>...</set.env>
 * Dscr:
 *	set NAME to CONTENT in the environment (system properties). 
 *	Optionally COPY content as result.
 */

/** Handler class for &lt;set.env&gt tag */
public class Set_env extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;

    SGML value = it.content().simplify();

    // === The system properties are not the environment, but they are
    //	   as close as we get in Java.  You can probably mess yourself
    //	   amazingly by changing some of the system properties.
    System.getProperties().put(name, value.toString());

    if (it.hasAttr("copy")) {
      ii.replaceIt(value);
    } else {
      ii.deleteIt();
    }
  }
}

