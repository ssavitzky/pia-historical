package PIA::Agent::Agency; ###### The "Agency" agent.
###	$Id$
###	Copyright 1997, Ricoh California Research Center.
###
###	This is the class for the ``Agency'' agent; i.e. the one that
###	manages agents and handles requests directed at them.
###
###	It no longer has a resolver field; one should use $main::resolver.
###

push(@ISA,PIA::Agent);


sub initialize {
    my $self=shift;
    
    $self->match_criterion('request',1);
    $self->match_criterion('agent_request',1);

    &PIA::Agent::initialize($self);

    ###get proxy information from environment
    $self->initialize_proxy();
    
    return $self;
}

sub classfile {
    my ($class) = @_;

    $class =~ s@::@/@g;
    return $class . '.pm';
}

sub install {
    my ($self, $options) = @_;

    ## Install a named agent.  Automatically loads the class if necessary. 

    my $name = $options->{'agent'};
    my $type = $options->{'type'};
    my $class = $options->{'class'};

    $type = $name unless defined $type;

    return unless $name;

    if (defined $class) {
	require &classfile($class) unless ($class eq PIA::Agent);
    } else {
	$class = PIA::Agent;
	eval {
	    require &classfile("${class}::$type");
	    $class .= "::$type";
	};
    }

    print "Installing agent $name	class $class\n" unless $main::quiet;
    my $agent = $class->new ($name, $type);    
    $agent->parse_options(0, $options);
    $main::resolver->register_agent($agent);
    $agent;
}

sub un_install_agent{
    my($self,$agent)=@_;
    $main::resolver->un_register_agent($agent);
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
    return unless $transaction->is('agent_request');
    my $url=$transaction->url; 
    return unless $url;

    print "  Acting on agency request for $url\n" if $main::debugging;

    my $path=$url->path;
    my $name = ($path =~ m:^/(\w+)/*:i) ? $1 : $self->name();

    my $agent=$resolver->agent($name);
    if (not defined $agent) {
	print "  no agent $name in $path\n" if  $main::debugging;
	$transaction->to_machine("No agent called '$name' is running.");
    } else {
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
