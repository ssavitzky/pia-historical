package PIA::Transaction; ###### HTTP Transactions
###	$Id$
###
###	Transactions generalize the HTTP classes Request and Response.
###	They are used in the rule-based resolver, which associates 
###	transactions with the interested agents.
###
###	A Transaction has a queue of ``handlers'', which are called
###	(from the $transaction->satisfy() method) after all agents
###	have acted on it.  At least one must return true, otherwise
###	the transaction will ``satisfy'' itself.
###
###	In a proper implementation, Transaction would be a subclass of 
###	DS::Thing, and Response and Request would be subclasses of it.
###	It's done backwards here in order to re-use existing libraries.

use HTTP::Request;
use HTTP::Response;
use HTTP::Message;

push(@ISA,HTTP::Request);
push(@ISA,HTTP::Response);
push(@ISA,HTTP::Message);

use PIA::TFeatures;
use PIA::Content;

sub new {
    my($class, $self, $from, $to)=@_;

    ## take request or response and make a transaction out of it

    my $old=ref($self);
    bless $self, $class;
    $self->initialize_content;   #content now an object
    $self->is_request(1) if $old eq 'HTTP::Request';
    $self->is_response(1) if $old eq 'HTTP::Response';

    $$self{queue} = [];

    print "Making $self from $old\n" if $main::debugging;

    $self->from_machine($from);
    $self->to_machine($to);
    my $type=$self->method;
    my $code = $self->code;
    print "  type is $type\n"  if defined($type) && $main::debugging;    
    print "  code is $code\n"  if defined($code) && $main::debugging;    

    ## Initialize features and compute a few that we already know.

    new PIA::TFeatures $self;  # automatically points $self at it.

    ## get any parameters from url or post

    if($self->is_request()){
	$self->compute_form_parameters() if $type eq 'POST';
	$self->compute_form_parameters($self->url->query) if $type eq 'GET';
    }

    ## need to compute form parameters if encoded in url

    return $self;
}

##temporary treatment of content objects
# while transitioning to full objectivity...

sub initialize_content{
    my($self)=@_;
    #content source set in from_machine method
    $$self{_content_object}=PIA::Content->new;
    $self->content_object->string($$self{_content});
    
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
### Access to components

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

### Queue handlers.

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

###### $transaction->handle($request, $resolver)
###
###	A transaction can handle a request by pushing itself
###	onto the given resolver.  This allows agents to push
###	responses onto a *transaction* to be handled.  We return
###	success, indicating that the request has been satisfied.
###
sub handle {
    my ($self, $request, $resolver) = @_;

#    $resolver -> push($self);
    $resolver -> unshift($self);  ##deliver responses before processingmore request
    return 1;
}


###### $transaction->satisfy($resolver)
###
###	Calls $agent->handle($self, $resolver) for every agent in its
###	queue.  $agent is usually a reference to an agent.  Other 
###	possibilities include:
###
###	Transaction: `handle' simply pushes itself onto the resolver.
###	   This lets ``act_on'' agents push a satisfying transaction.
###
###	boolean:  simply returned.  This allows an ``act_on'' agent to 
###	   mark the transaction as satisfied by pushing a 1 onto it.
###
###	any other object with a ``handle'' method. 
###
#### if we had threads we would put this in another thread...
sub satisfy {
    my ($self, $resolver) = @_;
    my $satisfied = 0;

    foreach $agent (@{$self->queue()}) {
	if (ref($agent)) {
	    $satisfied = 1 if $agent->handle($self, $resolver);
	} else {
	    $satisfied = 1 if $agent;
	}
    }

    if (! $satisfied) {
	if ($self->is_response()) {
	    $self->send_response();
	} elsif ($self->is_request()) {
	    $resolver->push($self->get_request);
	}
    }
}


############################################################################
###
### Utilities to actually respond to a transaction, get a request, 
###	or generate (return) an error response transaction.
###

sub send_response {
    my($reply)=@_;
    my $machine=$reply->to_machine();

    if (ref($machine)) {
	print "sending response.\n" if $main::debugging;
	my $status=$machine->send_response($reply) if ref($machine);
	return $status;
    } else {
	## responses to nowhere just get dropped on the floor.
	print "dropping response to $machine\n" if $main::debugging;
	return;
    }
}

sub  get_request{
    my($self)=@_;

    ## Default handling for a request:
    ##	  ask the destination machine to get it.
    ##	  complain if there's no destination to ask.

    my $destination=$self->to_machine;
    return $self->error_response($destination) unless ref $destination;
    
    my $response=$destination->get_request($self);

    if (ref($response) ne /PIA::Transaction/) {
	## If the destination machine returned an ordinary HTTP::Response,
	##	make it into a proper transaction so it can get returned.
	$response=PIA::Transaction->new($response, $destination,
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

    return PIA::Transaction->new($response,
				 $main::this_machine,  # no to_machine
				 $self->from_machine());
}


###### transaction->read_content($type)
###
###	Read the content from the from_machine's stream.
###
###	=== This needs work: ===
###	 o  Must make sure it works with both requests and responses
###	 o  Must make sure it works OK if repeated! (idempotent)
###	 o  Must handle the case where the content is in a file.
###	 o  Should be able to fetch only the <HEAD> of an html file
###	 o  Must work if content_length was not provided.
###      o   actual IO should be done by machine
sub read_content{
    my($self,$type)=@_;
    
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

###### response->title()
###
###	Return the title of an HTML page, if it has one.
###	Returns the URL if the content-type is not HTML.
###
sub title {
    my($self)=@_;
    return unless $self->is_response();
    return $self->{'_title'} if defined $self->{'_title'};

    my $ttl  = $self->request->url();
    my $type = $self->content_type();
    return unless $type;
    return $ttl unless $type =~ m:text/html:;

    my $page = $self->content();

    if ($page =~ m:<title>(.*)</title>:ig) { $ttl = $1; }
    $self->{'_title'} = $ttl;
    return $ttl;
}

### add controls (buttons,icons,etc.)for agents to this response
# actual final form determine by machine
sub add_control{
    my($self,$text)=@_;
    $$self{_controls}=[] unless exists $$self{_controls};
    my $controls=$$self{_controls};
    push(@{$controls},$text);
}

sub controls{
    my($self,$argument)=@_;
    return @{$$self{_controls}};
    
}
###### response->add_at_front($text)
###
###	Add $text at the front of an HTML page, just after <body>
### deprecated...use controls
sub add_at_front {
    my($self,$argument)=@_;
    $self->add_control($argument);
}

# utility function to turn post content into hash

# unescape URL-encoded data
sub unescape {
    my($todecode) = @_;
    $todecode =~ tr/+/ /;	# pluses become spaces
    $todecode =~ s/%([0-9a-fA-F]{2})/pack("c",hex($1))/ge;
    return $todecode;
}


sub compute_form_parameters{
    my($self,$tosplit)=@_;
    $tosplit=$self->content() unless $tosplit;
    $self->deny('has_parameters');
    my(@pairs) = split('&',$tosplit);
    my($param,$value);
    my %hash;
    foreach (@pairs) {
	($param,$value) = split('=');
	$param = &unescape($param);
	$value = &unescape($value);
	$hash{$param}=$value; #careful losing multiple values
	print "  $param=$value.\n"  if $main::debugging;
	$self->assert('has_parameters');#if any times through
    }
    $$self{parameters}=\%hash;
    
}
sub parameters{
    my ($self,$parameters)=@_;
    $$self{parameters}=$parameters if defined $parameters;
    return $$self{parameters};
    
}

1;
