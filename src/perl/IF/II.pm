###### Interform Interpretor
###	$Id$
###
###	The Interform Interpretor parses a string or file, evaluating any 
###	Interform Agents it runs across in the process.  Evaluation is
###	usually done concurrently with parsing because new tags and
###	entities can be defined at any time.  However, it is also
###	possible to execute a saved parse tree.  This is a good thing,
###	because agents are *stored* as parse trees.

package IF::II;

require IF::Parser;

use IF::IT;
use IF::IA;

push(@ISA,'IF::Parser');	# === At some point we will use our own. ===
				#     the problem is that HTML::Parser
				#     does some things we don't want.
				#     We should eventually move to the
				#     full SGML parser.

#############################################################################
###
### InterForm Interpretor Object:
###
###   Input:
###	The II object can take its input from either a file, a string, a
###	stream (subroutine that returns chunks), or a parse tree.
###
###   Output:
###	The II object can provide output as either a string, a
###	stream, or a parse tree.
###
### ===	the stream stuff is missing at this point. ===
###
###   Interface:
###	$interp = II->new(arg => value, ...);
###	$result= $interp->run;
###
###   Theory:
###	The interpretor regards its input as a sequence of SGML tokens:
###	either start tags, end tags, declarations (e.g. <!DOCTYPE...>),
###	comments, or text.  It is a subclass of HTML::Parser.
###
###	A set of ``agents'' may be provided which are matched against
###	tags and other features of items in the token stream.
###
###   Parse Stack:
###	The parse stack is kept in an array of IT (Interform Token)
###	objects; tokens are accumulated if necessary into the contents
###	of the top element on the stack.   An object remains on the 
###	stack until its end tag has been seen.  There is a separate
###	control stack for flags and state variables.
###
### ===	It might be simpler to consolidate the two stacks.
###
###   Output Queue:
###	The output queue contains tokens or strings that have been fully
###	processed and so are ready to be sent to the user.  It is in
###	the form of an anonymous token for easy processing.

sub new {
    my ($class, @attrs) = @_;
    my $self = HTML::Parser->new;

    $self->{_dstack} =  [];	# The stack of items under construction.
    $self->{_cstack} =  [];	# The control stack.
    $self->{_out_queue} = IF::IT->new(); # a queue of tokens to be output.
    $self->{_in_stack} =  [];	# a stack of tokens to be input.
    $self->{_state} = {};	# A ``stack frame''

    bless $self, $class;

    my $attr, $val;
    while (($attr, $val) = splice(@attrs, 0, 2)) {
	$val = 1 unless defined $val;
	$self->{_state}->{"_$attr"} = $val;
	print "  $attr = $val\n" if $main::debugging > 1;
    }

    $self;
}

#############################################################################
###
### Access to Global State:
###

sub cstack {
    my $self = shift;
    return $self->{_cstack};
}

sub dstack {
    my $self = shift;
    return $self->{_dstack};
}

sub out_queue {
    my $self = shift;
    return $self->{_out_queue};
}
sub in_stack {
    my $self = shift;
    return $self->{_in_stack};
}

sub state {
    my ($self, $v) = @_;

    ## Parser and interpretor state.  
    ##	  the push_state operation makes a *copy* of the state (a hash
    ##	  table).

    $self->{'_state'} = $v if defined($v);
    return $self->{'_state'};
}

sub context {
    my ($self, $level) = @_;

    ## current state if $level = 0 or undefined, 
    ##	  else stack frame [-$level].

    return $self->{'_state'} unless $level;
    return $self->{'_cstack'}->[- $level];
}

#############################################################################
###
### Access to Things in the stack frame:
###

sub token {
    my ($self, $v) = @_;

    ## Current token

    $self->state->{'_token'} = $v if defined($v);
    return $self->state->{'_token'};
}

sub handlers {
    my ($self, $v) = @_;

    ## Handler agents for the current token

    $self->state->{'_handlers'} = $v if defined($v);
    return $self->state->{'_handlers'};
}

sub agents {
    my ($self, $v) = @_;

    ## agents
    ##	  Should be a reference to a hash table of Interform Agent, 
    ##	  keyed by name.  Basically a symbol table.  This is not  
    ##	  defined until we start defining local agents. 

    $self->state->{'_agents'} = $v if defined($v);
    $self->state->{'_agents'};
}

sub active_agents {
    my ($self, $v) = @_;

    ## active_agents:
    ##	Should be a reference to a hash table of agents registered as 
    ##  associated with particular tags.

    $self->state->{'_active_agents'} = $v if defined($v);
    $self->state->{'_active_agents'};
}

