package PIA::Transaction; ###### HTTP Transactions
###	$Id$
###
###	Transaction is a wrapper for the library classes Request and Response.
###	It is used in the rule-based resolver, which associates 
###	transactions with the interested agents.
###
###	A Transaction has a queue of ``handlers'', which are called
###	(from the $transaction->satisfy() method) after all agents
###	have acted on it.  At least one must return true, otherwise
###	the transaction will ``satisfy'' itself.
###

use HTTP::Request;
use HTTP::Response;

use PIA::TFeatures;
use PIA::Content;
use DS::Thing;
push(@ISA, DS::Thing);


sub new {
    my($class, $t, $from, $to, $response)=@_;

    ## Make a request transaction.  $t may be:
    ##	  HTTP::Request -- make a request transaction
    ##	  PIA::Transaction -- make $response a response to $t.

    my $old=ref($t);
    my $self = {};
    $$self{queue} = [];

    bless $self, $class;

    if ($old eq 'PIA::Transaction') {
	$self->is_response(1);
	$self->response_to($t);
	print "Bogus response $response\n" unless ref($response) eq 'HTTP::Response';
	$response = new HTTP::Response unless $response;
	$self->response($response);
	$$self{_message} = $response;
    } else {
	print "Bogus request $t\n" unless ref($t) eq 'HTTP::Request';
	$self->is_request(1);
	$self->request($t);
	$$self{_message} = $t;
    }

    $self->url($t->url) if defined $t;
    $self->initialize_content;   #content now an object

    print "Making $self from $old\n" if $main::debugging;
    $self->from_machine($from);
    $self->to_machine($to);

    my $type = $self->method;
    my $code = $self->code;
    print "  type is $type\n"  if (defined($type) && $main::debugging);    
    print "  code is $code\n"  if (defined($code) && $main::debugging);    

    ## Initialize features and compute a few that we already know.

    new PIA::TFeatures $self;  # automatically points $self at it.

    ## get any parameters from url or post

    if($self->is_request()){
	$self->compute_form_parameters($self->content) if $type eq 'POST';
	$self->compute_form_parameters($self->url->equery) 
	    if ($type eq 'GET' && $self->url->equery);
	## === url->query dies if there are both + and %2b characters ===
    }

    ## need to compute form parameters if encoded in url
    return $self;
}

sub respond_with {
    my ($self, $response, $from, $to) = @_;

    ## Make a response Transaction out of a response.
    ##	 The recipient is the corresponding request.

    $from = $self->to_machine unless $from;
    $to = $self->from_machine unless $to;
    return PIA::Transaction->new($self, $from, $to, $response);
}


##temporary treatment of content objects
# while transitioning to full objectivity...

sub initialize_content{
    my($self)=@_;
    #content source set in from_machine method
    $$self{_content_object}=PIA::Content->new;
    $self->content_object->string($$self{_message}->{_content});
}

sub content_object{
    my($self)=@_;
    return $$self{_content_object};
}

#these for backwards compatibility
sub content {
    my($self,$string)=@_;
#    $self->read_content unless $self->content_object->is_at_end;
    return $self->content_object->as_string($string);
}


########################################################################
###
### Access to components:
###

sub from_machine{
    my($self,$machine)=@_;
    if( defined $machine){
	$$self{_from_machine}=$machine;
	$self->content_object->source($machine);
	$machine->slength($self->content_length);
    }
    return $$self{_from_machine};
}

sub to_machine{
    my($self,$machine)=@_;
    $$self{_to_machine}=$machine if defined $machine;
#if we are request, destination is given by url
    if(! $$self{_to_machine} && $self->is_request){
	my $host=$self->url->host if $self->url;
	$machine=PIA::Machine->new($host) if $host;
	$$self{_to_machine}=$machine;
    }
    return $$self{_to_machine};
}

sub request {
    my ($self, $value) = @_;

    ## an HTTP::Request object.  If this is a response, request points to the 
    ##	  request that this is a response _to_.  The corresponding 
    ##	 _transaction_ is in response_to.

    $$self{_request} = $value if defined $value;
    return $$self{_request};
}

sub response {
    my ($self, $value) = @_;

    ## an HTTP::Response object.

    $$self{_response} = $value if defined $value;
    return $$self{_response};
}

