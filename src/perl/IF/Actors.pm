package IF::Actors; ###### Standard actors for InterForms
###	$Id$
###
###	This module contains the definitions of the ``standard'' set of
###	InterForm Interpretor actors.  It is easy to create other sets.
###
### Actor Naming Convention:
###
###	-foo-	passive
###	foo	active, generic
###
### Attributes used in actors:
###
###	attr	abbrev	meaning
###	----	------	-------
###	name	n	name of a variable or other object
###	value	v	value associated with a name


use IF::IA qw(analyze remove_spaces list_items);
push(@ISA,IF::IA);

#############################################################################
###
### Standard Interform Actors:
###

$syntax = {

};

$actors = {};
$active_actors = {};
$passive_actors = [];

@if_defaults = (
	     'streaming' => 1,	# output is a string
	     'passing' => 1,	# pass completed tags to the output.
	     'syntax' => $syntax,
	     'active_actors' => $active_actors,
	     'passive_actors' => $passive_actors,
	     );


#############################################################################
###
### Initialization:
###

sub define_actor {
    my ($actor, @attrs) = @_;

    ## Define a new actor, globally.
    ##	  Optionally takes a name and attribute-list.

    if (! ref($actor)) {
      $actor = IF::IA->new($actor, @attrs)
    }

    my $name = $actor->attr('name');
    my $active = $actor->attr('active');

    if ($active) {
	$active_actors->{$name} = $actor;
    } else {
	push(@$passive_actors, $actor);
    }
    $actors->{$name} = $actor;
    print "Defined IF actor " . $actor->starttag . "\n" if $main::debugging; 
}


#############################################################################
###
### Passive actors:
###

### -eval_perl-		attr: language=perl

define_actor('-eval-perl-', 'quoted' => -1, 'unsafe' => 1,
	     'attr' => 'language', 'value' => 'perl', 
	     _match => \&eval_perl_match,
	     _handle => \&IF::Run::eval_perl,
	     'dscr' => "evaluate CONTENT as perl code.");

sub eval_perl_match {
    my ($self, $it, $ii, $incomplete, $quoting) = @_;

    return 0 if $quoting || $incomplete <= 0 || !ref($it);
    return 1 if (lc $it->attr('language') eq 'perl');
    return 0;
}

### -foreach-		attr: foreach
###	Expects attr's list="..." or list1="..." ...
###	Binds entities &li; &1; ... 
###	Optional: entities="n1 ..."

define_actor('-foreach-', 'quoted' => 1, 'attr' => 'foreach',
	     _match => \&foreach_match, _handle => \&foreach_handle,
	     'dscr' => "repeat CONTENT for each ENTITY in LIST of words");

sub foreach_match {
    my ($self, $it, $ii, $incomplete, $quoting) = @_;

    return 0 if $quoting || $incomplete <= 0 || !ref($it);
    return 1 if (defined $it->attr('foreach'));
    return 0;
}

sub foreach_handle {
    my ($self, $it, $ii) = @_;

    ## The right approach is to use pseudo-attributes _list and _expand,
    ## where _expand is the original content.  If present we're repeating.  
    ## Keep the handler.  If _list is non-empty, push again.  

    ## The kludgy approach is to re-expand without the "foreach" attribute,
    ## and with the original contents moved inside a <repeat> element.

    my $attrs = [];
    my $each = [];
    my $list = $it->attr_names;
    for (@$list) {
	my $v = $it->{$_};
	push(@$attrs, $_, $v) unless ($_ eq 'foreach' || $_ =~ /^list/);
	push(@$each, $_, $v) if ($_ =~ /^list/ || $_ eq 'entity');
    }
    $it = IF::IT->new($it->tag, @$attrs, 
		      IF::IT->new('repeat', @$each, $it->content));
    $ii->push_input($it);
    $ii->delete_it($it);
}



#############################################################################
###
### Active Actors:
###

###### Bindings:

### actor:  active, quoted.
###	Defines a new actor.

define_actor('actor', 'active' => 1, 'quoted' => 1,
	     _handle => \&actor_handle,
	     'dscr' => "define an InterForm actor");

