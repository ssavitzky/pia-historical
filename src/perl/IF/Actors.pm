package IF::Actors; ###### Standard actors for InterForms
###	$Id$
###	Copyright 1997, Ricoh California Research Center.
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


use IF::Tagset;
use IF::Semantics;
use PIA::Utilities;

#############################################################################
###
### Initialization:
###

sub define_actor {
    my ($actor, @attrs) = @_;

    ## Define a new actor in the tagset currently being initialized.
    ##	  Optionally takes a name and attribute-list.

    $actor = IF::IA->new($actor, 'handle'=>1, @attrs) unless ref($actor);
    $tagset->define_actor($actor);
}

sub define_element {
    my ($actor, @attrs) = @_;

    ## Define a new HTML element
    ##	  Optionally takes a name and attribute-list.

    $actor = IF::IA->new($actor, 'element'=> 1, @attrs) unless ref($actor);
    $tagset->define_actor($actor);
}


### Uncomment to test handle and package search
#define_actor('-foo1-', 'package' => frobozz, 'handle' => 'new');
#define_actor('-foo2-', 'package' => IF::Run, 'handle' => 'frobozz');
#define_actor('-foo3-', 'package' => IF::Run, 'handle' => 'eval_perl');
#define_actor('-foo4-', 'package' => PIA::Agent, 'handle' => 'new');

#############################################################################
###
### Standard HTML:
###

$tagset = IF::Tagset->new('HTML');

define_element('!', 'empty'=>1, 'special'=> 1);
define_element('!--', 'empty'=>1, 'special'=> 1);
define_element('?', 'empty'=>1, 'special'=> 1);

define_element('img', 'empty'=>1);
define_element('hr', 'empty'=>1);
define_element('br', 'empty'=>1);
define_element('link', 'empty'=>1);
define_element('input', 'empty'=>1);
define_element('p', 'empty'=>1, 'dscr'=>"Really needs to be content-optional");


#############################################################################
###
### Standard Interform Actors:
###

$tagset = IF::Tagset->new('Standard');
$tagset->include('HTML');


#############################################################################
###
### Passive actors:
###

### -eval_perl-		attr: language=perl

define_actor('-eval-perl-', 'quoted' => 'quoted', 'unsafe' => 1,
	     'match' => 'language=perl', 'package' => 'IF::Run', 
	     'dscr' => "evaluate CONTENT as perl code (DEPRECATED).");

### -foreach-		attr: foreach
###	Expects attr's list="..." or list1="..." ...
###	Binds entities &li; &1; ... 
###	Optional: entities="n1 ..."

define_actor('-foreach-', 'quoted' => 'quoted', 
	     'match' => 'foreach', 
	     'dscr' => "repeat ELEMENT for each ENTITY in LIST of words");

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


### -input.size-fit-
###	recognizes <input SIZE=fit MIN-SIZE=n MAX-SIZE=m>content</input>
###	and sets size to min(m, max(n, size(content)))

### -select.selected-
###	recognizes <select SELECTED="option">content</select>
###	and makes SELECTED, if present, the selected option.

#############################################################################
###
### Active Actors:
###

###### Bindings:

### actor:  active, quoted.
###	Defines a new actor.

define_actor('actor', 'quoted' => 'quoted', 
	     'dscr' => "define an InterForm actor");

sub actor_handle {
    my ($self, $it, $ii) = @_;
    $ii->replace_it($ii->define_actor(IF::IA->recruit($it)));
}

define_actor('element', 'empty' => 1,
	     'dscr' => "define an HTML element");

sub element_handle {
    my ($self, $it, $ii) = @_;
    $it->attr('element', 1);
    $ii->replace_it($ii->define_actor(IF::IA->recruit($it)));
}

define_actor('tagset', 'streamed'=>1,
	     'dscr' => "start using an InterForm tagset called NAME.
Optionally pass-through DOCumentation.");

sub tagset_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    $ii->use_tagset('name', $it->attr('doc'));
}

define_actor('tagset-include', 'empty'=> 1,
	     'dscr' => "include an InterForm tagset called NAME.");

sub tagset_include_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    $ii->replace_it($ii->tagset->include('name'));
}

