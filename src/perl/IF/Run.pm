###### Run the Interform Interpretor
###	$Id$
###
###	This module contains the routines that actually *run* the
###	InterForm Interpretor on inputs of various sorts.
###

package IF::Run;

use IF::II;
use IF::IA;
use IF::IT;
use IF::Actors;


#############################################################################
###
### Default Arguments:
###
###	These arrays contain default argument lists for constructing the
###	most common kinds of interpretors and parsers.
###

@html_defaults = (
	     'streaming' => 0,	# output is a tree
	     'parsing' => 1,	# push completed tags onto their parent.
	     'passing' => 0,
	     'syntax' => $IF::IT::syntax,
	     'passive_actors' => [],
	     'actors' => {},
	     );

local $entities;

#############################################################################
###
### Evaluators:
###
###	All take either a reference to an interpretor as their second
###	argument, or a list of attribute=>value pairs that are used to
###	construct one.  The default is to use @if_defaults, which 
###	produces a string as the result.

sub run_file {
    my ($file, $interp) = @_;

    ## Run the interpretor over a file. 
    ##	  The result is a string.

    print "\nrunning file $file\n" if $main::debugging;

    if (!defined $interp) {
	$interp = IF::II->new(@IF::Actors::if_defaults);
	$interp->entities($entities) if defined $entities;
    } elsif (! ref($interp)) {
	shift;
	$interp = IF::II->new(@_);
    }
    $interp->parse_file($file);
    return $interp->run;
}


sub run_string {
    my ($input, $interp) = @_;

    ## Run the interpretor over a string, such as a buffered file. 
    ##	  The result is a string.

    if (!defined $interp) {
	$interp = IF::II->new(@IF::Actors::if_defaults);
	$interp->entities($entities) if defined $entities;
    } elsif (! ref($interp)) {
	shift;
	$interp = IF::II->new(@_);
    }
    $interp->parse($input);
    return $interp->run;
}


sub run_tree {
    my ($input, $interp) = @_;

    ## Run the interpretor over a parse tree.
    ##	  The result is a string.

    if (!defined $interp) {
	$interp = IF::II->new(@IF::Actors::if_defaults);
	$interp->entities($entities) if defined $entities;
    } elsif (! ref($interp)) {
	shift;
	$interp = IF::II->new(@_);
    }
#    print "running hook " . $input->as_string . "\n";
#    $main::debugging=2;
    $interp->resolve($input, 2);
    return $interp->run;
}


sub run_stream {


}

sub parse_init_file {
    my ($file, $interp) = @_;

    ## Run the interpretor parser over a file in ``parser mode''.
    ##	  The result is a parse tree.  No actors are used, but the InterForm 
    ##	  entity table is enabled.  Eventually we should use an appropriate
    ##	  set of actors. ===

    print "\parsing file $file\n" if $main::debugging;
    local $entities = if_entities($agent, $file, $request);

    if (!defined $interp) {
	$interp = IF::II->new(@html_defaults);
	$interp->entities($entities) if defined $entities;
    } elsif (! ref($interp)) {
	shift;
	$interp = IF::II->new(@_);
    }
    $interp->parse_file($file);
    return $interp->run;
}

sub parse_html_file {
    my ($file, $interp) = @_;

    ## Run the interpretor parser over a file in ``parser mode''.
    ##	  The result is a parse tree.  No actors are used.

    print "\parsing file $file\n" if $main::debugging;

    if (!defined $interp) {
	$interp = IF::II->new(@html_defaults);
    } elsif (! ref($interp)) {
	shift;
	$interp = IF::II->new(@_);
    }
    $interp->parse_file($file);
    return $interp->run;
}

sub parse_html_string {

    my ($input, $interp) = @_;

    ## Run the interpretor parser over a string in ``parser mode''.
    ##	  The result is a parse tree.  No actors are used.

    if (!defined $interp) {
	$interp = IF::II->new(@html_defaults);
    } elsif (! ref($interp)) {
	shift;
	$interp = IF::II->new(@_);
    }
    $interp->parse($input);
    return $interp->run;
}


#############################################################################
###
### Evaluating Interforms on behalf of PIA Agents:
###

local $agent, $request, $transaction, $resolver;

sub interform_file {
    local ($agent, $file, $request, $resolver)=@_;

    ## Run a standard PIA InterForm file.

    $resolver = $main::resolver unless defined($resolver);
    local $entities = if_entities($agent, $file, $request);

    my $result = run_file($file);
    if (!result || ref($result)) {
	print "\nIF::II::run_file returned '$result'\n" if $main::debugging;
	$main::debugging=0;	# look at the first post-mortem.
    }
    return $result;
}

