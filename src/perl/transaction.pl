###### class TRANSACTION
###	$Id$
###
###	Transactions generalize the HTTP classes Request and Response.
###	They are used in the rule-based resolver, which associates 
###	transactions with the interested agents.
###
###	A transaction has a queue of ``handlers'', which are called
###	(from the $transaction->satisfy() method) after all agents
###	have acted on it.  At least one must return true, otherwise
###	the transaction will ``satisfy'' itself.

package TRANSACTION;

push(@ISA,HTTP::Request);
push(@ISA,HTTP::Response);
push(@ISA,HTTP::Message);


###take request or response and make transaction
sub new {
    my($class,$request,$from,$to)=@_;
    my $old=ref($request);
    bless $request,$class;
    $request->is_request(1) if $old eq 'HTTP::Request';
    $request->is_response(1) if $old eq 'HTTP::Response';

    $$self{queue} = [];

    print "Making transaction from $old\n" if $main::debugging;

    $request->from_machine($from);
    $request->to_machine($to);
    my $type=$request->method;
    print "  type is $type\n"  if $main::debugging;    
    if(($type eq "POST" || $type eq "PUT") && $request->is_request()){
	$request->read_content($type);
	$request->compute_form_parameters() if $type eq 'POST';
    }
    return $request;
}



sub from_machine{
    my($self,$machine)=@_;
    $$self{_from_machine}=$machine if defined $machine;
    return $$self{_from_machine};
}

sub to_machine{
    my($self,$machine)=@_;
    $$self{_to_machine}=$machine if defined $machine;
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

    $resolver -> push($self);
    return 1;
}


###### $transaction->satisfy($resolver)
###
###	Calls $agent->handle($self, $resolver) for every agent in its
###	queue.  (If the agent is not a reference, it is taken as a
###	boolean result; this allows an ``act-on'' agent to mark the
###	transaction as satisfied by pushing a 1 onto it.
###
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
	    print "pushing error_response\n" if $main::debugging;
	    $resolver->push($self->error_response());
	}
    }
}


############################################################################
###
### Utilities to actually respond to a transaction, or generate
###	(return) an error response transaction.
###

sub send_response {
    my($reply)=@_;
    my $machine=$reply->to_machine();

    if (ref($machine)) {
	print "sending response.\n" if $main::debugging;
	my $status=$machine->send_response($reply) if ref($machine);
	return $status;
    } else {
	print "sending response to $machine\n" if $main::debugging;
	return;
    }
}

sub error_response{
    my($self)=@_;
    my $response=HTTP::Response->new(&HTTP::Status::RC_NOT_FOUND, "not found");
    $response->content_type("text/plain");    
    my $url=$self->url()->as_string();
    
    $response->content("Agency could not find $url\n");

    $response=TRANSACTION->new($response,
			       $main::this_machine,
			       $argument->from_machine());
    return $response;
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
###
sub read_content{
    my($self,$type)=@_;
    
    my $bytes=$self->content_length;
        
    my $content;
    my $from=$self->from_machine();
    return unless defined $from;
    
    my $input=$from->stream();
    return unless defined $input;
    my $bytes_read=0;
	while($bytes_read < $bytes){
	    my $new_bytes=read($input,$content,$bytes-$bytes_read,$bytes_read);
	    $bytes_read+=$new_bytes;
	
	    last unless defined $new_bytes;
	}
    print "number of bytes = $bytes_read \n" if $main::debugging;
    
    $self->content($content);

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

    $self->read_content();	# === hopefully this is idempotent!
				# === really ought to just read header.

    my $page = $self->content();

    if ($page =~ m:<title>(.*)</title>:ig) { $ttl = $1; }
    $self->{'_title'} = $ttl;
    return $ttl;
}

#utilityfunction to turn post content into hash

# unescape URL-encoded data
sub unescape {
    my($todecode) = @_;
    $todecode =~ tr/+/ /;	# pluses become spaces
    $todecode =~ s/%([0-9a-fA-F]{2})/pack("c",hex($1))/ge;
    return $todecode;
}


sub compute_form_parameters{
    my($self)=@_;
    my $tosplit=$self->content();

    my(@pairs) = split('&',$tosplit);
    my($param,$value);
    my %hash;
    foreach (@pairs) {
	($param,$value) = split('=');
	$param = &unescape($param);
	$value = &unescape($value);
	$hash{$param}=$value; #careful losing multiple values
	print "  $param=$value.\n"  if $main::debugging;
    }
    $$self{parameters}=\%hash;
    
}
sub parameters{
    my ($self,$parameters)=@_;
    $$self{parameters}=$parameters if defined $parameters;
    return $$self{parameters};
    
}

1;
