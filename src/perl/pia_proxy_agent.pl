package PIA_AGENT::PROXY;

push(@ISA,PIA_AGENT);

# this is the agent that handles proxy requests 

sub initialize {
    my $self = shift;
    $self->match_criterion('proxy_request',1);
    return $self;
}


############################################################################

sub act_on {
    my($self, $transaction, $resolver) = @_;

    ## Act on the transaction by pushing $self as a satisfier.

    print "Proxy->act_on\n" if $main::debugging;
    $transaction -> push($self);
}


sub  handle {
    my($self, $request, $resolver)=@_;

    ## Handle (satisfy) the request.

    if ($request->is('agent_request')) {
	return $self->PIA_AGENT::handle($request, $resolver);
    }

    print "redirecting request to " if  $main::debugging;
    print $request->url() . "\n" if  $main::debugging;

    my $ua = new LWP::UserAgent;
    my $response=$ua->simple_request($request); 
    $response=TRANSACTION->new($response);
    $response->to_machine($request->from_machine());

    $resolver->push($response);	# push the response transaction
}

1;
