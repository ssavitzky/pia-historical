package DS::Features; ###### Lists of named features.
###	$Id$
###
###	A Features list is attached to a Thing when it is first 
###	needed.  It is used to perform lazy evaluation of named
###	features.  The association between names and functions is made
###	using a hash; a reference to this is returned by $f->computers, 
###	which returns a reference to a class-specific hash that
###	associates feature names with functions.
###
###	The Features list is normally accessed only by a method on
###	its parent:  $something->is('feature').  Note that there is
###	no backlink to the parent from its features; this makes
###	objects easier to delete by avoiding circularities.
###
###	Features may actually have any value, but the matching process
###	is normally only interested in truth value.  Some agents,
###	however, make use of feature values.  In particular, 'agent'
###	binds to the name of the agent at which a request is directed.


sub new {
    my ($class, $parent) = @_;

    ## Create a new FEATURES.  We pass the parent, even though 
    ##    no link is kept, so that we can compute a few features that
    ##    are closely related and that we know are always needed.

    my $self = {};

    bless $self,$class;

    ## Set the parent's features to $self.
    ##    This is essential in order for initialization to work.

    $parent->features($self);
    $self->initialize($parent);
    return $self;
}

############################################################################
###
### Access to feature values.
###

sub assert {
    my ($self, $feature, $value) = @_;

    ## Assert a named feature, with an optional value.

    $value = 1 unless defined $value;
    $value = 0 if $value eq '';
    print " $feature=>$value" if $main::debugging;
    $$self{$feature} = $value;
    return $value;
}

sub deny {
    my ($self, $feature) = @_;

    ## Deny a named feature, i.e. assign it a value of 0

    print " $feature=>0" if $main::debugging;
    $$self{$feature} = 0;
    return 0;
}

sub has {
    my ($self, $feature) = @_;

    ## Test for the presence of a named feature

    return defined $$self{$feature};
}

sub test {
    my ($self, $feature, $parent) = @_;

    ## Test a named feature and return its value.

    my $value = $$self{$feature};
    $value = $self->compute($feature, $parent) if ! defined $value;
    return $value;
}

sub compute {
    my ($self, $feature, $parent) = @_;

    ## Compute and assert the value of the given feature.
    ##	 Can be used to recompute features after changes

    my $computer = $self->computer_for($feature, $parent);

    if (defined $computer) {
	print "computing $feature with $computer \n" if $main::debugging;
	return $self->assert($feature, &{$computer}($parent, $feature)); 
    } else {
	## Not defined:
	##   Undefined features get asserted as a null string, so you can
	##   tell the difference between false and undefined, but don't
	##   have to recompute things you know aren't there.

	print "No computer for feature $feature\n" if $main::debugging;
	return $self->assert($feature, '');
    }
}

sub computer_for {
    my ($self, $feature, $parent) = @_;

    ## Return the feature computer (if any) for the named feature.

    my $computers = $parent->feature_computers($self);
    return unless defined $computers;
    return $computers->{$feature};
}

############################################################################
###
### Initialization:
###

sub initialize {
    my ($self, $parent) = @_;

    ## Compute and assert the value of some initial features.
    ##    We do this here because the features are closely related, so
    ##    we can get many assertions out of a small number of requests.

    $$self{'NEVER'} = 0;

    ## NEVER is useful for creating things that never match.
    ##	  A null criteria list will always match, so there's no need
    ##	  for an ALWAYS. 

}

sub register {
    my ($feature, $sub) = @_;

    ## Register a subroutine that computes a feature.

    my $computers = $parent->feature_computers($self, 1);
    
    if (defined $computers->{$feature}) {
	print "Multiply-defined feature: $feature\n" if ! $main::quiet;
#	return;			# === dubious ===
    }
    $computers->{$feature} = $sub;
}

############################################################################
###
### Matching.
###
###	The match criteria are a list (not a hash, because order might be
###	significant), of sublists or name=>value pairs.
###
###		[criteria]	ORed, i.e. fail if not matched.
###		x => bool	fail if !test(x) != !b
###		x => \&subr	fail if &subr(test(x)) returns false
###		x => \$var	$var = test(x)

### === maybe 	x => ["op" value]	comparison
###		x => [value] 		eq
###		x => [subr args...] (splice test(x) in as first arg.)

sub matches {
    my($self, $criteria)=@_;

    ## Match against a list of criteria.
    ##	  The criteria are a list because order might be important.
    ##    We index through the list so we don't have to copy it.
    ##	  A null list always matches; a non-list never does.

    return '' unless ref $criteria;

    my $i;
    for ($i = 0; $i <= $#$criteria; $i++) {
	my $c = $$criteria[$i];
	if (! ref $c) {
	    local $f = $self->test($c);
	    local $v = $$criteria[++$i];
	    print " $f" if $main::debugging;
	    if (! ref $v) {
		return 0 if !$f != !$v;
	    } elsif (ref($v) eq 'REF') {
		$$v = $f;
	    } elsif (ref($v) eq 'CODE') {
		return 0 if ! &$v($f);
	    } else {
		print "undefined match criterion $c=>$v\n";
	    }
	} elsif (ref($c) eq 'ARRAY') {
	    return 0 if ! self->matches($c);
	} else {
	    print "undefined match criterion $c\n";
	}
    }
    print " matched.\n" if  $main::debugging;
    return 1;
}


1;
