###### Interform Actor
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
###	This is the parent class for actors that operate inside of
###	Interforms.  An actor is basically an active SGML element;
###	indeed, it would be more correct to say that an element is an
###	especially trivial and passive actor.

package IF::IA;			# IA, IA Cthulhu f'htagn... (oops!)

use IF::IT;
push(@ISA,IF::IT);

### === remove_spaces and list_items should be methods in IF::IT
### === analyze should be a method in IF::IA

#############################################################################
###
### Creation:
###

sub new {
    my ($class, $name, @attrs) = @_;
    my $self = IF::IT->new('-actor-', @attrs);
    bless $self, $class;
    $self->initialize($name);
}

sub recruit {
    my ($class, $self) = @_;

    ## Recruit a new actor:
    ##	  Re-bless and properly initialize an InterForm Token.

    bless $self, $class;
    $self->initialize;
}

sub handler {
    my ($name, $pkg) = @_;

    ## Return a handler for $name in package $pkg.
    ##	  Requires major disgusting hacks to get around perl weirdness.

    my $foo = "${pkg}::$name";
    return \&{$foo} if defined &{$foo};
    return \&{$foo . "_handle"} if defined &{$foo . "_handle"};

    ## The first part will do the job UNLESS the handle we want is inside 
    ##	 a package that isn't loaded yet.  So load it.  This is _still_ not
    ##	 good enough, because it might be defined in a package that _uses_
    ##	 Actors.pm, (e.g. IF::Run) so the handle might not actually get 
    ##	 defined until after we need it.  You don't want to know.

    my $file = $pkg;
    $file =~ s@::@/@g;
    eval { 
	require $file . '.pm';
	print "$file loaded, looking for '$foo'\n" if $main::debugging;
	return \&{$foo} if defined \&{$foo};
	## Note that the blasted thing might not be defined.  
	##    It appears, though, that this will succeed anyway, so you 
	##    croak later if the name is never defined.
    };
}

sub initialize {
    my ($self, $name) = @_;

    ## Initialize an actor.
    ##	  Force the actor to obey the standard conventions:
    ##	    force name lowercase to match tag if active
    ##	    'active' attribute if active.

    ## If there's an 'element' attribute, this ``actor'' is just a 
    ## 	  syntactic description of an HTML element, and is not active.
    ##	  It's not used for anything at the moment.

    my $element = $self->attr('element');
    $self->tag($element? '-element-' : '-actor-');

    $name = $self->attr('name') unless defined $name;
    my $tag = $self->attr('tag');
    my $active = $self->attr('active');
    $active = ($tag || $name !~ /^-/ ) unless defined $active;
    my $hook = '_action';

    if ($self->attr('content')) {
	$self->hook($hook, \&act_generic);
    } elsif ($self->attr('empty')) {
	$self->hook($hook, \&act_empty);
    } else {
	$self->hook($hook, \&act_parsed) unless $self->attr('unparsed');
	$self->hook($hook, \&act_quoted) if $self->attr('quoted');
	$self->hook($hook, \&act_streamed) if $self->attr('streamed');
    }

    if ($active) {
	$name = lc $name;
	$self->attr('tag', $name) unless defined $tag;
    }
    $self->attr('name', $name);

    ## Handle match='name=value...'  Should handle attr=, value= as well.
    ## 	  List is encoded as a query string.
    my $match = $self->attr('match');
    if ($match) {
	my @list = split(/\&/, $match);
	my @pairs = ();
	for $item (@list) {
	    if ($item =~ /(.+)=(.*)/) {
		$2 =~ s/\+/ /g;
		push(@pairs, $1, $2);
	    } else {
		push(@pairs, $item, $item);
	    }
	}
	$self->{'_match'} = \@pairs;
    }

    my $handle = $self->attr('handle');
    if (lc $handle eq 'handle' || $handle eq '1') {
	$handle = $name;
    } elsif (lc $handle eq 'null') {
	$handle = '';
    }
    if ($handle) {
	$handle =~ s/^[-.]//;
	$handle =~ s/[-.]$//;
	$handle =~ s/[-.]/\_/g;
	my $package = $self->attr('package') || 'IF::Actors';
	my $handler = handler($handle, $package);
	if ($handler) {
	    $self->{'_handle'} = $handler;
	} else {
	    print "Cannot find handler $handle in package $package\n";
	}
    } elsif (! $self->is_empty) {
	$self->{'_handle'} = \&generic_handle;
    }
    $self;
}

sub name {
    my ($self, $v) = @_;

    defined $v?  $self->attr('name', $v) : $self->attr('name');
}


#############################################################################
###
### Access to hooks:
###

sub hook {
    my ($self, $name, $code) = @_;

    $self->{$name} = $code if defined $code;
    $self->{$name};
}

#############################################################################
###
### Matching:
###
###	Actors come in two (mutually exclusive) flavors:  
###	  1. those that match a particular tag.
###	  2. those that are activated based on features.
###
### ===	Eventually matching will use a feature set, just like the PIA.
###	However, it will still be useful to allow the use of generic code.
###

