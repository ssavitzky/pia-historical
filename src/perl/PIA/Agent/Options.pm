package PIA::Agent::Options; ###### Agent options
###	$Id$
###
###	An Options object is attached to a PIA::Agent object to serve
###	as a cache for data computed from the agent's options.  It is
###	implemented as a subclass of DS::Features.  The hash of
###	feature computers is kept on the agent, because different
###	agents have different requirements. 

###	PIA::Agent::Options contains a ``canned'' set of feature
###	computers for things like filenames, file contents, compiled
###	code snippets, and so on.  They are referred to by name.

use DS::Features;
push(@ISA, DS::Features);

%computers;			# the routines that compute features.

### Initialization:

sub initialize {
    my ($self, $parent) = @_;

    ## Compute and assert the value of some initial features.

    $self;
}

sub compute {
    my ($self, $feature, $parent) = @_;

    ## Compute and assert the value of the given feature (option).
    ##	 Can be used to recompute features after changes

    my $computer = $self->computer_for($feature, $parent);
    my $computer = $computers{$computer} unless ref($computer);

    if (defined $computer) {
	print "computing $feature with $computer \n" if $main::debugging;
	return $self->assert($feature, &{$computer}($parent, $feature)); 
    } else {
	return $parent->attr($feature);
    }
}


############################################################################
###
### Option (Feature) Computers.
###	All take ($agent, $feature), so the same computer can be used
###	for many different features.

$computers{response} = \&is_response;
sub is_response{
    my $request=shift;
    return $request->is_response;
}

sub attribute {
    ## Set or retrieve a named attribute
    my ($self, $key, $value) = @_;
    if (defined $value) {
	$$self{$key} = $value;
    } else {
	$value = $$self{$key};
	if (! defined $value) {
	    $value = $self->option($key);
	    $$self{$key} = $value if defined $value;
	}
    }
    return $value;
}

sub file_attribute {
    ## Set or retrieve a file attribute.
    ##	  Performs ~ expansion on the filename.
    my ($self, $key, $value) = @_;

    if (defined $value) {
	$$self{$key} = $value;
    } else {
	$value = $$self{$key};
	if (! defined $value) {
	    $value = $self->option($key);
	    my $home = $ENV{'HOME'};
	    print "substituting $home for ~ in $value\n" if $main::verbose;
	    $value =~ s:^\~/:$home/:;
	    $$self{$key} = $value if defined $value;
	}
    }
    return $value;
}

sub dir_attribute {
    ## Set or retrieve a directory attribute.
    ##	  Performs ~ expansion on the filename
    ##    Makes sure that it ends in a '/' character.
    my ($self, $key, $value) = @_;

    if (defined $value) {
	$$self{$key} = $value;
    } else {
	$value = $$self{$key};
	if (! defined $value) {
	    $value = $self->option($key);
	    if (defined $value){
		my $home = $ENV{'HOME'};
		print "substituting $home for ~ in $value\n" if $main::verbose;
		$value =~ s:^\~/:$home/:;
		if ($value !~ m:/$:) { $value .= '/'; }
		$$self{$key} = $value;
	    }
	}
    }
    return $value;
}

1;
