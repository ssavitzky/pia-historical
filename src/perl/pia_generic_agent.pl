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

    print "  ", $self->name, " (CIA) re-initialized.\n" unless $main::quiet;
    return bless $self;
}

sub load_file {
    my ($self, $attr, $fn_attr, $default) = @_;

    ## Read the file whose name is in attribute $fn_attr (or $default)
    ##	  and stash it as one enormous string attribute in $attr.

    my $name = $self->name;
    $fn = $self->file_attribute($fn_attr);
    $fn = $default unless defined $fn;
    $fn = $self->find_interform($fn);

    return unless defined $fn;

    my $code = readFrom($fn);
    ##$code =~ s/#.*$//g;		# strip comments.

    print "  $name loaded $attr from $fn\n" unless $main::quiet;

    $self->attribute($attr, $code);
    return 1;
}

sub compile_file {
    my ($self, $attr, $fn_attr, $default) = @_;

    ## Read the file whose name is in attribute $fn_attr (or $default)
    ##	  and make it into an anonymous subroutine in &$attr.  UCK!

    my $name = $self->name;
    $fn = $self->file_attribute($fn_attr);
    $fn = $default unless defined $fn;
    $fn = $self->find_interform($fn);

    return unless defined $fn;

    my $code = readFrom($fn);

    local $subr;
    $code = "$subr = sub{\n" . $code . "}";

    my $status = eval ($code) if defined $code;
    print "$name: error in $fn: $@\n" if $@ ne '';

    print "  $name compiled $attr from $fn\n" unless $main::quiet;

    $self->attribute($attr, $subr);
    return 1;
}

sub require_file {
    my ($self, $fn_attr, $default) = @_;

    ## require the file whose name is in attribute $fn_attr (or $default)
    ##	  The file is located as an interform and require'd.

    my $name = $self->name;
    $fn = $self->file_attribute($fn_attr);
    $fn = $default unless defined $fn;
    $fn = $self->find_interform($fn);

    return unless defined $fn;

    require $fn;

    print "  $name loaded $attr from $fn\n" unless $main::quiet;

    $self->attribute($code_attr, $code);
    return 1;
}

############################################################################
###
### Handler for responses.
###

sub  act_on {
    my($self, $response, $res) = @_;

    my $request = $response -> request;  return unless defined $request;
    my $code = $$self{act_on};           return unless defined $code;

    ## Declare local variables for use by spies

    local $type   = $response->content_type();    return unless $type;
    local $isHTML = ($type =~ m:text/html:)? 1 : 0;
    local $url    = $request->url();
    local $title  = $response->title();
    local $dir    = $main::USR_DIR . "/" . $self->option('dir');
    local $debug  = $main::debugging; 		# !$main::quiet; 

    local $agent    = $self;	 # in case a spy wants to contact us.
    local $context  = $response; # in case a spy needs more information
    local $resolver = $res;

    ## Run the spies

    my $status = eval ($code) if defined $code;
    print "act_on code error: $@\n" if $@ ne '';
}

###### handle($transaction, $resolver)
###
###	Handle a DOFS request.  
###	Start by making sure the URL matches the agent's type;
###	   if it doesn't we can assume that act_on handled it.
###
sub handle{
    my($self, $request, $resolver)=@_;
    return 0 unless $request -> is_request();

    my $url = $request->url;
    my $path = ref($url) ? $url->path() : $url;
    my $type = $self->type();
    my $name = $self->name();
    my $response;

    if ($name ne $type && $path =~ m:^/$name/:) {
	return $self->retrieve_file($url, $request, $resolver);
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



############################################################################

1;
