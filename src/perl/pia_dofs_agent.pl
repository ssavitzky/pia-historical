###### Class PIA_AGENT::DOFS -- Document-Oriented File System agent 
###	$Id$
###
###	The DOFS agent provides access to local files through the
###	Agency.  (It will eventually be possible to overlay multiple
###	directories, supply local annotations, and to access URL's as
###	well.) 

package PIA_AGENT::DOFS;

push(@ISA,PIA_AGENT);

sub initialize{
    my $self=shift;

    ## Overridden so we can set $type.

    my $name=$self->name;
    my $type='dofs';
    $self->type($type);

    $self->match_criterion('NEVER'); # Requests forwarded by agency

    my $url="/$type/$name/initialize.if";
    my $request=$self->create_request('GET',$url);
    $self->request($request);
}

sub root {
    my ($self, $value) = @_;
    return $self->file_attribute('root', $value);
}

sub path {
    my ($self, $value) = @_;

}


############################################################################
### 
### Transaction handling.
###
### === We need to be able to handle CGI scripts and plain HTML
###	eventually.  We need this for the DOFS, in particular.
###
### === URL-to-filetype mappings, whether interforms are permitted,
###	local interforms, and similar annotations belong in the
###	interform directory that corresponds to the DOFS agent.

sub retrieve_file {
    my($self, $url, $request, $resolver)=@_;

    ## Retrieve the file at $url in order to satisfy $request.

    print "DOFS generating response for $url\n" if $main::debugging;
    
#    return if $self->ignore($request);

#     my $wreck=$self->create_request('POST',"/$name/retrieve.if");
#     $response=TRANSACTION->new($response);
#     $response->to_machine($request->from_machine());
#     my $hash= $request->parameters() || {};
#     $$hash{'retrieve_item'}=$url;#could add $name to key
#     $response->parameters($hash);#putin the contents of the post
    
    my $filename=$self->url_to_filename($url);
    my $response;

    if (-d $filename) {
	$response = $self->retrieve_directory($filename, $request);
    } else {
	my $new_url= newlocal URI::URL $filename;
	my $new_request=$request->clone;
	print "DOFS looking up $new_url\n" if $main::debugging;
	$new_request->url($new_url);
	my $ua = new LWP::UserAgent;
	$response=$ua->simple_request($new_request); 

	## Show the original request, not the redirected one.
	$response->request($request->clone);

	## If the content is a directory, the UserAgent will have 
	##	  given it a base.  Fix it.
	if ($response->{'_content'} =~ m:<BASE HREF:) {
	    $response->{'_content'} =~ s/(<BASE HREF=\")([\S]*)(\">)/$1$url$3/; #";
	}
    }

    $response->header('Version', $self->version());
    $response=TRANSACTION->new($response);
    $response->to_machine($request->from_machine());

    $resolver->push($response);	# push the response transaction

    return 1;
}


sub retrieve_directory {
    my($self, $path, $request)=@_;

    ## generate the HTML for a local directory.
    ##	  Stolen from file.pm.

    unless (-e $path) {
	return new HTTP::Response &HTTP::Status::RC_NOT_FOUND,
				  "File `$path' does not exist";
    }
    unless (-r _) {
	return new HTTP::Response &HTTP::Status::RC_FORBIDDEN,
				  'User does not have read permission';
    }

    my($dev,$ino,$mode,$nlink,$uid,$gid,$rdev,$size,
       $atime,$mtime,$ctime,$blksize,$blocks)
	    = stat(_);

    # XXX should check Accept headers?

    # check if-modified-since
    my $ims = $request->header('If-Modified-Since');
    if (defined $ims) {
	my $time = HTTP::Date::str2time($ims);
	if (defined $time and $time >= $mtime) {
	    return new HTTP::Response &HTTP::Status::RC_NOT_MODIFIED,
				      "$method $path";
	}
    }

    # Ok, should be an OK response by now...
    my $response = new HTTP::Response &HTTP::Status::RC_OK;

    # fill in response headers
    $response->header('Last-Modified', HTTP::Date::time2str($mtime));

    opendir(D, $path) or
	return new HTTP::Response &HTTP::Status::RC_INTERNAL_SERVER_ERROR,
	"Cannot read directory '$path': $!";
    my(@files) = sort readdir(D);
    closedir(D);
    my @urls;
    my $head;
    my $all = $self->attribute('all');

    # Make directory listing
    for (@files) {
	$_ .= "/" if -d "$path/$_";
	if ($_ =~ /^HEADER.*[^~]$/) { $head = suck_body("$path/$_"); }
	push @urls, qq{<LI> <a href="$_">$_</a>} unless 
	    ! $all && $self->ignore_file($_, "$path$_");
    }
    # Ensure that the base URL is "/" terminated
    my $base = $request->url->clone;
    unless ($base->epath =~ m|/$|) {
	$base->epath($base->epath . "/");
    }
    my $html = join("\n",
		    "<HTML>\n<HEAD>",
		    "<TITLE>DOFS Directory $base</TITLE>",
		 #   "<BASE HREF=\"$base\">",
		    "</HEAD>\n<BODY>",
		    $head || "<H1>Directory listing of $base</H1>",
		    "<h3>local path: $path</h3>",
		    "<h3>DOFS path: $base</h3>",
		    "<UL>", @urls, "</UL>",
		    "</BODY>\n</HTML>\n");

    $response->header('Content-Type',   'text/html');
    $response->header('Content-Length', length $html);
    $response->content($html);
    #return $request->collect_once($arg, $response, $html);
    return $response;
}


###### DOFS -> handle($transaction, $resolver)
###
###	Handle a DOFS request.  
###
sub handle{
    my($self, $request, $resolver)=@_;
    return 0 unless $request -> is_request();

    my $url = $request->url;
    my $path = ref($url) ? $url->path() : $url;
    my $type = $self->type();
    my $name = $self->name();
    my $response;

    ## Examine the path to see what we have:
    ##    $name/path  -- this is a real file request.
    ##	  $name       -- home page InterForm
    ##	  $type/$name -- Interforms for $name
    ##	  $type/path  -- Interforms for DOFS

    if ($name ne $type && $path =~ m:^/$name/:) {
	return $self->retrieve_file($url, $request, $resolver);
    } elsif ($name ne $type && $path =~ m:^/$name$:) {
	$path = "/$name/home.if";
    } elsif ($path =~ m:^/$name/([^/]+)/:) {
	$type = $1;
	my $agent = $resolver->agent($type);
	$path =~ s:^/$type:: if defined $agent;
	$self = $agent if defined $agent;
    }

    $response = $self->respond_to_interform($request,$path);
    return 0 unless defined $response;
    $resolver->push($response);
    return 1;
}


sub url_to_filename{
#returns the file name corresponding to this url
# returns undefined unless url path begins with prefix
    my($self,$url)=@_;
    my $root=$self->root;
    return  unless $url;
    my $path=$url->path;
    my $prefix = $self->name;
    return unless $path=~ m:^/$prefix(.*)$:;
    my $filename="$root$1"; # bogus $prefix removed from path.
    return $filename;
    
}

sub ignore_file {
    my ($self, $filename, $path) = @_;
    return 1 if $filename =~ m/~$/;
    return 1 if $filename =~ m/^#/;
    return 1 if $filename eq './';
    return 1 if $filename eq 'CVS/';
    return 1 if $filename eq 'RCS/';
    return 0;	
}

sub suck_body {
    my ($fn, $str) = @_;

    open(FILE, "<$fn");
    while (<FILE>) {
	$str .= $_;
    }
    close(FILE);
    $str =~ s:<head.*</head>::i;
    $str =~ s:<html>::i;
    $str =~ s:</html>::i;
    return $str;
}


sub ignore{
#should we ignore this request?
    my($self,$request)=@_;
#for now ignore unless file exists
    my $filename=$self->url_to_filename($request->url);
    print "DOFS ignoring unless $filename exists\n" if $main::debugging;
    return ! -e $filename;
}

1;
