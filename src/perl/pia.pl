#!/usr/bin/perl 
##secure switches... -Tw

###### Small perl5 agency
###	$Id$
###	Copyright &copy; 1997 Ricoh Silicon Valley


### Globals.  Must be customized for each site.

$ENV{'PATH'} = "/bin:/usr/bin:/usr/local/bin:/usr/bin/X11" . $ENV{'PATH'};

### Additional globals from environment:

$HOME = $ENV{'HOME'};
$PIA_HOST = `hostname`;
$PIA_HOST =~ s/\n//;
$PIA_PORT = 8001;

### Command-line flags

$debugging = 0;			# controls debugging output.
$verbose = 0;			# enables informational output.
$quiet   = 0;			# suppresses ALL startup messages.
$printenv= 0;			# causes printout of environment vbls.
$command = '';
$exit = '0';
$ftp = 0;
$logfile = "";
$enable_cron=1;			# run cron jobs when idle?

### Parse the command line.

while ($arg = shift) {
    if ($arg eq '-d') 			{ $debugging = 1; }
    elsif ($arg =~ /-d([0-9]*)/) 	{ $debugging = $1; }
    elsif ($arg eq '-v') 		{ $verbose = 1; }
    elsif ($arg eq '-q') 		{ $quiet = 1; }
    elsif ($arg =~ /^[p:]?[0-9]+$/)	{ $PIA_PORT = $arg; }
    elsif ($arg eq '-e')		{ $printenv = 1; $quiet = 1; }
    elsif ($arg eq '-f')		{ $ftp = 1; }
    elsif ($arg eq '-p')		{ $PIA_PORT = shift; }
    elsif ($arg eq '-s')		{ $PIA_DIR = shift; }
    elsif ($arg eq '-u')		{ $USR_DIR = shift; }
    elsif ($arg eq '-l')		{ $logfile = shift; }
    elsif ($arg eq '-x')		{ $exit = 1; }
    elsif ($arg eq '-c')		{ $command = shift; }
    elsif ($arg =~ /^(.*)=(.*)$/)	{ my $x = $1; $$x = $2; }
    else 				{ usage(); exit; }
}

sub usage {
    print "usage: $0 [option]... [port]
    Options:
	-s PIA_DIR	source dir: (.:~/pia/src:/pia1/pia/src)
	-u USR_DIR	(~/.PIA)
	-l logfile
	-p port		(8001)
	-c command
	-v		verbose
	-q		quiet
	-d[N]		debugging
	-e		print out setenv commands for proxying
	-f		proxy ftp as well (optional because flaky)
	-x		exit after printing info, starting command (if any)
";
}

if ($debugging) { $verbose = 1; $quiet   = 0; }
if ($verbose)   { $quiet = 0; }

if ($logfile ne '') {
    open(STDOUT, ">$logfile");
    open(STDERR, ">&STDOUT");
    select(STDOUT); $|=1;
    select(STDERR); $|=1;
}


### Set up defaults for directories.
###	$PIA_DIR = reasonable default from (.:ENV:~/pia/src:/pia1/pia/src)

if ($PIA_DIR ne "") {		# the user has already set it
    # nothing to do.
} elsif (-f "./pia.pl") {	# We're in the working directory
    $PIA_DIR = "../..";
} elsif (-d $ENV{'PIA_DIR'}) {	# There's a PIA_DIR environment variable
    $PIA_DIR = $ENV{'PIA_DIR'};
} elsif (-d "$HOME/pia") {	# We have a home copy of the working directory
    $PIA_DIR = "$HOME/pia";
} elsif (-d "/usr/local/pia") {	# We have a system-wide copy of the tree
    $PIA_DIR = "/usr/local/pia";
} elsif (-d "/usr/local/src/pia") {
    $PIA_DIR = "/usr/local/src/pia";
} else {
    # === should also check for a path in $0
    die "Help!  no PIA source directory!\n";
}

### Now the directories that depend on it:

### === src should be optional here--test for $PIA_DIR/lib, etc.

