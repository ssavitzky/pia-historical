###### Interform Token
###	$Id$
###
###	An InterForm Token is like an HTML::Element, only much simpler.
###	In particular, it does not have a _parent link, and the _tag is
###	optional (i.e. it may be a simple list of elements).  Tokens may
###	also include declarations, comments, MIME headers, and simple text.
###

package IF::IT;


#############################################################################
###
### Constructor:
###

sub new
{
    my ($class, $tag, @attrs) = @_;

    ## IF::IT->new($tag, $attr => $val, ...)
    ##	 Make a new token.  
    ##	 $tag may take on the following special values:
    ##	    "!"	  -- SGML declaration
    ##	    "!--" -- comment
    ##	    ":"   -- MIME header
    ##	    undef -- text

    my $self = bless {}, $class;
    $self->{_tag} = lc $tag if defined $tag;
    my $list = [];
    $self->{_list} = $list;

    my($attr, $val);
    while (($attr, $val) = splice(@attrs, 0, 2)) {
	$val = $attr unless defined $val;
	$attr = lc $attr;
	$self->{$attr} = $val;
	push(@$list, lc $attr) unless $attr =~ /^\_/;
    }
    $self;
}



#############################################################################
###
### Access to Components:
###

sub tag
{
    ## Return (optionally set) the tag.

    my $self = shift;
    if (@_) {
	$self->{'_tag'} = lc $_[0];
    } else {
	$self->{'_tag'};
    }
}

sub attr
{
    my ($self, $attr, $val) = @_;

    ## Return (optionally set) an attribute

    my $attr = lc $attr;
    my $old = $self->{$attr};
    if ($val) {
	my $list = $self->{_list};
	$self->{$attr} = $val;
	push(@$list, $attr) unless defined $old;
    }
    $old;
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

    ## return all the attributes in a list of pairs

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
    my @list = $self->attr_list;
    for (@$list) {
	push(@$attrs, $_);
	push(@$attrs, $self->{$_});
    }
    return $attrs;
}

sub content
{
    # Return the content.

    shift->{'_content'};
}

sub is_empty
{
    my $self = shift;
    !exists($self->{'_content'}) || !@{$self->{'_content'}};
}

sub is_active { 
    return 0;
}

### Content:
###	We use the simplified syntax of push, pop, unshift, and shift
###	rather than the clumsier push_content, and provide a complete set.

sub push {
    ## Push something into the content.  Strings are merged.

    my $self = shift;
    $self->{'_content'} = [] unless exists $self->{'_content'};
    my $content = $self->{'_content'};
    for (@_) {
	if (ref $_) {
	    push(@$content, $_);
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

sub pop
{
    ## Pop the content

    my $self = shift;
    return unless exists $self->{'_content'};
    my $content = $self->{'_content'};
    pop @$content;
}

sub shift
{
    ## Shift the content

    my $self = shift;
    return unless exists $self->{'_content'};
    my $content = $self->{'_content'};
    shift @$content;
}

sub unshift
{
    ## unshift something into the content

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

#############################################################################
###
### Conversion to String
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
	    if (ref($_)) { $string .= $_ -> as_HTML; }
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
### Syntax:
###
###	...
###

@empty_tags = (	
	       '!', '!--', '?', 
	       'img', 'hr', 'br',
	       'li', 'dt', 'dd', 'link',endt
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

