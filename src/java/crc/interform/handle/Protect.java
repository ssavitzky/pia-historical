////// Protect.java:  Handler for <protect>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;protect&gt tag */
public class Protect extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================
### Expansion control: 
###	protect, protect-result

define_actor('protect', 'quoted' => 'quoted', 
	     'dscr' => "Protect CONTENT from expansion.  Optionally protect
MARKUP by converting special characters to entities.");

define_actor('protect-result', 'handle' => 'protect',
	     'dscr' => "Protect results of expanding CONTENT from further 
expansion.  Optionally protect MARKUP by converting special characters 
to entities.");

%protected_chars = ('&' => '&amp;', '<' => '&lt;', '>' => '&gt;');

sub protect_handle {
    my ($self, $it, $ii) = @_;

    if ($it->attr('markup')) {
	$ii->replace_it(protect_markup($it->content_string));
    } else {
	$ii->replace_it($it->content);
    }
}
*/
