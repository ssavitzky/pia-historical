###### GENERIC Agent Class code
###	$Id$
###
### 	The Generic Agent is just that.  It's easy to set up:
###	the initialization routine tries to load a file full of perl code,
###     that by default is ".../name/name.pl".  This, in turn, can load
###	additional files.  It might even create a new package.
###
###	It's perfectly feasible to load these things from the interform,
###	but it works better if we do it during initialization because
###	that way we don't respond to any transactions by mistake.
###

### Setup and installation.

package PIA_AGENT::GENERIC;
push(@ISA, PIA_AGENT);

sub initialize {
    my $self = shift;

    $self->match_criterion(NEVER);
    $self->option('home', $main::USR_DIR);
    &PIA_AGENT::initialize($self);

    return $self;
}

sub re_initialize {
    my $self = shift;

    ## Called from initialize.if after installing as class 

    print "  ", $self->name, " re-initialized.\n" unless $main::quiet;
    return bless $self;
}


############################################################################
###
### Acting on matched transactions:
###

sub  act_on {
    my($self, $transaction, $resolver) = @_;
    return $self->run_hook('act_on', $transaction, $resolver);
}

###### handle($transaction, $resolver)
###
###	Handle a transaction, typically a request.  
###
sub handle{
    my($self, $request, $res)=@_;
    return 0 unless $request -> is_request();

    local $url    = $request->url();
    my $path = ref($url) ? $url->path() : $url;
    my $type = $self->type();
    my $name = $self->name();
    my $response;

    ## Declare local variables for use by spies

    local $agent    = $self;	 # in case a spy wants to contact us.
    local $context  = $request; # in case a spy needs more information
    local $resolver = $res;

    ## Examine the path to see what we have:
    ##    $name/path  -- this is a real, DOFS-style request.
    ##	  $name       -- home page InterForm
    ##	  $type/$name -- Interforms for $name
    ##	  $type/path  -- Interforms for $type

    if ($name ne $type && $path =~ m:^/$name/:) {
	return $self->handle_path($url, $request, $resolver);
    } elsif ($name ne $type && $path =~ m:^/$name$:) {
	$path = "/$name/home.if";
    } elsif ($path =~ m:^/$name/([^/]+)/:) {
	$type = $1;
	my $agent = $resolver->agent($type);
	$path =~ s:^/$type:: if defined $agent;
	$self = $agent if defined $agent;
    }

    $response = $self->respond_to_interform($request,$path);
    return 0 unless defined $response;
    $resolver->push($response);
    return 1;
}


sub handle_path {
    my($self, $request, $resolver)=@_;

    ## Handle a DOFS-style request.  Subclass must override.

    return 0;
}



############################################################################
###
### Utilities:
###
########################################################################

### === The following need to lock their files! Otherwise Netscape's
###	simultaneous requests will bite us...

###### writeTo($fn, $str)
###	Writes $str to file $fn
###
sub writeTo {
    my ($fn, $str) = @_;
    open(FILE, ">$fn");
    print FILE $str;
    close(FILE);
}

###### appendTo($fn, $str)
###	Appends $str to file $fn
###
sub appendTo {
    my ($fn, $str) = @_;
    open(FILE, ">>$fn");
    print FILE $str;
    close(FILE);
}

###### asHTML($string)
###
###	convert $string to HTML by properly escaping &, <, and >.
###	
sub asHTML {
    my $s = shift;

    $s =~ s'&'&amp;'g;
    $s =~ s'<'&lt;'g;
    $s =~ s'>'&gt;'g;
    $s
}

###### readFrom($fn, &optional $str)
###	Read from file $fn and returns the contents as a string.
###	If $str is provided, it is appended to.
###
sub readFrom {
    my ($fn, $str) = @_;

    open(FILE, "<$fn");
    while (<FILE>) {
	$str .= $_;
    }
    close(FILE);
    return $str;
}

############################################################################

1;
