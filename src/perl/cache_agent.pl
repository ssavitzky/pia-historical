package PIA_AGENT::CACHE_AGENT; #######################################
###	$Id$
###
###	routines for maintaining a cache.
###

push(@ISA,PIA_AGENT);
use FileHandle;
sub url_to_filename{
    my($self,$url)=@_;

    ## Return the name of the file (directory) in which this URL is cached.

    return unless $url;
    my $name=$url->host . $url->path;  # paths should be full, with leading /
    return unless $name;
    return if length($name)>1024;      # just in case -- don't use long names
    my $spool = $self->option('spool');
    $name = $spool . $name;
    return $name;
}

sub filename_to_url{
    my($self,$directory)=@_;
    open(HEADER,"<$directory/.request-header");
    my $header=<HEADER>;
    $header =~ /^(\w*)\s+(.*)/;
    close HEADER;
    return $2;
    
}


sub quick_filename_to_url{
    my($self,$directory)=@_;
    my $spool = $self->option('spool');
    $directory =~ /$spool(.*)/;
    return "http://" . $1;
    
}

##maintain hash of directories that we know  about
# maybe should use url as key, not  directory name
## also should check modified date

### === Since the key is the directory, maybe the value should be the 
### === modified date. 

sub entry {
    my($self,$url,$directory)=@_;

    ## Look up or set a hash-table entry for a cached URL.

    my $hash=$$self{'_entries'};
    my $key=$self->url_to_filename( $url);
    if ($directory){
	$$hash{$key}=$directory ;
	print "adding $key $directory to cache\n" if $main::debugging;
	if($self->option('database')){
	    print {$self->option('database')} $key . " " . $directory . "\n";
#	    print " appended to file\n";
	}
    } elsif (! exists($$hash{$key})){
	## The following is supposed to update the hash table if
	## 	there's a cached file we haven't seen this session.

	## Without it, we check the cache once per session.
#	$$hash{$key}=$key if -e $key && ! $self->option('no_cache');
    }
    return $$hash{$key};
}

sub build_database{
    my($self,$database,$cache_root_directory,$entries)=@_;
    
    if(-e $database){
	my $status=open(DATABASE,"<$database");
	while(<DATABASE>){
	    my($key,$value)=split(" ");
	    $$entries{$key}=$value;
	    print "noticing $key $value from $_\n"  if $main::debugging;
	}
	close DATABASE;
	
    } else {
	my $status=open(DATABASE,">$database");
	my @files;
	push(@files,$cache_root_directory);
	while($file=shift(@files)){
	    print "Checking $file\n";
	    if(-d $file){
		$file.= "/" unless $file =~ /\/$/;
		push(@files,glob("$file*"));
	    }
	    if(-e "$file/.content"){
		chop($file) if $file =~ /\/$/;
		$$entries{$file}=$file;
		print DATABASE $file . " " . $file . "\n";
		
	    }
	}
	close DATABASE;
    }

    my $status=open(DATABASE,">>$database");
    autoflush DATABASE 1;
    
    $self->option('database',*DATABASE) if $status;
    
}
sub create_directories{
    my($self,$name)=@_;

    ## Create directory $name, plus any directories missing in the path to it.

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

    ## Handle a response.
    ##	Create a directory for it, and stash headers and content there.

    return unless $response->code eq '200';
    my $request=$response->request;
    return unless ref($request) eq 'TRANSACTION';
    my $url=$request->url;
    return unless $url;
    my $directory=$self->url_to_filename($url);
    return unless $directory;
    return unless $self->create_directories($directory);

    ## Cache the header.

    open(HEADER,">$directory/.header");
    print HEADER $response->headers_as_string;
    close HEADER;

    ## Cache the content.

    open(HEADER,">$directory/.content");
    print HEADER $response->content;
    close HEADER;

    ## Make an entry in the hash table so we can find it next time.

    print "saved $url to $directory \n"  if $main::debugging;
    $self->entry($url,$directory);

    ## Cache the request header.
    ##	 === at some point we may need to see whether it's different.
    ##	 some servers give different results for different browsers.

    open(HEADER,">$directory/.request-header");
    print HEADER $request->method . " " . $url->as_string . "\n";
    print HEADER $request->headers_as_string;
    close HEADER;
    if($request->test('has_parameters')){
	my $form=$response->parameters;
	open(HEADER,">$directory/.request-parameters");
	for (keys(%{$form})) {print HEADER $_ . "=" . $$form{$_} . "\n";}
	close HEADER;
    }

    $request->header('Cache-Location',$directory);
    return;  # we don't satisfy responses, just cache them.
}