$PIA_LIB = -d "$PIA_DIR/lib" ? "$PIA_DIR/lib/perl" : "$PIA_DIR/src/lib/perl";
$PIA_ROOT = -d "$PIA_DIR/Agents" ? "$PIA_DIR/Agents" : "$PIA_DIR/src/Agents";
$PIA_URL  = "http://$PIA_HOST:$PIA_PORT/";

$USR_DIR = "$HOME/.pia" unless $USR_DIR ne '';
$USR_ROOT = "$USR_DIR/Agents"; 		# The user's overridden interforms
#$USR_DOFS = "$USR_DIR/DOFS";		# The user's DOFS tree

if ($verbose) {
    print "PIA_DIR= $PIA_DIR	# (parent of src, lib, Agents)\n";
    print "PIA_LIB= $PIA_LIB	# (agency libraries)\n";
    print "PIA_ROOT=$PIA_ROOT	# (agent interforms)\n";
    print "USR_DIR =$USR_DIR	# (user directory)\n";
    print "USR_ROOT=$USR_ROOT	# (user interforms)\n";
    #print "USR_DOFS=$USR_DOFS	# (user DOFS root)\n";
    print "PIA_URL=$PIA_URL\n";
}

if ($printenv) {
    my $se = ($ENV{'SHELL'} =~ /csh/) ? "setenv" : "export";
    my $us = ($ENV{'SHELL'} =~ /csh/) ? "unsetenv" : "unset";
    my $eq = ($ENV{'SHELL'} =~ /csh/) ? " " : "=";
    print "$se http_proxy$eq$PIA_URL\n";
    print "$se wais_proxy$eq$PIA_URL\n";
    print "$se gopher_proxy$eq$PIA_URL\n";
    print "$se ftp_proxy$eq$PIA_URL\n" if $ftp;
    print "$us no_proxy \n";
}


if (! $quiet && !$exit) {
    print "Starting agency: <URL: http://$PIA_HOST:$PIA_PORT/>\n";
}

### Note: Simply setting no_proxy to '' makes some browsers think it means 
###	  everything, and there's no obvious way to unset, so we kludge it.
$proxies = "http_proxy=$PIA_URL wais_proxy=$PIA_URL "
         . "gopher_proxy=$PIA_URL no_proxy=noplace "; 

### FTP fetching is very eratic.  Some sites actually cause an error, and 
###     LWP doesn't handle directories correctly.  Disgustingly enough, 
###	it works if you proxy through the gateway.  May have something to do 
###	with the negotiation process.
$proxies .= "ftp_proxy=$PIA_URL " if $ftp;

if ($exit && $main::command ne '') {
    print "Executing command '$command'\n" unless $main::quiet;
    system("sh -c '$main::proxies $main::command </dev/null &>/dev/null&'");
}

if ($exit) { exit 0; }

### Now that we know where the libraries are, we can require them.

push (@INC,"$PIA_LIB");

### We have to use "require": "use" gets done in a BEGIN block,
###	which is executed before we figure out where $PIA_LIB is.

require PIA::Accepter;
require PIA::Resolver;
require PIA::Machine;

require PIA::Agent;
require PIA::Agent::Agency;
require PIA::Agent::DOFS;
require LWP::UserAgent;

use HTTP::Date;

#########################################################################
###
### Create and customize objects
###

### instantiate machine, resolver, agency agent

$this_machine=PIA::Machine->new($PIA_PORT, $PIA_HOST);

$main::resolver=new PIA::Resolver;
$agency = new PIA::Agent::Agency 'Agency';
$main::resolver->register_agent($agency);
$dofs = new PIA::Agent::DOFS 'DOFS';
$main::resolver->register_agent($dofs);

$main::resolver->resolve();	# handle any initialization requests...

#########################################################################
###
### manage the connections from clients and hand off replies
###

if ($main::command ne '') {
    print "Executing command '$command'\n" unless $main::quiet;
    system("sh -c '$main::proxies $main::command </dev/null &>/dev/null&'");
}

PIA::Accepter::listen($PIA_PORT, $main::resolver);	# start listening.
				# ... eventually a multithreaded resolver 
				#     will accept connections on its own. 
### ... Never returns
###
#########################################################################

