#!/usr/bin/perl
open(PIDLIST, "ps -p | grep '[0-9] perl ./server.pl' |");
while( <PIDLIST> ){
print;
/^\s*(\d*)/;
print $1;
print "\n";
print "killing $1\n";
system("kill $1");
}


