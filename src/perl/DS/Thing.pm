package DS::Thing; ###### Generic Objects
###	$Id$
###
###	A Thing is a very generic kind of data structure.  It has a
###	set of named ``attributes'' accessed with "attr($name)", and an
###	indexed list of ``items'' accessed with "item($index)".  Private
###	attributes have names that start with "_" and are accessed
###	only by means of access functions.
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
    my ($class, @attrs) = @_;

    ## DS::Thing->new($attr => $val, ..., [$content...])
    ##	 Make a new Thing

    ##	 Instance Variables:
    ##	    _list	list of attributes in the order defined.
    ##	    _tag	string tag (optional)
    ##	    _content	list of indexed items
    ##	    _features	list of named features

    my $self = bless {}, $class;
    my $list = [];
    $self->{_list} = $list;

    my($attr, $val);
    while (($attr, $val) = splice(@attrs, 0, 2)) {
	if (! defined $val) {
	    $self->push($attr);
	    return $self;
	}
	push(@$list, $attr) unless (exists $self->{$attr} || $attr =~ /^\_/);
	$self->{$attr} = $val;
    }
    $self;
}

sub init_content {
    my $self = shift;
    $self->{'_content'} = DS::List->new;
}



#############################################################################
###
### Access to Components:
###

sub tag {
    my $self = shift;

    ## Return (optionally set) the tag.

    if (@_) {
	$self->{'_tag'} = lc $_[0];
    } else {
	$self->{'_tag'};
    }
}

sub attr {
    my ($self, $attr, $val) = @_;

    ## Return (optionally set) an attribute
    ##	WARNING:  attribute names are NOT automatically lowercased!
    ##  WARNING:  previous versions returned the OLD value when redefined.

    my $old = $self->{$attr};
    if (defined $val) {
	if (! defined $old && $attr !~ /^\_/) {
	    my $list = $self->{_list};
	    push(@$list, $attr);
	}
	$old = $self->{$attr} = $val;
    }
    $old;
}

sub attr_default {
    my ($self, $attr, $val) = @_;

    ## Return an attribute, optionally set to a default.

    my $v = $self->{$attr};
    if (! defined $v) {
	$self->{$attr} = $v = $val;
	if ($attr !~ /^\_/) {
	    my $list = $self->{_list};
	    push(@$list, $attr);
	}
    }
    $v;
}

sub attrs {
    my ($self) = @_;

    ## return all the attributes in a hash

    my $attrs = {};
    for (sort keys %$self) {
	next if /^_/;
	$attrs{$_} = $self->{$_};
    }
    return $attrs;
}

sub attr_names {
    my ($self) = @_;

    ## return all the attribute names in a list

    my $attrs = $self->{'_list'};
    if (!defined($attrs)) {
	@list = sort keys %$self;
	$attrs = [];
	for (@$list) {
	    next if /^\_/;
	    push(@$attrs, $_);
	}
    }
    return $attrs;
}

sub attr_list {
    my ($self) = @_;

    ## return all the attributes in a list of pairs

    my $attrs = [];
    my $list = $self->attr_names;
    for (@$list) {
	push(@$attrs, $_);
	push(@$attrs, $self->{$_});
    }
    return $attrs;
}

#############################################################################
###
### Content:
###	We use the simplified syntax of push, pop, unshift, and shift
###	rather than the clumsier push_content, and provide a complete
###	set of operations.

sub is_list {
    my $self = shift;

    ## Things are not lists.

    return 0;
}

sub content {
    my $self = shift;

    ## Return (optionally set) the content list.

    if (@_) {
	@array = @_;
	$self->{'_content'} = \@array;
    } else {
	$self->{'_content'};
    }
}

sub as_thing {
    my ($self) = @_;

    return $self;
}

sub empty_element {
    my ($self, $tag) = @_;

    ## Returns true if there is no end tag.
    ##	 Optionally takes a tag, so you can test the syntax of an 
    ##	 arbitrary tag.

    $tag = $self->tag unless defined $tag;
    !defined($tag) || $emptyElement{$tag};
}


sub push {
    ## Push something into the content.  

    my $self = shift;
    $self->init_content unless exists $self->{'_content'};
    my $content = $self->{'_content'};
    for (@_) {
	if (ref($content) ne 'ARRAY') {
	    $content->push($_);
	} elsif (ref($_) eq 'ARRAY') {
	    $self->push(@$_);
	} else {
	    push(@$content, $_);
	}
    }
    $self;
}


sub unshift {
    ## unshift something into the content, i.e. attach it to the front.

    my $self = shift;
    $self->init_content unless exists $self->{'_content'};
    my $content = $self->{'_content'};
    for (@_) {
	if (ref($content) ne 'ARRAY') {
	    $content->unshift($_);
	} elsif (ref($_) eq 'ARRAY') {
	    unshift(@$content, $_);
	} else {
	    unshift(@$content, $_);
	}
    }
    $self;
}