sub interform_hook {
    local ($agent, $tree, $request, $resolver)=@_;

    ## Run a standard PIA InterForm file.

    $resolver = $main::resolver unless defined($resolver);
    local $entities = if_entities($agent, '', $request);

    my $result = run_tree($tree);
    if (!result || ref($result)) {
	print "\nIF::II::run_tree returned '$result'\n" if $main::debugging;
    }
    return $result;
}

@dayNames=(Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday);
@monthNames=(January, February, March, April, May, June,
	     July, August, September, October, November, December);

sub if_entities {
    my ($agent, $file, $trans) = @_;

    ## Load a standard set of entity bindings.
    ##	  === eventually this should be a set of separately-loaded packages

    my $ents = $trans->test('entities') if defined $trans;
    print "bogus entities = $ents\n" if ($ents && ! ref $ents);

    if (! ref $ents) {
	my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst)=
	    localtime(time());
	my $date=sprintf("%d%02d%02d", $year, $mon+1, $mday);
	## === should be $year + 1900, of course.
	my $time=sprintf("%02d:%02d", $hour, $min);

	my ($request, $response);
	if (defined $trans && $trans->is_response) {
	    $response = $trans;
	    $request  = $response->response_to;
	} else {
	    $request = $trans;
	}
	my $url = $trans->url;
	my $path = $url->path if defined $url;
	my $query = $url->query if defined $url;
	$file =~ m:([^/]*)$:;
	my $fn = $1;

	## === request method, response type, etc. ===

	my $agentNames = join(' ', sort($resolver->agent_names));

	## === $trans->test('agent') returns 1; doesn't compute.
	my $transAgentName = $trans->test('agent');
	my $transAgentType;
	if ($transAgentName) {
	    my $ta = $resolver->agent($transAgentName);
	    $transAgentType = $ta->type if ref $ta;
	}

	$ents = {
	    'transAgentName'	=> $transAgentName,
	    'transAgentType'	=> $transAgentType,

	    'agentName' 	=> $agent->name,
	    'agentType' 	=> $agent->type,
	    'fileName' 		=> $fn,
	    'filePath' 		=> $file,

	    'url'		=> ((ref $url)? $url->as_string : ''),
	    'urlQuery'		=> $query,
	    'urlPath'		=> $path,

	    'piaUSER'		=> $ENV{'USER'} || getlogin,
	    'piaHOME'		=> $ENV{'HOME'},
	    'piaHOST'		=> $main::PIA_HOST,
	    'piaPORT'		=> $main::PIA_PORT,
	    'piaDIR'		=> $main::PIA_DIR,

	    'agentNames'	=> $agentNames,
	    'entityNames'   	=> '',
	    'actorNames'	=> join(' ', sort( keys %$IF::Actors::actors)),

	    'second'		=> $sec,
	    'minute'		=> $min,
	    'hour'		=> $hour,
	    'day'		=> $mday,
	    'month'		=> $mon+1,
	    'year'		=> $year+1900,
	    'weekday'		=> $wday,
	    'dayName'		=> $dayNames[$wday],
	    'monthName'		=> $monthNames[$mon],
	    'yearday'		=> $yday,
	    'date'		=> $date,
	    'time'		=> $time,
	};

	$ents->{'entityNames'} = join(' ', sort keys %$ents);

	$trans -> assert('entities', $ents) if defined $trans;
	## === WARNING! Agents can pass info through entities now ===
    }

    $ents->{'agentName'} 	= $agent->name;
    $ents->{'agentType'} 	= $agent->type;
    $ents;
}

### === These really ought to be in Agent

sub agent {
    return $agent;
}
sub request {
    return $request;
}
sub transaction {
    return $request;
}
sub resolver {
    return $resolver;
}

sub eval_perl {
    my ($ia, $it, $ii) = @_;

    ## This bit of legacy crud evaluates the contents of $it as PERL code.
    ##	  The local variables $agent and $request will already have been
    ##	  set up by run_interform.

    print "II Error: missing token\n" unless defined $it;
    print "II Error: $it not a token\n" unless ref($it);
    return unless ref($it);

    my $status = $agent->run_code($it->content_string, $request, $resolver);
    print "Interform error: $@\n" if $@ ne '' && ! $main::quiet;
    print "code result is $status\n" if  $main::debugging;

    $ii->token(IF::IT->new($it->tag, $status));    
    return;
}

1;


