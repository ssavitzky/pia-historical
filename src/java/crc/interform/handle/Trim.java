////// Trim.java:  Handler for <trim>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;trim&gt tag */
public class Trim extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================

### <trim>content</trim>

define_actor('trim', 
	     'dscr' => "eliminate leading and trailing whitespace
from CONTENT.");

sub trim_handle {
    my ($self, $it, $ii) = @_;

    my $text = remove_spaces($it);
    $ii->replace_it(IF::IT->new()->push($text));
}
*/
