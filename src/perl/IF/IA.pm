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
    $self->initialize;
}


sub initialize {
    my ($self, $name, $active) = @_;

    ## Initialize an actor.
    ##	  Force the actor to obey the standard conventions:
    ##	    force name lowercase to match tag if active
    ##	    'active' attribute if active.

    $name = $self->attr('name') unless defined $name;
    $active = $self->attr('active') unless defined $active;
    my $hook = $active? '_act_for' : '_act_on';

    if ($self->attr('content')) {
	$self->hook($hook, \&act_generic);
    } elsif ($self->attr('empty')) {
	$self->hook($hook, \&act_empty);
    } else {
	$self->hook($hook, \&act_quoted) if $self->attr('quoted');
	$self->hook($hook, \&act_parsed) if $self->attr('parsed');
    }

    if ($active) {
	$name = lc $name;
	$self->attr('tag', $name);
	$self->attr('active', 'active');
    }
    $self->attr('name', $name);

    if (! $self->attr('_handle')) {
	$self->attr('_handle', \&generic_handle);
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
###	a handler.  An actor that *operates on* content can register
###	itself as a passive actor.  An actor that operates on *itself*
###	must return true from is_active and supply an act routine.  A 
###	passive actor can activate an element by re-blessing it as an
###	actor. 
###
###	When an action routine is called, the actor can call
###	$ii->token with an argument to replace the parser's current
###	token.   

sub is_active {
    my ($self) = @_;

    ## Return true if the actor is active.

    return defined $self->{_act};
}

sub act {
    my ($self, $ii, $inc, $quoting) = @_;

    ## Perform the action routine associated associated with this token.
    ##	  Called if some passive actor marks the token ``active'' by
    ##	  re-blessing it as an actor.

    my $code = $self->{_act};
    return &$code($self, $ii, $inc, $quoting) if (ref($code) eq 'CODE');
}

sub act_on {
    my ($self, $it, $ii, $inc, $quoting) = @_;

    ## Act on a token.  
    ##	  $incomplete will be true if this is the start tag for an
    ##	  element for which content is expected.  In that case, the 
    ##	  parse stack will already have been pushed.

    my $code = $self->{_act_on};
    return unless defined $code;
    return &$code($self, $it, $ii, $inc, $quoting) if (ref($code) eq 'CODE');
}

sub act_for {
    my ($self, $it, $ii, $inc, $quoting) = @_;

    ## Act ``for'' a token.  
    ##	  This is called for ``active'' actors that match the tag of the
    ##	  token being evaluated.

    my $code = $self->{_act_for};
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


sub generic_handle {
    my ($self, $it, $ii) = @_;

    ## This is the handler for a generic agent with content.
    ## === not really clear what to do about context ($it)
    ## => use an attribute for the name (or parts), and define entities
    ##	  using shallow binding.

    $ii->defvar("context", $it); # === not very satisfactory
    $ii->push_into($self->content);
    $ii->delete_it;
}

1;
