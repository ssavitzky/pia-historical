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


use IF::Semantics;
use PIA::Utilities;

#############################################################################
###
### Standard Interform Actors:
###

$syntax = {

};

$actors = {};
$passive_actors = [];

@if_defaults = (
	     'streaming' => 1,	# output is a string
	     'passing' => 1,	# pass completed tags to the output.
	     'syntax' => $syntax,
	     'passive_actors' => $passive_actors,
	     'actors' => $actors,
	     );


#############################################################################
###
### Initialization:
###

sub define_actor {
    my ($actor, @attrs) = @_;

    ## Define a new actor, globally.
    ##	  Optionally takes a name and attribute-list.

    $actor = IF::IA->new($actor, @attrs) unless ref($actor);

    my $name = $actor->attr('name');
    my $tag = $actor->attr('tag');

    push(@$passive_actors, $actor) unless $tag;
    $actors->{$name} = $actor;
    print "Defined IF actor " . $actor->starttag . "\n" if $main::debugging; 
}


#############################################################################
###
### Matching routines:
###

#############################################################################
###
### Shared handles:
###

sub null_handle {
    my ($self, $it, $ii) = @_;

    ## Just pass the tag and its contents.  Take no action

}

sub generic_handle {
    my ($self, $it, $ii) = @_;

    ## This is the handler for a generic agent with content.
    ## === not really clear what to do about context ($it)
    ## => use an attribute for the name (or parts), and define entities
    ##	  using shallow binding.

    $ii->defvar("element", $it);
    $ii->defvar("content", $it->content);
    $ii->push_into($self->content);
    $ii->delete_it;
}

#############################################################################
###
### Passive actors:
###

### -eval_perl-		attr: language=perl

define_actor('-eval-perl-', 'quoted' => 'quoted', 'unsafe' => 1,
	     'attr' => 'language', 'value' => 'perl', 
	     _match => \&eval_perl_match,
	     _handle => \&IF::Run::eval_perl,
	     'dscr' => "evaluate CONTENT as perl code (DEPRECATED).");

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

define_actor('-foreach-', 'quoted' => 'quoted', 'attr' => 'foreach',
	     _match => \&foreach_match, _handle => \&foreach_handle,
	     'dscr' => "repeat ELEMENT for each ENTITY in LIST of words");

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

### -input.size-
###	recognizes <input SIZE=fit MIN=n MAX=m>content</input>
###	and sets size to min(m, max(n, size(content)))

### -input.options-
###	recognizes <input OPTIONS="list of options">content</input>
###	and makes CONTENT, if present, the selected option.

#############################################################################
###
### Active Actors:
###

###### Bindings:

### actor:  active, quoted.
###	Defines a new actor.

define_actor('actor', 'quoted' => 'quoted', _handle => \&actor_handle,
	     'dscr' => "define an InterForm actor");

