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
	$self->{$attr} = $val;
	push(@$list, $attr) unless $attr =~ /^\_/;
    }
    $self;
}



#############################################################################
###
### Access to Components:
###

sub tag {
    my $self = shift;

    ## Return (optionally set) the tag.

    if (@_) {
	$self->{'_tag'} = $_[0];
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
	if (! defined $old && $attr !~ /^_/) {
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
	if ($attr !~ /^_/) {
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
	    next if /^_/;
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

sub is_empty {
    my $self = shift;

    ## Returns true if there is no content.

    !exists($self->{'_content'}) || !@{$self->{'_content'}};
}


sub push {
    ## Push something into the content.  
    ##	  Strings are merged.  Arrays are appended.
    ##	  Tagless tokens have their content treated as arrays.
    ## === We should leave the merging, etc. to subclasses that need it

    my $self = shift;
    $self->{'_content'} = [] unless exists $self->{'_content'};
    my $content = $self->{'_content'};
    for (@_) {
	if (ref($_) eq 'ARRAY') {
	    $self->push(@$_);
	} elsif (ref $_) {
	    my $t = $_->{'_tag'};
	    if ($t) {
		push(@$content, $_);
	    } else {
		$self->push($_->content);
	    }
	} else {
	    # The current element is a text segment
	    if (@$content && !ref $content->[-1]) {
		# last content element is also text segment
		$content->[-1] .= $_;
	    } else {
		push(@$content, $_);
	    }
	}
    }
    $self;
}

sub pop {
    ## Pop the content

    my $self = shift;
    return unless exists $self->{'_content'};
    my $content = $self->{'_content'};
    pop @$content;
}

sub shift {
    ## Shift the content

    my $self = shift;
    return unless exists $self->{'_content'};
    my $content = $self->{'_content'};
    shift @$content;
}

sub unshift {
    ## unshift something into the content, i.e. attach it to the front.

    my $self = shift;
    $self->{'_content'} = [] unless exists $self->{'_content'};
    my $content = $self->{'_content'};
    for (@_) {
	if (ref $_) {
	    unshift(@$content, $_);
	} else {
	    # The current element is a text segment
	    if (@$content && !ref $content->[0]) {
		# last content element is also text segment
		$content->[0] = $content->[0] . $_;
	    } else {
		unshift(@$content, $_);
	    }
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
	$$self{_features} = $features = new DS::Features($self);
    }
    return $features;
}

sub feature_computers {
    my ($self, $features, $demand) = @_;

    ## Return a reference to the hash that associates feature names
    ##	 with the functions that compute them.  This should be
    ##	 overridden in subclasses.

    ##   If $demand is true, create the hash if it's not defined.

    return $self->attr_default('_computers', {}) if $demand;
    return $self->attr('_computers');
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

    my $string = '';
    $string .= $self->starttag unless $contentOnly;

    my $content = $self->content;
    if (defined $content) {
	for (@$content) {
	    if (ref($_)) { 
		if(ref($_) eq 'HTML::Element') {
		    $string .= $_ -> as_HTML; } 
		else {$string .= $_->as_string($contentOnly);}}
	    else         { $string .= $_; }
	    ## Note that we need as_HTML because legacy code is still
	    ## generating HTML::Element's
	}
    }
    return $string if $contentOnly;
    $string .=  $self->endtag 
	if ($self->needs_end_tag || $self->internal_content);
    return $string;
}

sub content_string {
    my ($self) = @_;

    ## Returns the content as a string

    return $self->as_string(1);
}

sub content_text {
    my ($self) = @_;

    ## Returns only the text part of the content.  
    ##	  All markup is stripped off.

    my $string = '';
    my $content = $self->content;
    if (defined $content) {
	for (@$content) {
	    if (ref($_)) { $string .= $_ -> content_text; }
	    else         { $string .= $_; }
	}
    }
    return $string;
}

sub is_text {
    my ($self) = @_;

    ## Returns true if the content is a single string.

    my $content = $self->content;
    return @$content == 1 && !ref($content->[0]);
}

sub content_thing {
    my ($self) = @_;

    ## Returns the content as a single Thing (or string).

    my $content = $self->content;
    return '' if undef $content || !@$content;
    return $content->[0] if @$content == 1;
    my $token = DS::Thing->new();
    for (@$content) {
	$token->push($_); 
    }
    return $token;
}


sub as_HTML {
    my ($self) = @_;
    return $self->as_string;
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
### Traversal:
###

sub traverse
{
    my($self, $callback, $ignoretext, $depth) = @_;
    $depth ||= 0;

    print "traversing $depth tag = " . $self->{_tag} . "\n" if $main::debugging;

    if (! defined $self->{_tag} || &$callback($self, 1, $depth)) {
	for (@{$self->{'_content'}}) {
	    if (ref $_) {
		$_->traverse($callback, $ignoretext, $depth+1);
	    } else {
		&$callback($_, 1, $depth+1) unless $ignoretext;
	    }
	}
	&$callback($self, 0, $depth) 
	    unless ! defined $self->{_tag} || $emptyElement{$self->{'_tag'}};
    }
    $self;
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

