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


############################################################################
###
### Code Files:
###
### === This stuff probably belongs in pia_agent.pl or a separate class.
###

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
### Running Hooks:
###
### ===	 Wouldn't it be nice if we could run an interform, too...

sub run_hook {
    my ($self, $attr, $trans, $res) = @_;

    ## Run the code attached to a hook attribute.
    ##	  The context, defined by local variables, is suitable for either
    ##	  an act_on or a handle operation.

    my $code = $$self->attribute($attr);
    return if !defined $code;
    my $status;

    local $agent = $self;
    local $resolver;	$resolver = $res if defined $res;
    local $context = $trans;
    local $request = $trans;
    local $response;
    local $url;

    if (defined $trans && $trans->is_response) {
	$response = $trans;
	$request  = $trans->request;
    }
    $url = $request->url if defined $request;
    

    if (ref($code) eq 'CODE') {
	$status = &$code($trans, $res);
    } elsif (ref($code)) {
	$status = $code -> $attr($trans, $res);
    } else {
	$status = eval ($code) if defined $code;
	print "Error in $attr string: $@\n" if $@ ne '';
    }
    return $status;
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
    my($self, $request, $resolver)=@_;
    return 0 unless $request -> is_request();

    my $url = $request->url;
    my $path = ref($url) ? $url->path() : $url;
    my $type = $self->type();
    my $name = $self->name();
    my $response;

    ## Declare local variables for use by spies

    local $type   = $response->content_type();    return unless $type;
    local $isHTML = ($type =~ m:text/html:)? 1 : 0;
    local $url    = $request->url();
    local $title  = $response->title();
    local $debug  = $main::debugging; 		# !$main::quiet; 

    local $agent    = $self;	 # in case a spy wants to contact us.
    local $context  = $response; # in case a spy needs more information
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

1;
