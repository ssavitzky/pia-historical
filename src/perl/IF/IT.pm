package IF::IT;  ###### Interform Token
###	$Id$
###	Copyright 1997, Ricoh California Research Center.
###
###	An InterForm Token is like an HTML::Element, only somewhat simpler.
###	In particular, it does not have a _parent link, and the _tag is
###	optional (i.e. it may be a simple list of elements).  Tokens may
###	also include declarations, comments, MIME headers, and simple text.
###

use DS::Thing;
push(@ISA, DS::Thing);

#############################################################################
###
### Constructor:
###

sub new {
    my ($class, $tag, @attrs) = @_;

    ## IF::IT->new($tag, $attr => $val, ..., [$content...])
    ##	 Make a new token.  
    ##	 $tag may take on the following special values:
    ##	    "!"	  -- SGML declaration
    ##	    "!--" -- comment
    ##	    ":"   -- MIME header
    ##	    ''    -- text
    ##	    undef -- token list

    ##	 Instance Variables:
    ##	    _tag    -- the tag
    ##	    _list   -- list of attributes in the order defined.
    ##	    _status -- 0: complete, 1: start tag, -1: end tag
    ##	    _empty  -- true if no end tag is required.


    my $self = bless {}, $class;
    $self->{_tag} = lc $tag if defined $tag;
    my $list = [];
    $self->{_list} = $list;

    my($attr, $val);
    while (($attr, $val) = splice(@attrs, 0, 2)) {
	if (! defined $val) {
	    $self->push($attr);
	    return $self;
	}
	$attr = lc $attr;
	push(@$list, $attr) unless (exists $self->{$attr} || $attr =~ /^\_/);
	$self->{$attr} = $val;
    }
    $self;
}

sub init_content {
    my $self = shift;
    $self->{'_content'} = DS::Tokens->new;
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
    ##	 Overridden in order to force attributes to lowercase.

    my $attr = lc $attr;
    my $old = $self->{$attr};
    if (defined $val) {
	my $list = $self->{_list};
	$self->{$attr} = $val;
	push(@$list, $attr) unless defined $old;
    }
    $old;
}

sub is_empty {
    my $self = shift;

    ## Returns true if the tag has no content.
    ##	  This is different from empty, which really means no end tag
    ##	  is required, but content may be present.

    $self->{_empty} || !exists($self->{'_content'}) || !@{$self->{'_content'}};
}

sub empty {
    my ($self, $v) = @_;

    ## Returns true if the tag has been marked as not requiring an end tag.

    $self->{_empty} = $v if defined $v;
    $self->{_empty};
}

sub status {
    my ($self, $v) = @_;
    $self->{_status} = $v if defined $v;
    $self->{_status};
}

sub is_active { 
    return 0;
}

### Handlers:
###	There are two kinds of handlers: enter and leave.  Both are
###	called by the Interform Interpretor to perform semantic actions
###	on an already-parsed subtree.

sub enter_handlers {
    my ($self, @v) = @_;

    ## Returns a reference to the list of enter handlers.  
    ##	  Any additional arguments are pushed onto the list.

    my $list = $self->{'_enter_handlers'};
    return $list unless @v;

    $self->{'_enter_handlers'} = $list = [] unless defined $list;
    push(@$list, @v);
    return $list;
}

sub leave_handlers {
    my ($self, @v) = @_;

    ## Returns a reference to the list of enter handlers.  
    ##	  Any additional arguments are pushed onto the list.

    my $list = $self->{'_leave_handlers'};
    return $list unless @v;

    $self->{'_leave_handlers'} = $list = [] unless defined $list;
    push(@$list, @v);
    return $list;
}

#############################################################################
###
### Content:
###	Most of this is inherited from DS::Thing; we only override what we
###	have to in order to get strings merged, etc.

sub push {
    ## Push something into the content.  
    ##	  Strings are merged.  Arrays are appended.
    ##	  Tagless tokens have their content treated as arrays.

    my $self = shift;
    $self->init_content unless exists $self->{'_content'};
    my $content = $self->{'_content'};
    for (@_) {
	if (ref($content) ne 'ARRAY') {
	    $content->push($_);
	} elsif (ref($_) eq 'ARRAY') {
	    $self->push(@$_);
	} elsif (ref $_) {
	    my $t = $_->tag;
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

sub push_content {
    ## Push something into the content.  Same as push, for legacy code.

    my $self = shift;
    $self->push(@_);
}


sub unshift {
    ## unshift something into the content, i.e. attach it to the front.

    my $self = shift;
    $self->init_content unless exists $self->{'_content'};
    my $content = $self->{'_content'};
    for (@_) {
	if (ref($content) ne 'ARRAY') {
	    $content->unshift($_);
	} elsif (ref $_) {
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
    my ($self) = @_;

    ## Convert a token, or its content if $contentOnly is true,
    ##	  to a string.

    my $string = '';
    $string .= $self->starttag . $self->content_string;
    $string .=  $self->endtag 
	if ($self->needs_end_tag || $self->internal_content);
    return $string;
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

sub link_text {
    my ($self, $tags) = @_;

    ## Returns only the text part of the content that is inside one of
    ##	  the listed tags (default <a>).  All markup is stripped off.
    ##	  The tags are passed as a reference to a hash.

    $tags = {'a' => 1} unless $tags;
    my $string = '';
    my $content = $self->content;
    if (defined $content) {
	for (@$content) {
	    if (ref($_) && $$tags{$_->tag}) {
		$string .= $_ -> content_text;
	    }
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

sub content_token {
    my ($self) = @_;

    ## Returns the content as a single token (or string).
    ##	 This means making a tagless node if necessary.

    my $content = $self->content;
    return if (! defined $content);
    return $content->[0] if ((1 == @{$content}) && ref($content->[0]));
    my $token = IF::IT->new();
    for (@{$content}) {
	$token->push($_);
    }
    return $token;
}


sub as_HTML {
    my ($self) = @_;

    ## === eventually, protect markup. ===

    $string = $self->as_string;
    HTML::Entities::encode_entities($string, '&">'); #"
    $string;
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
	$val = $val->as_string if ref $val; # === should really be as_attr
	if ($_ eq $val 
	    ## && exists($boolean_attr{$name}) && $boolean_attr{$name} eq $_
	    ) {
	    $tag .= " $_";
	} else {
	    ## Had & in encode_entities, but this breaks entities already there
	    HTML::Entities::encode_entities($val, '">'); #"
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

sub traverse {
    my($self, $callback, $ignoretext, $depth) = @_;

    ## Call &$callback($token, $start, $depth) for $self and every
    ##	  token in $self->content.  callback is invoked only once 
    ##	  (with $start=1) for empty tags, twice for non-empty tags.

    $depth ||= 0;
    print "traversing $depth tag = " . $self->{_tag} . "\n" 
	if $main::debugging > 1;

    if (! defined $self->{_tag} || &$callback($self, 1, $depth)) {
	for (@{$self->{'_content'}}) {
	    if (ref $_) {
		$_->traverse($callback, $ignoretext, $depth+1);
	    } else {
		&$callback($_, 1, $depth+1) unless $ignoretext;
	    }
	}
	&$callback($self, 0, $depth) 
	    unless (! defined $self->{_tag} || $emptyElement{$self->{'_tag'}});
    }
    $self;
}

### === extract_links removed: unused.

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
	       'get',
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

