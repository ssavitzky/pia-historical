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

sub act_parsed {
    my ($self, $it, $ii, $incomplete) = @_;

    ## parsed:
    ##	  Tell the interpretor to parse (and evaluate) the contents.

    return 0 if $incomplete <= 0;
    $ii->add_handler($self);
    $ii->parse_it;
}

sub act_quoted {
    my ($self, $it, $ii, $incomplete) = @_;

    ## quoted:
    ##	  Tell the interpretor to parse the contents without evaluating.

    return 0 if $incomplete <= 0;
    $ii->add_handler($self);
    $ii->quote_it($self->attr('quoted'));
}

sub act_empty {
    my ($self, $it, $ii, $incomplete) = @_;

    ## empty:
    ##	  Tell the interpretor not to expect an end tag

    if ($incomplete > 0) { 
	$ii->complete_it($it);
    } else {
	$ii->add_handler($self);
    }
}

sub act_generic {
    my ($self, $it, $ii, $incomplete) = @_;

    ## Generic:
    ##	  Presence of end tag is based on whether there's a content attribute. 
    ##	  The name of the attribute is the value of 'content'

    if ($incomplete > 0) { 
	my $content = $self->attr('content');
	if (defined $content && defined $it->attr($content)) {
	    $ii->complete_it($it);
	} else {
	    my $quoted = $self->attr('quoted');
	    if (defined $quoted) {
		$ii->quote_it($quoted);
	    } else {
		$ii->parse_it;
	    }
	}
    } else {
	$ii->add_handler($self);
    }
}


#############################################################################
###
### Utility routines:
###

sub remove_spaces {
    my ($in) = @_;

    ## The result is an array that contains each item of $in with
    ##	  leading and trailing whitespace removed, and with items that
    ##	  consist only of whitespace deleted completely.

    my @out;
    $in = [$in] unless ref $in;

    for $x (@$in) {
	if (! ref $x) {
	    $x =~ s/^[\n]*//s;
	    $x =~ s/[\n\s]*$//s;
	}
	push(@out, $x) unless $x eq '';
    }
    return \@out;
}


sub analyze {
    my ($in, $tags, $flag) = @_;

    ## The result is a hash that associates each of the given tags with 
    ##	  that tag's content in the top level of array @$in.  Anything
    ##	  outside any of the tags is associated with '_', or the first
    ##	  empty tag if $flag is true. 

    ##	  If applied to a token instead of an array, attributes will be
    ##	  used if they exist, and the token will be returned instead of
    ##	  constructing a new hash.

    my $out, $x, @tmp, %tags;

    if (ref($in) eq 'ARRAY') {
	$out = {};
    } else {
	$out = $in;
	$in = $in->content;
    }

    print "Analying\n" if  $main::debugging>1;
    for $x (@$tags) {
	$tags{$x} = 1;
    }
    for $x (@$in) {
	if (ref $x) {
	    my $tag = $x->tag;
	    if (exists $tags{$tag}) {
		print "pushing <$tag...> to attributes\n" if $main::debugging>1;
		$out->{$tag} = $x->content;
	    } else {
		print "pushing <$tag...> to tmp\n" if $main::debugging>1;
		push(@tmp, $x);
	    }
	} else {
	    print "pushing '$x' to tmp\n" if $main::debugging>2;
	    push(@tmp, $x) unless $x eq '';
	}
    }
    if (@tmp) {
	if ($flag) {
	    for $x (@$tags) {
		if (! exists $out->{$x}) {
		    $out->{$x} = \@tmp;
		    return $out;
		}
	    }	    
	} else {
	    $out->{'_'} = \@tmp;
	}
    }
    return $out;
}

sub list_items {
    my ($in) = @_;

    ## $in is turned into an array of list items as follows:
    ##	  If it contains (or is) a string, it is split on whitespace.
    ##	  If it contains a single list element, each list item is
    ##	  extracted and its <li> tag removed.

    my @out;

    if (! ref $in) {
	my $x = $in;
	$x =~ s/\n/ /s;
	@out = split $x;
    } else {
	$in = remove_spaces($in);
	if (@$in == 1 && ref($in->[0])) {
	    $in = $in->[0]->content;
	}
	my $x;
	for $x ($in) {
	    if (ref($x) && $x->tag eq 'li') {
		push(@out, $x->content);
	    } else {
		push(@out, $x);
	    }
	}
    }
    return \@out;
}

#############################################################################
###
### Action routines for standard agents:
###
### Agent Naming Convention:
###
###	-foo-	passive
###	foo-	active, generic
###	foo.	active, empty
###	foo..	active, parsed

###### Passive

### -eval_perl-

sub eval_perl_match {
    my ($self, $it, $ii, $incomplete) = @_;

    return 0 if $incomplete <= 0;
    return 0 if ! ref($it);
    return 1 if (lc $it->attr('language') eq 'perl');
    return 0;
}

