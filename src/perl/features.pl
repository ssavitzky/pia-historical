 # class for computing features of transactions
 # this class should evolve as transaction model evolves


package FEATURES;

#true if content type is text/*
sub is_text{
    my($request)=shift;
    return $request->content_type() =~ /^text/i;
    
}

sub is_html{
    my($request)=shift;
    return $request->content_type() =~ /^text\/html/i;
    
}

sub is_image{
    my($request)=shift;
    return $request->content_type() =~ /^image/i;
    
}

sub is_local_source{
    my $request=shift;
    ##need to fill transaction model first
}

sub is_response{
    my $request=shift;
    return $request->is_response;
    
}

sub is_request{
    my $request=shift;
    return $request->is_request;
    
}


sub client_is_netscape{
    my($request)=shift;
    return $request->header()->header('User-Agent') =~ /netscape/i;
}

sub  is_agency_request{
    my $request=shift;
    my $url=$request->url;
    return 0 unless defined $url;
    
    my $host=$url->host;

    return 1 if ($host=~/^agency/);
    return 1 if ($url->port == $main::PIA_PORT && $main::PIA_HOST =~ /^$host/);

    #return 1 if ($url->path=~/^\/agency/); # dubious.  What about other hosts?
    return 0;
}

sub is_agent_response{
    my $request=shift;
    my $agent=$request->header('Version');
    return $agent =~ /^PIA/i;
}

sub is_proxy_request{
    my $request=shift;
    my $url=$request->url;
    return 0 unless $url;
    
    my $host=$url->host;

    return 0 if ($host =~ /^agency/);
    return 0 if ($url->port == $main::PIA_PORT && $main::PIA_HOST =~ /^$host/);

    return 1 if $host;
    return 0;
}

sub is_agent_request{
    my $request=shift;
    return is_request($request) && !is_proxy_request($request);
}

sub is_file_request{
    my $request=shift;
    my $url=$request->url;
    my $scheme=$url->scheme;
    return $scheme=~/file/i;
    
}

sub is_interform{
   my $request=shift;
    my $url=$request->url;
 
   my $path=$url->path;
   return $path=~/.if$/i;
 
}


1;
