////// Text.java:  Handler for <text>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;text&gt tag */
public class Text extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================

### <text>content</text>

define_actor('text', 
	     'dscr' => "eliminate markup from CONTENT.");

sub text_handle {
    my ($self, $it, $ii) = @_;

    my $text = $it->content_text;
    $ii->replace_it($text);
}

*/
