////// Get.java:  Handler for <get>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;get&gt tag */
public class Get extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================
### <get [name="n"] [pia] [entity]>name</get->
###	Gets the value of a variable named in the 'name' attribute or content.
###	if the 'pia' attribute is set, uses the pia (PERL) context,
###	if the 'entity' attribute is set, uses the entity table.
###
### Namespace options:
###	namespace="whatever", or one of the following:
###	local	current element 
###	attr	attributes of the current element
###	form	this InterForm's outer context
###	pia	pia (PERL) context
###	agent	attributes of the current PIA agent
###	
###	Variables can also be accessed as entities:
###	e.g. &AGENT.Foo.bar;
###	

### === <get file="name"> should map to read

define_actor('get', 'empty' => 1, 
	     'dscr' => "Get value of NAME, 
optionally in PIA, ENV, AGENT, FORM, ELEMENT, TRANSaction, or ENTITY context.
If FILE or HREF specified, functions as read.");

### === EXPAND/PROTECT ?===

sub get_handle {
    my ($self, $it, $ii) = @_;

    if ($it->attr('file') || $it->attr('href')) {
	return read_handle($self, $it, $ii);
    }

    my $name = $it->attr('name');
    $name = $name->as_string if ref $name;
    my $result;

    if ($it->attr('pia')) {
	local $agent = IF::Run::agent();
	local $request = IF::Run::request();
	$name = '$' . $name;
	my $status = $agent->run_code("$name", $request);
	print "Interform error: $@\n" if $@ ne '' && ! $main::quiet;
	print "code status is $status\n" if  $main::debugging;
	$result = ($status);
    } elsif ($it->attr('env')) {
	$result = ($ENV{$name});
    } elsif ($it->attr('form')) {
	my $hash = IF::Run::request()->parameters;
	$result = ($$hash{$name});
    } elsif ($it->attr('agent')) {
	local $agent = IF::Run::agent();
	$result = ($agent->option($name)) if defined $agent;
    } elsif ($it->attr('trans')) {
	local $trans = IF::Run::transaction();
        if ($it->attr('feature')) {
	    $result = ($trans->get_feature($name)) if defined $trans;
	} elsif ($it->attr('headers')) {
	    if ($it->attr('request')) {
		$result = $trans->is_request? $trans->request->headers_as_string
		    : $trans->response_to->request->headers_as_string;
	    } else {
	        $result = $trans->message->headers_as_string;
	    }
	} else {
	    $result = ($trans->attr($name)) if defined $trans;
	}
    } elsif ($it->attr('entity')) {
	$result = ($ii->entities->{$name});
    } elsif ($it->attr('element')) {
	$result = ($ii->in_token->attr($name));
    } elsif ($it->attr('local')) {
        $result = ($ii->getvar($name));
    } else {
	## default is to search local variables first, then global entities.
        $result = ($ii->get_entity($name));
    }
    $ii->replace_it($result);
}

*/
