package DS::Content; ###### Content mixin
###	$Id$

##############################################################################
 # The contents of this file are subject to the Ricoh Source Code Public
 # License Version 1.0 (the "License"); you may not use this file except in
 # compliance with the License.  You may obtain a copy of the License at
 # http://www.risource.org/RPL
 #
 # Software distributed under the License is distributed on an "AS IS" basis,
 # WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 # for the specific language governing rights and limitations under the
 # License.
 #
 # This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 # created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 # Rights Reserved.
 #
 # Contributor(s):
 #
############################################################################## 

###
###	This package implements the shared behavior of DS::Thing and
###	DS::List with respect to their content.  It should have a different 
###	name; possibly DS::Items.
###


#############################################################################
###
### Predicates:
###

sub is_empty {
    my $self = shift;

    ## Returns true if there is no content.

    my $content = $self->content;
    return !ref($content) || !@$content;
}


sub content_size {
    my $self = shift;

    ## Returns the number of items

    my $content = $self->content;
    (ref $content)? scalar @$content : 0;
}


#############################################################################
###
### Insertion:
###	We use the simplified syntax of push, pop, unshift, and shift
###	rather than the clumsier push_content, and provide a complete
###	set of operations.
###

sub pop {
    my $self = shift;

    ## Pop the content

    my $content = $self->content;
    return unless ref $content;
    pop @$content;
}

sub shift {
    my $self = shift;

    ## Shift the content

    my $content = $self->content;
    return unless ref $content;
    shift @$content;
}


#############################################################################
###
### Conversion to String:
###

sub as_string {
    my ($self, $contentOnly) = @_;

    ## Convert content to a string.
    ##	 overridden in DS::Thing

    return $self->content_string;
}

sub content_string {
    my ($self) = @_;

    ## Convert content to a string.

    my $string = '';

    my $content = $self->content;
    if (defined $content) {
	for (@$content) {
	    if (ref($_)) { 
		if(ref($_) eq 'HTML::Element') {
		    ## Note: used only in legacy code.
		    $string .= $_ -> as_HTML; } 
		else {$string .= $_->as_string;}}
	    else         { $string .= $_; }
	}
    }
    return $string;
}

sub as_HTML {
    my ($self) = @_;
    return $self->as_string;
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

sub as_ARRAY {
    my ($self) = @_;

    return unbless($self->content);
}



#############################################################################
###
### Traversal:
###

sub traverse
{
    my($self, $callback, $ignoretext, $depth) = @_;
    $depth ||= 0;

    my $tag = $self->tag;

    print "traversing $depth tag = $tag\n" if $main::debugging;

    if (! defined $tag || &$callback($self, 1, $depth)) {
	for (@{$self->content}) {
	    if (ref $_) {
		$_->traverse($callback, $ignoretext, $depth+1);
	    } else {
		&$callback($_, 1, $depth+1) unless $ignoretext;
	    }
	}
	&$callback($self, 0, $depth) 
	    unless (! defined $tag || $self->empty_element);
    }
    $self;
}


1;