sub actor_handle {
    my ($self, $it, $ii) = @_;
    $ii->define_actor(IF::IA->recruit($it));
    $ii->delete_it;
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

### === <get file [path="p1:p2..."]
###	<file read|write|append [path=] [name=] >content</file>

define_actor('get', 'content' => 'name', _handle => \&get_handle,
	     'dscr' => "Get value of NAME, 
optionally in PIA, ENV, AGENT, FORM, ELEMENT, or ENTITY context.");

### === EXPAND/PROTECT ?===

sub get_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    $name = $it->content_string unless defined $name;

    if ($it->attr('pia')) {
	local $agent = IF::Run::agent();
	local $request = IF::Run::request();
	$name = '$' . $name;
	my $status = $agent->run_code("$name", $request);
	print "Interform error: $@\n" if $@ ne '' && ! $main::quiet;
	print "code status is $status\n" if  $main::debugging;
	$ii->replace_it($status);
    } elsif ($it->attr('env')) {
	$ii->replace_it($ENV{$name});
    } elsif ($it->attr('form')) {
	my $hash = IF::Run::request()->parameters;
	$ii->replace_it($$hash{$name});
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

define_actor('set', 'content' => 'value', _handle => \&set_handle,
	     'dscr' => "set NAME to VALUE, 
optionally in PIA, AGENT, ACTOR, ELEMENT, or ENTITY context.   
ELEMENT and ENTITY may define a LOCAL binding.  ELEMENT may have a TAG.  
Optionally COPY new or PREVIOUS value.");

### === COPY / COPY PREVIOUS ===
### === TAG= / ACTOR ===

sub set_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    my $value = $it->attr('value');
    $value = $it->content_string unless defined $value;
    
    if ($it->attr('pia')) {
	local $agent = IF::Run::agent();
	local $request = IF::Run::request();
	$name = '$' . $name; 
	my $status = $agent->run_code("$name='$value';", $request);
	print "Interform error: $@\n" if $@ ne '' && ! $main::quiet;
	print "code status is $status\n" if  $main::debugging;
	$ii->replace_it($status);
    } elsif ($it->attr('agent')) {
	local $agent = IF::Run::agent();
        if ($it->attr('hook')) {
            $value = IF::IT->new()->push($it->content);
        }
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

define_actor('if', _handle => \&if_handle,
	     'dscr' => "if TEST non-null, expand THEN, else ELSE.");
define_actor('then', 'quoted' => 'quoted', _handle => \&null_handle,
	     'dscr' => "expanded if TEST true in an &lt;if&gt;");
define_actor('else', 'quoted' => 'quoted', _handle => \&null_handle,
	     'dscr' => "expanded if TEST true in an &lt;if&gt;");

sub if_handle {
    my ($self, $it, $ii) = @_;

    ## The right way to do this would be to parse the condition, then activate 
    ##	  appropriate actors for <then> and <else>.

    my $parts = &analyze($it->content, ['cond', 'then', 'else'], 1);
    my $test = remove_spaces($parts->{'cond'});
    $test = scalar @$test if ref($test);

    if ($test) {
	print "<if >$test<then>...\n" if $main::debugging > 1;
	$ii->push_into($parts->{'then'});
    } else {
	print "<if >$test<else>...\n" if $main::debugging > 1;
	$ii->push_into($parts->{'else'});
    }
    $ii->delete_it;
}


### <repeat list="..." entity="name">...</repeat>
###	
define_actor('repeat', 'quoted' => 'quoted',
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

### Expansion control: 
###	protect, protect-result

define_actor('protect', 'quoted' => 'quoted', _handle => \&protect_handle,
	     'dscr' => "Protect CONTENT from expansion.  Optionally protect
MARKUP by converting special characters to entities.");

define_actor('protect-result', _handle => \&protect_handle,
	     'dscr' => "Protect results of expanding CONTENT from further 
expansion.  Optionally protect MARKUP by converting special characters 
to entities.");

%protected_chars = ('&' => '&amp;', '<' => '&lt;', '>' => '&gt;');

sub protect_handle {
    my ($self, $it, $ii) = @_;

    if ($it->attr('markup')) {
	$ii->replace_it(protect_markup($it->content_string));
    } else {
	$ii->replace_it($it->content);
    }
}

define_actor('expand', _handle => \&expand_handle,
	     'dscr' => "Expand CONTENT, then either re-expand or PROTECT it.
Optionally protect MARKUP as well.");

sub expand_handle {
    my ($self, $it, $ii) = @_;

    if ($it->attr('protect')) {
	protect_handle($self, $it, $ii);
    } else {
	$ii->push_into($it->content);
	$ii->delete_it;
    }
}

### <parse [tagset="..."]>text to reparse</parse>
###	basically backtic.  The content gets parsed TWICE: it's expanded, 
###	(maybe the text extracted) then reparsed.

###### Tests:

### <test [options]>string</test>
###
###  Condition Options:
###	zero
###	positive
###	negative
###	match="pattern"
###	null	(stronger than the normal test in which false is nonblank)
### ===	numeric
### ===	length="n"
### ===	defined [pia|env|entity|agent|actor] name="..."
### ===	file [exists|writable|readable|directory|link] name="pathname"
###
###  Modifiers:
###	not
###	case (sensitive)
###	text
###
###  Other Options:
###	iftrue="..."	string to return if result is true->content
###	iffalse="..."	string to return if result is false
###
define_actor('test', 'content' => 'value', _handle => \&test_handle,
	     'dscr' => "test VALUE (content); 
return null or IFFALSE if false, else '1' or IFTRUE. 
Tests: ZERO, POSITIVE, NEGATIVE, MATCH='pattern'.  
Modifiers: NOT, CASE (sensitive), TEXT, LINK, EXACT (match).");

sub test_handle {
    my ($self, $it, $ii) = @_;
    my $result = '';
    my $value = $it->attr('value');
    my ($match, $text);

    if ($it->attr('link')) {
	$text = $it->link_text unless defined $value;
    } elsif ($it->attr('text')) {
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
	$match = "^$match\$" if $it->attr('exact');
	if ($it->attr('case')) {
	    $result = 1 if $text =~ /$match/;
	} else {
	    $result = 1 if $text =~ /$match/i;
	}
    } else {
	$result = 1 unless $text =~ /^\s*$/;
    }

    test_result($result, $it, $ii);
}

### <equal [options]>strings</equal>

define_actor('equal', 'content' => 'list', _handle => \&equal_handle,
	     'dscr' => "test tokens in LIST (content) for equality; 
return null or IFFALSE if false, else '1' or IFTRUE. 
Modifiers: NOT, CASE (sensitive), TEXT, LINK, NUMERIC.");

sub equal_handle {
    my ($self, $it, $ii) = @_;

    my $list = get_list($it);
    my ($a, $b, $compare);
    my $prep = prep_item_sub($it);

    if ($it->attr('numeric')) {
	$compare = \sub {$a == $b};
    } else {
	$compare = \sub{$a eq $b};
    }

    foreach $b (@$list) {
	$b = &$prep($b);
	if (defined $a && ! &$compare($a, $b)) {
	    test_result('', $it, $ii);
	    return;
	}	    
	$a = $b;
    }
    test_result(1, $it, $ii);
}

### <sorted [options]>strings</sorted>

define_actor('sorted', 'content' => 'list', _handle => \&sorted_handle,
	     'dscr' => "test tokens in  LIST (content) for sortedness;
return null or IFFALSE if false, else '1' or IFTRUE. 
Modifiers: NOT, CASE (sensitive), TEXT, LINK, NUMERIC, REVERSE.");

sub sorted_handle {
    my ($self, $it, $ii) = @_;

    my $list = get_list($it);
    my $reverse = $it->attr('reverse');
    my $prep = prep_item_sub($it);
    my $compare;

    if ($it->attr('numeric')) {
	$compare = $reverse? \sub {$a >= $b} : \sub {$a <= $b};
    } elsif ($it->attr('text')) {
	$compare = $reverse? \sub {$a ge $b} : \sub {$a le $b};
    } else {
	$compare = $reverse? \sub {$a ge $b} : \sub {$a le $b};
    }

    foreach $b (@$list) {
	$b = &$prep($b);
	if (defined $a && ! &$compare($a, $b)) {
	    test_result('', $it, $ii);
	    return;
	}	    
	$a = $b;
    }
    test_result(1, $it, $ii);
}

###### Number Processing:

### <eval>expression</eval> ???
### <sum>list</sum> <diff>list</diff> 
### <product>list</product> <quotient>list</quotient>
###


###### List Processing:

### <list tag="ul">items separated by whitespace</list>

### <sort [options]>strings</sort>
###	sort tokens
### ===	needs to handle pairs, too!
###
###  Modifiers:
###	not
###	case (sensitive)
###	text
###	numeric
###
define_actor('sort', 'content' => 'value', _handle => \&sort_handle,
	     'dscr' => "sort tokens in  LIST (content).
Modifiers: CASE (sensitive), TEXT, NUMERIC, REVERSE.");

sub sort_handle {
    my ($self, $it, $ii) = @_;

    my $list = get_list($it);
    my $reverse = $it->attr('reverse');
    my $prep = prep_item_sub($it);

    my @tmp = map { [&$prep($_), $_] } @$list;
    my @out; 

    if ($it->attr('numeric')) {
	if ($reverse) {
	    @out = map {$_->[1]} sort {$b->[0] <=> $a->[0]} @tmp;
	} else {
	    @out = map {$_->[1]} sort {$a->[0] <=> $b->[0]} @tmp;
	}
    } else {
	if ($reverse) {
	    @out = map {$_->[1]} sort {$b->[0] cmp $a->[0]} @tmp;
	} else {
	    @out = map {$_->[1]} sort {$a->[0] cmp $b->[0]} @tmp;
	}
    }

    list_result(\@out, $it, $ii);
}


###### String Processing:

### <text>content</text>

define_actor('text', _handle => \&text_handle,
	     'dscr' => "eliminate markup from CONTENT.");

sub text_handle {
    my ($self, $it, $ii) = @_;

    my $text = $it->content_text;
    $ii->replace_it($text);
}


### <trim>content</trim>

define_actor('trim', _handle => \&trim_handle,
	     'dscr' => "eliminate leading and trailing whitespace
from CONTENT.");

sub trim_handle {
    my ($self, $it, $ii) = @_;

    my $text = remove_spaces($it);
    $ii->replace_it(IF::IT->new()->push($text));
}

### <pad width=N align=[left|right|center] [spaces]>string</pad>
###	If the "spaces" attribute is present, only the spaces are 
###	returned.  This lets you pad the contents of a link (for
###	example) without having to put the padding inside the link
###	where it will get underlined and look ugly.

define_actor('pad', _handle => \&pad_handle,
	     'dscr' => "Pad CONTENT to a given WIDTH with given ALIGNment
(left/center/right).  Optionally just generate the SPACES.  Ignores markup.");

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
	$ii->replace_it(IF::IT->new()->push($right)->push($it)->push($left));
    }
}

### <add-markup>text</add-markup>

define_actor('add-markup', _handle => \&add_markup_handle,
	     'dscr' => "convert common text conventions to markup");

sub add_markup_handle {
    my ($self, $it, $ii) = @_;

    my $content = $it->content;
    my $uc = $it->attr('uc') || 'strong'; # What to do with uppercase
    my $text;
    my @result;

    foreach $text (@$content) {
	if (ref $text) {
	    push(@result, $text); # already marked up
	} else {
	    ## Uppercased words.
	    while ($text =~ /([A-Z][A-Z]+)/ ) {
		my $x = "<$uc>" . lc $1 . "</$uc>";
		$text =~ s/$1/$x/;
	    }

	    ## Words surrounded by _ or *

	    ## Paragraph breaks

	    ## Line breaks

	    ## Horizontal rules

	    push(@result, $text);
	}
    }

    $ii->replace_it(\@result);
}

### <ignore [spaces | lines | text | markup]>content</ignore>
###	default--ignore *everything* and expand for side-effects

### <split [separator="string" | pattern="pattern"]>text</split>

### <subst match="pattern" result="pattern">text</subst>


###### InterForm Actors:

### === very kludgy -- should just use attributes ===
### <actor-dscr name="name">
define_actor('actor-dscr', 'content' => 'name', 
	     _handle => \&actor_dscr_handle,
	     'dscr' => "get an actor's DSCR attribute");

sub actor_dscr_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    $name = $it->content_string unless defined $name;
    my $link = $it->attr('link');

    my $a = $ii->actors->{$name};
    if (!defined $a) {
	$ii->replace_it('');
	return;
    }

    my $dscr = $a->attr('dscr');
    $dscr = '' unless defined $dscr;
    $ii->replace_it($dscr);
}


### <actor-attrs name="name">
###	Get an actor's attributes in form suitable for documentation.
###	The "name", "tag",  and "dscr" attributes are not included.

define_actor('actor-attrs', 'content' => 'name', 
	     _handle => \&actor_attrs_handle,
	     'dscr' => "get an actor's attributes in documentation format");

%no_show = ('dscr'=>1, 'tag'=>1, 'name'=>1);

sub actor_attrs_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    $name = $it->content_string unless defined $name;
    my $link = $it->attr('link');

    my $ia = $ii->actors->{$name};
    if (!defined $ia) {
	$ii->replace_it(' -unknown-');
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



###### PIA Agents:

### <agent-home>name</agent-home>
###	expands to the agent's home interForm name.
###	Makes a link if the "link" attribute is present.

###	This is incredibly kludgy, but it works!

define_actor('agent-home', 'content' => 'name', 
	     _handle => \&agent_home_handle,
	     'dscr' => "Get path to a pia agent's home InterForm.
Optionally make a LINK.  Very kludgy." );

sub agent_home_handle {
    my ($self, $it, $ii) = @_;

    my $name = get_text($it, 'name');
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

define_actor('agent-running', 'content' => 'name', 
	     _handle => \&agent_running_handle,
	     'dscr' => "Tests whether an agent is running (installed)" );

sub agent_running_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    $name = $it->content_text unless defined $name;

    my $a = IF::Run::resolver()->agent($name);
    if (ref $a) {
	$ii->replace_it($name);
    } else {
	$ii->delete_it();
    }
}

### <agent-set-options>query_string</agent-set-options>

define_actor('agent-set-options', 'unsafe' => 1,
	     'content' => 'options', _handle => \&agent_set_options_handle,
	     'dscr' => "Sets OPTIONS for agent NAME" );

sub agent_set_options_handle {
    my ($self, $it, $ii) = @_;

    my $options;# = get_hash($it, 'options');
    $options = IF::Run::request()->parameters unless ref $options;
    my $name = $it->attr('name');
    my $agent;

    if ($name) {
	$agent = IF::Run::resolver()->agent($name);
    } else {
	$agent = IF::Run::agent();
	$name = $agent->name;
    }

    my $i = 0;
    if ($options) {
	foreach $key (keys(%{$options})){
	    print("  setting $key = ",$$option{$key},"\n") if  $main::debugging;
	    $agent->option($key, $$options{$key});
	    ++$i;
	}
    }
    if ($i) {
	$ii->replace_it("$i");
    } else {
	$ii->delete_it();
    }
}

### <agent-options>

define_actor('agent-options', 'empty' => 1,  'unsafe' => 1,
	     _handle => \&agent_options_handle,
	     'dscr' => "Returns list of option names for agent NAME" );

sub agent_options_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    my $agent;

    if ($name) {
	$agent = IF::Run::resolver()->agent($name);
    } else {
	$agent = IF::Run::agent();
	$name = $agent->name;
    }

    $ii->replace_it(join(' ', @{$agent->attr_names}));
}


### <agent-install name='n' type='t'>

define_actor('agent-install', 
	     'content' => 'options', _handle => \&agent_install_handle,
	     'dscr' => "Installs an agent with given OPTIONS (content).
Returns the agent's name." );

sub agent_install_handle {
    my ($self, $it, $ii) = @_;

    ## urlQuery is undefined because installation is normally a POST
    #my $options = get_hash($it, 'options'); # === broken: urlQuery undef. ===
    my $options = IF::Run::request()->parameters;

    my $agent = IF::Run::agent(); # had better be agency
    $agent = $agent->install($options);
    my $name = ref $agent ? $agent->name : '';
    $ii->replace_it($name);
}

### <agent-remove>name</agent-remove>

define_actor('agent-remove', 'unsafe' => 1,
	     'content' => 'name', _handle => \&agent_remove_handle,
	     'dscr' => "Remove (uninstall) an agent with given NAME." );

sub agent_remove_handle {
    my ($self, $it, $ii) = @_;

    my $name = get_text($it, 'name');
    my $agent = IF::Run::agent(); # had better be agency

    $agent->un_install_agent($name) if defined $name;
    $ii->replace_it($name);
}

define_actor('agent-list', 'content' => 'type', 
	     _handle => \&agent_list_handle,
	     'dscr' => "List the agents with given TYPE. Possibly SUBS only." );

sub agent_list_handle {
    my ($self, $it, $ii) = @_;

    my $type = get_text($it, 'type');
    my $subs = $it->attr('subs');

    my $resolver = IF::Run::resolver();
    my @names = sort($resolver->agent_names);
    my @list = ();

    foreach $name (@names) {
	my $agent = $resolver->agent($name);
	next if ($subs && $agent->type eq $agent->name);
	push (@list, $name) if $agent->type eq $type;
    }
    $ii->replace_it(join(' ', @list));
}

define_actor('agent-control', _handle => \&agent_control_handle,
	     'dscr' => "Add a control to the current response." );

sub agent_control_handle {
    my ($self, $it, $ii) = @_;

    my $text = $it->content_string;
    my $response = IF::Run::request();

    $response -> add_control($text);
    print "add_control($text)\n" if $main::debugging;

    $ii->delete_it;
}


### Operating System:

### <os-command>command</os-command>

define_actor('os-command', 'unsafe' => 1,
	     'content' => 'command', _handle => \&os_command_handle,
	     'dscr' => "Execute CONTENT as an operating system command 
in the background with proxies set." );

sub os_command_handle {
    my ($self, $it, $ii) = @_;

    my $command = get_text($it, 'command');
    print "Executing command '$command'\n" unless $main::quiet;

    my $pid;
    unless ($pid = fork) {
	unless (fork) {
	    system("sh -c '$main::proxies $command</dev/null &>/dev/null&'");
	    exit 0;
	}
	exit 0;
    }
    waitpid($pid, 0);

    $ii->delete_it;
}

### <os-command-output>command</os-command-output>

define_actor('os-command-output', 'unsafe' => 1,
	     'content' => 'command', _handle => \&os_command_output_handle,
	     'dscr' => "Execute CONTENT as an operating system command 
and capture its output." );

sub os_command_output_handle {
    my ($self, $it, $ii) = @_;

    my $command = get_text($it, 'command');
    print "Executing command `$command`\n" unless $main::quiet;

    my $result;
    eval {
	$result = `$command`;
	$result = `sh -c '$main::proxies $command </dev/null'`;
    };
    $ii->replace_it($result);
}



1;