sub actor_handle {
    my ($self, $it, $ii) = @_;
    $ii->define_actor($it);
}

### ===	it's not clear that we want entities and variables to be different ===

### <get [name="n"] [pia] [entity]>name</get->
###	Gets the value of a variable named in the 'name' attribute or content.
###	if the 'pia' attribute is set, uses the pia (PERL) context,
###	if the 'entity' attribute is set, uses the entity table.
###
### Namespace options:
###	namespace="whatever", or one of the following:
###	local	current element 
###	attr	attributes of the current element
###	form	this InterForm's outer context
###	pia	pia (PERL) context
###	agent	attributes of the current PIA agent
###	

define_actor('get', 'active' => 1, 'content' => 'name',
	     _handle => \&get_handle,
	     'dscr' => "Get value of NAME, 
optionally in PIA, ENV, AGENT, ELEMENT, or ENTITY context.");

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
    } elsif ($it->attr('env')) {
	$ii->replace_it($ENV{$name});
    } elsif ($it->attr('agent')) {
	local $agent = IF::Run::agent();
	$ii->replace_it($agent->option($name)) if defined $agent;
    } elsif ($it->attr('entity')) {
	$ii->replace_it($ii->entities->{$name});
    } elsif ($it->attr('element')) {
	$ii->replace_it($ii->in_token->attr($name));
    } else {
        $ii->replace_it($ii->getvar($name));
    }
}

### <set name="name" value="value">
### <set name="name">value</set>
###	sets the value of a variable named in the 'name' attribute
###	to the value in the 'value' attribute.
###
### Namespace options:
###	namespace="whatever", or one ore more of the following:
###	entity	the current entity namespace
###	local	current element 
###	element	attributes of the containing element
###	form	this InterForm's outer context
###	pia	pia (PERL) context
###	agent	options of the current PIA agent
###	entity	entity

define_actor('set', 'active' => 1, 'content' => 'value',
	     _handle => \&set_handle,
	     'dscr' => "set NAME to VALUE, 
optionally in PIA, AGENT, ELEMENT, or ENTITY context.");

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
    } elsif ($it->attr('agent')) {
	local $agent = IF::Run::agent();
	$agent->option($name, $value) if defined $agent;
    } elsif ($it->attr('local')) {
	$ii->defvar($name, $value);
    } elsif ($it->attr('entity')) {
	$ii->entities->{$name} = $value;
    } elsif ($it->attr('element')) {
	$ii->in_token->attr($name, $value);
    } else {
	$ii->setvar($name, $value);
    }
    $ii->replace_it('');
}

###### Control Structure:

### <if><test>condition</test><then>...</then><else>...</else></if>
###	condition is false if it is empty or consists only of whitespace.

define_actor('if', 'active' => 1, 'parsed' => 1, _handle => \&if_handle,
	     'dscr' => "if TEST non-null, expand THEN, else ELSE.");
define_actor('then', 'active' => 1, 'quoted' => 1,
	     'dscr' => "expanded if TEST true in an &lt;if&gt;");
define_actor('else', 'active' => 1, 'quoted' => 1,
	     'dscr' => "expanded if TEST true in an &lt;if&gt;");

sub if_handle {
    my ($self, $it, $ii) = @_;

    ## The right way to do this would be to parse the condition, then activate 
    ##	  appropriate actors for <then> and <else>.

    my $it = &analyze($it->content, ['cond', 'then', 'else'], 1);
    my $test = remove_spaces($it->{'cond'});
    $test = scalar @$test if ref($test);

    if ($test) {
	print "<if >$test<then>...\n" if $main::debugging > 1;
	$ii->push_into($it->{'then'});
    } else {
	print "<if >$test<else>...\n" if $main::debugging > 1;
	$ii->push_into($it->{'else'});
    }
    $ii->delete_it;
}


### <repeat list="..." entity="name">...</repeat>
###	
define_actor('repeat', 'active' => 1, 'quoted' => 1,
	     _handle => \&repeat_handle, _end_input => \&repeat_end_input,
	     'dscr' => "repeat CONTENT with ENTITY in LIST of words.");

