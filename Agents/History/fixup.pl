#!/usr/bin/perl
### 	$Id$

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


### fixup.pl -- move yymmdd.html to yyyy/mm/dd.html
###             in $HOME/.pia/History 
### === should really take the directory to hack on the command line ===

$home = $ENV{'HOME'};
chdir("$home/.pia/History");

if (opendir(DIR, ".")) {
    @names = readdir(DIR);
    closedir(DIR);
} else {
    die "No directory ~/.pia/History";
}

foreach $fn (@names) {
    if ($fn =~ /([0-9][0-9])([0-9][0-9])([0-9][0-9]).html/) {
	my ($yy, $mm, $dd) = ($1, $2, $3);
	mkdir ("19$yy", 0777);
	mkdir ("19$yy/$mm", 0777);
	print $fn, " -> 19$yy/$mm/$dd.html\n";
	rename ($fn, "19$yy/$mm/$dd.html");
    } else {
	print "$fn not a history file\n";
    }
}