sub item {
    my ($self, $index, $value) = @_;

    ## Return (optionally set) an indexed item of content.

    $self->{'_content'} = [] unless exists $self->{'_content'};
    my $content = $self->{'_content'};
    return unless defined $content;

    if (defined $value) {
	$content->[$index] = $value;
    } else {
	$content->[$index];
    }
}

#############################################################################
###
### Features:
###	Features are functions, usually boolean, whose values are
###	computed the first time they are used and cached in a
###	DS::Features object.
###

sub features {
    my($self, $features) = @_;

    if (defined $features) {
	$$self{_features} = $features;
    } elsif (! defined ($features = $$self{_features})) {
	$$self{_features} = $features = new DS::Features;
    }
    return $features;
}

sub feature_computers {
    my ($self, $demand) = @_;

    ## Return a reference to the hash that associates feature names
    ##	 with the functions that compute them.  Override in subclasses 
    ##	 that share computers among their instances.

    ##   If $demand is true, create the hash if it's not defined.

    return $self->attr_default('_computers', {}) if $demand;
    return $self->attr('_computers');
}

sub feature_computer {
    my ($self, $feature, $computer) = @_;

    ## Add a new feature computer.

    my $computers = $self->feature_computers(1);
    $$computers{$feature} = $computer;
}

sub compute_feature {
    my ($self, $feature) = @_;

    ## Compute a named feature and return its value.
    ##	 the feature's value is _not_ cached; that's done by compute.

    my $computers = $self->feature_computers;
    return '' unless $computers;
    my $computer = $computers->{$feature};
    return $computer? &{$computer}($self, $feature) : '';
}

### The following are all delegated to DS::Features:

sub get_feature {
    my ($self, $feature) = @_;
    $self->features->get_feature($feature, $self);
}

sub set_feature {
    my ($self, $feature, $value) = @_;
    $self->features->set_feature($feature, $value);
}

sub has {
    my ($self, $feature) = @_;
    $self->features->has($feature);
}

sub is {
    my ($self, $feature) = @_;
    return $self->features->test($feature, $self);
}

sub test {
    my ($self, $feature) = @_;
    return $self->features->test($feature, $self);
}

sub compute {
    my ($self, $feature) = @_;
    return $self->features->compute($feature, $self);
}

sub assert {
    my ($self, $feature, $value) = @_;
    $self->features->assert($feature, $value);
}

sub deny {
    my ($self, $feature) = @_;
    $self->features->deny($feature);
}


#############################################################################
###
### Conversion to String:
###

sub as_string {
    my ($self, $contentOnly) = @_;

    ## Convert a token, or its content if $contentOnly is true,
    ##	  to a string.


    my $string = $self->content_string;
    return $string if $contentOnly;

    $string = $self->starttag . $string;
    $string .=  $self->endtag 
	if ($self->needs_end_tag || $self->internal_content);
    return $string;
}


sub starttag {
    my ($self) = @_;

    ## Returns the string representing the element's start tag.
    ##	  Note that some tags have nonstandard representations,
    ##	  and that attributes might be references to objects.

    my $name = $self->{'_tag'};

    return '' unless defined $name;

    if ($self->internal_content) {
	return "<$name";
    }
    my $tag = "<$name";
    my $list = $self->{'_list'};
    $list = sort keys %$self unless defined $list;
    for (@$list) {
	next if /^_/;
	my $val = $self->{$_};
	if ($_ eq $val &&
	    exists($boolean_attr{$name}) && $boolean_attr{$name} eq $_) {
	    $tag .= " $_";
	} else {
	    HTML::Entities::encode_entities($val, '&">'); #"
	    $val = qq{"$val"} unless $val =~ /^\d+$/;
	    $tag .= qq{ $_=$val};
	}
    }
    "$tag>";
}

sub endtag {
    my ($self) = @_;

    my $name = $self->{'_tag'};
    return '' if $name eq '' || $name eq ':' || !defined($name);
    my $ending = $internal_content_tags{$name};
    if (defined $ending) {
	return "$ending>";
    }
    "</$name>";
}



#############################################################################
###
### Syntax:
###
###	...
###	It's not clear what to do when we see an end tag for something
###	that shouldn't need it.  We really need to get the full SGML
###	parser running.

@empty_tags = (	
	       '!', '!--', '?', 
	       'img', 'hr', 'br', 'link', 'input',

	       ## The following are dubious; there's no good way to
	       ##    handle implicit end tags yet.
	        'p', # 'li', 'dt', 'dd',

	       );


%empty_elements = {};
foreach $e (@empty_tags) {
    $empty_elements{$e} = 1;
}

%internal_content_tags = ('!' => '',
			  '!--' => '--',
			  '?' => '',
			  );

sub needs_end_tag {
    my ($self, $interp) = @_;

    return 0 unless $self->tag;
    return 0 if $self->{_endless};
    (defined $empty_elements{$self->tag})? 0 : 1;
}

sub internal_content {
    my ($self, $interp) = @_;

    return 0 unless $self->tag;
    return exists $internal_content_tags{$self->tag};
}

$syntax = {

};

1;

