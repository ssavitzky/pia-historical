###### Class PIA_AGENT::DOFS -- Document-Oriented File System agent 
###	$Id$
###
package PIA_AGENT::DOFS;

push(@ISA,PIA_AGENT);

# this  agent maintains file systems
# 4 example instantiations of DOFS answer request for local files
# but they also do much more, for example caching, local annotations, etc.

sub initialize{
    my $self=shift;

    ## Overridden so we can set type.

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

sub retrieve_file {
    my($self, $url, $request, $resolver)=@_;

    print "DOFS generating response for $url\n" if $main::debugging;
    
#    return if $self->ignore($request);

#     my $wreck=$self->create_request('POST',"/$name/retrieve.if");
#     $response=TRANSACTION->new($response);
#     $response->to_machine($request->from_machine());
#     my $hash= $request->parameters() || {};
#     $$hash{'retrieve_item'}=$url;#could add $name to key
#     $response->parameters($hash);#putin the contents of the post
    
    my $filename=$self->url_to_filename($url);
    my $new_url= newlocal URI::URL $filename;
    my $new_request=$request->clone;
    print "DOFS looking up $new_url\n" if $main::debugging;
    $new_request->url($new_url);
    my $ua = new LWP::UserAgent;
    my $response=$ua->simple_request($new_request); 
    $response->header('Version', $self->version());
    $response=TRANSACTION->new($response);
    $response->to_machine($request->from_machine());

#replace base with appropriate
#warning violates encapsulation for efficiency
    my $base = $url->clone;
    unless ($base->epath =~ m|/$|) {
	$base->epath($base->epath . "/");
    }
    ## If the content is a directory, the UserAgent will have 
    ##	  given it a base.  Fix it.
    if ($response->{'_content'} =~ m:<BASE HREF:) {
	$response->{'_content'} =~ s/(<BASE HREF=\")([\S]*)(\">)/$1$url$3/; #";
    }
#    $response->{'_content'} =~ s/<BASE/<a/;
    print "base is <BASE HREF=$base> \n"  if $main::debugging;

    $resolver->push($response);	# push the response transaction

    return 1;
}

###### DOFS -> handle($transaction, $resolver)
###
###	Handle a DOFS request.  
###	Start by making sure the URL matches the agent's type;
###	   if it doesn't we can assume that act_on handled it.
###
sub handle{
    my($self, $request, $resolver)=@_;
    return 0 unless $request -> is_request();

    my $url = $request->url;
    my $path = ref($url) ? $url->path() : $url;
    my $type = $self->type();
    my $name = $self->name();
    my $response;

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

sub ignore{
#should we ignore this request?
    my($self,$request)=@_;
#for now ignore unless file exists
    my $filename=$self->url_to_filename($request->url);
    print "DOFS ignoring unless $filename exists\n" if $main::debugging;
    return ! -e $filename;
}

1;
