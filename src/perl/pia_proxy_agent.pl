package PIA_AGENT::PROXY;

push(@ISA,PIA_AGENT);

# this is the agent that handles proxy requests 

sub initialize {
    my $self = shift;
    $self->match_criterion('is_request',1,\&FEATURES::is_request);
    $self->match_criterion('proxy_request',1,\&FEATURES::is_proxy_request);
    $self->match_criterion('agency_request',0,\&FEATURES::is_agency_request);
    return $self;
}


############################################################################

#handles are done by super class-- things like processing interform

# here  we translate proxy requests into responses
sub  new_requests{
    my($self,$request)=@_;

    print "redirecting request to" if  $main::debugging;
    print $request->url() if  $main::debugging;
    my @responses;
    
    my $ua = new LWP::UserAgent;
    my $response=$ua->simple_request($request); 
    $response=TRANSACTION->new($response);
    $response->to_machine($request->from_machine());
    push(@responses,$response);
    
    return @responses;
}

1;
