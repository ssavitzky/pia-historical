package PIA_AGENT::SPY;

push(@ISA,PIA_AGENT);

# this is an agent that looks at incoming requests and either modifies the
# request or creates new requests
# requests and responses can be treated differently

sub initialize {
    my $self = shift;
#a hash to store function pointers
    $$self{'generators'}={};
#do superclass initialize
    &PIA_AGENT::initialize($self);
    
    return $self;
}


############################################################################

#handles are done by super class-- things like processing interform

# here  we translate requests by generators...
sub  new_requests{
    my($self, $request, $resolver)=@_;
    my @responses;
    my @generators=$self->generators($request);
 
    foreach $generator (@generators){
	push(@responses,&{$generator}($self,$request)) if $generator;
    }
    
    my $response;
    foreach $response (@responses) {
	$request->push($response);
    }
}

sub generators{
# return list of functions which want to process this request
    my($self,$request)=@_;
    my @generators;
    push(@generators,$self->generator($request->method())) if $request->is_request();
    push(@generators,$self->generator('Response')) if $request->is_response();
    return @generators;
}

sub generator{
#  return  and optionally set function to process this type of transaction
 # type is get, put, post, head, request( e.g. all of the above), response
#generator should take self and transaction as input, return array of  new transactions
    my($self,$type,$generator)=@_;
    my $generators=$$self{'generators'};
    $type=lc $type;
    $$generators{$type}=$generator if defined $generator;
    $generator=$$generators{$type} if exists $$generators{$type};
    if(! defined $generator){
	$generator=$$generators{'request'} if $type =~ /(get)|(put)|(post)|(head)/i;
    }
    print "found $generator generator for $type" if $main::debugging;
    
    return $generator;
}

1;