sub matches {
    my ($self, $it, $ii, $incomplete) = @_;

    my $code = $self->{_match};
    if (ref($code) eq 'CODE') {
	return &$code($self, $it, $ii, $incomplete);
    } elsif (ref($code)) {
	## Assume it's a list of attr, value...
	## Values are matched exactly, ignoring case

	return 0 if $incomplete <= 0 || !ref($it);
	for ($i = 0; $i <= $#$code; $i += 2) {
	    my $a = $$code[$i];
	    my $v = $$code[$i+1];
	    my $try = $it->attr($a);
	    return 0 unless (($v && lc $try eq lc $v) || (! $v && ! $try));
	}
	return 1;
    }
    return 0;
}

#############################################################################
###
### Action:
###
###	Actors are checked for when a start tag is encountered.  At
###	that point it might be an empty tag, in which case the actor
###	can simply do its thing, or there might be content coming.
###
###	An actor that needs to wait for content can register itself as
###	a handler.  The act_on routine for a start tag can eitherperform 
###	the whole action, or register a handler (preferred).
###

sub act_on {
    my ($self, $it, $ii, $inc, $quoting) = @_;

    ## Act on a token.  
    ##	  $incomplete will be true if this is the start tag for an
    ##	  element for which content is expected.  In that case, the 
    ##	  parse stack will already have been pushed.

    my $code = $self->{_action};
    return unless defined $code;
    return &$code($self, $it, $ii, $inc, $quoting) if (ref($code) eq 'CODE');
}

sub handle {
    my ($self, $it, $ii) = @_;

    ## Handle a token.  
    ##	  This is called when acting on an empty element, or if
    ##	  registered as end-tag handler.  The parse stack will have 
    ##	  been popped (or never pushed in the first place).

    my $code = $self->{_handle};
    return &$code($self, $it, $ii) if (ref($code) eq 'CODE');
}
sub end_input {
    my ($self, $it, $ii) = @_;

    ## Handle end of input
    ##	  This is called when an actor has been pushed onto the 
    ##	  interpretor's input stack, and the end of its associated
    ##	  input has been reached.

    ##	  The handler should return a string (end tag) or undefined.

    my $code = $self->{_end_input};
    return &$code($self, $it, $ii) if (ref($code) eq 'CODE');
}

#############################################################################
###
### Action routines shared by many actors:
###
###	These are picked up by $actor->initialize according to the various
###	attributes of the new actor:
###

sub act_parsed {
    my ($self, $it, $ii, $inc, $quoting) = @_;

    ## parsed:
    ##	  Tell the interpretor to parse (and evaluate) the contents.

    return 0 if $quoting || $inc <= 0;
    $ii->add_handler($self) if $self->{_handle};
    $ii->parse_it;
}

sub act_quoted {
    my ($self, $it, $ii, $inc, $quoting) = @_;

    ## quoted:
    ##	  Tell the interpretor to parse the contents without evaluating.

    return 0 if $quoting || $inc <= 0;
    $ii->add_handler($self) if $self->{_handle};
    $ii->quote_it($self->attr('quoted'));
}

sub act_empty {
    my ($self, $it, $ii, $incomplete, $quoting) = @_;

    ## empty:
    ##	  Tell the interpretor not to expect an end tag

    if ($incomplete > 0) { 
	$ii->complete_it($it);
	$it->attr(_endless, 1);
    } elsif (! $quoting) {
	$ii->add_handler($self) if $self->{_handle};
    }
}

sub act_streamed {
    my ($self, $it, $ii, $inc, $quoting) = @_;

    ## streamed:
    ##	  Perform the handler immediately.  Leave the parser doing whatever 
    ##	  it _was_ doing.  === Worry about whether or not to pass it.

    return 0 if $quoting || $inc <= 0;
    $self->handle($it, $ii, $inc);
}

sub act_generic {
    my ($self, $it, $ii, $incomplete, $quoting) = @_;

    ## Generic:
    ##	  Presence of end tag is based on whether there's a content attribute. 
    ##	  The name of the attribute is the value of 'content'

    if ($incomplete > 0) { 
	my $content = $self->attr('content');
	if (defined $content && defined $it->attr($content)) {
	    $ii->complete_it($it);
	    $it->attr(_endless, 1);
	} elsif (!$quoting) {
	    my $quoted = $self->attr('quoted');
	    if (defined $quoted) {
		$ii->quote_it($quoted);
	    } else {
		$ii->parse_it;
	    }
	}
    } elsif (! $quoting) {
	$ii->add_handler($self) if $self->{_handle};
    }
}

#############################################################################
###
### Shared handles:
###

sub null_handle {
    my ($self, $it, $ii) = @_;

    ## Just pass the tag and its contents.  Take no action
    ##	 === not really used in the Perl version.

}

sub generic_handle {
    my ($self, $it, $ii) = @_;

    ## This is the handler for a generic agent with content.
    ## => use an attribute for the name (or parts), and define entities
    ##	  using shallow binding.

    ## === not clear if this will end up in the right state frame; 
    ##	   we may need to push a tagless node in order to have the
    ##	   variables and tagset popped when $self->content is done.

    $ii->defvar("element", $it);
    $ii->defvar("content", $it->content);
    $ii->push_into($self->content);

    ## Set up tagset context for content.
    my $ts = $self->attr('tagset');
    $ii->tagset($ts) if $ts;

    $ii->delete_it;
}

1;
