#!/usr/bin/perl -n
#  $Id$
#  Copyright 1997 Ricoh Silicon Valley
#  stringify.pl [file]...
#	output the concatenation of the files or STDIN
#	suitably quoted for use as a Java String constant.

BEGIN { 
    $line=0;
}

print (($line > 0)? '     + "' : '       "');

s/([\\"])/\\$1/g;
s/$/\\n"/;

print $_;

++$line;
