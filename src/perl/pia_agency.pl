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

    ###get proxy information from environment
    $self->initialize_proxy();
    
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
	# modified default now request is set to virtual agent machine
	$transaction->to_machine($agent->machine);
	#$transaction->push($agent);
    }
}

######   utility functions that provide global functionally
###

######  agency->proxy_for(url)
###  
###    return a string indicating the proxy to use for retrieving this request
###    this is for standard proxy notions only, for automatic redirection
###    or re-writes of addresses, use an appropriate agent

sub  proxy_for{
    my($self,$destination,$protocol)=@_;

    my $no;
    foreach $no (@{$self->no_proxy()}) {
	if($destination =~ /$no/) {
	    return;
	}
    }
    return $self->proxy($protocol);
    	
}
### getproxy information from environment
sub initialize_proxy{
    my($self)=@_;
    while(($k, $v) = each %ENV) {
	$k = lc($k);
	next unless $k =~ /^(.*)_proxy$/;
	$k = $1;
	if ($k eq 'no') {
	     $self->no_proxy(split(/\s*,\s*/, $v));
	    
	}
	else {
	    $self->proxy($k, $v);
	}
    }
}

sub no_proxy{
    my($self,@destination)=@_;
    $$self{'no_proxies'}=[] unless exists $$self{'no_proxies'};
    my $array=$$self{'no_proxies'};
    push(@$array,@destination) if @destination;
    return $array;
}

sub proxy{
    my($self,$protocol,$destination)=@_;
    $$self{'proxies'} = {} unless exists $$self{'proxies'};
    my $hash=$$self{'proxies'};
    $$hash{$protocol}=$destination if $destination;
    return $$hash{$protocol} if $protocol;
    return $$hash{'default'};
}

1;