define_agent(IF::IA->new('-eval-perl-',
			 'quoted' => -1,
			 _match => \&eval_perl_match,
			 _handle => \&IF::Run::eval_perl));


###### Active

### agent-:  active, quoted.
###	Defines a new agent.

define_agent('agent-', 'active' => 1, 'quoted' => 1,
	     _handle => \&agent_handle);
sub agent_handle {
    my ($self, $it, $ii) = @_;
    $ii->define_agent($it);
}

### get-:  active, generic.
###	Gets the value of a variable named in the 'name' attribute or content.
###	if the 'pia' attribute is set, uses the pia (PERL) context

define_agent('get-', 'active' => 1, 'generic' => 1, 'content' => 'name',
	     _handle => \&get_handle);

### get.:  active, empty.
###	Gets the value of a variable named in the 'name' attribute.
###	if the 'pia' attribute is set, uses the pia (PERL) context

define_agent('get.', 'active' => 1, 'empty' => 1, _handle => \&get_handle);

### get.:  active, empty.
###	Gets the value of a variable named in the 'name' attribute.
###	if the 'pia' attribute is set, uses the pia (PERL) context

define_agent('get.', 'active' => 1, 'empty' => 1, _handle => \&get_handle);

### get..:  active, parsed.
###	Gets the value of a variable named in the contents.
###	if the 'pia' attribute is set, uses the pia (PERL) context

define_agent('get..', 'active' => 1, 'parsed' => 1, _handle => \&get_handle);

sub get_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    $name = $it->content_string unless defined $name;

    if ($it->attr('pia')) {
	local $agent = IF::Run::agent();
	local $request = IF::Run::request();
	my $status = $agent->run_code("$name", $request);
	print "Interform error: $@\n" if $@ ne '' && ! $main::quiet;
	print "code status is $status\n" if  $main::debugging;
	$ii->replace_it($status);
    } else {
        $ii->replace_it($ii->getvar($name));
    }
}

### set.:  active, empty.
###	sets the value of a variable named in the 'name' attribute
###	to the value in the 'value' attribute.
###	if the 'local' attribute is set, uses the current context.
###	if the 'pia' attribute is set, uses the pia (PERL) context

define_agent('set.', 'active' => 1, 'empty' => 1, _handle => \&set_handle);

### set..:  active, parsed.
###	Sets the value of a variable named in the 'name' attribute
###	to the value in the (parsed) contents.
###	if the 'local' attribute is set, uses the current context.
###	if the 'pia' attribute is set, uses the pia (PERL) context

define_agent('set..', 'active' => 1, 'parsed' => 1, _handle => \&set_handle);

sub set_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    my $value = $it->attr('value');
    $value = $it->content_token unless defined $value;

    if ($it->attr('pia')) {
	local $agent = IF::Run::agent();
	local $request = IF::Run::request();
	my $status = $agent->run_code("$name='$value';", $request);
	print "Interform error: $@\n" if $@ ne '' && ! $main::quiet;
	print "code status is $status\n" if  $main::debugging;
	$ii->replace_it($status);
    } elsif ($it->attr('local')) {
	$ii->defvar($name, $value);
    } else {
	$ii->setvar($name, $value);
    }
    $ii->replace_it('');
}

### if-: active, parsed
###	<if->condition<then>...</then><else>...</else></if>
###	condition is false if it is empty or consists only of whitespace.
###

define_agent('if-', 'active' => 1, 'parsed' => 1, _handle => \&if_handle);
define_agent('then', 'active' => 1, 'quoted' => 1);
define_agent('else', 'active' => 1, 'quoted' => 1);

sub if_handle {
    my ($self, $it, $ii) = @_;

    ## The right way to do this would be to parse the condition, then activate 
    ##	  appropriate agents for <then> and <else>.

    ## The easy way at the moment is to quote the whole contents
    ##	  and pick it apart later.

    analyze($it, ['test', 'then', 'else'], 1);
    my $test = remove_spaces($it->attr('test'));
    $test = @$test if ref($test);

    if ($test) {
	print "<if- >$test<then>...\n" if $main::debugging > 1;
	$ii->push_into($it->{'then'});
    } else {
	print "<if- >$test<else>...\n" if $main::debugging > 1;
	$ii->push_into($it->{'else'});
    }
    $ii->delete_it;
}


### for-: active, parsed
###	<for- n="name">tokens<do->body</do-></for->
###	<for- n="name" in="tokens">body</for->
###	<for- n="name"><in->tokens</in->body</for->
###	    "body" is expanded for each token in "tokens"
###	    if "tokens" is a single list it is expanded.
###	    if "tokens" is a string it is split on whitespace

define_agent('for-', 'active' => 1, 'quoted' => 1, _handle => \&for_handle);

sub for_handle {
    my ($self, $it, $ii) = @_;


}








1;