sub passive_agents {
    my ($self, $v) = @_;

    ## passive_agents:
    ##	Should be a reference to an array of agents that might be interested
    ##	in objects going by.

    $self->state->{'_passive_agents'} = $v if defined($v);
    $self->state->{'_passive_agents'};
}

sub syntax {
    my ($self, $v) = @_;

    ## syntax table:
    ##	  A hash table that contains the various tables that control
    ##	  the SGML parser.

    $self->state->{'_syntax'} = $v if defined($v);
    $self->state->{'_syntax'};
}

sub variables {
    my ($self, $v) = @_;

    ## variable table:
    ##	  A hash table that defines local variables for this level of
    ##	  the parse tree.

    $self->state->{'_variables'} = $v if defined($v);
    $self->state->{'_variables'};
}

sub passing {
    my ($self, $v) = @_;

    ## passing flag:
    ##	  if true, tokens are passed to the output queue
    ##	  parsing and passing are mutually exclusive

    $self->state->{'_passing'} = $v if defined($v);
    $self->state->{'_parsing'} = !$v if defined($v);
    $self->state->{'_passing'};
}

sub parsing {
    my ($self, $v) = @_;

    ## parsing flag:
    ##	  if true, incoming tokens are pushed onto their parent.
    ##	  parsing and passing are mutually exclusive

    $self->state->{'_parsing'} = $v if defined($v);
    $self->state->{'_passing'} = !$v if defined($v);
    $self->state->{'_parsing'};
}

sub skipping {
    my ($self, $v) = @_;

    ## Turn off both parsing and passing flags if $v is supplied.

    $self->state->{'_parsing'} = 0 if defined($v);
    $self->state->{'_passing'} = 0 if defined($v);
    $self->state->{'_parsing'} || $self->state->{'_passing'};
}

sub streaming {
    my ($self, $v) = @_;

    ## streaming flag:
    ##	  if true, tokens are passed to the output as strings.

    $self->state->{'_streaming'} = $v if defined($v);
    $self->state->{'_streaming'};
}

sub quoting {
    my ($self, $v) = @_;

    ## quoting flag.
    ##	  if true, no processing is done on incoming tokens.
    ##	  if negative, the entire content of the current tag is sucked
    ##	  into a single string without parsing.

    $self->state->{'_quoting'} = $v if defined($v);
    $self->state->{'_quoting'};
}

#############################################################################
###
### Access to Variables:
###
###	A variable table is maintained in each stack frame; dynamic scoping
###	is used to access them.  Attributes in the current tag can be used 
###	to provide private scope.

sub getvar {
    my ($self, $v) = @_;

    ## Retrieve the value of a variable.  Look up the stack if
    ##	  necessary. 

    my $level = 0;
    my $context;
    while (defined $self->context($level)) {
	$bindings = $self->context($level) -> {_variables};
	if (defined $bindings && defined $bindings->{$v}) {
	    return $bindings->{$v};
	}
	$level += 1;
    }
    return;
}

sub setvar {
    my ($self, $v, $value) = @_;

    ## Set the value of a variable.  Look up the stack to find the
    ##	  current binding, and change it.

    my $level = 0;
    my $context;
    while (defined $self->context($level)) {
	$bindings = $self->context($level) -> {_variables};
	if (defined $bindings && defined $bindings->{$v}) {
	    return $bindings->{$v} = $value;
	}
	$level += 1;
    }
    return $self->defvar($v, $value);
}

sub defvar {
    my ($self, $v, $value) = @_;

    ## Bind a variable to a value in the current context.

    my $vars = $self->variables;
    if (! defined $vars) {
	$vars = $self->variables(\ {});
    }

    $vars->{$v} = $value;    
}


#############################################################################
###
### Token Input:
###
###	These routines are the ones that HTML::Parser calls on itself
###	when it recognizes an input token:
###
###		declaration($text)
###		start($tag, $attrs)
###		end($tag)
###		comment($text)
###		text($text)
###

sub declaration {
    my ($self, $text) = @_;

    ## HTML declaration, e.g. doctype.
    ##	initial "<!" and ending ">" stripped off.

    $self->resolve(IF::IT->new('!')->push($text))
}