sub response_to {
    my ($self, $value) = @_;

    ## the PIA::Transaction object for the request to which this transaction
    ##	 is a response.

    if (defined $value) {
	$$self{_response_to} = $value;
	$self->url($value->url);
    }
    return $$self{_response_to};
}

sub url {
    my ($self, $value) = @_;
    return $self->attr('url', $value) if defined $value;
    return $$self{url};
}

sub method {
    my ($self) = @_;
    return uc $self->request->method if $self->request;
}

sub code {
    my ($self) = @_;
    return $self->response->code if $self->response;
}

sub content_length {
    my ($self) = @_;
    return $self->message->content_length;
}

sub content_type {
    my ($self) = @_;
    return $self->message->content_type;
}

sub message {
    my ($self, $v) = @_;
    $$self{_message} = $v if defined $v;
    return $$self{_message};
}

sub is_response{
    my($self,$argument)=@_;
    $$self{_response_Boolean}=$argument if defined $argument;
    return $$self{_response_Boolean};
}

sub is_request{
    my($self,$argument)=@_;
    $$self{_request_Boolean}=$argument if defined $argument;
    return $$self{_request_Boolean};
}

### Features

sub features {
    my($self, $features) = @_;
    $$self{_features} = $features if defined $features;
    return $$self{_features};
}

sub is {
    my ($self, $feature) = @_;
    return $self->features->test($feature, $self);
}

sub test {
    my ($self, $feature) = @_;
    return $self->features->test($feature, $self);
}

sub compute {
    my ($self, $feature) = @_;
    return $self->features->compute($feature, $self);
}

sub assert {
    my ($self, $feature, $value) = @_;
    $self->features->assert($feature, $value);
}

sub deny {
    my ($self, $feature) = @_;
    $self->features->deny($feature);
}

sub has {
    my ($self, $feature) = @_;
    $self->features->has($feature);
}

### Satisfier queue handlers.

sub queue{
    my($self)=@_;
    $$self{queue} = [] if !defined $$self{queue};
    return $$self{queue};
}

sub shift{
    my($self)=@_;
    return shift(@{$self->queue()});
}
sub unshift{
    my($self,$argument)=@_;
    return unshift(@{$self->queue()},$argument);
}
sub push{
    my($self,$argument)=@_;
    return push(@{$self->queue()},$argument);
}
sub pop{
    my($self)=@_;
    return pop(@{$self->queue()});
}

############################################################################
###
### Satisfying transactions:
###
###	A transaction can handle a request by pushing itself
###	onto the given resolver.  This allows agents to push
###	responses onto a *transaction* to be handled.  We return
###	success, indicating that the request has been satisfied.
###

sub handle {
    my ($self, $trans, $resolver) = @_;

    ## Satisfy _another_ transaction.

    $resolver -> unshift($self);  ## deliver responses before processing
				  ## more request
    return 1;
}


sub satisfy {
    my ($self, $resolver) = @_;

    ## ``Satisfy'' the transaction.
    ##	  Start by going through the queue of possible satisfiers.
    ##	  Transactions just push themselves on the resolver; booleans 
    ##	  satisfy if they're true.  Agents can do anything they like.

    my $satisfied = 0;
    foreach $agent (@{$self->queue()}) {
	if (ref($agent)) {
	    $satisfied = 1 if $agent->handle($self, $resolver);
	} else {
	    $satisfied = 1 if $agent;
	}
    }

    ## If still not satisfied, do the default thing:
    ##	  send a response or get a request.

    if (! $satisfied) {
	if ($self->is_response()) {
	    $self->send_response($resolver);
	} elsif ($self->is_request()) {
	    $resolver->push($self->get_request($resolver));
	}
    }
}


############################################################################
###
### Utilities to actually respond to a transaction, get a request, 
###	or generate (return) an error response transaction.
###
###	These pass the resolver down to the Machine that actually does the 
###	work, because it might belong to an agent.

sub send_response {
    my($self, $resolver)=@_;

    ## Default handling for a response:
    ##	  send it to the to_machine.  If the destination is not a reference 
    ##	  to a machine, the response just gets dropped.  'Nowhere' is a good
    ##	  non-reference to use in this case.

    my $machine=$self->to_machine();

    if (ref($machine)) {
	print "sending response.\n" if $main::debugging;
	return $machine->send_response($self, $resolver);
    } else {
	## responses to nowhere just get dropped on the floor.
	print "dropping response to $machine\n" if $main::debugging;
	return;
    }
}

