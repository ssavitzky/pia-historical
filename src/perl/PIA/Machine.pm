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
#getdata
$chunksize=1; #defaulttoread
sub read_chunk{
    my($self,$content)=@_;
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
    my($self, $reply, $resolver)=@_;

    ## Send a response to whatever this machine refers to.
    ##	  $resolver is unused; it's here for subclasses (i.e. agents)

    print "sending response to $self\n" if  $main::debugging;
    my $output=$self->stream();

    if (! defined $output) {
	warn("nowhere to send") if $main::debugging;
	print $reply->content if $main::debugging > 1;
	return;
    }
    
    my $string="HTTP/1.0 ";
    $string.=$reply->code;
    $string.=" ";
    $string.=$reply->message;
    $string.="\n";
    print $string  if $main::debugging;
    my $control;
    $control=join(" ",$reply->controls)	if $reply->content_type =~ /text/;
    $reply->content_length($reply->content_length + length($control)) if $control;
    
    print "content type ". $reply->content_type . "\n"  if $main::debugging;
    	print $reply->headers_as_string() ."\n"  if $main::debugging;
##What to do if connection dies?
    # this doesn't work...
  #  local $SIG{PIPE}=sub {$abort = 1; die 'connection closed';};

    eval {
	local $SIG{PIPE}=sub { print 'connection closed';};
	## never seems to print.  die, however, bombs completely.
	## Use eval so we don't die if the browser closes the connnection.
	print {$output} $string;
	print {$output} $reply->headers_as_string();
	print {$output} "\n";
##Temporary

	
	print {$output} $control if $control;
	print "sent controls $control" if $control && $main::debugging;
	##$reply->content_object->add_hook(sub { shift =~ s/<body>/$controls/i});
	print {$output} $reply->content;
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

sub get_request {
    my($self, $request, $resolver)=@_;

    ## Pass a request on to whatever this machine refers to.
    ##	  $resolver is unused; it's here for subclasses (i.e. agents)

    if(!$ua) {
	$ua = new LWP::UserAgent;

	$ua->use_eval(1);
	$ua->use_alarm(1);
	$ua->timeout($main::timeout) if $main::timeout; #testing 
###Configuration --is proxy necessary?
### Should Be careful not to proxy through ourselves
	my $proxy=$self->proxy($request->url->scheme);
    
    
#### if agency returns negative number, generate error
###    network unavailable, or denied

	if ($proxy < 0) {
	    return $request->error_response("negative proxy specified: network available?");
	}
	print "getting request" . $request->url . " through $proxy \n" . $request->headers_as_string() . "\n" if $main::debugging;
	

	$ua->proxy($request->url->scheme,$proxy) if $proxy;
    }
    # === should really use simple_request and handle redirect with agents.
#    my $response=$ua->request($self); 
    my $response=$ua->simple_request($request); 
    return $response;
    
}

1;
