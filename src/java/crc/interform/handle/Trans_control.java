////// Trans_control.java:  Handler for <trans-control>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;trans-control&gt tag */
public class Trans_control extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================

### Transaction

define_actor('trans-control', 
	     'dscr' => "Add a control to the current response." );

sub trans_control_handle {
    my ($self, $it, $ii) = @_;

    my $text = $it->content_string;
    my $response = IF::Run::request();

    $response -> add_control($text);
    print "add_control($text)\n" if $main::debugging;

    $ii->delete_it;
}
*/
