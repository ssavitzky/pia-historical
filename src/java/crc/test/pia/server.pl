#!/usr/bin/perl

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

($port)=@ARGV;
$port = 6666 unless $port;

$cgidir="/home/rithy/cgi-bin";
$AF_INET = 2;
$SOCK_STREAM = 1;

$sockaddr = 'S n a4 x8';

($name, $aliases, $proto) = getprotobyname('tcp');
if ($port !~ /^\d+$/) {
    ($name, $aliases, $port) = getservbyport($port, 'tcp');
}

print "Port = $port\n";

$this = pack($sockaddr, $AF_INET, $port, "\0\0\0\0");

select(NS); $| = 1; select(stdout);

socket(S, $AF_INET, $SOCK_STREAM, $proto) || die "socket: $!";
bind(S,$this) || die "bind: $!";
listen(S,5) || die "connect: $!";
open(FIZZLE, ">> myprog.log");
select(FIZZLE);$|=1;
open(STDERR, ">&FIZZLE");

select(S); $| = 1; select(stdout);

for($con=1;;$con++) {
    printf("Listening for connection %d...\n", $con);
    unless ($addr = accept(NS,S)) {
       die "Can't accept a connection: $!\n";
    }

    if (($child = fork()) == 0) {
	open(STDIN, "+<&NS") || die "dup2 ns->stdin";
	open(STDOUT, "+>&NS") || die "dup2 ns->stdout";
	select(STDOUT); $|=1;
	&read;
	&print_condoc ;
	close(STDIN); close(STDOUT);close(STDERR);
	close(NS);
	exit  ;
    }


#	($af,$port,$inetaddr) = unpack($sockaddr,$addr);
#	@inetaddr = unpack('C4',$inetaddr);
#	print "$con: $af $port @inetaddr\n";

    close(NS);
}

sub read{
 $_=<STDIN>;
 print FIZZLE $_;
 ($method, $url, $proto) = split;
 if ($proto) {
     while (<STDIN>) {
	print FIZZLE $_;
	 s/\n|\r//g; # kill CR and NL chars
	 
	 /^Content-Length: (\S*)/i && ($content_length=$1);
	 /^Content-Type: (\S*)/i && ($content_type=$1);
	 length || last; # empty line - end of header
     }
     read(STDIN, $buffer, $content_length);
     print FIZZLE $buffer;
 } else {
     $proto="HTTP/0.9";
 }
}


sub print_condoc {
   print "HTTP/1.0 200 OK\nMIME-Version: 1.0\nContent-Type: ";
   print "text/html\n\n";
   print "\n";
   print "<html>\n <head>\n  <title>Test-Connection-doc</title>\n </head>\n";
   print "<body>\n <center>\n  <h1>Test-Connection-doc $title</h1>\n </center>\n";

   print "Hello World\n";
   print "Your connection is-->$con\n";
   print "</body>\n</html>\n";
}


