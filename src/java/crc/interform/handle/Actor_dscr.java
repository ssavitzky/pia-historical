////// Actor_dscr.java:  Handler for <actor-dscr>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.sgml.SGML;

/* Syntax:
 *	<actor-dscr name="name">
 * Dscr:
 *	get an actor's DSCR attribute in documentation format.
 */

/** Handler class for &lt;actor-dscr&gt tag. */
public class Actor_dscr extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.unimplemented(ia);
  }
}

/* ====================================================================
### === very kludgy -- should just use attributes ===
### <actor-dscr name="name">
define_actor('actor-dscr', 'content' => 'name', 
	     'dscr' => "get an actor's DSCR attribute");

sub actor_dscr_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    $name = $it->content_string unless defined $name;
    my $link = $it->attr('link');

    my $a = $ii->tagset->actors->{$name};
    if (!defined $a) {
	$ii->replace_it('');
	return;
    }

    my $dscr = $a->attr('dscr');
    $dscr = '' unless defined $dscr;
    $ii->replace_it($dscr);
}


*/


