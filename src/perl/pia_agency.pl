###### class PIA_AGENCY
###	$Id$
###
###	This is the class for the ``agency'' agent; i.e. the one that
###	handles requests directed at agents.  It slso owns the resolver, 
###	which may not be a good idea.
###

package PIA_AGENCY;

push(@ISA,PIA_AGENT);


sub initialize {
    my $self=shift;
    
    $self->match_criterion('request',1);
    $self->match_criterion('agent_request',1);

    &PIA_AGENT::initialize($self);
    return $self;
}

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


###### agency -> act_on($transaction, $resolver)
###
###	Act on a transaction that we have matched.  
###	Since the Agency matches all requests to agents, this means
###	that we need to find the agent that should handle this request
###	and push it onto the transaction.
###
sub act_on {
    my($self, $transaction, $resolver) = @_;

    print "Agency->act_on\n" if $main::debugging;
    if ($transaction->is('agent_request')) {
	my $url=$transaction->url;
	return unless $url;

	print "  Acting on agency request for $url\n" if $main::debugging;

	my $path=$url->path;
	my $name = ($path =~ m:^/(\w+)/*:i) ? $1 : $self->name();

	my $agent=$resolver->agent($name);
	print "  no agent in $path\n" if ! defined $agent && $main::debugging;
	next if not defined $agent;

	my $class = ref($agent);
	print "  found agent $name class $class in $path\n" if $main::debugging;
	
	$transaction->push($agent);
    }
}


1;