sub repeat_handle {
    my ($self, $it, $ii) = @_;

    my $entity = $it->attr('entity') || 'li';
    my $list = $it->attr('list');
    my @list = split(/ /, $list);
    print "repeating: $entity for (". join(' ', @list) . ")\n"
	if $main::debugging > 1;
    my $body = $it->content;
    my $item = shift @list;
    my $context = $ii->entities;

    return unless defined $item;

    $ii->open_entity_context;
    $ii->define_entity($entity, $item);
    $ii->push_input([$self, 0, $body, $entity, \@list, $context]);
    $ii->delete_it;
}

sub repeat_end_input {
    my ($self, $it, $ii) = @_;

    my ($foo, $pc, $body, $entity, $list, $entities) = @$it;

    print "repeat: $entity @$list \n" if $main::debugging > 1;

    my $item = shift @$list;

    if (defined $item) {
	$ii->define_entity($entity, $item);
	$it->[1] = 0;		# reset the pc
	$ii->push_input($it);
    } else {
	$ii->entities($entities);
    }
    return $undefined;
}

###### Tests:

### <test [options]>string</test>
###	Inside an <if>...</if> element, returns with empty or non-empty 
###	content, as appropriate.  Outside, returns only the test results.
###
###  Condition Options:
###	zero
###	positive
###	negative
###	match="pattern"
###	null	(stronger than the normal test in which false is nonblank)
###	numeric
###	length="n"
###	defined [pia|env|entity|agent|actor] name="..."
###	file [exists|writable|readable|directory] name="pathname"
###
###  Modifiers:
###	not
###	case (sensitive)
###	text
###
###  Other Options:
###	iftrue="..."	string to return if result is true
###	iffalse="..."	string to return if result is false
###
define_actor('test', 'active' => 1, 'content' => 'value', 'parsed'=>1,
	     _handle => \&test_handle,
	     'dscr' => "test VALUE (content); return null if false, else '1'. 
Tests include ZERO, POSITIVE, NEGATIVE, MATCH='pattern'.  
Modifiers include NOT, CASE (sensitive), TEXT.  
Others include IFTRUE, IFFALSE.");

sub test_handle {
    my ($self, $it, $ii) = @_;
    my $result = '';
    my $value = $it->attr('value');
    my ($match, $text);

    if ($it->attr('text')) {
	$text = $it->content_text unless defined $value;
    } else {
	$text = $it->content_string unless defined $value;
    }

    my $test = remove_spaces($value);
    $test = @$test if ref($test);

    if ($it->attr('zero')) {
	$result = 1 if $text == 0;
    } elsif ($it->attr('positive')) {
	$result = 1 if $text > 0;
    } elsif ($it->attr('negative')) {
	$result = 1 if $text < 0;
    } elsif (($match = $it->attr('match'))) {
	if ($it->attr('case')) {
	    $result = 1 if $text =~ /$match/;
	} else {
	    $result = 1 if $text =~ /$match/i;
	}
    } else {
	$result = 1 unless $text =~ /^\s*$/;
    }

    ## handle NOT, IFTRUE, IFFALSE attrs.

    $result = ! $result if $it->attr('not');
    if ($result) {
	my $res = $it->attr('iftrue');
	$result = $res if defined $res;
    } else {
	my $res = $it->attr('iffalse');
	$result = $res if defined $res;
    }

    ## Strip off <test> element unless inside an <if>.
    if (0 && $ii->in_tag eq 'if') { # === doesn't seem to work ===
	$it->content([$result]);
    } elsif ($result) {
	$ii->replace_it($result);
    } else {
	$ii->delete_it;
    } 
}

###### Number Processing:

### <eval>expression</eval>
###


###### List Processing:

### <list tag="ul">items separated by whitespace</list>


###### String Processing:

### <text>content</text>

### 
### <pad width=N align=[left|right|center] [spaces]>string</pad>
###	If the "spaces" attribute is present, only the spaces are 
###	returned.  This lets you pad the contents of a link (for
###	example) without having to put the padding inside the link
###	where it will get underlined and look ugly.

define_actor('pad', 'active' => 1, 'parsed' => 1, 
	     _handle => \&pad_handle,
	     'dscr' => "Pad CONTENT to a given WIDTH with given ALIGNment.
Optionally just generate the SPACES");

sub pad_handle {
    my ($self, $it, $ii) = @_;

    my $text   = $it->content_text;
    
    my $align  = (lc $it->attr('align')) || 'left';
    my $width  = $it->attr('width') || 8;
    my $spaces = $it->attr('spaces') || 0;

    my $pad  = $width - length $text;
    my ($left, $right) = ('', '');

    while ($pad-- > 0) {
	if ($align eq 'left' || ($align eq 'center' && ($pad & 1))) {
	    $left .= ' ';
	} else {
	    $right .= ' ';
	}
    }

    if ($it->is_text || $spaces) {
	$text = '' if $spaces;
	$ii->replace_it("$right$text$left");
    } else {
	$it->replace_it(IF::IT->new()->push($right)->push($it)->push($left));
    }
}

###### InterForm Actors:

### === very kludgy -- should just use attributes ===
### <actor-dscr name="name">
define_actor('actor-dscr', 'active' => 1, 'parsed' => 1, 
	     'content' => 'name', _handle => \&actor_dscr_handle,
	     'dscr' => "get an actor's DSCR attribute");

sub actor_dscr_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    $name = $it->content_string unless defined $name;
    my $link = $it->attr('link');

    my $a = $actors->{$name};
    if (!defined $a) {
	$ii->replace_it('unknown');
	return;
    }

    my $dscr = $a->attr('dscr');
    $dscr = $a->attr('active')? "active" : "passive" unless defined $dscr;
    $ii->replace_it($dscr);
}


### <actor-attrs name="name">
###	Get an actor's attributes in form suitable for documentation.
###	The "name", "tag",  and "dscr" attributes are not included.

define_actor('actor-attrs', 'active' => 1, 'parsed' => 1, 
	     'content' => 'name', _handle => \&actor_attrs_handle,
	     'dscr' => "get an actor's attributes in documentation format");

%no_show = ('dscr'=>1, 'tag'=>1, 'name'=>1);

sub actor_attrs_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    $name = $it->content_string unless defined $name;
    my $link = $it->attr('link');

    my $ia = $actors->{$name};
    if (!defined $ia) {
	$ii->replace_it('unknown');
	return;
    }

    my $dscr = '';

    for $a (@{$ia->attr_names}) {
	next if $no_show{$a};
	$v = $ia->attr($a);
	$dscr .= ' ' . $a;
	$dscr .= "='$v'" unless ($v == 1 || $v eq $a);
    }
    $ii->replace_it($dscr);
}



