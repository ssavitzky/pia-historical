#!/usr/bin/perl
#
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




