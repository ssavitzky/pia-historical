###### Run the Interform Interpretor
###	$Id$
###
###	This module contains the routines that actuall *run* the
###	InterForm Interpretor on inputs of various sorts.
###

package IF::Run;

use IF::II;
use IF::IA;
use IF::IT;


#############################################################################
###
### Default Arguments:
###
###	These arrays contain default argument lists for constructing the
###	most common kinds of interpretors and parsers.
###

@if_defaults = (
	     'streaming' => 1,	# output is a string
	     'passing' => 1,	# pass completed tags to the output.
	     'syntax' => $IF::IA::syntax,
	     'active_agents' => $IF::IA::active_agents,
	     'passive_agents' => $IF::IA::passive_agents,
	     );

@html_defaults = (
	     'streaming' => 0,	# output is a tree
	     'parsing' => 1,	# push completed tags onto their parent.
	     'passing' => 0,
	     'syntax' => $IF::IT::syntax,
	     'active_agents' => {},
	     'passive_agents' => [],
	     );


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
	$interp = IF::II->new(@if_defaults);
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
	$interp = IF::II->new(@if_defaults);
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
	$interp = IF::II->new(@if_defaults);
    } elsif (! ref($interp)) {
	shift;
	$interp = IF::II->new(@_);
    }
    $interp->process_it($input);
    return $interp->run;
}


sub run_stream {


}

sub parse_html_file {
    my ($file, $interp) = @_;

    ## Run the interpretor parser over a file in ``parser mode''.
    ##	  The result is a parse tree.  No agents are used.

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
    ##	  The result is a parse tree.  No agents are used.

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
### Evaluating Interforms on behalf of PIA agents:
###

local $agent, $request;

sub interform_file {
    local ($agent,$file,$request)=@_;

    my $string = run_file($file);

    if (!string || ref($string)) {
	print "\nIF::II::run_file returned '$string'\n" if $main::debugging;
	$main::debugging=0;	# look at the first post-mortem.
    }
    return $string;
}

### These are used by eval_perl, which is really in II.pm
### === they really need to be in Agent.

sub agent {
    return $agent;
}
sub request {
    return $request;
}

sub eval_perl {
    my ($ia, $it, $ii) = @_;

    ## This bit of legacy crud evaluates the contents of $it as PERL code.
    ##	  The local variables $agent and $request will already have been
    ##	  set up by run_interform.

    ## These aren't needed as long as we're inside IF::Run
    ## local $agent = agent();
    ## local $request = request();

    print "II Error: missing token\n" unless defined $it;
    print "II Error: $it not a token\n" unless ref($it);
    return unless ref($it);

    my $status = $agent->run_code($it->content_string, $request);
    print "Interform error: $@\n" if $@ ne '' && ! $main::quiet;
    print "code result is $status\n" if  $main::debugging;

    $ii->token(IF::IT->new($it->tag, $status));    
    return;
}

1;


