package PIA::Accepter; ###### Accept and handle connections on a port
###	$Id$
###	Copyright 1997, Ricoh California Research Center.

######	Accept and handle connections on a port.
###
### Requires a global hook function:
###	handleconnection($iaddr,$port,$lient)

require 5.002;
use strict;
#BEGIN { $ENV{PATH} = '/usr/ucb:/bin' }
use Socket;
use Carp;


use PIA::Transaction;
use PIA::Machine;
use HTTP::Headers;
use HTTP::Request;


sub logmsg { 
    print "$0 $$: @_ at ", scalar localtime, "\n" unless $main::quiet;
}



sub listen {
    my ($port, $resolver) = @_;

    ## Accept connections on $port and resolve them with $resolver
    ##   calls handleconnection for all requests
    ##   never returns


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

 # we use a single thread of execution
 # accept a connection, handle it,  repeat

    my $exit_status=0;
    my $connection_timeout=60;	# run cron if no requests in a minute
    my $cron_request=new HTTP::Request("HEAD","/cron");
    $cron_request=PIA::Transaction->new($cron_request,$main::this_machine);
    
    while(!$exit_status){
				#accept connections with time-out alarm 
     eval {
	 local $SIG{ALRM} = sub { die "no connections" };
	 alarm $connection_timeout;
	 $paddr = accept(Client,Server);
	 alarm 0;
     };
     if ($@){
	 
	 if( $@ !~ /no connections/) {
	     $exit_status=$@; #unknown error
	 }else{
	     ##accept timed out 
	     ##  runs cron jobs during idle time if enable_cron is set

	     $resolver->do_timed_submits($cron_request) if $main::enable_cron;
	
	 }
     } else {
	 # handle connection
	 my($port,$iaddr) = sockaddr_in($paddr);
	 my $name = gethostbyaddr($iaddr,AF_INET);
	 
	 logmsg "connection from $name [",
	 inet_ntoa($iaddr), "] at port $port" if $main::verbose;
	 handleconnection($iaddr,$port,\*Client, $resolver);
	 close Client;
     }
 }


}

sub create_request_transaction {
    my($address, $port, $input)=@_;

    ## Create a request transaction

    my $machine=PIA::Machine->new($address, $port, $input);
    
    my $proto = <$input>;
   
    my($type,$r_url,$protocol)=split(' ',$proto);
    my $head = new HTTP::Headers;
    while (<$input>) {
	last if (length($_) < 3); # Must be a better way to detect end of header

	next unless /^(\S*):(.*)$/;
	my $key = $1;
	my $value = $2;
	$head->push_header($key,$value);
    }

    my $request=new HTTP::Request($type, $r_url, $head);
    # transaction creation function reads content for POST and PUT

    return PIA::Transaction->new($request, $machine);
}

sub handleconnection {
# this gets called by accepter whenever new request is received
# creates transaction and places on stack of resolver, currently also calls resolver to handle request
    my($address, $port, $client, $resolver)=@_;

    my $transaction=create_request_transaction($address,$port,$client);
    
##Set up signal handlers...for now use sigtrap for debugging    
#    $SIG{IO} = sub { print "problem IO...\n"}; ##'IGNORE';
#    $SIG{PIPE} = sub { print "problem pipe...\n"};
    use sigtrap;

    $resolver->push($transaction);
    my $status=$resolver->resolve(); #won't need if multi-threaded
    print "\n\n"  if $main::debugging;
}

1;















