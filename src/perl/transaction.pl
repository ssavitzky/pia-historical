#transactions

package TRANSACTION;

push(@ISA,HTTP::Request);
push(@ISA,HTTP::Response);
push(@ISA,HTTP::Message);


#take request or response andmake transaction
sub new{
    my($class,$request,$from,$to)=@_;
    my $old=ref($request);
    bless $request,$class;
    $request->is_request(1) if $old eq 'HTTP::Request';
    $request->is_response(1) if $old eq 'HTTP::Response';

    print "request is $old\n";

    $request->from_machine($from);
    $request->to_machine($to);
    my $type=$request->method;
print " typeis $type\n";    
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

sub read_content{
    my($self,$type)=@_;
	my $bytes=$self->header(content_length);
	my $content;
    my $input=$self->from_machine()->stream();
    
	my $bytes_read=0;
	while($bytes_read < $bytes){
	    my $new_bytes=read($input,$content,$bytes-$bytes_read,$bytes_read);
	    $bytes_read+=$new_bytes;
	    
	    last unless defined $new_bytes;
	}
  #  print "number of bytes = $bytes_read \n";
    
    $self->content($content);

    return $self;
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
print "computing form parameters\n $tosplit\n";    
    my(@pairs) = split('&',$tosplit);
    my($param,$value);
    my %hash;
    foreach (@pairs) {
	($param,$value) = split('=');
	$param = &unescape($param);
	$value = &unescape($value);
	$hash{$param}=$value; #careful losing multiple values
	print "$param as value $value \n";
    }
    $$self{parameters}=\%hash;
    
}
sub parameters{
    my $self=shift;
    return $$self{parameters};
    
}

1;
