package PIA_AGENCY;

push(@ISA,PIA_AGENT);

# this is the agent that handles requests of the form http://agency/
# or /agency/ ...

# an agency is just an agent that can install other agents and can have its own resolver
sub resolver{
    my($self,$resolve)=@_;
    $$self{resolver}=$resolve if defined $resolve;
    return $$self{resolver};
    
}

sub install_agent{
    my($self,$agent)=@_;
    $self->resolver()->register_agent($agent);
    return $agent;
    
}

sub un_install_agent{
    my($self,$agent)=@_;
    $self->resolver()->un_register_agent($agent);
    return $agent;
    
}




1;
