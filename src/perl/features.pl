###### Class FEATURES -- Features of transactions.
###	$Id$
###
###	A FEATURES object is attached to a transaction when it is
###	first noticed by the Resolver.  It is used to perform lazy
###	evaluation of named features.  The association between names
###	and functions is made using a hash: %FEATURES::computers.  
###
###	The FEATURES object is normally accessed only by a method on
###	TRANSACTION:  transaction->is('feature').  Note that there is
###	no backlink to a transaction from its features; this makes
###	transactions easier to delete by avoiding circularities.
###
###	Features may actually have any value, but the resolver is only
###	interested in truth value.  Some agents, however, make use of
###	feature values.  In particular, 'agent'
###

package FEATURES;

%computers;			# the routines that compute features.

sub new {
    my ($class, $transaction) = @_;

    ## Create a new FEATURES.  We pass the transaction, even though 
    ##    no link is kept, so that we can compute a few features that
    ##    are closely related and that we know are always needed.

    my $self = {'NEVER'=>0};

    ## NEVER is useful for creating agents that never match; these
    ##    have their handlers pushed by the agency.  A null criteria
    ##    list will always match, so there's no need for an ALWAYS.

    bless $self,$class;

    ## Set the transaction's features to $self.
    ##    This is essential in order for initialization to work.

    $transaction->features($self);
    $self->initialize($transaction);
    return $self;
}

### Access to feature values.

sub assert {
    my ($self, $feature, $value) = @_;

    ## Assert a named feature, with an optional value.

    $value = 1 unless defined $value;
    $value = 0 if $value eq '';
    print " $feature=>$value" if $main::debugging;
    $$self{$feature} = $value;
    return $value;
}

sub deny {
    my ($self, $feature) = @_;

    ## Deny a named feature, i.e. assign it a value of 0

    print " $feature=>0" if $main::debugging;
    $$self{$feature} = 0;
    return 0;
}

sub has {
    my ($self, $feature) = @_;

    ## Test for the presence of a named feature

    return defined $$self{$feature};
}

sub test {
    my ($self, $feature, $transaction) = @_;

    ## Test a named feature and return its value.

    my $value = $$self{$feature};
    $value = $self->compute($feature, $transaction) if ! defined $value;
    return $value;
}

sub compute {
    my ($self, $feature, $transaction) = @_;

    ## Compute and assert the value of the given feature.
    ##	 Can be used to recompute features after changes

    my $computer = $computers{$feature};
    print "computing $feature with $computer \n" if $main::debugging;

    return $self->assert($feature, &{$computer}($transaction)) 
	if defined $computer;

    ## Not defined:

    print "No computer for feature $feature\n";
    return;
}

### Initialization:

sub initialize {
    my ($self, $transaction) = @_;

    ## Compute and assert the value of some initial features.
    ##    We do this here because the features are closely related, so
    ##    we can get many assertions out of a small number of requests.

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
### Matching.
###

sub matches {
    my($self, $criteria)=@_;

    ## Match against a list of criteria.
    ##	  The criteria are a list because order might be important.
    ##	  Sublists are OR'ed.

    my $i;
    for ($i = 0; $i <= $#$criteria; $i++) {
	my $c = $$criteria[$i];
	if (ref $c) {
	    return 0 if ! self->matches($c);
	} else {
	    my $f = $self->test($c);
	    my $v = $$criteria[++$i];
	    print " $f" if $main::debugging;
	    return 0 if !$f != !$v;
	}
    }
    print " matched.\n" if  $main::debugging;
    return 1;
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

$computers{response} = \&is_response;
sub is_response{
    my $request=shift;
    return $request->is_response;
}

$computers{request} = \&is_request;
sub is_request{
    my $request=shift;
    return $request->is_request;
}

$computers{agent_response} = \&is_agent_response;
sub is_agent_response{
    my $request=shift; 		return 0 unless $request->is_response;
    my $agent=$request->header('Version');
    return 1 if $agent =~ /^PIA/i;
    return 0;
}

$computers{proxy_request} = \&is_proxy_request;
sub is_proxy_request{
    my $request=shift;    	return 0 unless $request->is_request;
    my $url=$request->url; 	return 0 unless $url;
    
    my $host=$url->host;

    return 0 if ($host =~ /^agency/ || $host eq '');
    return 0 if ($url->port == $main::PIA_PORT && $main::PIA_HOST =~ /^$host/);

    return 1 if $host;
    return 0;
}

$computers{agent_request} = \&is_agent_request;
sub  is_agent_request{
    my $request=shift; 		return 0 unless $request->is_request;
    my $url=$request->url; 	return 0 unless defined $url;
    my $host=$url->host;

    if ($host=~/^agency/ || $host eq ''
	|| $url->port == $main::PIA_PORT && $main::PIA_HOST =~ /^$host/) {
	return 1;
    }
    return 0;
}


### Non-default Features:

$computers{text} = \&is_text;
sub is_text{
    my($request)=shift;
    return $request->content_type() =~ /^text/i;
}

$computers{html} = \&is_html;
sub is_html{
    my($request)=shift;
    return $request->content_type() =~ /^text\/html/i;
}

$computers{image} = \&is_image;
sub is_image{
    my($request)=shift;
    return $request->content_type() =~ /^image/i;
}

$computers{local_source} = \&is_local_source;
sub is_local_source{
    my $request=shift;
    ##need to fill transaction model first
}

$computers{client_is_netscape} = \&client_is_netscape;
sub client_is_netscape{
    my($request)=shift;
    return $request->header()->header('User-Agent') =~ /netscape/i;
}

$computers{file_request} = \&is_file_request;
sub is_file_request{
    my $request=shift;
    my $url=$request->url;
    my $scheme=$url->scheme;
    return $scheme=~/file/i;
}

$computers{interform} = \&is_interform;
sub is_interform{
   my $request=shift;
    my $url=$request->url;
 
   my $path=$url->path;
   return $path=~/.if$/i;
 
}


### Features with values:

$computers{agent} = \&agent;
sub agency {
    my($request)=shift;
    my $path=$url->path;
    my $url=$request->url;
    return unless defined $url;
    my $name = ($path =~ m:^/(\w+)/*:i) ? $1 : 'agency';
    return $name;
}


1;
