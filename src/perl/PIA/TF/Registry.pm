package PIA::TF::Registry; ###### Registry for Transaction Features.
###	$Id$

##############################################################################
 # The contents of this file are subject to the Ricoh Source Code Public
 # License Version 1.0 (the "License"); you may not use this file except in
 # compliance with the License.  You may obtain a copy of the License at
 # http://www.risource.org/RPL
 #
 # Software distributed under the License is distributed on an "AS IS" basis,
 # WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 # for the specific language governing rights and limitations under the
 # License.
 #
 # This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 # created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 # Rights Reserved.
 #
 # Contributor(s):
 #
############################################################################## 

###
###	This is the factory and repository for the objects that compute 
###	Transaction Features.  Note that in PERL these can be references
###	to subroutines; in more object-oriented languages they may have 
###	to be functor objects.

###	Note that computers in TF can assume that their parent object
###	is a Transaction. 

use DS::Features;

############################################################################
###
### Feature Computer Registry:
###

%computers;			# the routines that compute features.
sub computers {
    return \%computers;
}


############################################################################
###
### Feature Computers:
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

$computers{'content-type'} = \&get_content_type;
sub get_content_type {
    my($trans)=@_;
    return $trans->content_type;
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
