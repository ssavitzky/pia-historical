
## routines for Maintaining cache

package PIA_AGENT::CACHE_AGENT;

push(@ISA,PIA_AGENT);

sub url_to_filename{
    my($self,$url)=@_;
    return unless $url;
    my $name=$url->host . $url->path;  ##paths should be full, with leading /
    return unless $name;
    return if length($name)>1024;  ##justincasedon'tuselongnames
    my $spool =$self->option('spool');
    $name=$spool . $name;
    return $name;
}
sub entry{
    my($self,$url,$directory)=@_;
    my $hash=$$self{'_entries'};
    my $key=$self->url_to_filename( $url);
    $$hash{$key}=$directory if $directory;
    return $$hash{$key};
}

sub create_directories{
    my($self,$name)=@_;
    my (@directories)=split("/",$name);
    
    my $path;
    for (@directories) {
	$path .= "/" . $_;
	return unless (-e $path || mkdir($path,0777));
    }
    return $path;
}

sub handle_response{
    my($self,$response)=@_;
    return unless $response->code eq '200';
    my $request=$response->request;
    return unless $request;
    my $url=$request->url;
    return unless $url;
    my $directory=$self->url_to_filename($url);
    return unless $directory;
    return unless $self->create_directories($directory);
    open(HEADER,">$directory/.header");
    print HEADER $response->headers_as_string;
    close HEADER;
    open(HEADER,">$directory/.content");
    print HEADER $response->content;
    close HEADER;
    print "saved $url to $directory \n"  if $main::debugging;
    $self->entry($url,$directory);
    open(HEADER,">$directory/.request-header");
    print HEADER $request->method . $url->as_string;
    print HEADER $request->headers_as_string;
    close HEADER;
    if($request->test('has_parameters')){
	my $form=$response->parameters;
	open(HEADER,">$directory/.request-parameters");
	for (keys(%{$form})) {print HEADER $_ . "=" . $$form{$_} . "\n";}
	close HEADER;
    }
    return;  #we don't satisfy responses
}

sub handle_request{
    my($self,$request,$resolver)=@_;
    my $directory=$self->url_to_filename($request->url);
    return unless -e "$directory/.content";
    
    return if $request->test('has_parameters');
    ##shouldcheck if same as before
    my $response=HTTP::Response->new(&HTTP::Status::RC_OK, "OK");
    open(HEADER,"<$directory/.header");
    while (<HEADER>){
	/^([^:]*):(.*)$/;
	($key,$value) = ($1,$2);
	$response->header($key,$value);
    }
    close HEADER;
    $response->header('Version',$self->version());
    return unless    open(CONTENT,"<$directory/.content");
    my ($dev,$ino,$mode,$nlink,$uid,$gid,$rdev,$size,
	$atime,$mtime,$ctime,$blksize,$blocks)
	= stat CONTENT;
    $response->content_length($size) unless $response->content_length;
    my $machine=AGENT_MACHINE->new($self);
    $machine->stream(*CONTENT);
    my $transaction=TRANSACTION->new($response,$machine,$request->from_machine);

    $resolver->push($transaction);
    return 1; ##request is satisfied

}

sub handle{
    my($self,$transaction,$resolver)=@_;
    return $self->handle_response($transaction,$resolver) if $transaction->is_response;
    return $self->handle_request($transaction,$resolver) if $transaction->is_request;
    return ;# should never get here
}

sub act_on{
    my($self,$transaction)=@_;
    $transaction->push($self) if $transaction->is_response;
    if($transaction->is_request) {
	$transaction->push($self) if ($self->entry($transaction->url));
    }
    return;
}
1;
