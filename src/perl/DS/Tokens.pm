package DS::Tokens; ###### List of InterForm Tokens
###	$Id$
###
###	A Token List is a List of IF::IT tokens.  Unlike a simple DS::List,
###	strings and lists are merged when appended.
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
    my $self = shift;

    ## Push something into the content.  
    ##	  Strings are merged.  Arrays and Lists are appended.

    for (@_) {
	if (ref($_) eq 'ARRAY') {
	    $self->push(@$_);
	} elsif (ref $_) {
	    if (! $_->is_list) {
		push(@$self, $_);
	    } else {
		$self->push(@{$_->content});
	    }
	} else {
	    # The current element is a text segment
	    if (@$self && !ref $self->[-1]) {
		# last content element is also text segment
		$self->[-1] .= $_;
	    } else {
		push(@$self, $_);
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

