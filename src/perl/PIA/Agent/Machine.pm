package PIA::Agent::Machine; ###### ``Virtual'' Machine Model for Agents
###	$Id$

##############################################################################
 # The contents of this file are subject to the Ricoh Source Code Public
 # License Version 1.0 (the "License"); you may not use this file except in
 # compliance with the License.  You may obtain a copy of the License at
 # http://www.risource.org/RPL
 #
 # Software distributed under the License is distributed on an "AS IS" basis,
 # WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 # for the specific language governing rights and limitations under the
 # License.
 #
 # This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 # created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 # Rights Reserved.
 #
 # Contributor(s):
 #
############################################################################## 

###
###	subclass of Machine for agents, these are really virtual machines
###	used by agents when they want to receive transactions.

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
