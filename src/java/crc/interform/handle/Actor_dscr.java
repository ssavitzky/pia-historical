////// Actor_dscr.java:  Handler for <actor-dscr>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;actor-dscr&gt tag */
public class Actor_dscr extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
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


### <actor-attrs name="name">
###	Get an actor's attributes in form suitable for documentation.
###	The "name", "tag",  and "dscr" attributes are not included.

define_actor('actor-attrs', 'content' => 'name', 
	     'dscr' => "get an actor's attributes in documentation format");

%no_show = ('dscr'=>1, 'tag'=>1, 'name'=>1);

sub actor_attrs_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    $name = $it->content_string unless defined $name;
    my $link = $it->attr('link');

    my $ia = $ii->tagset->actors->{$name};
    if (!defined $ia) {
	$ii->replace_it(' -unknown-');
	return;
    }

    my $dscr = '';

    for $a (@{$ia->attr_names}) {
	next if $no_show{$a};
	$v = $ia->attr($a);
	$dscr .= ' ' . $a;
	$dscr .= "='$v'" unless ($v == 1 || $v eq $a);
    }
    $ii->replace_it($dscr);
}
*/
