package PIA::Agent::cache; #######################################
###	$Id$
###
###	routines for maintaining a cache.
###

push(@ISA,PIA::Agent);
use FileHandle;

### === spool directory really needs to be agent_directory/$protocol

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


############################################################################
###
### Maintaining the cache:
###

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

sub create_directories {
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


############################################################################
###
### Satisfying transactions:
###

sub handle_response{
    my($self,$response)=@_;

    ## Handle a response.
    ##	Create a directory for it, and stash headers and content there.

    return unless $response->code eq '200';
    my $url=$response->url;
    return unless $url;
    my $directory=$self->url_to_filename($url);
    return unless $directory;
    return unless $self->create_directories($directory);

    ## Get the content.
    ##	  Insist on its being non-empty.

    my $content = $response->content;
    return unless $content;

    ## Cache the header.

    open(FILE,">$directory/.header");
    print FILE $response->response->headers_as_string;
    close FILE;

    ## Cache the content.

    open(FILE,">$directory/.content");
    print FILE $content;
    close FILE;

    ## Make an entry in the hash table so we can find it next time.

    print "saved $url to $directory \n"  if $main::debugging;
    $self->entry($url,$directory);

    ## Cache the request header.
    ##	 === at some point we may need to see whether it's different.
    ##	 some servers give different results for different browsers.

    my $request = $response->respond-to;

    open(FILE,">$directory/.request-header");
    print FILE $request->method . " " . $url->as_string . "\n";
    print FILE $request->request->headers_as_string;
    close FILE;
    if($request->test('has_parameters')){
	my $form=$request->parameters;
	open(FILE,">$directory/.request-parameters");
	for (keys(%{$form})) {print FILE $_ . "=" . $$form{$_} . "\n";}
	close FILE;
    }

    $request->request->header('Cache-Location',$directory);
    return;  # we don't satisfy responses, just cache them.
}

sub handle_request{
    my($self,$request,$resolver)=@_;

    ## This is a request -- see if the cache directory exists.

    my $directory=$self->url_to_filename($request->url);
    print "cache handling request $directory "  if $main::debugging;
    return unless -e "$directory/.content";

    ## If there is a query string attached, give up.
    ##	  Should really check $directory/.request-parameters and see ===
    ##	  if it's the same as the last time.  Better yet, save all queries.
    return if $request->test('has_parameters');

    ## OK, we have it.  Cook up a response.

    my $response=HTTP::Response->new(&HTTP::Status::RC_OK, "OK");
    
    open(FILE,"<$directory/.header");
    while (<FILE>){
	/^([^:]*): (.*)$/;
	($key,$value) = ($1,$2);
	$response->header($key,$value);
    }
    close FILE;

    if(! ($self->option("check_frequency") eq 'never')){
	my $date = $response->header('Last-Modified');
	$date = $response->header('Date') unless $date;
	$date = $response->header('Client-Date') unless $date;
	if(!$date){
	    my ($dev,$ino,$mode,$nlink,$uid,$gid,$rdev,$size,
		$atime,$mtime,$ctime,$blksize,$blocks)
		= stat("$directory/.content");
	    $date=HTTP::Date::time2str($mtime);
	}
	$request->request->header('If-Modified-Since',$date);
	my $newresponse=$main::resolver->simple_request($request->request);
	if($newresponse->code eq '200'){
	    my $transaction=$request->respond_with($newresponse,$machine,
						   $request->from_machine);
	    $transaction->assert('cache_response');
	    $resolver->push($transaction);
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
    my $machine=PIA::Agent::Machine->new($self);
    $machine->stream($content);
#    input_record_separator CONTENT undef;
#    my $string=<CONTENT>;
#    print $string;
#    $response->add_content($string);

    my $transaction=$request->respond_with($response,$machine,
					   $request->from_machine);
    $transaction->assert('cache_response');
    $resolver->push($transaction);
    return 1; ##request is satisfied

}

sub handle {
    my($self,$transaction,$resolver)=@_;

    ## Dispatch request or response to the right handler.

    if ($transaction->is_request) {
	return $self->handle_request($transaction,$resolver); 
    } elsif ($transaction->is_response) {
	return $self->handle_response($transaction,$resolver);
    }
    return ;# should never get here
}

sub act_on {
    my($self,$transaction)=@_;

    ## Act on a transaction we've matched.

    if ($transaction->is_request) {
	## We only handle a request if we have a cache entry for it.
	print "looking for" . $transaction->url ." in cache\n" 
	    if $main::debugging;
	if ($self->entry($transaction->url)){
	    $transaction->push($self) ;
	    print "retrieving from cache\n" if $main::debugging;
	}
    } elsif ($transaction->is_response) {
	## Responses are _always_ handled (but never satisfied), by caching.
	$transaction->push($self);
    }
    return;
}
1;
