package PIA::Resolver; ###### the PIA's Resolver (main loop)
###	$Id$
###	Copyright 1997, Ricoh California Research Center.
###
###	A Resolver (i.e. an instance of PIA::Resolve) acts like a stack of
###	Transaction objects.  A resolver also has a list of agents which have
###	registered their interest.  For each transaction in the list, the
###	resolver attempts to match its features against every agent, and each
###	agent that matches  is given the chance to act_on the transaction.  In
###	general agent->act_on will either push some new transactions, register
###	a handler agent, or both.

###	After each agent has had its chance to act_on the transaction, any
###	registered handlers are called, after which the transaction is
###	discarded. 

### === being a subclass of DS::Thing wouldn't buy us much ===
#use DS::Thing;			# Generic data structure
#push(@ISA, DS::Thing);

######################################################################
###
### utility functions for creating self and internal variables
###

sub new {
    my($class) = @_;
    my $self = {};
    my $agents={};
    my $computers={};
    
    bless $self,$class;
    $$self{agents}={};
    $$self{_content}=[];
    $$self{computers}=$computers;
    return $self;
}

sub queue {
    my($self)=@_;
    return $$self{_content};
}

sub shift {
    my($self)=@_;
    return shift(@{$self->queue()});
}
sub unshift {
    my($self,$argument)=@_;
    return unshift(@{$self->queue()},$argument);
}
sub push {
    my($self,$argument)=@_;
    return push(@{$self->queue()},$argument);
}
sub pop {
    my($self)=@_;
    return pop(@{$self->queue()});
}


############################################################
###
### Timed requests:
###

@events = ();			# entries [ m h d m w repeat transaction ]
$last_run = 0;			# Time lf last run

sub timed_submission {
    my ($self, $trans, $attrs) = @_;

    ## Submit a request at a given time, optionally repeating.
    ##	 it is not at all clear what to do if the PIA dies 
    ##   or if the time is in the past. 

    my $repeat = $$attrs{'repeat'};
    my $cancel = $$attrs{'cancel'};

    my $minute = $$attrs{'minute'};
    my $hour = $$attrs{'hour'};
    my $day = $$attrs{'day'};
    my $month = $$attrs{'month'};
    my $weekday = $$attrs{'weekday'};

    $minute = 0 unless defined $minute;	# Minute cannot be wildcard.
    my $entry = [ $minute, $hour, $day, $month, $weekday, $repeat, $trans ];

    
}

sub do_timed_submits {
    my ($self) = @_;

    ## Go through the events and do anything that needs doing.
    ##	  This should be called every few minutes.

    my $ev;
    foreach $ev (@events) {
	
    }
}


############################################################
###
### utility functions to maintain agent array:
###

sub agent {
    my($self, $name, $agent) = @_;

    ## Return (or set) an agent by name.

    my $agents=$$self{'agents'};
    $$agents{$name}=$agent if defined $agent;
    return $$agents{$name};
}

sub register_agent {
    my($self, $agent) = @_;

    ## Register an agent.

    my $name=$agent->name();
    return $self->agent($name, $agent);
}

sub un_register_agent {
    my($self, $agent_name) = @_;

    ## Unregister an agent (by either name or value).

    $agent_name=$agent_name->name() if ref($agent_name);
    my $agents=$$self{'agents'};
    my $agent=$$agents{$agent_name};
    delete($$agents{$agent_name});
    return $agent;
}    

sub agents {
    my($self) = @_;
    return values(%{$$self{'agents'}});
}

sub agent_names {
    my($self) = @_;
    return keys(%{$$self{'agents'}});
}


############################################################################
###
### resolve
###
###	This is the resolver's main loop.  It starts with one or more
###	incoming transactions that have been pushed onto its queue, and
###	loops until they're all taken care of.
###

sub resolve {
    my($self)=@_;

    ## Main loop.  
    ##	 Entered with some transactions in the input queue.
    ##	 Returns total number of transactions processed.

    my $count=0;
    my $queue=$self->queue();
    my $numb=@$queue;
#    my @garbage;
   
#be careful: queue may change in other threads while we are here
#TBD  tracing of request, matches, responses

    print "resolve: entered with $numb transactions\n" if $main::debugging;
    while(@$queue){  ##&& $count<100){
	$count+=1;

	## get the next request off the queue.
	##    It's an open question whether this should be a stack or a queue. 

	my $transaction = pop @$queue;
	$numb=@$queue;
	my $u=$transaction->url;
	print "resolve: trans. $count ($numb left): $u\n" if $main::debugging;

	## Look for matches.
	##    Matching agents have their act_on method called with both the
	##    transaction and the resolver as arguments; they can either push
	##    transactions onto the resolver, push satisfiers onto the
	##    transaction, or directly modify the transaction.

	$self->match($transaction);

	## Tell the transaction to go satisfy itself.  
	##    It does this by calling each of the handlers that matched agents
	##    have pushed onto its queue, and looking for a true response.

	$transaction->satisfy($self);

#	push(@garbage,$transaction);
    }
    print "resolve: finished after $count transactions\n" if $main::debugging;
    $count;
}


sub match {
    my($self,$transaction)=@_;

    ## Find all agents that match the given transaction.
    ##	  Each agent that matches gets its act_on method called.
    ##    Returns the number of matches.

    ## Loop through all the agents looking for matches.
    ##    Every agent that matches is allowed to push new requests onto the
    ##    resolver, or to modify the transaction directly.

    my $matches = 0;
    print "matching:" if $main::debugging;
    foreach $agent ($self->agents()){
	print " " . $agent->name . "?" if  $main::debugging;
	if ($transaction->features->matches($agent->criteria)) {
	    print " matched\n" if $main::debugging;
	    $agent->act_on($transaction, $self);
	    ++ $matches;
	}
    }
    print "... $matches agents matched\n" if $main::debugging;
    return $matches;
}

sub simple_request{
    my($self,$transaction,$file)=@_;

    ## Return a response to a caller.
    ##	  This lets the resolver serve as a user agent in place of LWP.

    print "gettting simpleton ...\n" if $main::debugging;
    $self->match($transaction);
    $transaction->satisfy($self);

    my $response=$self->pop;
    ###bad things happen if this is not a response
    print "something wrong with simple request" if $response->to_machine;
    if($file){
    print "simple request into $file \n";
	open(F,">$file");
	print F $response->content;
	close F;
    }

    return $response;
}

1;

