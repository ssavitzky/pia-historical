#!/usr/bin/perl -n
#  $Id$

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
