###### Interform Actor
###	$Id$
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
    my $self = IF::IT->new('_actor_', @attrs);
    bless $self, $class;
    $self->initialize($name);
}

sub recruit {
    my ($class, $self) = @_;

    ## Recruit a new actor:
    ##	  Re-bless and properly initialize an InterForm Token.

    bless $self, $class;
    $self->tag('_actor_');
    $self->initialize;
}


sub initialize {
    my ($self, $name) = @_;

    ## Initialize an actor.
    ##	  Force the actor to obey the standard conventions:
    ##	    force name lowercase to match tag if active
    ##	    'active' attribute if active.

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
    }

    if ($active) {
	$name = lc $name;
	$self->attr('tag', $name) unless defined $tag;
    }
    $self->attr('name', $name);

    if (! $self->attr('_handle')) {
	$self->{'_handle'} = ($self->is_empty) ? \&IF::Actors::null_handle 
	                                       : \&IF::Actors::generic_handle;
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
    return &$code($self, $it, $ii, $incomplete) if (ref($code) eq 'CODE');
    return false;
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
    $ii->add_handler($self);
    $ii->parse_it;
}

sub act_quoted {
    my ($self, $it, $ii, $inc, $quoting) = @_;

    ## quoted:
    ##	  Tell the interpretor to parse the contents without evaluating.

    return 0 if $quoting || $inc <= 0;
    $ii->add_handler($self);
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
	$ii->add_handler($self);
    }
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
	$ii->add_handler($self);
    }
}


1;
