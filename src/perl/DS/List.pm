package DS::List; ###### Generic Lists
###	$Id$
###	Copyright 1997, Ricoh California Research Center.
###
###	A List is an indexed collection if ``items'' accessed with 
###	"item($index)".  It has no attributes, but has tag and content 
###	access functions that make it behave in many ways like a DS::Thing
###	with no attributes.  This can greatly simplify some things.
###

use Exporter;
push(@ISA, Exporter);

use DS::Content;
push(@ISA, DS::Content);

#############################################################################
###
### Constructor:
###

sub new {
    my ($class, @content) = @_;

    ## DS::List->new($content...)
    ##	 Make a new List

    my $self = bless [], $class;

    my($val);
    for $val (@content) {
	$self->push($val);
    }
    $self;
}


#############################################################################
###
### Access to Components:
###
###	These are provided for compatibility with DS::Thing
###

sub tag {
    my $self = shift;

    ## Return undefined (for compatibility with Thing)

    return;
}

#############################################################################
###
### Content:
###	These are isomorphic to the equivalent operations on DS::Thing.
###

sub is_list {
    my $self = shift;

    ## Lists are lists.

    return 1;
}

sub content {
    my $self = shift;

    ## Return (optionally set) the content list.

    if (@_) {
	@$self = @_;
    }
    $self;
}

sub as_thing {
    my ($self) = @_;

    return $self->content_thing;
}

sub is_empty {
    my $self = shift;

    ## Returns true if there is no content.

    !@$self;
}

sub empty_element {
    my $self = shift;

    ## Returns true if there is no end tag.

    return 1;
}

sub content_size {
    my $self = shift;

    ## Returns the number of items.

    @$self;
}


sub push {
    my $self = shift;

    ## Push something into the content.  
    ##	  Arrays are appended.

    for (@_) {
	if (ref($_) eq 'ARRAY') {
	    $self->push(@$_);
	} else {
	    push(@$self, $_);
	}
    }
    $self;
}

sub pop {
    ## Pop the content

    my $self = shift;
    pop @$self;
}

sub shift {
    ## Shift the content

    my $self = shift;
    shift @$self;
}

sub unshift {
    my $self = shift;

    ## unshift something into the content, i.e. attach it to the front.

    unshift(@$self, $_);
    $self;
}

sub item {
    my ($self, $index, $value) = @_;

    ## Return (optionally set) an indexed item of content.

    if (defined $value) {
	$self->[$index] = $value;
    } else {
	$self->[$index];
    }
}

#############################################################################
###
### Features:
###	These exist purely to prevent crashes if we mistake a List for a Thing.
###

sub features {
    my($self) = @_;
    return;
}

sub has {
    my ($self, $feature) = @_;
    return
}

sub is {
    my ($self, $feature) = @_;
    return;
}

sub test {
    my ($self, $feature) = @_;
    return;
}

sub compute {
    my ($self, $feature) = @_;
    return;
}

sub assert {
    my ($self, $feature, $value) = @_;
    return;
}

sub deny {
    my ($self, $feature) = @_;
    return;
}



1;

