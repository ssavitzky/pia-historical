 # class to resolve transactions with agents

# a resolve object acts like an array where the elements are transactions
package RESOLVE;

######################################################################
 # utility functions for creating self and internal variables
sub new {
    my($class) = @_;
    my $self = {};
    my $agents={};
    my $stack=[];
    my $computers={};
    
    bless $self,$class;
    $$self{agents}=$agents;
    $$self{stack}=$stack;
    $$self{computers}=$computers;
    return $self;
}

sub stack{
    my($self)=@_;
    return $$self{stack};
    
}

sub shift{
    my($self)=@_;
    return shift(@{$self->stack()});
}
sub unshift{
    my($self,$argument)=@_;
    return unshift(@{$self->stack()},$argument);
}
sub push{
    my($self,$argument)=@_;
    return push(@{$self->stack()},$argument);
}
sub pop{
    my($self)=@_;
    return pop(@{$self->stack()});
}

############################################################
 # utility functions to maintain agent array
sub register_agent{
    my($self,$agent)=@_;
#    push(@{$$self{agents}},$agent);
    my $name=$agent->name();
    $self->agent($name,$agent);
    
    $self->_feature_computers($agent);
    
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

############################################################
# compute features of a transaction for matching

sub compute_features{
    my($self,$request)=@_;
    my $features=$self->feature_computers();
    my %values;
    print "computing features in $features\n" if $main::debugging;
    foreach $feature (keys %$features){
	print "  computing $feature" if $main::debugging;
	$values{$feature}=&{$$features{$feature}}($request);
	print "  -> " . $values{$feature} . "\n" if $main::debugging;
    }
    return \%values;
    
}
sub _feature_computers{
    my($self,$agent)=@_;
    my $computers=$$self{computers};
    my $hash=$agent->criterion_computation();
    print "registering computers for $agent\n" if $main::debugging;
    while (($key,$value)=each %$hash){
	print "registering feature $key $value\n" if $main::debugging;
	$$computers{$key}=$value;
	    #should check for clobber here
    }
    return $hash;
        
}
sub feature_computers{
    my($self)=@_;
    
    if(!exists $$self{computers}){
	foreach $agent ($self->agents()){
	    $self->_feature_computers($agent);
	}
    }
    
    return $$self{computers};

				     
}

############################################################################
###
### resolve
###
###	This is the resolver's main loop.  It starts with one or more
###	incoming transactions that have been pushed onto its stack, and
###	loops until they're all taken care of.
###

sub resolve {
    my($self)=@_;
    my $count=0;
    my $stack=$self->stack();
    my @garbage;
#temporary hack variable to determine if response has been sent
#TBD link requests and responses then test requests for response...
    my $sent_response=0;
    
#be careful stack may change in other threads while we are here
#TBD  tracing of request, matches, responses

    while(@$stack && $count<100){
	$count+=1;

	## Pop a request off the stack.

	my $request=shift @$stack;
	my $numb=@$stack;
	my $u=$request->url;
	print "handling request $count ($numb left): $u\n" if $main::debugging;

	## Look for a match.  
	##    Matching agents typically add new transactions.
	##    In addition, one might be the target of the request.

	my $handler = $self->match($request,$stack);
	if (ref($handler)) {
	    print "running agent handler.\n" if $main::debugging;
	    my $status=$self->run($handler,$request);
	} else {
	    my $additions = $handler;
	    print "$additions transactions added.  " if $main::debugging;
	    if ($request->is_response()) {
		send_response($request);
		$sent_response = 1;
	    } 
	    if ($request->is_request() && ! $additions && ! $sent_response) {
		## not found error if additions are 0;TBD
		print "pushing error_response\n" if $main::debugging;
		push(@$stack,$self->error_response($request));
	    }
	}
	push(@garbage,$request);
    }
    # === should take out the garbage
}


###### RESOLVE->run($agent, $request, $context)
###
### 	Run $agent's handle method on the $request
###
sub run {
    my($self,$agent,$request,$context)=@_;
    local $resolver = $self;
    my $response=$agent->handle($request);

    my $type = ref($response);
    my $name = $agent->name;
    print "agent $name returnobject $type\n" if $main::debugging;
    
#    push(@$context,$response) if ref($response);
    $self->push($response) if ref($response);
    return $response;
    
}

###### RESOLVE->match $transaction, $stack)
###
###	Match the current transaction against all the agents.
###	All are allowed to modify the transaction and add new ones.
##
### === we'll change in future to make efficient and use context
###
sub match {
    my($self,$transaction,$stack)=@_;

    ## Find an agent that matches the given transaction.
    ##    Returns either the number of new transactions added,
    ##    or a reference to an agent that wants to handle the request.

    my $additions=0;
#these could be done at push time TBD

    ## Compute features of the transaction.

    my $features=$self->compute_features($transaction);

    ## Loop through all the agents looking for matches.
    ##    Every agent that matches is allowed to add new requests via its
    ##    new_requests method, which can also modify the transaction.
    print "matching:" if $main::debugging;
    foreach $agent ($self->agents()){
	if ($agent->matches($features)) {
	    my @requests = $agent->new_requests($transaction);
	    push(@$stack,@requests);
	    my $new_requests = @requests;
	    $additions += $new_requests;
	    print($agent->name," added $new_requests\n") if $main::debugging;
	}
    }

    ## Now look for an agent to which the request is directed,
    ##    (as opposed to one merely interested) 
    ##    === this should really be done using an agent! ===

    print "looking for agent\n" if $main::debugging;
    
    if (&FEATURES::is_agent_request($transaction)) {
	my $url=$transaction->url;
	return $additions unless $url;

	my $path=$url->path;
	$path =~ m:^/(\w+)/*:i;
	my $name=$1;
	my $agent=$self->agent($name);
	print "found agent $name in $path\n" if $main::debugging;
	print ref($agent) if $main::debugging;
	
	return $agent if defined $agent;
    }

    return $additions;
    
}

############################################################################
###
### utility functions to send a normal or error response if no agent does.
###

sub send_response{
    my($reply)=@_;
    my $machine=$reply->to_machine();

    if (ref($machine)) {
	print "sending response.\n" if $main::debugging;
	my $status=$machine->send_response($reply) if ref($machine);
	return $status;
    } else {
	print "sending response to $machine\n" if $main::debugging;
	return;
    }
}

sub error_response{
    my($self,$argument)=@_;
    my $response=HTTP::Response->new(&HTTP::Status::RC_NOT_FOUND, "not found");
    $response->content_type("text/plain");    
    my $url=$argument->url()->as_string();
    
    $response->content("could not find $url\n");

    $response=TRANSACTION->new($response,
			       $main::this_machine,
			       $argument->from_machine());
    return $response;
}


1;

