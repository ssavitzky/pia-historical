package PIA::TFeatures; ###### Features of Transactions.
###	$Id$
###
###	A TFeatures object is attached to a transaction when it is
###	first noticed by the Resolver.  It is used to perform lazy
###	evaluation of named features.  The association between names
###	and functions is made using a hash: %TFeatures::computers.  
###
###	Features may actually have any value, but the resolver is only
###	interested in truth value.  Some agents, however, make use of
###	feature values.  In particular, 'agent' binds to the name of the
###	agent at which a request is directed.
###	
###	An agent can easily create a transaction and assert features in
###	order to ensure that some other agent will or will not match it.  It
###	is also possible to add a feature and ``recycle'' a transaction.  In
###	this case we will eventually need a feature to prevent unwanted
###	matching by agents that have already seen it.

use DS::Features;
push(@ISA, DS::Features);

%computers;			# the routines that compute features.

### Initialization:

sub initialize {
    my ($self, $transaction) = @_;

    ## Compute and assert the value of some initial features.
    ##    We do this here because the features are closely related, so
    ##    we can get many assertions out of a small number of requests.

    $$self{'NEVER'} = 0;

    if ($transaction->is_request) {
	$self->assert('request');
	my $f = $self->compute('agent_request', $transaction);
	$self->assert('proxy_request', ! $f);

	$self->deny('response');
	$self->deny('proxy_response');
	$self->deny('agent_response');
    } else {
	$self->assert('response');
	my $f = $self->compute('agent_response', $transaction);
	$self->assert('proxy_response', ! $f);

	$self->deny('request');
	$self->deny('proxy_request');
	$self->deny('agent_request');
    }
}

sub compute {
    my ($self, $feature, $parent) = @_;

    ## Compute and assert the value of the given feature.
    ##	 Can be used to recompute features after changes

    my $computer = $computers{$feature};

    if (defined $computer) {
	print "computing $feature with $computer \n" if $main::debugging;
	return $self->assert($feature, &{$computer}($parent)); 
    } else {
	print "No computer for feature $feature\n" if $main::debugging;
	return $self->assert($feature, '');
    }
}

sub register {
    my ($feature, $sub) = @_;

    ## Register a subroutine that computes a feature.

    if (defined $computers{$feature}) {
	print "Multiply-defined feature: $feature\n" if ! $main::quiet;
	return;			# === dubious ===
    }
    $computers{$feature} = $sub;
}


############################################################################
###
### Feature Computers.
###	All take a transaction as their argument, and most return a
###	boolean.  Feature computers may use the utility method
###	transaction->assert(name,value) to set additional features. 
###
###	By convention, a feature computer "is_foo" computes a feature
###	named "foo". 

### Default Features: 
###	These are computed by default when a transaction is created;
###	they may have to be recomputed if the transaction is modified.

$computers{'response'} = \&is_response;
sub is_response{
    my $trans=shift;
    return $trans->is_response;
}

$computers{'request'} = \&is_request;
sub is_request{
    my $trans=shift;
    return $trans->is_request;
}

$computers{'agent_response'} = \&is_agent_response;
sub is_agent_response{
    my $trans=shift; 		
    return 0 unless $trans->is_response;
    my $agent=$trans->response->header('Version');
    my $request = $trans->response_to;
    my $url = $request->url if defined $request;

    return 0 if defined $url && ($url->path =~ /^\/http:/i );

    if ($agent =~ /^PIA/i) {
	return 1 unless defined $request;
	return 1 unless ref($request) =~ /PIA::Transaction/;
	return 1 if $request->is('agent_request');
    }

    return 0;
}