sub  get_request{
    my($self, $resolver)=@_;

    ## Default handling for a request:
    ##	  ask the destination machine to get it.
    ##	  complain if there's no destination to ask.

    my $destination=$self->to_machine;
    return $self->error_response($destination) unless ref $destination;
    
    my $response=$destination->get_request($self, $resolver);

    if (ref($response) ne 'PIA::Transaction') {
	## If the destination machine returned an ordinary HTTP::Response,
	##	make it into a proper transaction so it can get returned.
	$response=$self->respond_with($response, $destination,
				      $self->from_machine);
    }
    return $response;	# push the response transaction
}

sub error_response{
    my($self, $message)=@_;

    ## Return a "not found" error for a request with no destination.

    my $response=HTTP::Response->new(&HTTP::Status::RC_NOT_FOUND, "not found");
    $response->content_type("text/plain");    
    my $url=$self->url()->as_string() if(ref($self->url));
    
    print "Sending error respose for $url\n" if $main::debugging;

    $response->content("Agency could not find $url.\n$message\n");

    return $self->respond_with($response,
			       $main::this_machine,  # no to_machine
			       $self->from_machine());
}


############################################################################
###
### Getting at content:
###

sub read_content{
    my($self,$type)=@_;

    ## === read_content appears not to be used anymore ===

    ## Read the content from the from_machine's stream.
    ##	=== This needs work: ===
    ##	 o  Must make sure it works with both requests and responses
    ##	 o  Must make sure it works OK if repeated! (idempotent)
    ##	 o  Must handle the case where the content is in a file.
    ##	 o  Should be able to fetch only the <HEAD> of an html file
    ##	 o  Must work if content_length was not provided.
    ##	 o   actual IO should be done by machine
    
    my $bytes=$self->content_length;
    my $flag=1 unless $bytes;
    
    my $content="";
    my $from=$self->from_machine();
    return unless defined $from;
    my $max=1024;

#add form proc hooks
    
    my $bytes_read=0;
	while($flag || ($bytes_read < $bytes)){
	    $max=$bytes-$bytes_read unless $flag;
	    my $new_bytes=$from->read_chunk(\$content,$max);
	    $self->content_object->push($content);
	    
	    $bytes_read+=$new_bytes;
	
	    last unless defined $new_bytes;
	}
    print "number of bytes = $bytes_read \n" if $main::debugging;
    
    return $self;
}    

sub title {
    my($self)=@_;

    ## Return the title of an HTML page, if it has one.
    ##	  Returns the URL if the content-type is not HTML.
    ##	  (now implemented as a feature)

    return $self->test('title');
}

sub add_control{
    my($self,$text)=@_;

    ## Add controls (buttons,icons,etc.) for agents to this response
    ##	 actual final form determined by machine

    $$self{_controls}=[] unless exists $$self{_controls};
    my $controls=$$self{_controls};
    push(@{$controls},$text);
}

sub controls{
    my($self,$argument)=@_;
    return @{$$self{_controls}};
}

### utility function to turn post content into hash:

# unescape URL-encoded data
sub unescape {
    my($todecode) = @_;
    $todecode =~ tr/+/ /;	# pluses become spaces
    $todecode =~ s/%([0-9a-fA-F]{2})/pack("c",hex($1))/ge;
    return $todecode;
}


sub compute_form_parameters{
    my($self,$tosplit)=@_;

    ## Split a urlencoded query string or content into pairs,
    ##	  and store the result as a DS::Thing (usable as a hash table)
    ##	  on $self's "parameters" attribute.

    $tosplit=$self->content() unless $tosplit;
    $self->deny('has_parameters');
    my(@pairs) = split('&',$tosplit);
    my($param,$value);
    my $hash = DS::Thing->new;
    foreach (@pairs) {
	($param,$value) = split('=');
	$param = &unescape($param);
	$value = &unescape($value);
	$hash->attr($param, $value); #careful losing multiple values
	print "  $param=$value.\n"  if $main::debugging;
	$self->assert('has_parameters');#if any times through
    }
    $$self{parameters}=$hash;
}

sub parameters{
    my ($self,$parameters)=@_;
    $$self{parameters}=$parameters if defined $parameters;
    return $$self{parameters};
}

1;
