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

sub new
{
    my ($class, $tag, @attrs) = @_;
    my $self = IF::IT->new($tag, @attrs);
    bless $self, $class;
}


#############################################################################
###
### Access to hooks:
###

sub hook {
    my ($self, $name, $code) = @_;

    
}

#############################################################################
###
### Matching:
###
###	Agents come in two (mutually exclusive) flavors:  
###	  1. those that match a particular tag.
###	  2. those that are activated based on features.
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

    ## Perform the associated action routine.

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
    my ($agent, $name, $active) = @_;
    
    if ($active) {
	$name = lc $name;
	$agent->tag($name);
	$agent->attr('active', 'active');
	$active_agents->{$name} = $agent;
    } else {
	$name = uc $name;
	$agent->attr('name', $name);
	push(@$passive_agents, $agent);
	print "  passive: " . @$passive_agents . 
	    " in $passive_agents\n" if $main::debugging;
    }
    $agents->{$name} = $agent;
    print "Defined IF agent " . $agent->starttag . "\n" if $main::debugging; 
}


#############################################################################
###
### Action routines for standard agents:
###


sub eval_perl_handle {
    my ($self, $it, $ii) = @_;

    $ii->eval_perl($it);	# Has to be done in the interpretor's context.
}

sub eval_perl_match {
    my ($self, $it, $ii, $incomplete) = @_;

    return 0 if $incomplete <= 0;
    return 0 if ! ref($it);
    if (lc $it->attr('language') eq 'perl') {
	## should really be in act_on...
	$ii->add_handler($self);
	$ii->parsing(1);
	$ii->quoting(-1);
	return 1;
    }
    return 0;
}

define_agent(IF::IA->new('eval_perl', 
			 _match => \&eval_perl_match,
			 _handle => \&eval_perl_handle),
	     'EVAL_PERL', 0);

1;
