#!/usr/local/bin/perl -Tw
###	$Id$

######	Accept and handle connections on a port.
###
### Requires a global hook function:
###	handleconnection($iaddr,$port,$lient)

require 5.002;
use strict;
#BEGIN { $ENV{PATH} = '/usr/ucb:/bin' }
use Socket;
use Carp;


sub logmsg { 
    print "$0 $$: @_ at ", scalar localtime, "\n" unless $main::quiet;
}

sub acceptconnections {
 # starts listening on specified port
# calls handle connection for all requests
 # never returns
    my $port = shift || 8001;
    my $proto = getprotobyname('tcp');
    socket(Server, PF_INET, SOCK_STREAM, $proto)        || die "socket: $!";
    setsockopt(Server, SOL_SOCKET, SO_REUSEADDR,
	       pack("l", 1))   || die "setsockopt: $!";
    bind(Server, sockaddr_in($port, INADDR_ANY))        || die "bind: $!";
# === Linux doesn't define SOMAXCONN ===
#    listen(Server,SOMAXCONN)                            || die "listen: $!";
    listen(Server,5)                            || die "listen: $!";
    
    logmsg "server started on port $port";

    my $waitedpid = 0;
    my $paddr;

# sub REAPER {
# $SIG{CHLD} = \&REAPER;  # loathe sysV
# $waitedpid = wait;
# logmsg "reaped $waitedpid" . ($? ? " with exit $?" : '');
# }

#$SIG{CHLD} = \&REAPER;

    for ( $waitedpid = 0;
	 ($paddr = accept(Client,Server)) || $waitedpid;
	 $waitedpid = 0, close Client)
    {
	next if $waitedpid;
	my($port,$iaddr) = sockaddr_in($paddr);
	my $name = gethostbyaddr($iaddr,AF_INET);

	logmsg "connection from $name [",
	inet_ntoa($iaddr), "]
at port $port" if $main::debugging;
	handleconnection($iaddr,$port,\*Client);
    }
}

#   open(STDIN,  "<&Client")   || die "can't dup client to stdin";
#    open(STDOUT, ">&Client")   || die "can't dup client to stdout";
## open(STDERR, ">&STDOUT") || die "can't dup stdout to stderr";
1;
