#machine models
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
    print "sending response to $self\n";
    my $output=$self->stream();
    warn("nowhere to send") unless defined    $output;
    return unless defined    $output;
    
    my $string="HTTP/1.0 ";
    $string.=$reply->code;
    $string.=" ";
    $string.=$reply->message;
    $string.="\n";
    print $string;
    
    print {$output} $string;
#    print {$output} "Content-type: $ct\n\n" if defined $ct;
    print {$output} $reply->headers_as_string();
    print {$output} "\n";
    print {$output} $reply->content;
    $self->close_stream;
    
}
1;
