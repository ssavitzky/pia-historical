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
    
    print {$output} $string;
#    print {$output} "Content-type: $ct\n\n" if defined $ct;
    print {$output} $reply->headers_as_string();
    print {$output} "\n";
    print {$output} $reply->content;
    $self->close_stream;
    
}
1;
