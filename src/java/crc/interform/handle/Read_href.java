////// Read_href.java:  Handler for <read.href>
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
import crc.interform.Run;

/* Syntax:
 *	<read href="url" [resolve] [base="path"] [process [tagset="name"]] >
 * Dscr:
 *	Input from HREF, with optional BASE path.  
 *	Optionally PROCESS with optional TAGSET.
 *	HREF can optionally RESOLVE in pia.  
 */

/** Handler class for &lt;read-href&gt tag */
public class Read_href extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;

    SGML result = null;
    Run env = Run.environment(ii);

    ii.unimplemented(ia); 
  }
}