sub handle_request{
    my($self,$request,$resolver)=@_;

    ## This is a request -- see if the cache directory exists.
    my $directory=$self->url_to_filename($request->url);
    print "cache handling request $directory "  if $main::debugging;
    return unless -e "$directory/.content";

    ## If there is a query string attached, give up.
    ##	  Should really check $directory/.request-parameters and see
    ##	  if it's the same as the last time.  Better yet, save all queries.
    return if $request->test('has_parameters');

    ## OK, we have it.  Cook up a response.

    my $response=HTTP::Response->new(&HTTP::Status::RC_OK, "OK");
    $response->request($request);
    
    open(HEADER,"<$directory/.header");
    while (<HEADER>){
	/^([^:]*): (.*)$/;
	($key,$value) = ($1,$2);
	$response->header($key,$value);
    }
    close HEADER;

    if(! ($self->option("check_frequency") eq 'never')){
	$request->assert('cache_response');
	my $date = $response->header('Last-Modified');
	$date = $response->header('Date') unless $date;
	$date = $response->header('Client-Date') unless $date;
	if(!$date){
	    my ($dev,$ino,$mode,$nlink,$uid,$gid,$rdev,$size,
		$atime,$mtime,$ctime,$blksize,$blocks)
		= stat("$directory/.content");
	    $date=HTTP::Date::time2str($mtime);
	}
	$request->header('If-Modified-Since',$date);
	my $newresponse=$main::main_resolver->simple_request($request);
	if($newresponse->code eq '200'){
	    $resolver->push($newresponse);
	    return 1; ##request is satisfied
	}
    }

    my $content=new FileHandle;
#    local *CONTENT;    
#$response->header('Version',$self->version());
    return unless    open($content,"<$directory/.content");
    $response->header('Cache-Location',$directory);
    my ($dev,$ino,$mode,$nlink,$uid,$gid,$rdev,$size,
	$atime,$mtime,$ctime,$blksize,$blocks)
	= stat $content;
    $response->content_length($size) unless $response->content_length;
    my $machine=AGENT_MACHINE->new($self);
    $machine->stream($content);
#    input_record_separator CONTENT undef;
#    my $string=<CONTENT>;
#    print $string;
#    $response->add_content($string);

    my $transaction=TRANSACTION->new($response,$machine,$request->from_machine);
    $transaction->assert('cache_response');
    $resolver->push($transaction);
    return 1; ##request is satisfied

}

sub handle{
    my($self,$transaction,$resolver)=@_;

    ## Dispatch request or response to the right handler.

    if ($transaction->is_request) {
	return $self->handle_request($transaction,$resolver); 
    } elsif ($transaction->is_response) {
	return $self->handle_response($transaction,$resolver);
    }
    return ;# should never get here
}

sub act_on{
    my($self,$transaction)=@_;

    ## Act on a transaction we've matched.

    if ($transaction->is_request) {
	## We only handle a request if we have a cache entry for it.
	print "looking for" . $transaction->url ." in cache\n" if $main::debugging;
	if ($self->entry($transaction->url)){
	    $transaction->push($self) ;
	    print "retrieving from cache\n";
	}
    } elsif ($transaction->is_response) {
	## Responses are _always_ handled, by caching.
	$transaction->push($self);
    }
    return;
}
1;
