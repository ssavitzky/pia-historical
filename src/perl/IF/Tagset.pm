package IF::Tagset; ###### Sets of Tags (Interform Actors)
###	$Id$
###
###	A Tagset is a collection of related Actors.  It is defined
###	using a <tagset name="...">...</tagset> element containing the
###	<actor> elements that are part of the set.  A Tagset element
###	can contain arbitrary text, so it can also serve as its own
###	documentation. 

###	The <process tagset=name>...</process> actor actually selects a 
###	tagset for use.  The tagset name may be omitted when <process> 
###	is inside a <tagset> element.

###	There will eventually be three sets of actors for processing
###	tagsets: one that actually defines the tagset and its actors,
###	one that formats it as a document, and one that deletes
###	extraneous text for more efficient loading.  The latter two
###	are usually run to generate .html and .ts files, respectively.

use IF::IT;
push(@ISA,IF::IT);


#############################################################################
###
### Global Tagsets:
###
###	In addition to the global tagsets, each document passing through the 
###	interpretor has a _local_ tagset.  Named tagsets may be appended to.
###

%tagsets;

sub tagsets {
    return \%tagsets;
}

sub tagset {
    my ($name) = @_;
 
    ## Return a global tagset with a given name.

    return unless $name;
    return $tagsets{$name};
}

#############################################################################
###
### Creation:
###

sub new {
    my ($class, $name, @attrs) = @_;

    ## Return a tagset with the given name.
    ##	  If the name is non-null, we return a global tagset with that name
    ##	  if it exists.

    if (ref tagset($name)) {
	return tagset($name);
    }
    my $self = IF::IT->new('_tagset_', @attrs);
    bless $self, $class;

    $$self{_actors} = {} unless $$self{_actors};
    $$self{_passive} = [] unless $$self{_passive};
    $$self{_entities}= {} unless $$self{_entities};

    $self->attr('name', $name) if $name;
    $tagsets{$name} = $self if $name;

    print ("Defined $name tagset\n") if ($name && ! $main::quiet);
    return $self;
}

sub clone {
    my ($self) = @_;

    ## Return an anonymous clone of $self, with disjoint actor tables.
    ##	 Note that there is no need to copy the content, if any.

    my $actors   = $$self{_actors};
    my $passive  = $$self{_passive};
    my $entities = $$self{_entities};

    my %newactors  = %$actors;
    my @newpassive = @$passive;
    my %newents    = %$entities;

    ## === We can worry about attributes later if necessary.

    return IF::Tagset->new('', _actors=>\%newactors, _passive=>\@newpassive,
			   _entities=>\%newents);
}

sub include {
    my ($self, $ts) = @_;

    ## Include all of $ts's tags and entities in $self.

    $ts = tagset($ts) unless ref $ts;
    print "Undefined tagset $ts\n" unless ref $ts;

    my ($k, $v);

    for $v (values %{$ts->actors}) {
	$self->define_actor($v);
    }

    my $entities = $self->entities;
    while (($k, $v) = each %{$ts->entities}) {
	$$entities{$k} =$v;
    }
    print "Including " . $ts->name . " in " . $self->name . "\n"
	unless $main::quiet;

    return $self;
}
    

#############################################################################
###
### Components:
###

sub actors {
    my ($self) = @_;
    return $$self{_actors};
}

sub passive {
    my ($self) = @_;
    return $$self{_passive};
}

sub entities {
    my ($self) = @_;
    return $$self{_entities};
}

sub name {
    my ($self, $v) = @_;
    return $self->attr('name', $v);
}

#############################################################################
###
### Actors:
###

sub for_tag {
    my ($self, $tag) = @_;

    ## Return the actor with a name that matches the given tag

    return $self->actors->{$tag};
}

sub define_actor {
    my ($self, $actor, @attrs) = @_;

    ## Add an actor to the tagset.

    if (! ref($actor)) {
      $actor = IF::IA->new($actor, @attrs)
    }

    my $name = $actor->attr('name');
    my $tag = $actor->attr('tag');

    push(@{$self->passive}, $actor) unless $tag;
    $self->actors->{$name} = $actor;

    print "Defined IF actor $name in ".$self->name."\n" if $main::debugging; 
}


#############################################################################
###
### initialization:
###
###	These are the tagsets and actors required to initialize the
###	InterForm Interpretor and the PIA.  We have to be able to
###	process START-UP.html (which initializes the PIA) and
###	Standard.if (which defines the Standard tagset).  This
###	requires the actors -form-submit-, tagset, and actor.

#############################################################################
1;