sub start {
    my ($self, $tag, $attrs) = @_;

    ## Start tag.
    ##	The tag and attribute names have been lowercased.
    ##	Entities in the attribute values have already been expanded.

    my $it = IF::IT->new($tag);
    if (ref($attrs) eq 'HASH') {
	for (sort keys %$attrs) {
	    next if /^_/;
	    $it->attr($_, $attrs->{$_});
	    print " $_=" . $attrs->{$_} if $main::debugging>1;
	}
    } elsif (ref($attrs) eq 'ARRAY') {
	my $attr, $val;
	while (($attr, $val) = splice(@$attrs, 0, 2)) {
	    $it->attr($attr, $val);
	    print " $attr=$val " if $main::debugging>1;
	}
    }
    $self->start_it($it);
}

sub end {
    my ($self, $tag) = @_;

    ## End tag.

    $self->end_it($tag, !$tag);
}

sub comment {
    my ($self, $text) = @_;

    ## Comment.
    ##	The leading and trailing "<!--" and "-->" have been stripped off.

    $self->resolve(IF::IT->new('!--')->push($text))
}

sub text {
    my ($self, $text, $expanded) = @_;

    ## Text.
    ##	if $expanded is false, entities have NOT been expanded, so
    ##  HTML::Entities::decode($text) may need to be called.

    $self->resolve($text);
}

#############################################################################
###
### Extended Input:
###
###	In addition to the parser's parse_file and parse_string operations, 
###	we add the following to permit reprocessing of parse trees. 
###

sub process_element {
    my ($self, $elt) = @_;

    ## Process an HTML::Element, including its contents if any.

    my $content = $elt->content;

    my $attrs = {};
    for (sort keys %$elt) {
	next if /^_/;
	$attrs{$_} = $elt->{$_};
    }
    $self->start($elt->tag, $attrs);

    if (defined $content) {
	for (@$content) {
	    if (ref($_)) { $self->process_element($_); }
	    else         { $self->text($_, 1); }
	}
    }
    $self->end($elt->tag);
}

sub process_it {
    my ($self, $it, $contentOnly) = @_;

    ## Process an Interform Token parse tree.  
    ##	  This is not the same as simply pushing it on the output.
    ##	  The idea is that this is the body of an agent, so we have to
    ##	  run it through the interpretor again in the current context.
    ##	  If nothing is active, this has the effect of making a deep
    ##	  copy of the original tree.

    $self->start($it->tag, $it->attrs) unless $contentOnly;

    my $content = $it->content;
    if (defined $content) {
	for (@$content) {
	    if (ref($_)) { $self->process_it($_); }
	    else         { $self->text($_, 1); }
	}
    }
    $self->end($elt->tag) unless $contentOnly;
}


#############################################################################
###
### Processing:
###

sub step {
    my ($self) = @_;

    ## === not completed yet.  Will probably be needed for streaming. ===
}

sub run {
    my ($self) = @_;

    ## === Really needs to get input off the input queue, 
    ##	   going to a file or input stream as needed.

    $self->flush;
    return $self->streaming? $self->out_queue->as_string : $self->out_queue;
}

sub flush {
    my ($self) = @_;

    $self->end_it;
}

#############################################################################
###
### The Parse Stack(s):
###
### ===	These are now inlined in resolve, for efficiency.
###

sub push_state {
    my ($self) = @_;

    ## Push current state onto the stack.

    my $cstack = $self->cstack;
    my $state = $self->state;
    my %newstate = %$state;	# copy the state
    push(@$cstack, \%newstate);	# push the copy
    $self->variables({});	# clear the variable table.
}

sub pop_state {
    my ($self) = @_;

    my $cstack = $self->cstack;
    $self->state(pop(@$cstack));
}


#############################################################################
###
### Syntactic Action Routines:
###
###	Once we have a complete token, here are the basic operations we
###	perform on it.
###

sub start_it {
    my ($self, $it) = @_;

    ## Start work on an HTML element.  
    ##	  If we are expecting more input, push the element onto the
    ##	  stack.  Otherwise, handle it appropriately for a completed
    ##	  element.

    if ($it->needs_end_tag($self)) {
	$self->resolve($it, 1);
    } else {
	$self->resolve($it, 0);
    }
}

sub end_it {
    my ($self, $tag, $one) = @_;

    ## End an element.
    ##	  The tag is optional.  If $one is true, only pop one item
    ##	  whether or not it matches.  With no arguments, it pops the
    ##	  entire stack.

    my $dstack = $self->dstack;
    my $cstack = $self->cstack;
    my $it, $t;
    print " </$tag> " if $main::debugging > 1;
    while (defined($it = pop(@$dstack))) {
	my $was_parsing = $self->parsing;
	$self->state(pop(@$cstack));
	$t = $it->tag;
	$self->resolve($it, $was_parsing? 0 : -1);
	return if ($t eq $tag) or $one;
    }    
}

