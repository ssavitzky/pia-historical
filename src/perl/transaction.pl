###### class TRANSACTION
###	$Id$
###
###	Transactions generalize the HTTP classes Request and Response.
###	They are used in the rule-based resolver, which associates 
###	transactions with the interested agents.

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
