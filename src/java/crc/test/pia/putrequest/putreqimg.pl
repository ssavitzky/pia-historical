#!/usr/bin/perl
#
require "ctime.pl";
push (@INC,"/usr/local.mnt/lib/perl5.shared/site_perl");
#use URI::URL;
#use URI::Escape;
require LWP::UserAgent;

#input record separator; new line by default
undef $/;

($#ARGV == 1) || die "Need to know url and gif filename.\n
http://lena.crc.ricoh.com:8888/My/Buss/foo.gif directory.gif";

#$url = "http://lena.crc.ricoh.com:8888/My/Buss/foo.gif";
$url = $ARGV[0];

$ua = new LWP::UserAgent;
$ua->agent("AgentName/0.1");

$myinfile= $ARGV[1];
open(MYINFILE, "$myinfile");
$lines = <MYINFILE>;
# Create a request
my $req = new HTTP::Request ('PUT', $url);
$req->content_type('image/gif');
$req->content($lines);
$response = $ua->request($req);
if ($response->is_success) {
    print $response->content;
} else {
    print $response->error_as_HTML;
}




