////// Expand.java:  Handler for <expand>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;expand&gt tag */
public class Expand extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================

define_actor('expand', 
	     'dscr' => "Expand CONTENT, then either re-expand or PROTECT it.
Optionally protect MARKUP as well.");

sub expand_handle {
    my ($self, $it, $ii) = @_;

    if ($it->attr('protect')) {
	protect_handle($self, $it, $ii);
    } else {
	$ii->push_into($it->content);
	$ii->delete_it;
    }
}
*/
