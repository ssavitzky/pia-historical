////// Pad.java:  Handler for <pad>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;pad&gt tag */
public class Pad extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================

### <pad width=N align=[left|right|center] [spaces]>string</pad>
###	If the "spaces" attribute is present, only the spaces are 
###	returned.  This lets you pad the contents of a link (for
###	example) without having to put the padding inside the link
###	where it will get underlined and look ugly.

define_actor('pad', 
	     'dscr' => "Pad CONTENT to a given WIDTH with given ALIGNment
(left/center/right).  Optionally just generate the SPACES.  Ignores markup.");

sub pad_handle {
    my ($self, $it, $ii) = @_;

    my $text   = $it->content_text;
    
    my $align  = (lc $it->attr('align')) || 'left';
    my $width  = $it->attr('width') || 8;
    my $spaces = $it->attr('spaces') || 0;

    my $pad  = $width - length $text;
    my ($left, $right) = ('', '');

    while ($pad-- > 0) {
	if ($align eq 'left' || ($align eq 'center' && ($pad & 1))) {
	    $left .= ' ';
	} else {
	    $right .= ' ';
	}
    }

    if ($it->is_text || $spaces) {
	$text = '' if $spaces;
	$ii->replace_it("$right$text$left");
    } else {
	$ii->replace_it(IF::IT->new()->push($right)->push($it)->push($left));
    }
}
*/
