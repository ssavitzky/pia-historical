###### class RESOLVE
###	$Id$
###
###	A resolver (i.e. an instance of class RESOLVE) acts like a queue of
###	transaction objects.  A resolver also has a list of agents which have
###	registered their interest.  For each transaction in the list, the
###	resolver attempts to match its features against every agent, and each
###	agent that matches  is given the chance to act_on the transaction.  In
###	general agent->act_on will either push some new transactions, register
###	a handler agent, or both.

###	After each agent has had its chance to act_on the transaction, any
###	registered handlers are called, after which the transaction is
###	discarded. 

package RESOLVE;

######################################################################
###
### utility functions for creating self and internal variables
###
sub new {
    my($class) = @_;
    my $self = {};
    my $agents={};
    my $queue=[];
    my $computers={};
    
    bless $self,$class;
    $$self{agents}=$agents;
    $$self{queue}=$queue;
    $$self{computers}=$computers;
    return $self;
}

sub queue{
    my($self)=@_;
    return $$self{queue};
    
}

sub shift{
    my($self)=@_;
    return shift(@{$self->queue()});
}
sub unshift{
    my($self,$argument)=@_;
    return unshift(@{$self->queue()},$argument);
}
sub push{
    my($self,$argument)=@_;
    return push(@{$self->queue()},$argument);
}
sub pop{
    my($self)=@_;
    return pop(@{$self->queue()});
}

############################################################
 # utility functions to maintain agent array
sub register_agent{
    my($self,$agent)=@_;
#    push(@{$$self{agents}},$agent);
    my $name=$agent->name();
    $self->agent($name,$agent);
    
    return $agent;
        
}

sub un_register_agent{
    my($self,$agent_name)=@_;
    $agent_name=$agent_name->name() if ref($agent_name);
    my $agents=$$self{agents};
    my $agent=$$agents{$agent_name};
    delete($$agents{$agent_name});
    return $agent;
    
}    

sub agent{
    my($self,$name,$agent)=@_;
    my $agents=$$self{agents};
    $$agents{$name}=$agent if defined $agent;
    return $$agents{$name};
    
}
sub agents{
    my($self)=@_;
    return values(%{$$self{agents}});
    
}

sub agent_names{
    my($self)=@_;
    return keys(%{$$self{agents}});
    
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
    my $count=0;
    my $queue=$self->queue();
    my $numb=@$queue;
    my @garbage;
   
#be careful: queue may change in other threads while we are here
#TBD  tracing of request, matches, responses

    print "resolve: entered with $numb transactions\n" if $main::debugging;
    while(@$queue && $count<100){
	$count+=1;

	## shift a request off the queue.

	my $transaction=shift @$queue;
	$numb=@$queue;
	my $u=$transaction->url;
	print "resolve: trans. $count ($numb left): $u\n" if $main::debugging;

	## Look for matches.
	##    Matching agents have their act_on method called with both the
	##    transaction and the resolver as arguments; they can either push
	##    transactions onto the resolver, push handlers onto the
	##    transaction, or directly modify the transaction.

	$self->match($transaction);

	## Tell the transaction to go satisfy itself.  
	##    It does this by calling each of the handlers that matched agents
	##    have pushed onto its queue, and looking for a true response.

	$transaction->satisfy($self);

	push(@garbage,$transaction);
    }
    print "resolve finished after $count transactions\n" if $main::debugging;

    # === should take out the garbage
}


###### resolver->match $transaction, \@handlers)
###
###	Match the current transaction against all the agents.
###	All are allowed to modify the transaction and add new ones.
###	Responses are pushed onto the @handlers array.
###
###	Returns the number of handlers and/or transactions added.
##
### === we'll change in future to make efficient and use context
###
sub match {
    my($self,$transaction)=@_;
    my $matches = 0;
    ## Find all agents that match the given transaction.
    ##    Returns either the number of matches.

    ## Loop through all the agents looking for matches.
    ##    Every agent that matches is allowed to push new requests onto the
    ##    resolver, or to modify the transaction directly.

    print "matching:" if $main::debugging;
    foreach $agent ($self->agents()){
	if ($agent->matches($transaction)) {
	    print "About to call $agent -> act_on\n" if $main::debugging;
	    $agent->act_on($transaction, $self);
	    ++ $matches;
	}
    }
    print "... $matches agents matched\n" if $main::debugging;
    return $matches;
}



1;

