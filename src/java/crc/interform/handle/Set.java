////// Set.java:  Handler for <set>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;set&gt tag */
public class Set extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================

### <set name="name" value="value">
### <set name="name">value</set>

define_actor('set', 'content' => 'value',
	     'dscr' => "set NAME to VALUE, 
optionally in PIA, AGENT, ACTOR, ELEMENT, TRANSaction, or ENTITY context.   
ELEMENT and ENTITY may define a LOCAL binding.  ELEMENT may have a TAG.  
TRANSaction item may be FEATURE.
Optionally COPY new or PREVIOUS value.");

### === COPY / COPY PREVIOUS ===
### === TAG= / ACTOR ===

sub set_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    my $value = $it->attr('value');

    $value = $it->content unless defined $value;
    
    if ($it->attr('pia')) {
	local $agent = IF::Run::agent();
	local $request = IF::Run::request();
	$name = "\$" . $name; 
        $value = $value->as_string if ref $value;
	my $status = $agent->run_code("$name='$value';", $request);
	print "Interform error: $@\n" if $@ ne '' && ! $main::quiet;
	print "code status is $status\n" if  $main::debugging;
	$ii->replace_it($status);
    } elsif ($it->attr('agent')) {
	local $agent = IF::Run::agent();
        if ($it->attr('hook')) {
            $value = IF::IT->new()->push($it->content);
        } else {
            $value = $value->as_string if ref $value;
	}
	$agent->option($name, $value) if defined $agent;
    } elsif ($it->attr('trans')) {
	local $trans = IF::Run::transaction();
        if ($it->attr('feature')) {
	    $trans->set_feature($name, $value) if defined $trans;
	} else {
	    $trans->attr($name, $value) if defined $trans;
	}
    } elsif ($it->attr('local')) {
	$ii->defvar($name, $value);
    } elsif ($it->attr('entity')) {
	$ii->entities->{$name} = $value;
    } elsif ($it->attr('element')) {
	$ii->in_token->attr($name, $value);
    } else {
	$ii->setvar($name, $value);
    }
    if ($it->attr('copy')) {
    	$ii->replace_it($it->content);
    } else {
	$ii->delete_it;
    }
}
*/
