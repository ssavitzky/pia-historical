#!/usr/bin/perl
### 	$Id$
###	Copyright 1997 Ricoh Silicon Valley

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