###### PIA Actors:

### <agent-home>name</agent-home>
###	expands to the agent's home interForm name.
###	Makes a link if the "link" attribute is present.

###	This is incredibly kludgy, but it works!

define_actor('agent-home', 'active' => 1, 'parsed' => 1, 
	     'content' => 'name', _handle => \&agent_home_handle,
	     'dscr' => "Get path to a pia agent's home InterForm.
Optionally make a LINK.  Very kludgy." );

sub agent_home_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    $name = $it->content_string unless defined $name;
    my $link = $it->attr('link');

    my $a = IF::Run::resolver()->agent($name);

    if (!ref $a) {
	$ii->delete_it;
	return;
    }
    my $type = $a->type;
    my $home = ($type ne $name)? "$type/$name" : "$name";

    $home = IF::IT->new('a', 'href'=>"/$home/home.if", $home) if $link;
    $ii->replace_it($home);
}

### <agent-running>name</agent-running>
###	Tests whether an agent is running

define_actor('agent-running', 'active' => 1, 'parsed' => 1, 
	     'content' => 'name', _handle => \&agent_running_handle,
	     'dscr' => "Tests whether an agent is running (installed)" );

sub agent_running_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    $name = $it->content_string unless defined $name;

    my $a = IF::Run::resolver()->agent($name);
    if (ref $a) {
	$ii->replace_it($name);
    } else {
	$ii->delete_it();
    }
}

1;
