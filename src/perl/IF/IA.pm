###### Interform Agent
###	$Id$
###
###	This is the parent class for agents that operate inside of
###	Interforms.  An agent is basically an active SGML element;
###	indeed, it would be more correct to say that an element is an
###	especially trivial and passive agent.

package IF::IA;			# IA, IA Cthulhu f'htagn... (oops!)

use IF::IT;
push(@ISA,IF::IT);


#############################################################################
###
### Creation:
###

sub new {
    my ($class, $name, @attrs) = @_;
    my $self = IF::IT->new('_agent_', @attrs);
    bless $self, $class;
    $self->initialize($name);
}

sub recruit {
    my ($class, $self) = @_;

    ## Recruit a new agent:
    ##	  Re-bless and properly initialize an InterForm Token.

    bless $self, $class;
    $self->initialize;
}


sub initialize {
    my ($self, $name, $active) = @_;

    ## Initialize an agent.
    ##	  Force the agent to obey the standard conventions:
    ##	    force name lowercase to match tag if active
    ##	    'active' attribute if active.

    $name = $self->attr('name') unless defined $name;
    $active = $self->attr('active') unless defined $active;

    $self->hook(_act_on, \&act_on_quoted) if $self->attr('quoted');

    if ($active) {
	$name = lc $name;
	$self->attr('tag', $name);
	$self->attr('active', 'active');
    } else {
	$self->attr('name', $name);
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
###	Agents come in two (mutually exclusive) flavors:  
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
###	Agents are checked for when a start tag is encountered.  At
###	that point it might be an empty tag, in which case the agent
###	can simply do its thing, or there might be content coming.
###
###	An agent that needs to wait for content can register itself as
###	a handler.  An agent that *operates on* content can register
###	itself as a passive agent.  An agent that operates on *itself*
###	must return true from is_active and supply an act routine.  A 
###	passive agent can activate an element by re-blessing it as an
###	agent. 
###
###	When an action routine is called, the agent can call
###	$ii->token with an argument to replace the parser's current
###	token.   

sub is_active {
    my ($self) = @_;

    ## Return true if the agent is active.

    return defined $self->{_act};
}

sub act {
    my ($self, $ii, $incomplete) = @_;

    ## Perform the action routine associated associated with this token.
    ##	  Called if some passive agent marks the token ``active'' by
    ##	  re-blessing it as an agent.

    my $code = $self->{_act};
    return &$code($self, $ii, $incomplete) if (ref($code) eq 'CODE');
}

sub act_on {
    my ($self, $it, $ii, $incomplete) = @_;

    ## Act on a token.  
    ##	  $incomplete will be true if this is the start tag for an
    ##	  element for which content is expected.  In that case, the 
    ##	  parse stack will already have been pushed.

    my $code = $self->{_act_on};
    return unless defined $code;
    return &$code($self, $it, $ii, $incomplete) if (ref($code) eq 'CODE');
}

sub act_for {
    my ($self, $it, $ii, $incomplete) = @_;

    ## Act ``for'' a token.  
    ##	  This is called for ``active'' agents that match the tag of the
    ##	  token being evaluated.

    my $code = $self->{_act_for};
    return unless defined $code;
    return &$code($self, $it, $ii, $incomplete) if (ref($code) eq 'CODE');
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

#############################################################################
###
### Standard Interform Agents:
###

$syntax = {

};

$agents = {};
$active_agents = {};
$passive_agents = [];


#############################################################################
###
### Initialization:
###

sub define_agent {
    my ($agent, @attrs) = @_;

    ## Define a new agent, globally.
    ##	  Optionally takes a name and attribute-list.

    if (! ref($agent)) {
      $agent = IF::IA->new($agent, @attrs)
    }

    my $name = $agent->attr('name');
    my $active = $agent->attr('active');

    if ($active) {
	$active_agents->{$name} = $agent;
    } else {
	push(@$passive_agents, $agent);
    }
    $agents->{$name} = $agent;
    print "Defined IF agent " . $agent->starttag . "\n" if $main::debugging; 
}


#############################################################################
###
### Action routines shared by many agents:
###
###	These are picked up by $agent->initialize according to the various
###	attributes of the new agent:
###

sub act_on_parsed {
    my ($self, $it, $ii, $incomplete) = @_;

    ## parsed:
    ##	  Tell the interpretor to parse (and evaluate) the contents.

    return 0 if $incomplete <= 0;
    $ii->add_handler($self);
    $ii->parse_it;
}

sub act_on_quoted {
    my ($self, $it, $ii, $incomplete) = @_;

    ## quoted:
    ##	  Tell the interpretor to parse the contents without evaluating.

    return 0 if $incomplete <= 0;
    $ii->add_handler($self);
    $ii->quote_it;
}


#############################################################################
###
### Action routines for standard agents:
###

### Eval_PERL

sub eval_perl_handle {
    my ($self, $it, $ii) = @_;

    $ii->eval_perl($it);	# Has to be done in the interpretor's context.
}

sub eval_perl_match {
    my ($self, $it, $ii, $incomplete) = @_;

    return 0 if $incomplete <= 0;
    return 0 if ! ref($it);
    return 1 if (lc $it->attr('language') eq 'perl');
    return 0;
}

define_agent(IF::IA->new('Eval-PERL',
			 'quoted' => 1,
			 _match => \&eval_perl_match,
			 _handle => \&eval_perl_handle));


### agent-:  active, quoted.
###	Defines a new agent.

sub agent_handle {
    my ($self, $it, $ii) = @_;
    $ii->define_agent($it);
}
define_agent('agent-', 'active' => 1, 'quoted' => 1,
	     _handle => \&agent_handle);

### get-:  active, empty.
###	Gets the value of a variable.

### === need syntax tables after all, or a syntax agent.

sub get_handle {
    my ($self, $it, $ii) = @_;
    $ii->define_agent($it);
}
define_agent('get-', 'active' => 1, 'empty' => 1,
	     _handle => \&get_handle);

1;





