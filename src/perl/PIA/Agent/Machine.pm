package PIA::Agent::Machine; ###### Machine Model for Agents
###	$Id$
###
###	subclass of machine for agents, these are really virtual machines
###	used by agents when they want to receive transactions

use PIA::Machine;
push(@ISA,PIA::Machine);

sub new {
    my($class,$agent,$address)=@_;
    my $self={};
    bless $self,$class;
    $$self{address}=$address;
    $self->agent($agent);
    return $self;
}

sub agent {
    my($self,$argument)=@_;
    $$self{_agent}=$argument if $argument;
    return $$self{_agent};
}

sub callback{
    my($self,$callback)=@_;

    #register a callback for responses

    $$self{_callback}=$callback if $callback;
    return $$self{_callback};
    
}


sub send_response {
    my($self, $response, $resolver)=@_;

    ## Send a response using a predefined callback

    print "\n response for agent " if $main::debugging;
    my $callback=$self->callback;
    if(ref($callback) eq 'CODE'){
	&$callback($self->agent, $response, $resolver);
    }
    # otherwise just drop it

}

sub get_request {
    my($self, $request, $resolver)=@_;

    ## Handle a direct request to an agent.
    ##	 Normally done by running an InterForm, but the agent can 
    ##	 perform special processing first.

    my $agent=$self->agent;
    my $response=$agent->respond($request, $resolver) if $agent;
    return $response;
    
}



1;
