#!/usr/bin/perl
#
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

require "ctime.pl";
push (@INC,"/usr/local.mnt/lib/perl5.shared/site_perl");
#use URI::URL;
#use URI::Escape;
require LWP::UserAgent;

($#ARGV == 0) || die "Need to know url.\n
Example 1:  http://lena.crc.ricoh.com:8888/Buss/testwrite.if/test.txt?name=chunk&you=me\n
Example 2:  http://lena.crc.ricoh.com:8888/My/Buss/wongfoo.txt: $#ARGV ";

#input record separator; new line by default
undef $/;

#$url = "http://lena.crc.ricoh.com:8888/Buss/testwrite.if/test.txt?name=chunk&you=me";
$url = $ARGV[0];

$ua = new LWP::UserAgent;
$ua->agent("AgentName/0.1");

# Create a request
my $req = new HTTP::Request ('PUT', $url);
#$req->content_type('application/x-www-form-urlencoded');
#$req->content('match=www&errors=0');
$req->content_type('text/html');
$req->content('To Wongfoo. Thanks for everthing');
$response = $ua->request($req);
if ($response->is_success) {
    print $response->content;
} else {
    print $response->error_as_HTML;
}




