////// User_message.java:  Handler for <user-message>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;user-message&gt tag */
public class User_message extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================
### <user-message>string</user-message>

define_actor('user-message', 
	     'dscr' => "Display a message to the user." );

sub user_message_handle {
    my ($self, $it, $ii) = @_;

    my $content = $it->content_string;
    print "$content\n" unless $main::quiet;
    $ii->delete_it;
}

*/
