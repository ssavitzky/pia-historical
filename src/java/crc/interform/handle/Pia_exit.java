////// Pia_exit.java:  Handler for <pia-exit>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;pia-exit&gt tag */
public class Pia_exit extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================
### <pia-exit>

define_actor('pia-exit', 'unsafe' => 1, _handle => \&pia_exit_handle,
	     'dscr' => "Exit from the pia, after printing CONTENT." );

sub pia_exit_handle {
    my ($self, $it, $ii) = @_;

    my $content = $it->content_string;

    ## === should really set a flag and let the resolver quit cleanly.
    die "$content\n";

    $ii->delete_it;
}
*/
