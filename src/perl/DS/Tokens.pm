package DS::Tokens; ###### List of InterForm Tokens
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
###	A Token List is a List of IF::IT tokens.  Unlike a simple DS::List,
###	strings and lists are merged when appended.  This should probably be 
###	moved to the IF package.
###

use DS::List;
push(@ISA, DS::List);

#############################################################################
###
### Constructor:
###

### Inherited from DS::List


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
    ##	  Strings are merged.  Arrays and Lists are appended.

    for $foo (@values) {
	if ((ref $foo) eq 'ARRAY') {
	    $self->push(@$foo);
	} elsif (ref $foo) {
	    if (! $foo->is_list) {
		push(@$self, $foo);
	    } else {
		$self->push(@{$foo->content});
	    }
	} else {
	    # The current element is a text segment
	    if (@$self && !ref $self->[-1]) {
		# last content element is also text segment
		$self->[-1] .= $foo;
	    } else {
		push(@$self, $foo);
	    }
	}
    }
    $self;
}

sub unshift {
    my $self = shift;

    ## unshift something into the content, i.e. attach it to the front.

    for (@_) {
	if (ref $_) {
	    unshift(@$self, $_);
	} else {
	    # The current element is a text segment
	    if (@$self && !ref $self->[0]) {
		# first content element is also text segment
		$self->[0] =$_ . $self->[0];
	    } else {
		unshift(@$self, $_);
	    }
	}
    }
    $self;
}

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


1;

