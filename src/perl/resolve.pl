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

############################################################
# real work gets done here
# for each transaction compute features, get new requests from matching agents,
# give appropriate agent( determined by path)request to handle
# finally send a response if appropriate

sub run{
    my($self,$agent,$request,$context)=@_;
    my $response=$agent->handle($request);
    my  $type =ref($response);
    my $name=$agent->name;
    
    print "agent $name returnobject $type\n" if $main::debugging;
    
#    push(@$context,$response) if ref($response);
    $self->push($response) if ref($response);
    return $response;
    
}

#which agents go with which requests/responses
#be careful stackmaychange in other threads while we are here
#TBD  tracing of request,matches,responses
sub resolve{
    my($self)=@_;
    my $count=0;
    my $stack=$self->stack();
    my @garbage;
#temporary hack variable to determine if response hasbeen sent
#TBD link requests and responses then test requests for response...
    my $sent_response=0;
    

    while(@$stack && $count<100){
	$count+=1;
	
	my $request=shift @$stack;
	my $numb=@$stack;
	my $u=$request->url;
	print "handling request $u  number $count, $numb left\n" if $main::debugging;
	
	print "looking for match\n" if $main::debugging;
	my $handler=$self->match($request,$stack);
	if(ref($handler)){
	    print "running request\n" if $main::debugging;
	    my $status=$self->run($handler,$request);
	}else{
	    my $additions = $handler;
	    print "$additions added no agent found for request" if $main::debugging;
	    if($request->is_response()){
		print "sending response\n" if $main::debugging;
		send_response($request);
		$sent_response=1;
		
	    } 
	    if($request->is_request() && ! $additions && ! $sent_response){
		
#not founderror if additions are 0;TBD
		print "pushing error_response\n" if $main::debugging;
		push(@$stack,$self->error_response($request)) unless $additions;
	    }

	}
	push(@garbage,$request);
	
earn    }
    #should take out the garbage
}


#return number of transactions added orthe agent to whomrequest isdirected
#we'll changein future to make efficient and use context
sub match{
    my($self,$transaction,$stack)=@_;
    my $additions=0;
#these could be done at push time TBD
    
    my $features=$self->compute_features($transaction);

#allow agents to make requests in response to this transaction
    foreach $agent ($self->agents()){
	my @requests=$agent->new_requests($transaction) if $agent->matches($features);
	push(@$stack,@requests);
	my $new_requests=@requests;
	$additions+=$new_requests;
	print($agent->name,"added $new_requests \n") if $new_requests && $main::debugging;
    }

    print "lookingfor agent\n" if $main::debugging;
    
    if(&FEATURES::is_agent_request($transaction)){
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
#utility function to send areply if nobody else those
sub send_response{
    my($reply)=@_;
    my $machine=$reply->to_machine();

    my $status=$machine->send_response($reply) if ref($machine);
    return $status;
}
sub error_response{
    my($self,$argument)=@_;
    my $response=HTTP::Response->new(&HTTP::Status::RC_NOT_FOUND, "not found");
 $response->content_type("text/plain");    
    my $url=$argument->url()->as_string();
    
    $response->content("could not find $url\n");

    $response=TRANSACTION->new($response,$main::this_machine,$argument->from_machine());
    
    return $response;
    
}


1;

