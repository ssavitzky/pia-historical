###### Class MACHINE -- Machine Model.
###	$Id$
###
###	Ideally these should be persistent so that we can keep track of what
###	kind of browser or server we're talking to, but at the moment we
###	don't do that.
###

package MACHINE;

sub new{
    my($class,$address,$port,$socket)=@_;
    my $self={};
    bless $self,$class;
    $$self{address}=$address;
    $$self{port}=$port;
    $self->stream($socket);
    
    return $self;
    
}
sub stream{
    my($self,$socket)=@_;
    $$self{stream}=$socket if defined $socket;
    return $$self{stream};
}
sub close_stream{
    my $self=shift;
    close($$self{stream});
    delete $$self{stream};
    
}

sub send_response{
    my($self,$reply)=@_;
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

    eval {
	## Use eval so we don't die if the browser closes the connnection.
	print {$output} $string;
	print {$output} $reply->headers_as_string();
	print {$output} "\n";
	print {$output} $reply->content;
	$self->close_stream;
    }
}

# TBD accommodate multiple schemes in cached value
sub proxy{
    my($self,$scheme,$proxy)=@_;
    #isaproxy necessary for talking to me?
    $$self{_proxy}=$proxy if $proxy;
    if(!$$self{_proxy}){
	$proxy=$main::agency->proxy_for($$self{address},$scheme);	
	$$self{_proxy}=$proxy if $proxy;
    }
    return $$self{_proxy};
    
}

sub get_request{
    my($self,$request)=@_;
    
    my $ua = new LWP::UserAgent;

    $ua->use_eval();

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

    # === should really use simple_request and handle redirect with agents.
#    my $response=$ua->request($self); 
    my $response=$ua->simple_request($request); 
    return $response;
    
}

1;
