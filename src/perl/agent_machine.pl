###### Class  AGENT_MACHINE -- Machine Model.
###  subclass of machine for agents, these are really virtual machines
#     used by agents when they want to receive transactions

package AGENT_MACHINE;
push(@ISA,MACHINE);


sub new{
    my($class,$agent,$address)=@_;
    my $self={};
    bless $self,$class;
    $$self{address}=$address;
    $self->agent($agent);
    return $self;
    
}

sub agent{
    my($self,$argument)=@_;
    $$self{_agent}=$argument if $argument;
    return $$self{_agent};
}

sub callback{
    my($self,$callback)=@_;
    #registera callback for responses
    $$self{_callback}=$callback if $callback;
    return $$self{_callback};
    
}


## a transaction has come back  for the agent
## use the agent call back, or just dump it
sub send_response{
    my($self,$response)=@_;
    my $callback=$self->callback;
    if(ref($callback) eq 'CODE'){
	&$callback($self->agent,$response);
    }
#otherwise just drop it

}

#somebody asking for something
# agency should treat as interform request
sub get_request{
    my($self,$request)=@_;
    my $agent=$self->agent;
    my $response=$agent->respond_to_interform($request) if $agent;
    return $response;
    
}



1;