#############################################################################
###
###  The ``Resolver'':
###
###	This is the heart of the Interpretor:  it takes a start tag, end 
###	tag, or completed subtree and ``does the right thing'' with it.
###	This means:
###	   1.	push or pop the parse stack
###	   2.	apply any interested agents
###	   3.	put the token in the right place.
###
### ===	Really needs to do process_it iteratively.

sub resolve {
    my ($self, $it, $incomplete) = @_;

    ## Do the right thing to an incoming token.  
    ##	  $incomplete = 0 -- complete subtree
    ##	  $incomplete > 0 -- start tag
    ##	              = 2 -- reprocess tree
    ##	  $incomplete < 0 -- end tag

    my $dstack = $self->dstack;
    my $cstack = $self->cstack;

    while ($it) {
	## Loop as long as there are tokens to be processed.

	$self->token($it);
	$incomplete = 0 unless defined $incomplete;
	$it->status($incomplete) if ref($it);

	print " (" . (ref($it)? $it->tag : "...") . " $incomplete) "
	    if $main::debugging > 1;

	if ($incomplete > 0) {

	    ## Start tag: 
	    ##	 Push $it onto the data stack, and push $state onto the 
	    ##	 control stack.

	    $self->handlers([]);	# Clear the handler list.
	    my $state = $self->state;
	    my %newstate = %$state; 	# copy the state
	    push(@$cstack, \%newstate);	# Push it on the control stack
	    $self->variables({});	# clear the variable table.
	    push(@$dstack, $it);	# push token on the data stack

	    ## check for interested agents.
	    ##	keep track of any that register as handlers.

	    $self->check_for_interest($it, 1) unless $self->quoting;

	    ## See if any interested agent modified the token 

	    $it = $self->token;	# might have been changed.
	    if (!defined($it) || (! ref($it) && $it eq '')) {
		## Some agent has deleted the token.
		print " deleted " if $main::debugging > 1;
		$self->state(pop(@$cstack));
		pop(@$dstack);
		return;
	    } elsif (!ref($it) || !$it->status) {
		## Some agent has marked the token as finished
		##	so pop it and go 'round again.
		print " finished " if $main::debugging > 1;
		$self->state(pop(@$cstack));
		pop(@$dstack);
		$incomplete = 0;
		next;
	    } else {
		## Nothing serious happened, so we're done.
		##	Clear the handler lisst in the new state.
		$self->handlers([]);
		$self->pass_it($it, 1) if $self->passing;
		return;
	    }
	} elsif ($incomplete <= 0) {
	    ## End tag or complete token (already popped)
	    ## 	check for interested agents and handler actions.

	    $self->check_for_interest($it) unless $self->quoting;
	    $self->check_for_handlers($it);

	    $it = $self->token;	# might have been changed.
	    return if (!defined($it) || $it eq ''); # might have been deleted.

	    $self->push_it($it) if $self->parsing;
	    $self->pass_it($it, $incomplete) if $self->passing;
	    return;
	}
    }
}


#############################################################################
###
### Output Routines:
###
###	These are called to output a completely-processed token to the 
###	appropriate place. 
###

sub pass_it {
    my ($self, $it, $incomplete) = @_;

    ## Pass a token or tree to the output.

    return unless defined $it;
    my $out_queue = $self->out_queue;

    if (! $self->streaming) {
	$it = $it->endtag if $incomplete < 0;
	$out_queue->push($it);
    } elsif ($incomplete > 0) {
	print "  passing ". $it->starttag ." \n" if $main::debugging > 1;
	$out_queue->push($it->starttag);
    } elsif ($incomplete < 0) {
	print "  passing ".$it->endtag." \n" if $main::debugging > 1;
	$out_queue->push($it->endtag);
    } elsif (! ref($it)) {
	print "  passing $it \n" if $main::debugging > 1;
	$out_queue->push($it);
    } else {
	print "  passing ".$it->starttag."... \n" if $main::debugging > 1;
	$out_queue->push($it->as_HTML);
    }
}

sub push_it {
    my ($self, $it) = @_;

    ## Push a completed tree onto the contents of its parent,
    ##	  or the output queue if we're at the top level.

    return unless defined $it;
    my $dstack = $self->dstack;
    if (! @$dstack) {
	## If there's no parent, pass it to the output instead.
	$self->pass_it($it);
    } else {
	print " pushing to ". $dstack->[-1]->tag if $main::debugging > 1;
	$dstack->[-1]->push($it);
    }
}