### submit
###	Submits $it or every form in its contents.  Used during initialization. 
###	The following InterForm code makes <form> active:
###		<actor name=form handle="submit_forms"></actor>

define_actor('submit-forms', 
	     'dscr' => "Submit a form or link ELEMENT
 or every form (not links) in CONTENT.  
 Optionally submit at HOUR, MINUTE, DAY, MONTH, WEEKDAY. 
 Optionally REPEAT (missing hour, day, month, weekday are wildcards).  
 Optionally CANCEL a previous submission, matched by url and form data. ");

sub submit_forms_handle {
    my ($self, $it, $ii) = @_;

    if ($it->tag eq 'form') {
	my $url = $it->attr('action');
	my $method = $it->attr('method');
	my $agent = IF::Run::agent();
	my $request = $agent->create_request($method,$url,$it);
	timed_submission($it, $request) ||
	    $IF::Run::resolver->unshift($request);
    } elsif ($it->attr('href')) {
	my $url = $it->attr('href');
	my $request = $agent->create_request('GET', $url);
	timed_submission($it, $request) ||
	    $IF::Run::resolver->unshift($request);
    } else {
	$it->traverse(sub {
			  my($elt, $start, $depth) = @_;
			  return 1 unless $start;
			  submit_forms_handle($self, $elt, $ii) 
			      if $elt->tag eq 'form';
			  return 1;
		      }, 'ignoretext');
    }
}

@time_attrs = qw( repeat hour minute day month weekday cancel );

sub timed_submission {
    my ($it, $request) = @_;

    ## Submit $request if $it has any timing attributes,
    ##	otherwise return false

    my $timed = 0;
    my %attrs;
    my ($a, $v);

    foreach $a (@time_attrs) {
	if (($v = $it->attr($a))) {
	    $attrs{$a} = $v;
	    $timed ++;
	}
    }
    return 0 unless $timed;

    print "timed submit \n";
    $IF::Run::resolver->timed_submission($request, \%attrs);
   
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
###	Variables can also be accessed as entities:
###	e.g. &AGENT.Foo.bar;
###	

### === <get file="name"> should map to read

define_actor('get', 'empty' => 1, 
	     'dscr' => "Get value of NAME, 
optionally in PIA, ENV, AGENT, FORM, ELEMENT, TRANSaction, or ENTITY context.
If FILE or HREF specified, functions as read.");

### === EXPAND/PROTECT ?===

sub get_handle {
    my ($self, $it, $ii) = @_;

    if ($it->attr('file') || $it->attr('href')) {
	return read_handle($self, $it, $ii);
    }

    my $name = $it->attr('name');
    $name = $name->as_string if ref $name;
    my $result;

    if ($it->attr('pia')) {
	local $agent = IF::Run::agent();
	local $request = IF::Run::request();
	$name = '$' . $name;
	my $status = $agent->run_code("$name", $request);
	print "Interform error: $@\n" if $@ ne '' && ! $main::quiet;
	print "code status is $status\n" if  $main::debugging;
	$result = ($status);
    } elsif ($it->attr('env')) {
	$result = ($ENV{$name});
    } elsif ($it->attr('form')) {
	my $hash = IF::Run::request()->parameters;
	$result = ($$hash{$name});
    } elsif ($it->attr('agent')) {
	local $agent = IF::Run::agent();
	$result = ($agent->option($name)) if defined $agent;
    } elsif ($it->attr('trans')) {
	local $trans = IF::Run::transaction();
        if ($it->attr('feature')) {
	    $result = ($trans->get_feature($name)) if defined $trans;
	} elsif ($it->attr('headers')) {
	    if ($it->attr('request')) {
		$result = $trans->is_request? $trans->request->headers_as_string
		    : $trans->response_to->request->headers_as_string;
	    } else {
	        $result = $trans->message->headers_as_string;
	    }
	} else {
	    $result = ($trans->attr($name)) if defined $trans;
	}
    } elsif ($it->attr('entity')) {
	$result = ($ii->entities->{$name});
    } elsif ($it->attr('element')) {
	$result = ($ii->in_token->attr($name));
    } elsif ($it->attr('local')) {
        $result = ($ii->getvar($name));
    } else {
	## default is to search local variables first, then global entities.
        $result = ($ii->get_entity($name));
    }
    $ii->replace_it($result);
}

### <set name="name" value="value">
### <set name="name">value</set>

define_actor('set', 'content' => 'value',
	     'dscr' => "set NAME to VALUE, 
optionally in PIA, AGENT, ACTOR, ELEMENT, TRANSaction, or ENTITY context.   
ELEMENT and ENTITY may define a LOCAL binding.  ELEMENT may have a TAG.  
TRANSaction item may be FEATURE.
Optionally COPY new or PREVIOUS value.");

### === COPY / COPY PREVIOUS ===
### === TAG= / ACTOR ===

sub set_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    my $value = $it->attr('value');

    $value = $it->content unless defined $value;
    
    if ($it->attr('pia')) {
	local $agent = IF::Run::agent();
	local $request = IF::Run::request();
	$name = "\$" . $name; 
        $value = $value->as_string if ref $value;
	my $status = $agent->run_code("$name='$value';", $request);
	print "Interform error: $@\n" if $@ ne '' && ! $main::quiet;
	print "code status is $status\n" if  $main::debugging;
	$ii->replace_it($status);
    } elsif ($it->attr('agent')) {
	local $agent = IF::Run::agent();
        if ($it->attr('hook')) {
            $value = IF::IT->new()->push($it->content);
        } else {
            $value = $value->as_string if ref $value;
	}
	$agent->option($name, $value) if defined $agent;
    } elsif ($it->attr('trans')) {
	local $trans = IF::Run::transaction();
        if ($it->attr('feature')) {
	    $trans->set_feature($name, $value) if defined $trans;
	} else {
	    $trans->attr($name, $value) if defined $trans;
	}
    } elsif ($it->attr('local')) {
	$ii->defvar($name, $value);
    } elsif ($it->attr('entity')) {
	$ii->entities->{$name} = $value;
    } elsif ($it->attr('element')) {
	$ii->in_token->attr($name, $value);
    } else {
	$ii->setvar($name, $value);
    }
    if ($it->attr('copy')) {
    	$ii->replace_it($it->content);
    } else {
	$ii->delete_it;
    }
}

###### Control Structure:

### <if><test>condition</test><then>...</then><else>...</else></if>
###	condition is false if it is empty or consists only of whitespace.

define_actor('if', 
	     'dscr' => "if TEST non-null, expand THEN, else ELSE.");
define_actor('then', 'quoted' => 'quoted', 'handle' => 'null',
	     'dscr' => "expanded if TEST true in an &lt;if&gt;");
define_actor('else', 'quoted' => 'quoted', 'handle' => 'null',
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
    my $list = list_items($it->attr('list'));
    print "repeating: $entity for (". join(' ', @$list) . ")\n"
	if $main::debugging > 1;
    my $body = $it->content;
    my $item = shift @$list;
    my $context = $ii->entities;

    return unless defined $item;

    $ii->defvar($entity, $item);
    $ii->push_input([$self, 0, $body, $entity, $list, $context]);
    $ii->delete_it;
}

sub repeat_end_input {
    my ($self, $it, $ii) = @_;

    my ($foo, $pc, $body, $entity, $list, $entities) = @$it;

    print "repeat: $entity @$list \n" if $main::debugging > 1;

    my $item = shift @$list;

    if (defined $item) {
	#$ii->define_entity($entity, $item);
	$ii->defvar($entity, $item);
	$it->[1] = 0;		# reset the pc
	$ii->push_input($it);
    } else {
	$ii->entities($entities);
    }
    return $undefined;
}

### Expansion control: 
###	protect, protect-result

define_actor('protect', 'quoted' => 'quoted', 
	     'dscr' => "Protect CONTENT from expansion.  Optionally protect
MARKUP by converting special characters to entities.");

define_actor('protect-result', 'handle' => 'protect',
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

define_actor('expand', 
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
define_actor('test', 'content' => 'value',
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
	eval {
	    ## in an eval block because an ill-formed match will croak.
	    if ($it->attr('case')) {
		$result = 1 if $text =~ /$match/;
	    } else {
		$result = 1 if $text =~ /$match/i;
	    }
	}
    } else {
	$result = 1 unless $text =~ /^\s*$/;
    }

    test_result($result, $it, $ii);
}

### <equal [options]>strings</equal>

define_actor('equal', 'content' => 'list',
	     'dscr' => "test tokens in LIST (content) for equality; 
return null or IFFALSE if false, else '1' or IFTRUE. 
Modifiers: NOT, CASE (sensitive), TEXT, LINK, NUMERIC.");

sub equal_handle {
    my ($self, $it, $ii) = @_;

    my $list = get_list($it);
    my ($a, $b, $compare);
    my $prep = prep_item_sub($it);

    if ($it->attr('numeric')) {
	$compare = sub{$a == $b};
    } else {
	$compare = sub{$a eq $b};
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

define_actor('sorted', 'content' => 'list', 
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
	$compare = $reverse? sub {$a >= $b} : sub {$a <= $b};
    } elsif ($it->attr('text')) {
	$compare = $reverse? sub {$a ge $b} : sub {$a le $b};
    } else {
	$compare = $reverse? sub {$a ge $b} : sub {$a le $b};
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

### <sum>list</sum> <difference>list</difference> 
### <product>list</product> <quotient>list</quotient>

define_actor('sum', 'dscr' => "Return sum of numbers in CONTENT");

sub sum_handle {
    my ($self, $it, $ii) = @_;

    my $list = list_items($it);
    my $result=0;

    my $n;
    foreach $n (@$list) {
	$result += ref($n)? $n->content_text : $n;
    }
    $ii->replace_it($result);
}

define_actor('difference', 'dscr' => "Return difference of numbers in CONTENT");

sub difference_handle {
    my ($self, $it, $ii) = @_;

    my $list = list_items($it);
    my $result=shift(@$list);
    $result = $result->content-text if ref($result);

    my $n;
    foreach $n (@$list) {
	$result -= ref($n)? $n->content_text : $n;
    }
    $ii->replace_it($result);
}

define_actor('product', 'dscr' => "Return product of numbers in CONTENT");

sub product_handle {
    my ($self, $it, $ii) = @_;

    my $list = list_items($it);
    my $result=1;

    my $n;
    foreach $n (@$list) {
	$result *= ref($n)? $n->content_text : $n;
    }
    $ii->replace_it($result);
}

define_actor('quotient', 'dscr' => "Return quotient of numbers in CONTENT");

sub quotient_handle {
    my ($self, $it, $ii) = @_;

    my $list = list_items($it);
    my $result=shift(@$list);
    $result = $result->content-text if ref($result);

    my $n;
    foreach $n (@$list) {
	$n = $n->content_text if ref($n);
	if ($n == 0) {
	    $ii->replace_it('***');
	    return;
	}
	$result /= $n;
    }
    $ii->replace_it($result);
}


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
define_actor('sort', 'content' => 'value', 
	     'dscr' => "sort tokens in  LIST (content).
Modifiers: CASE (sensitive), TEXT, NUMERIC, REVERSE.");

sub sort_handle {
    my ($self, $it, $ii) = @_;

    my $list = get_list($it);
    my $reverse = $it->attr('reverse');
    my $prep = prep_item_sub($it);

    my @tmp = map { [&{$prep}($_), $_] } @$list;
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

define_actor('text', 
	     'dscr' => "eliminate markup from CONTENT.");

sub text_handle {
    my ($self, $it, $ii) = @_;

    my $text = $it->content_text;
    $ii->replace_it($text);
}


### <trim>content</trim>

define_actor('trim', 
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

define_actor('pad', 
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

define_actor('add-markup', 
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

### <join [separator="string"] [pairs [pair-separator="string"]]>list</split>

### <subst match="pattern" result="pattern">text</subst>


###### InterForm Actors:

### === very kludgy -- should just use attributes ===
### <actor-dscr name="name">
define_actor('actor-dscr', 'content' => 'name', 
	     'dscr' => "get an actor's DSCR attribute");

sub actor_dscr_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    $name = $it->content_string unless defined $name;
    my $link = $it->attr('link');

    my $a = $ii->tagset->actors->{$name};
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
	     'dscr' => "get an actor's attributes in documentation format");

%no_show = ('dscr'=>1, 'tag'=>1, 'name'=>1);

sub actor_attrs_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    $name = $it->content_string unless defined $name;
    my $link = $it->attr('link');

    my $ia = $ii->tagset->actors->{$name};
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

###### I/O:

sub file_lookup {
    my ($self, $it, $ii, $write) = @_;

    ## Look up a file.

    my $file = $it->attr('file');
    my $base = $it->attr('base');

    if ($it->attr('interform')) {
	$file = IF::Run::agent()->find_interform($file);
	$base = '';
    }
    if ($file =~ /^~/) {
	$file =~ s/^~//;
	$base = $ENV{'HOME'};
    } elsif ($file =~ /^\//) {
	$base = '';
    } elsif ($base eq '') {
	$base = IF::Run::agent()->agent_directory;
    }
    if ($base ne '' && $base !~ /\/$/) {
	$base .= '/';
    }
    my $fn = "$base$file";
    $fn =~ s://:/:g;

    return $fn;
}

### <read [file="name" | href="url"] [base="path"] [tagset="name"] [resolve]>

define_actor('read', 'empty' => 1, 
	     'dscr' => "Input from FILE or HREF, with optional BASE path.
FILE may be looked up as an INTERFORM.   Optionally read only INFO or HEAD.
For DIRECTORY, read names or LINKS, and return TAG or ul.  DIRECTORY can read 
ALL names or those that MATCH; default is all but backups.
Optionally PROCESS with optional TAGSET.  Optionally RESOLVE in pia.");

### === not clear what to do with directories.  LINKS? FILES? ===
### === HEAD? IF-LATER-THAN="date"? 

sub read_handle {
    my ($self, $it, $ii) = @_;

    my $file = $it->attr('file');
    my $href = $it->attr('href');
    my $base = $it->attr('base');
    my $info = $it->attr('info');
    my $head = $it->attr('head');
    my $dir  = $it->attr('directory');
    my $tag  = $it->attr('tag');
    my $f;

    my $content;

    if ($file && ! $href) {		# File
	my $fn = file_lookup($self, $it, $ii);

	## Check to see if the file exists.

	my $exists = -e $fn;
	my $isdir  = -d $fn;

	if (! $exists) {
	    ## Requested file doesn't exist.  Return null.
	    $content = '';
	} elsif ($dir && ! $isdir) {
	    ## Requested a directory, but it isn't.  Return null.
	    $content = '';
	} elsif ($info) {
	    my $w = -w $fn;
	    my $x = -x $fn;
	    my $r = -r $fn;

	    ## === use stat stuff if ALL ===
	    my ($dev,$ino,$mode,$nlink,$uid,$gid,$rdev,$size,
		$atime,$mtime,$ctime,$blksize,$blocks) = stat($fn);

	    if ($info =~ /^d/i)    { $content = $isdir? 'd' : ''; }
	    elsif ($info =~ /^r/i) { $content = $r? 'r' : ''; }
	    elsif ($info =~ /^w/i) { $content = $w? 'w' : ''; }
	    elsif ($info =~ /^x/i) { $content = $x? 'x' : ''; }
	    elsif ($info =~ /^p/i) { $content = $fn; }    	# path
	    elsif ($info =~ /^m/i) { $content = $mtime; } 	# modified
	    elsif ($info =~ /^s/i) { $content = $size; }  	# size
	    else {
		$content = $isdir? 'd' : '-';
		$content .= $r? 'r' : '-';
		$content .= $w? 'w' : '-';
		$content .= $x? 'x' : '-';
		$content .= " $size";
		$content .= "	$fn";
	    }
	} elsif ($dir || $isdir) {
	    my @names;
	    if (opendir(DIR, $fn)) {
		@names = readdir(DIR);
		closedir(DIR);
	    }
	    my @names = sort @names;
	    if (! $it->attr('all')) {
		my @tmp = @names;
		@names = ();
		my $match = $it->attr('match');
		$match = '[^~]$' unless defined $match; #'
		for $f (@tmp) {
		    if ($f ne '.' && $f =~ /$match/) {
			push (@names, $f);
		    }
		}
	    }
	    my $itag = 'li';
	    $itag = 'dt' if $tag eq 'dl';
	    
	    if ($it->attr('links')) {
		$tag = 'ul' unless $tag;
		$content = IF::IT->new($tag);
		for $f (@names) {
		    my $entry = IF::IT->new('a', href=>"file:$fn/$f", $f);
		    $content->push(IF::IT->new($itag, $entry));
		}
	    } elsif ($tag) {
		$content = IF::IT->new($tag);
		for $f (@names) {
		    $content->push(IF::IT->new($itag, $f));
		}
	    } else {
		$content = join(' ', @names);
	    } 
	} elsif ($it->attr('process')) {
	    ## Really just want to push the input stream.
	    ## Requires an input stack that can handle streams.
	    print "processing $fn\n" unless $main::quiet;
	    $content = IF::Run::parse_html_file($fn);
#	    $content = IF::Run::run_file($fn, $ii);
	} else {
	    $content = readFrom($fn);
	}
    } elsif ($href && ! $file) { 	# Href

	## === read href unimplemented ===

    } elsif ($href) {
	print "InterForm error: both HREF and FILE specified\n";
    } else {
	print "InterForm error: neither HREF nor FILE specified\n";
    }

    if ($it->attr('process')) {
	$ii->push_into($content);
    } else {
	$ii->replace_it($content);
    }
}

### <write [file="name" | href="url"] [base="path"] [tagset="name"]
###	    [append] [copy [protect [markup]]] >content</output>

define_actor('write', 'content' => 'value', 
	     'dscr' => "Output CONTENT to FILE or HREF, with optional BASE 
path.  FILE may be looked up as an INTERFORM.  BASE directory is created if 
necessary.  Optionally APPEND or POST.  Optionally TRIM leading and trailing 
whitespace. Optionally end LINE.  Optionally COPY content to InterForm.");

sub write_handle {
    my ($self, $it, $ii) = @_;

    my $file = $it->attr('file');
    my $href = $it->attr('href');
    my $base = $it->attr('base');

    my $text = $it->attr('text');
    my $content = $text? $it->content_text : $it->content_string;

    if ($it->attr('trim')) {
	$content =~ s/^[\n\s]*//s;
	$content =~ s/[\n\s]*$//s; 
    }
    if ($it->attr('line')) {
	$content .= "\n" unless $content =~ /\n$/s;
    }
    if ($file && ! $href) {	# File
	my $append = $it->attr('append');
	my $dir = $it->attr('directory');

	if ($it->attr('interform')) {
	    $base = IF::Run::agent()->agent_if_root();
	}
	if ($file =~ /^~/) {
	    $file =~ s/^~//;
	    $base = $ENV{'HOME'};
	} elsif ($file =~ /^\//) {
	    $base = '';
	} elsif ($base eq '') {
	    $base = IF::Run::agent()->agent_directory;
	}
	$base =~ s:/$:: if $base;
	if ($base ne '' && ! -d $base) {
	    if (! mkdir($base, 0777)) {
		my $err = "InterForm error: can't create directory $base\n";
		print $err;
		$ii->replace_it($err);
		return;
	    }
	}

	my $fn = $base? "$base/$file" : $file;
	$fn =~ s://:/:g;
	$fn =~ s:/$::;

	if ($file eq '.') {
	    # nothing to do; just make sure the base directory exists.
	} elsif ($append) {
	    appendTo($fn, $content);
	} else {
	    writeTo($fn, $content);
	}
    } elsif ($href && ! $file) {	# Href (PUT or POST)
	my $post = $it->attr('post');

	## === write href unimplemented ===

    } elsif ($href) {
	my $err = "InterForm error: both HREF and FILE specified\n";
	print $err;
	$ii->replace_it($err);
	return;
    } else {
	my $err = "InterForm error: neither HREF nor FILE specified\n";
	print $err;
	$ii->replace_it($err);
	return;
    }

    if ($it->attr('copy')) {
	$ii->replace_it($it->content);
    } else {
	$ii->delete_it;
    }
}



###### PIA Agents:

### <agent-home>name</agent-home>
###	expands to the agent's home interForm name.
###	Makes a link if the "link" attribute is present.

###	This is incredibly kludgy, but it works!

define_actor('agent-home', 'content' => 'name', 
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

define_actor('agent-set-options', 'unsafe' => 1, 'content' => 'options', 
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


### <agent-set-criteria>query_string</agent-set-criteria>

if (0) { #=== buggy!
define_actor('agent-set-criteria', 'unsafe' => 1, 'content' => 'criteria',
	     'dscr' => "Sets CRITERIA for agent NAME" );
}
sub agent_set_criteria_handle {
    my ($self, $it, $ii) = @_;

    my $criteria = get_list($it, 'criteria');
    my $name = $it->attr('name');
    my $agent;

    if ($name) {
	$agent = IF::Run::resolver()->agent($name);
    } else {
	$agent = IF::Run::agent();
	$name = $agent->name;
    }

    $agent->criteria($criteria); # === almost certainly wrong ===
    $ii->delete_it();
}

define_actor('agent-set-criterion', 'empty' => 1, 'unsafe' => 1, 
	     'dscr' => "set match criterion NAME to VALUE (default 1), 
optionally in AGENT.");

sub agent_set_criterion_handle {
    my ($self, $it, $ii) = @_;

    my $aname = $it->attr('agent');
    my $agent;

    if ($aname) {
	$agent = IF::Run::resolver()->agent($aname);
    } else {
	$agent = IF::Run::agent();
	$aname = $agent->name;
    }

    my $name = $it->attr('name');
    my $value = $it->attr('value');
    $value = 1 unless defined $value;
    $agent->match_criterion($name, $value);
    $ii->delete_it();
}

### <agent-criteria>

define_actor('agent-criteria', 'empty' => 1,  'unsafe' => 1,
	     'dscr' => "Returns list of match criteria for agent NAME" );

sub agent_criteria_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    my $agent;

    if ($name) {
	$agent = IF::Run::resolver()->agent($name);
    } else {
	$agent = IF::Run::agent();
	$name = $agent->name;
    }

    $ii->replace_it(criteria_to_list($agent->criteria));
}

sub criteria_to_list {
    my ($criteria) = @_;

    my $list = IF::IT->new('dl');
    my $i;

    for ($i = 0; $i <= $#$criteria; $i++) {
	my $c = $$criteria[$i];
	if (! ref $c) {
	    $list->push(IF::IT->new('dt', $c));
	    local $v = $$criteria[++$i];
	    if (! ref $v) {
		$list->push(IF::IT->new('dd', $v));
	    } else {
		$list->push(IF::IT->new('dd', $v));
	    }
	} elsif (ref($c) eq 'ARRAY') {
	    $list->push(IF::IT->new('dt', criteria_to_list($c)));
	}
    }
    return $list;
}    

### <agent-install name='n' type='t'>

define_actor('agent-install', 'content' => 'options',
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

define_actor('agent-remove', 'unsafe' => 1, 'content' => 'name',
	     'dscr' => "Remove (uninstall) an agent with given NAME." );

sub agent_remove_handle {
    my ($self, $it, $ii) = @_;

    my $name = get_text($it, 'name');
    my $agent = IF::Run::agent(); # had better be agency

    $agent->un_install_agent($name) if defined $name;
    $ii->replace_it($name);
}

define_actor('agent-list', 'content' => 'type', 
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

### Transaction

define_actor('trans-control', 
	     'dscr' => "Add a control to the current response." );

sub trans_control_handle {
    my ($self, $it, $ii) = @_;

    my $text = $it->content_string;
    my $response = IF::Run::request();

    $response -> add_control($text);
    print "add_control($text)\n" if $main::debugging;

    $ii->delete_it;
}

###### PIA:

### <user-message>string</user-message>

define_actor('user-message', 
	     'dscr' => "Display a message to the user." );

sub user_message_handle {
    my ($self, $it, $ii) = @_;

    my $content = $it->content_string;
    print "$content\n" unless $main::quiet;
    $ii->delete_it;
}

### <pia-exit>

define_actor('pia-exit', 'unsafe' => 1, _handle => \&pia_exit_handle,
	     'dscr' => "Exit from the pia, after printing CONTENT." );

sub pia_exit_handle {
    my ($self, $it, $ii) = @_;

    my $content = $it->content_string;

    ## === should really set a flag and let the resolver quit cleanly.
    die "$content\n";

    $ii->delete_it;
}

###### Operating System:

### <os-command>command</os-command>

define_actor('os-command', 'unsafe' => 1, 'content' => 'command', 
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

define_actor('os-command-output', 'unsafe' => 1, 'content' => 'command', 
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
