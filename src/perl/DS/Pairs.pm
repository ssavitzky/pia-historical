package DS::Pairs; ###### List of Pairs
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
###	A DS::Pairs is a list of objects.  Unlike a simple DS::List,
###	items are array references, which are interpreted as [name value] 
###	pairs.  Note that it is possible to distinguish between a name 
###	associated with a null string, and a name not associated with any
###	value at all.  
###
###	Conversion operators are provided between DS::Pairs and lists,
###	headers, query strings, description lists, and tables.
###

use DS::List;
push(@ISA, DS::List);

#############################################################################
###
### Constructon:
###

### new is inherited from DS::List

sub from_query {
    my ($class, @content) = @_;

}

sub from_header {
    my ($class, @content) = @_;

}

sub from_token {
    my ($class, @content) = @_;

}

#############################################################################
###
### Content:
###	We use the simplified syntax of push, pop, unshift, and shift
###	rather than the clumsier push_content, and provide a complete
###	set of operations.

sub push {
    my ($self, @values) = @_;
    my $foo;

    ## Push something into the content.  
    ##	  Arrays and strings of the form "name=value" are pairs.

    for $foo (@values) {
	if ((ref $foo) eq 'ARRAY') {
	    $self->push(@$foo);
	} elsif (ref $foo) {
	    if (! $foo->is_list) {
		push(@$self, [$foo]);
	    } else {
		$self->push(@{$foo->content});
	    }
	} else {
	    push(@$self, [$foo]);
	}
    }
    $self;
}

sub unshift {
    my $self = shift;

    ## unshift something into the content, i.e. attach it to the front.

    for (@_) {
	if (ref $_ eq 'ARRAY') {
	    unshift(@$self, $_);
	} else {
	    unshift(@$self, [$_]);
	}
    }
    $self;
}

#############################################################################
###
### Conversion:
###

sub as_token {
    my ($self) = @_;

    ## Returns $self as a single Token.

    if (@$self == 1 && ref($self->[0])) {
	return $self->[0];
    } else {
	# return IF::IT->new()->push($self);
	my $token = IF::IT->new();
	for (@$self) {
	    $token->push($_); 
	}
	return $token;
    }
}

sub as_query {
    my ($self) = @_;

    ## Returns $self as a query string

}

sub as_header {
    my ($self) = @_;

    ## Returns $self as a header string

}

1;

