////// Repeat.java:  Handler for <repeat>
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

/* Syntax:
 *	
 * Dscr:
 *	
 */

/** Handler class for &lt;repeat&gt tag */
public class Repeat extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.unimplemented(ia);
  }
}

/* ====================================================================

### <repeat list="..." entity="name">...</repeat>
###	
define_actor('repeat', 'quoted' => 'quoted',
	     _handle => \&repeat_handle, _end_input => \&repeat_end_input,
	     'dscr' => "repeat CONTENT with ENTITY in LIST of words.");

sub repeat_handle {
    my ($self, $it, $ii) = @_;

    my $entity = $it->attr('entity') || 'li';
    my $list = list_items($it->attr('list'));
    print "repeating: $entity for (". join(' ', @$list) . ")\n"
	if $main::debugging > 1;
    my $body = $it->content;
    my $item = shift @$list;
    my $context = $ii->entities;

    return unless defined $item;

    $ii->defvar($entity, $item);
    $ii->push_input([$self, 0, $body, $entity, $list, $context]);
    $ii->delete_it;
}

sub repeat_end_input {
    my ($self, $it, $ii) = @_;

    my ($foo, $pc, $body, $entity, $list, $entities) = @$it;

    print "repeat: $entity @$list \n" if $main::debugging > 1;

    my $item = shift @$list;

    if (defined $item) {
	#$ii->define_entity($entity, $item);
	$ii->defvar($entity, $item);
	$it->[1] = 0;		# reset the pc
	$ii->push_input($it);
    } else {
	$ii->entities($entities);
    }
    return $undefined;
}

*/
