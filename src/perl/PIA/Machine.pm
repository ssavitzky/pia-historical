package PIA::Machine; ###### Machine Model.
###	$Id$
###
###	Ideally these should be persistent so that we can keep track of what
###	kind of browser or server we're talking to, but at the moment we
###	don't do that.
###

$main::timeout=50;  #measured in seconds...this is a global var
##maybe should move to pia.pl  use agency/timeout2.if to double...

sub new {
    my($class,$address,$port,$socket)=@_;
    my $self={};
    bless $self,$class;
    $$self{address}=$address;
    $$self{port}=$port;
    $self->stream($socket);
    $$self{_bytes_read}=0;
    return $self;
}
sub stream {
    my($self,$socket)=@_;
    $$self{stream}=$socket if defined $socket;
    print (ref($$self{stream})) . "stream ref\n"  if $main::debugging;
    return $$self{stream};
}
sub close_stream{
    my $self=shift;
    close($$self{stream});
    delete $$self{stream};
    
}

sub slength{
    my($self,$argument)=@_;
    $$self{_stream_length}=$argument if $argument;
    print "stream length $argument"  if $main::debugging;
    
    return $$self{_stream_length};
}

sub has_more_data{
    my($self,$argument)=@_;
    return unless exists $$self{stream};
    my $remaining=$$self{_stream_length} - $$self{_bytes_read};
    return $remaining;
    
}

$chunksize=1024; #default bytes to be read in one chunk
sub read_chunk{
    my($self,$content)=@_;

    ## Get a chunk of data from an input stream.

    my $offset=0 unless $offset;
    my $foo;
    my $input=$self->stream;
    return unless $input;
    ### eventually be more efficient in use of content string
    my $max=$chunksize;
    $max=$$self{_stream_length} - $$self{_bytes_read} if $$self{_stream_length};
    my $bytes = read($input,$foo,$max,$offset);
 
    $$self{_bytes_read} += $bytes;
    print "read $bytes bytes\n" if $main::debugging;
 
    $content->push($foo);
    return $bytes;
}


sub send_response{
    my($self, $trans, $resolver)=@_;

    ## Send a response to whatever this machine refers to.
    ##	  $resolver is unused; it's here for subclasses (i.e. agents)

    print "sending response to $self\n" if  $main::debugging;
    my $output=$self->stream();
    my $reply = $trans->response;

    if (! defined $output) {
	warn("nowhere to send") if $main::debugging;
	print $reply->content if $main::debugging > 1;
	return;
    }
    
    my $string="HTTP/1.0 " . $reply->code . " " . $reply->message . "\n";
    print $string  if $main::debugging;
    my ($control, $content);
    $control=join(" ",$trans->controls) 
	if $reply->content_type =~ /text\/html/i;

    if ($control) {
	### === should treat content as a stream unless we have to ===
	$content = $trans->content;
	$reply->content_length($reply->content_length + length($control))
	    if $reply->content_length;
    }
    
    print $reply->headers_as_string ."\n"  if $main::debugging;
##What to do if connection dies?
    # this doesn't work...
  #  local $SIG{PIPE}=sub {$abort = 1; die 'connection closed';};

    eval {
	local $SIG{PIPE}=sub { print 'connection closed';};
	## never seems to print.  die, however, bombs completely.
	## Use eval so we don't die if the browser closes the connnection.
	print {$output} $string;
	print {$output} $reply->headers_as_string;
	print {$output} "\n";

##Temporary because hooks don't work
	if ($control && $content =~ m/\<body[^>]*\>/is) {
	    $content =~ s/(\<body[^>]*\>)/$1$control/is;
	} else {
	    print {$output} $control if $control;
	}
	print "sent controls $control" if $control && $main::debugging;
##$reply->content_object->add_hook(sub { shift =~ s/\<body[^>]*\>/$controls/i});
	print {$output} ($control? $content : $reply->content);
	$self->close_stream;
    };
    warn $@ if $@;

}

# TBD accommodate multiple schemes in cached value
sub proxy {
    my($self,$scheme,$proxy)=@_;
    #isaproxy necessary for talking to me?
    $$self{_proxy}=$proxy if $proxy;
    if(!$$self{_proxy}){
	$proxy=$main::agency->proxy_for($$self{address},$scheme);	
	$$self{_proxy}=$proxy if $proxy;
    }
    return $$self{_proxy};
    
}

$user_agent_id;			# cache for library's user agent ID string.

sub get_request {
    my($self, $trans, $resolver)=@_;

    ## Pass a request transaction on to whatever this machine refers to.
    ##	  $resolver is unused; it's here for subclasses (i.e. agents)

    if(!$ua) {
	$ua = new LWP::UserAgent;
	$user_agent_id=$ua->agent;

	$ua->use_eval(1);
	$ua->use_alarm(1);
	$ua->env_proxy();
	$ua->timeout($main::timeout) if $main::timeout; #testing 
###Configuration --is proxy necessary?
### Should Be careful not to proxy through ourselves
	my $proxy=$self->proxy($trans->url->scheme);
    
#### if agency returns negative number, generate error
###    network unavailable, or denied

	if ($proxy < 0) {
	    return $trans->error_response("negative proxy specified: network available?");
	}
	print "getting request" . $trans->url . " through $proxy \n" . 
	    $trans->request->headers_as_string() . "\n" if $main::debugging;

	#$ua->proxy($trans->url->scheme,$proxy) if $proxy;
    }

    $ua->agent($trans->request->user_agent . ' PIA/1.0 ' . $user_agent_id);

    ## Set request content if we're posting.
    my $content = $trans->content;
    if ($content) {
	$trans->request->content($content) unless $trans->request->content; 
	print "Sending request with content '$content'\n" unless $main::quiet;
	print "content_length doesn't match\n" unless 
	    $trans->request->content_length == length $content;
	print $trans->request->url->as_string . "\n" unless $main::quiet;
	print $trans->request->headers_as_string . "\n" . $trans->request->content . "\n" unless $main::quiet;
    }

    ## Actually make the request.
    ##	  We _must_ use simple_request and pass the results, whatever they
    ##	  are, to the browser.  Otherwise it never finds out about redirects.

    my $response=$ua->simple_request($trans->request); 
    if ($content && ! $main::quiet) {
	print "Response: \n" . $response->headers_as_string; 
    }

    return $trans->respond_with($response);
}

1;
