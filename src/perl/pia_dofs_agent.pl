package PIA_AGENT::DOFS;

push(@ISA,PIA_AGENT);

# this  agent maintains file systems
# 4 example instantiations of DOFS answer request for local files
# but they also do much more, for example caching, local annotations, etc.


############################################################################

#handles are done by super class-- things like processing interform

# here  we translate requests into  requests for  myself with appropriate path
#actually translate into responses... using requests could work, but reduces
# efficiency and confuses response...

#TBD figure  out proper way to specify machine

sub  new_requests{
    my($self,$request)=@_;
    my  $url=$request->url();
    my @responses;
    my $name=$self->name;
    print "DOFS generating  new requests for $url" if $main::debugging;
    
    return if $self->ignore($request);
#     my $wreck=$self->create_request('POST',"/$name/retrieve.if");
#     $response=TRANSACTION->new($response);
#     $response->to_machine($request->from_machine());
#     my $hash= $request->parameters() || {};
#     $$hash{'retrieve_item'}=$url;#could add $name to key
#     $response->parameters($hash);#putin the contents of the post
    my $filename=$self->url_to_filename($url);
    
    my $new_url= newlocal URI::URL $filename;
    my $new_request=$request->clone;
    print "DOFS lookingup $new_url\n" if $main::debugging;
    $new_request->url($new_url);
    my $ua = new LWP::UserAgent;
    my $response=$ua->simple_request($new_request); 
    $response=TRANSACTION->new($response);
    $response->to_machine($request->from_machine());
#replace base with appropriate
#warning violates encapsulation for efficiency
    my $base = $url->clone;
    unless ($base->epath =~ m|/$|) {
	$base->epath($base->epath . "/");
    }
	#Specifying a base just screws us up....
    $response->{'_content'} =~ s/<BASE HREF=\"[\S]*\">//;
#    $response->{'_content'} =~ s/<BASE/<a/;
    print "baseis <BASE HREF=$base> \n"  if $main::debugging;
    push(@responses,$response);
    
    return @responses;
}

sub url_to_filename{
#returns the file name corresponding to this url
# returns undefined unless url path begins with prefix
    my($self,$url)=@_;
    my $document_root=$self->option('document_root');
    return  unless $url;
    my $path=$url->path;
    my $prefix = $self->option('path_prefix');
    return unless $path=~ /^$prefix(.*)$/;
    my $filename="$document_root$prefix$1";
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