$computers{'proxy_request'} = \&is_proxy_request;
sub is_proxy_request{
    my $trans=shift;    	return 0 unless $trans->is_request;
    my $url=$trans->url; 	return 0 unless $url;

    my ($host, $port) = ($url->host, $url->port);
    return 0 if ($host =~ /^agency/ || $host eq '');
    return 0 if ($port == $main::PIA_PORT && $main::PIA_HOST =~ /^$host/i);
    return 1;

    # For some reason this test doesn't work!  Request must be outsmarting us.
    return  0 unless ($url->path =~ m|^\/http://([\w.]+)|i );

    $host = $1;
    $port = 80;
    if ($path =~ m|http://$host:([0-9]+)/|i) { $port = $1; }

    print "possible proxy; $host:$port\n";

    return 0 if ($host =~ /^agency/ || $host eq '');
    return 0 if ($port == $main::PIA_PORT && $main::PIA_HOST =~ /^$host/i);

    return 1 if $host;
    return 0;
}

$computers{'agent_request'} = \&is_agent_request;
sub  is_agent_request{
    my $trans=shift; 		return 0 unless $trans->is_request;
    my $url=$trans->url; 	return 0 unless defined $url;
    my $host= lc $url->host;

    my ($host, $port) = ($url->host, $url->port);
    return 1 if ($host =~ /^agency/ || $host eq '');
    return 1 if ($port == $main::PIA_PORT && $main::PIA_HOST =~ /^$host/i);
    return 0;

    ## === this attempts to detect proxying; it fails. ===
    return  1 unless ($url->path =~ m|^\/http://([\w.]+)|i );
    $host = $1;
    $port = 80;
    if ($path =~ m|http://$host:([0-9]+)/|i) { $port = $1; }

    print "possible proxy; $host:$port\n";

    return 1 if ($host =~ /^agency/ || $host eq '');
    return 1 if ($port == $main::PIA_PORT && $main::PIA_HOST =~ /^$host/i);
    return 0;
}


### Non-default Features:

$computers{'text'} = \&is_text;
sub is_text {
    my $trans=shift;
    return $trans->content_type() =~ /^text/i;
}

$computers{'html'} = \&is_html;
sub is_html {
    my $trans=shift;
    return $trans->content_type() =~ /^text\/html/i;
}

$computers{'image'} = \&is_image;
sub is_image{
    my $trans = shift;
    return $trans->content_type() =~ /^image/i;
}

$computers{'local'} = \&is_local;
sub is_local{
    my $trans=shift;
    my $url=$trans->url; 	return 1 unless $url;
    my $host=$url->host;

    return 1 if ($host =~ /^agency/ || $host eq '');
    return 1 if ($main::PIA_HOST =~ /^$host/);
    return 1 if ($host =~ /localhost/i);
    return 0;
}

$computers{'local_source'} = \&is_local_source;
sub is_local_source{
    my $trans=shift;
    ##need to fill transaction model first
}

$computers{'client_is_netscape'} = \&client_is_netscape;
sub client_is_netscape{
    my($trans)=shift;
    return 0 unless $trans->is_request;
    return $trans->request->header('User-Agent') =~ /netscape/i;
}

$computers{'file_request'} = \&is_file_request;
sub is_file_request{
    my $trans=shift;
    return 0 unless $trans->is_request;
    my $url=$trans->url;
    my $scheme=$url->scheme;
    return $scheme=~/file/i;
}

$computers{'interform'} = \&is_interform;
sub is_interform{
   my $trans=shift;
   my $url=$trans->url;
   my $path=$url->path;
   return $path=~/\.if$/i;
}


### Features with values:

$computers{'agent'} = \&get_agent;
sub get_agent {
    my($trans)=shift;
    my $url=$trans->url;
    return unless defined $url;
    my $path=$url->path if ref $url;
    return unless defined $path;
    my $name = ($path =~ m:^/(\w+)/*:i) ? $1 : 'agency';
    return $name;
}

$computers{'title'} = \&get_title;
sub get_title {
    my($trans)=@_;

    ## Return the title of an HTML page, if it has one.
    ##	  Returns the URL if the content-type is not HTML.

    return unless $trans->is_response();
    my $ttl  = $trans->url;
    $ttl = $ttl->as_string if ref $ttl;
    my $type = $trans->content_type();
    return unless $type;
    return $ttl unless $type =~ m:text/html:;

    my $page = $trans->content();

    if ($page =~ m:<title>(.*)</title>:ig) { $ttl = $1; }
    return $ttl;
}

### The following just mirror methods; there may be a better way.

$computers{'url'} = \&get_url;
sub get_url {
    my($trans)=@_;
    my $url = $trans->url;
    return $url->as_string if ref $url;
    return $url;
}

$computers{'method'} = \&get_method;
sub get_method {
    my ($trans) = @_;
    return $trans->method;
}

$computers{'code'} = \&get_code;
sub get_code {
    my ($trans) = @_;
    return $trans->code;
}


1;