#############################################################################
###
### Checking for agents:
###

sub check_for_interest {
    my ($self, $it, $incomplete) = @_;
    my $a;
    my $passive_agents = $self->passive_agents;

    foreach $a (@$passive_agents) {
	if ($a->matches($it, $self, $incomplete)) {
	    $a->act_on($it, $self, $incomplete);
	    print "    agent ".$a->name." is interested\n" if $main::debugging>1;
	}
    }
    my $t = ref($it)? $it->tag : '';
    if (defined($a = $self->active_agents->{$t})) {
	$a->act_for($it, $self, $incomplete);
	print "    agent ".$a->name." acted for it\n" if $main::debugging>1;
    }
    return unless ref($it);
    if ($it->is_active) {
	$it->act($self, $incomplete);
	print "    token ".$it->tag." is active\n" if $main::debugging>1;
    }
}

sub check_for_handlers {
    my ($self, $it) = @_;
    my $handlers = $self->handlers;
    my $a;

    while ($a = shift @$handlers) {
	print "    agent ".$a->name." handles it\n" if $main::debugging>1;
	$a->handle($it, $self);
    }
}



#############################################################################
###
### Routines called by agents:
###

sub add_handler {
    my ($self, $agent) = @_;
    my $handlers = $self->handlers;
    push(@$handlers, $agent);
}

sub complete_it {
    my ($self, $it) = @_;

    ## Mark the token as completed.
    ##	  This tells the parser not to expect an end tag.
    if (ref ($it)) {
	$it->status(0);
	$it->empty(1);
	print "  Completed " . $it->tag . " status " . $it->status . "\n"
	    if $main::debugging > 1;
    }
}

sub open_agent_context {
    my ($self, $active, $passive) = @_;

    ## Start using a new set of agents.
    ##	  The old set are copied unless $active and $passive are
    ##	  supplied.

    $active = $self->active_agents unless defined $active;
    $passive = $self->passive_agents unless defined $passive;

    my %newactive = %$active;	# copy the active agent table
    $self->state->{_active_agents} = (\%newactive);

    my @newpassive = @$passive;	# copy the passive agent list
    $self->state->{_passive_agents} = (\@newpassive);

    $self->state->{_agents} = {}; # make a new agent symbol table
}


sub define_agent {
    my ($self, $agent, @attrs) = @_;

    ## Define an agent local to the current interpretor.
    ##	  Exactly what to do with names and tags is unsettled so far.

    $self->open_agent_context unless defined $self->agents;
    if (! ref($agent)) {
      $agent = IF::IA->new($agent, @attrs)
    }

    my $name = $agent->attr('name');
    my $active = $agent->attr('active');

    if ($active) {
	$self->active_agents->{$name} = $agent;
    } else {
	$self->passive_agents->push($agent);
    }
    $self->agents->{$name} = $agent;
}

sub parse_it {
    my ($self) = @_;

    $self->parsing(1);
}

sub quote_it {
    my ($self, $v) = @_;

    $v = 1 unless defined $v;
    $self->parsing(1);
    $self->quoting($v);
}

sub replace_it {
    my ($self, $it) = @_;

    $self->token($it);
}

sub eval_perl {
    my ($self, $it) = @_;

    ## This bit of legacy crud evaluates the contents of $it as PERL code.
    ##	  The local variables $agent and $request will already have been
    ##	  set up by run_interform.

    local $agent = IF::Run::agent();
    local $request = IF::Run::request();

    print "II Error: missing token\n" unless defined $it;
    print "II Error: $it not a token\n" unless ref($it);
    return unless ref($it);

    my $foo = $it->content;
    print "II Error: missing content\n" unless defined $foo;
    my @code_array=@$foo;
    my $result = IF::IT->new($it->tag);
    my $code, $status;
    while($code=shift(@code_array)){
	print "execing $code \n" if $main::debugging > 1;
	if (ref($code)){
	    $status = $code;	# this is an html element
	} else {
	    #evaluate string and return last expression value
	    $status= $agent->run_code($code, $request);
	    print "Interform error: $@\n" if $@ ne '' && ! $main::quiet;
	    print "code status is $status\n" if  $main::debugging;
	}
	$result->push($status) if $status;
    }

    $self->token($result);    
}

1;
