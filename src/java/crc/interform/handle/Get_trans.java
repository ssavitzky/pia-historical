////// Get_trans.java:  Handler for <get.trans>
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
 *	<get-trans [name="name"]>
 * Dscr:
 *	Get value of NAME, in the TRANSaction context.  Optionally get
 *	HEADERS (optionally from REQUEST) or a FEATURE.
 */

/** Handler class for &lt;get-trans&gt tag */
public class Get_trans extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
      if (name == null || "".equals(name)) {
	ii.error(ia, "name attribute required");
	return;
      }
    SGML result = null;
    Run env = Run.environment(ii);
    crc.pia.Transaction trans = env.transaction;

    ii.unimplemented(ia); // === really not clear how to do get.trans!
  }
}

/* ============================================================

        if ($it->attr('feature')) {
	    $result = ($trans->get_feature($name)) if defined $trans;
	} elsif ($it->attr('headers')) {
	    if ($it->attr('request')) {
		$result = $trans->is_request? $trans->request->headers_as_string
		    : $trans->response_to->request->headers_as_string;
	    } else {
	        $result = $trans->message->headers_as_string;
	    }
	} else {
	    $result = ($trans->attr($name)) if defined $trans;
	}
*/
