////// Read_href.java:  Handler for <read.href>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;
import crc.interform.Tokens;
import crc.interform.Text;
import crc.interform.Util;
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
      if (name == null || "".equals(name)) {
	ii.error(ia, "name attribute required");
	return;
      }
    SGML result = null;
    Run env = Run.environment(ii);

    ii.unimplemented(ia); // === really not clear how to do read.href!
  }
}
