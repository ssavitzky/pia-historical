package IF::II; ###### Interform Interpretor
###	$Id$
###	Copyright 1997, Ricoh California Research Center.
###
###	The Interform Interpretor parses a string or file, evaluating any 
###	Interform Actors it runs across in the process.  Evaluation is
###	usually done concurrently with parsing because new tags and
###	entities can be defined at any time.  However, it is also
###	possible to execute a saved parse tree.  This is a good thing,
###	because actors are *stored* as parse trees.



require IF::Parser;
use HTML::Entities ();
use IF::IT;
use IF::IA;
use DS::Tokens;
use IF::Semantics qw(is_list);
use IF::Tagset;

push(@ISA,'IF::Parser');	# === At some point we will use our own. ===
				#     the problem is that HTML::Parser
				#     does some things we don't want.
				#     We should eventually move to the
				#     full SGML parser.

#############################################################################
###
### InterForm Interpretor Object:
###
###   Input:
###	The II object can take its input from either a file, a string, a
###	stream (subroutine that returns chunks), or a parse tree.
###
###   Output:
###	The II object can provide output as either a string, a
###	stream, or a parse tree.
###
### ===	the stream stuff is missing at this point. ===
###
###   Interface:
###	$interp = II->new(arg => value, ...);
###	$result= $interp->run;
###
###   Theory:
###	The interpretor regards its input as a sequence of SGML tokens:
###	either start tags, end tags, declarations (e.g. <!DOCTYPE...>),
###	comments, or text.  It is a subclass of HTML::Parser.
###
###	A set of ``actors'' may be provided which are matched against
###	tags and other features of items in the token stream.
###
###   Parse Stack:
###	The parse stack is kept in an array of IT (Interform Token)
###	objects; tokens are accumulated if necessary into the contents
###	of the top element on the stack.   An object remains on the 
###	stack until its end tag has been seen.  There is a separate
###	control stack for flags and state variables.
###
### ===	It might be simpler to consolidate the two stacks.
###
###   Output Queue:
###	The output queue contains tokens or strings that have been fully
###	processed and so are ready to be sent to the user.  It is in
###	the form of an anonymous token for easy processing.

%kludge = ('tagset'  =>1,	# attributes we need to set directly
	   'entities'=>1);

sub new {
    my ($class, @attrs) = @_;
    my $self = IF::Parser->new;

    $self->{_dstack} =  [];	# The stack of items under construction.
    $self->{_cstack} =  [];	# The control stack.
    $self->{_out_queue} = DS::Tokens->new(); # a queue of tokens to be output.
    $self->{_in_stack} =  [];	# a stack of tokens to be input.
    $self->{_state} = {};	# A ``stack frame''

    bless $self, $class;

    my $attr, $val;
    while (($attr, $val) = splice(@attrs, 0, 2)) {
	$val = 1 unless defined $val;
	if ($kludge{$attr}) {
	    $self->$attr($val);
	} else {
	    $self->{_state}->{"_$attr"} = $val;
	}
	print "  $attr = $val\n" if $main::debugging > 1;
    }

    $self;
}

#############################################################################
###
### Access to Global State:
###

sub cstack {
    my $self = shift;
    return $self->{_cstack};
}

sub dstack {
    my $self = shift;
    return $self->{_dstack};
}

sub out_queue {
    my $self = shift;
    return $self->{_out_queue};
}
sub in_stack {
    my $self = shift;
    return $self->{_in_stack};
}

sub state {
    my ($self, $v) = @_;

    ## Parser and interpretor state.  
    ##	  the push_state operation makes a *copy* of the state (a hash
    ##	  table).

    $self->{'_state'} = $v if defined($v);
    return $self->{'_state'};
}

sub context {
    my ($self, $level) = @_;

    ## current state if $level = 0 or undefined, 
    ##	  else stack frame [-$level].

    return $self->{'_state'} unless $level;
    return $self->{'_cstack'}->[- $level];
}

#############################################################################
###
### Access to Things in the stack frame:
###

sub token {
    my ($self, $v) = @_;

    ## Current token

    $self->state->{'_token'} = $v if defined($v);
    return $self->state->{'_token'};
}

sub handlers {
    my ($self, $v) = @_;

    ## Handler actors for the current token

    $self->state->{'_handlers'} = $v if defined($v);
    return $self->state->{'_handlers'};
}

sub variables {
    my ($self, $v) = @_;

    ## variable table:
    ##	  A hash table that defines local variables for this level of
    ##	  the parse tree.

    $self->state->{'_variables'} = $v if defined($v);
    $self->state->{'_variables'};
}

sub entities {
    my ($self, $v) = @_;

    ## entity table:
    ##	  A hash table that defines the current set of entities.
    ##	  Unlike variables and like actors, entities are rarely rebound.

    $self->{'_entities'} = $v if defined($v);
    $self->{'_entities'};
}

sub passing {
    my ($self, $v) = @_;

    ## passing flag:
    ##	  if true, tokens are passed to the output queue
    ##	  parsing and passing are mutually exclusive

    $self->state->{'_passing'} = $v if defined($v);
    $self->state->{'_parsing'} = !$v if defined($v);
    $self->state->{'_passing'};
}

sub parsing {
    my ($self, $v) = @_;

    ## parsing flag:
    ##	  if true, incoming tokens are pushed onto their parent.
    ##	  parsing and passing are mutually exclusive

    $self->state->{'_parsing'} = $v if defined($v);
    $self->state->{'_passing'} = !$v if defined($v);
    $self->state->{'_parsing'};
}

sub skipping {
    my ($self, $v) = @_;

    ## Turn off both parsing and passing flags if $v is supplied.

    $self->state->{'_parsing'} = 0 if defined($v);
    $self->state->{'_passing'} = 0 if defined($v);
    $self->state->{'_parsing'} || $self->state->{'_passing'};
}

sub streaming {
    my ($self, $v) = @_;

    ## streaming flag:
    ##	  if true, tokens are passed to the output as strings.

    $self->state->{'_streaming'} = $v if defined($v);
    $self->state->{'_streaming'};
}

sub quoting {
    my ($self, $v) = @_;

    ## quoting flag.
    ##	  if true, no processing is done on incoming tokens.
    ##	  if negative, the entire content of the current tag is sucked
    ##	  into a single string without parsing.

    $self->state->{'_quoting'} = $v if defined($v);
    $self->state->{'_quoting'};
}

sub has_local_tagset {
    my ($self, $v) = @_;

    ## local_tagset flag
    ##	  if true, a local tagset exists.  Otherwise, we're using a
    ##	  global one, which has to be copied if we want to change it.

    $self->state->{'_has_local_tagset'} = $v if defined($v);
    $self->state->{'_has_local_tagset'};
}

sub tagset {
    my ($self, $v) = @_;

    ## Tagset:
    ##	  A tagset object contains the actors and syntax information needed 
    ##	  to control the interpretor and parser.

    if (defined $v) {
	$v = IF::Tagset::tagset($v) unless ref $v;
	$self->state->{'_tagset'} = $v;
    }
    $self->state->{'_tagset'};
}

sub use_tagset {
    my ($self, $name, $doc) = @_;

    ## Use a named tagset.
    ##	  If $doc is true, actor declarations will be documented.

    my $ts;
    if ($name) {
	$ts = $self->tagset($name);
    } else {
	$ts = $self->open_actor_context;
    } 

    if ($doc) {
	$ts->attr('doc', 1);
	return IF::IT->new('h2', ($name? $name : '(Default)') . ' Tagset');
    }
    return;
}


#############################################################################
###
### Access to Variables:
###
###	A variable table is maintained in each stack frame; dynamic scoping
###	is used to access them.  Attributes in the current tag can be used 
###	to provide private scope.

###      variable names look like "scope.name.key1.key2...keyN"
###      internally, these are represented as hashes or description lists
###       each key moves 1 level down the hash tree
###      ranges can be specified by "start-end" or "start,second,third", 
###      or wildcards "*"
###      variable values should eventually be tokens


 # returns an array of tokens, references to hashes/ arrays, or atomic structure
##  note array values in variables should be references

## most of the function in the following hacks should be moved to
# the respective classes (e.g. lookup key)
sub  get_value_internal{
    my($self,$hash,$key)=@_;
				# keys should already be parsed
    my @result;
				#  hash might be an object that is blessed
				# as something else... treat special cases first

    return unless ref($hash);
    
    if(ref($hash) eq 'ARRAY'){
	#checkfor numbers
	$key=~ s/-/../;
	if($key eq '*'){
	    push(@result,@$hash);
	}else{
	    push(@result,$$hash[$key]);
	}
    } elsif(ref($hash) eq 'IF::IT' || ref($hash) eq 'DS::Tokens'){
	# what to do for non description list?

	my $content = $hash;
	$content=$hash->content if ref($hash) eq 'IF::IT';
	if (defined $content) {
	    my $dd;
	    my @content = @$content;

	     while(@content) {
		 $dt = shift(@content);
#		 print "DT is" . $dt->tag ." \n" if ref($dt);
		 if (ref($dt) && $dt->tag  eq  'dt') {
		     my $dd=$dt->content_text;
		     if($key eq '*' || $key =~ /^\s*$dd\s*$/){
			 while(@content){
			     $dd=shift(@content);
			     next unless ref($dd);
			     
			     if( $dd->tag  eq  'dd'){
				 push(@result,@{$dd->content}) ;
				 last;
			     }
			     if ($dd->tag  eq  'dt'){
				 unshift(@content,$dd);
				 last;
			     }
			    
			 }
		     }
		 }
	     }
	}
    } else {
	##unknown reference type...
	 ##  if( ref($hash) eq 'HASH'){
	if( exists $$hash{$key}){
	    push(@result,$$hash{$key}) ;
	} else {
	    # checkfor wildcards
	    if($key =~ /\*/){
		foreach $value (values %$hash){
		    push(@result, $value);
		}
	    }
	}
    }

    return  @result;
}

 # parse a variable name, return an array of keys
sub  parse_variable_name{
    my($self,$name)=@_;
    return split(/\./, $name);
        
}

sub get_variable_value{
    my($self,$variable)=@_;
print "getting variable $variable !\n" if $main::debugging;
    my @keys=$self->parse_variable_name($variable);
    my $space=shift @keys;
     return unless exists $self->entities->{$space};

    my @hashes;

    my @result;
    push(@result,$self->entities->{$space});
    
    foreach $key (@keys){
	push(@hashes,@result);
	splice(@result,0);	# remove intermediate results
	
	while(@hashes){
	    push(@result,$self->get_value_internal(shift @hashes, $key));
	}

    }
				# tidyup result here
    return $self->convert_array_to_token(@result);
        
}

#turn hashes into dl tokens,  lists into ul, single text unchanged
sub convert_array_to_token{
    my($self)=shift;
    my @copy;
    foreach $value (@_){
	my $type=ref($value);

	if($type){
#	    print "package type not responds to attr? " unless $ {$type}{attr};

    
				# deal with known types first
	    if($type eq 'IF::IT' || $type eq 'DS::Tokens'){
				# donothing
	    }elsif($type eq 'ARRAY'){
		$value=$self->convert_array_to_token(@$value);
	    }elsif($type eq 'HASH'){
		$value=$self->convert_hash_to_token($value);
	    }else{
# generic  in object
 # we should put in a check for weather the object has appropriate token creation methods 
		$value=$self->convert_object_to_token($value);
				
	    }}
	push(@copy,$value);
    }
#only one item, then return it
return shift(@copy) unless $#copy > 0;
    #make token for array
    return IF::IT->new('ol', @copy);
}

#create a description list
sub convert_hash_to_token{
    my($self,$hash)=@_;
    my $token=IF::IT->new('dl');
    foreach $key (keys %$hash){
	$token->push(IF::IT->new('dt',$key));
	$token->push(IF::IT->new('dd',$self->convert_array_to_token($$hash{$key})));
    }
    return $token;
    
}

#create a description list
sub convert_object_to_token{
    my($self,$hash)=@_;
    my $token=IF::IT->new('ul');
    foreach $key (keys %$hash){
	$token->push(IF::IT->new('li',$key));
 #	$token->push(IF::IT->new('dd',$self->convert_array_to_token($$hash{$key})));
    }
    return $token;
    
}

sub getvar {
    my ($self, $v) = @_;

    ## Retrieve the value of a variable.  Look up the stack if
    ##	  necessary. 

## if v is in dotted notation "AGENT.foo.bar" do special processing lookup
    return $self->get_variable_value($v) if $v =~ /\./;
    

    my $level = 0;
    my $context;
    while (defined $self->context($level)) {
	$bindings = $self->context($level) -> {_variables};
	if (ref $bindings && defined $bindings->{$v}) {
	    return $bindings->{$v};
	}
	$level += 1;
    }
    return;
}


sub set_variable_value{
    my($self,$variable,$value)=@_;

#this get real tricky
#first retrieve normally except final key...

    my @keys=$self->parse_variable_name($variable);
    my $space=shift @keys;
#    print "setting " . join(" ",@keys) . "in $space\n";
    return unless exists $self->entities->{$space};
    my @hashes;
    my @result;
    push(@result,$self->entities->{$space});
    
    my $lastkey=pop(@keys);
    return unless $lastkey;

    foreach $key (@keys){
	push(@hashes,@result);
	splice(@result,0);	# remove intermediate results
	
	while(@hashes){
	    push(@result,$self->get_value_internal(shift @hashes, $key));
	}
    }

 # if value is single token in tokens, just use it, not whole array
#  should use remove_spaces in semantics
    if(ref($value) eq 'DS::Tokens'){
	$value=IF::Semantics::remove_spaces($value);
# 	my @values;
# 	foreach $token (@$value){
# 	    if(ref($token)){
# 		push(@values,$token);
# 	    }else{
# 		push(@values,$token) if $token =~ /\S/;	# lose garbage
# 	    }
#           }
	if(@$value == 1){		# more than one element?
	    $value=shift(@$value);
	}
    }
		
	

#anything that is a hash gets this new value
    foreach $hash (@result){
	my $type=ref($hash);
	if($type){ 
#	    print "type is $type\n";
	    if ($type eq 'IF::IT' || $type eq 'DS::Tokens') {
# tokens act as description lists
		$hash->push(IF::IT->new('dt',$lastkey));
		$hash->push(IF::IT->new('dd',$value));
	    }else{		# treat everythingelse as hash
				# possibly convert value to hash?
		$$hash{$lastkey}=$value;
	    }
	}
    }
}



sub setvar {
    my ($self, $v, $value) = @_;

    ## Set the value of a variable.  Look up the stack to find the
    ##	  current binding, and change it.

## if v is in dotted notation "AGENT.foo.bar" do special processing lookup
    return $self->set_variable_value($v,$value) if $v =~ /\./;

    my $level = 0;
    my $context;
    while (defined $self->context($level)) {
	$bindings = $self->context($level) -> {_variables};
	if (ref $bindings && defined $bindings->{$v}) {
	    return $bindings->{$v} = $value;
	}
	$level += 1;
    }
    return $self->defvar($v, $value);
}

sub defvar {
    my ($self, $v, $value) = @_;

    ## Bind a variable to a value in the current context.

    my $vars = $self->variables;
    if (! ref $vars) {
	$vars = $self->variables({});
    }

    $vars->{$v} = $value;    
}

sub get_entity {
    my ($ii, $name) = @_;

    ## Get the value of a named entity, or a variable if one is
    ##    defined locally.  Variables take precedence.

    my $x = $ii->getvar($name);
    return (defined($x)? $x : $ii->entities->{$name});
}

sub expand_entities {
    my ($ii, $string, $expand_hex) = @_;

    ## expand_entities($ii, $string) replaces valid HTML entities 
    ##	in the string with the corresponding entry in $ii's current 
    ##	current entity table, returning either a string or a token list.

    ## #hex is *not* expanded unless $expand_hex is true.

    my $ents = $ii->entities;
    $ents = \%entity2char unless defined $ents;

    my $result = new DS::Tokens;

    $string =~ s/(&\#(\d+);?)/$2 < 256 ? chr($2) : $1/eg if $expand_hex;
    return $string unless ($string =~ /(&([-.\w]+);?)/);

    while ($string =~ s/^([^&]*)\&//) {
	$result->push($1) if (length($1));
	if ($string =~ s/^(\w[-.\w]*)(;?)//) {
	    ## Don't use get_entity -- it's faster to cache $ents.
	    my $x = $ii->getvar($1);
	    $result->push(defined($x)? $x :
			  (exists $ents->{$1})? $ents->{$1} : "&$1$2");
	} else {
	    $result->push("&");
	}
    }
    $result->push($string) if length($string);
    return ($result->is_text)? $result->as_string : $result;
}

sub expand_string_entities {
    my $ii = shift;

    ## expand_string_entities($ii, $string) replaces valid HTML entities 
    ##	in the string with the corresponding entry in $ii's current 
    ##	entity table.  #hex is *not* expanded.
    ##	The string, passed by reference, is expanded in place.

    ## === This is currently unused; it's kept around for historical reasons.

    my $ents = $ii->entities;
    $ents = \%entity2char unless defined $ents;

    for (@_) {
#	s/(&\#(\d+);?)/$2 < 256 ? chr($2) : $1/eg;
	s/(&([-.\w]+);?)/(exists $ents->{$2})? (ref ($ents->{$2})
						? $ents->{$2}->as_string
						: $ents->{$2}) : $1/eg;
    }
    $_[0];
}

#############################################################################
###
### Utilities:
###


#############################################################################
###
### Input from the input stack:
###
###	The input stack contains either strings, tokens, or lists.
###	Lists have the form [tag, pc, [token...]] and are used for
###	stepping through the content of a token without excessive copying.
###	When the end of the token list is reached, the entire list is returned.
###
###	If "tag" is a reference, its start_input method is called by 
###	push_input, and its end_input method is called after the last token
###	has been used.  Additional state can be kept after the token list.

sub next_input {
    my ($self) = @_;

    ## Return the next item on the input stack.  Pop if necessary.

    my $in_stack = $self->in_stack;
    my $input = pop @$in_stack;

    return $input unless (ref($input) eq 'ARRAY');

    my ($tag, $pc, $tokens) = @$input;
    return $input unless is_list($tokens); # might be undefined.

    my $out = $tokens->[$pc++];

    return $input unless defined $out;
    
    $input->[1] = $pc;
    push @$in_stack, $input;
    return $out;
}

sub push_input {
    my ($self, @input) = @_;

    ## Push a single item or list onto the input stack

    return unless defined @input;
    my $in_stack = $self->in_stack;
    push @$in_stack, @input;
}

sub push_into {
    my ($self, $it, $st) = @_;

    ## Push the content of a token onto the input stack
    ##	  Return a copied start tag for the token, or $st if present.
    ##	  Attributes in the copy are entity-expanded in the current context

    my $in_stack = $self->in_stack;
    if (!ref($it)) {
	print "pushing empty\n" if (! $it && $main::debugging > 1);
	return unless $it;
	print "pushing string\n" if $main::debugging > 1;
	push(@$in_stack, $it);
    } elsif (is_list($it)) {
	print "pushing list\n" if $main::debugging > 1;
	push(@$in_stack, ['', 0, $it]);
	return $st;
    } else {
	print "pushing token, tag=" . $it->tag . "\n" if $main::debugging > 1;
	push (@$in_stack, [$it->tag, 0, $it->content]);
	return $st if defined $st;

	my $attrs = [];
	my $list = $it->attr_names;
	for (@$list) {
	    my $v = $it->{$_};
	    push(@$attrs, $_);
	    push(@$attrs, $self->expand_entities($v));
	}
	return IF::IT->new($it->tag, @$attrs);
    }
}

sub expand_attrs {
    my ($self, $it) = @_;

    ## Expand entities in the attributes of a tag 
    ##	  or in a content string.

    return $self->expand_entities($it) unless ref($it);
    my $expanded = 0;
    my $attrs = [];
    my $list = $it->attr_names;
    for (@$list) {
	my $v = $it->{$_};
	my $ev = $self->expand_entities($v);
	push(@$attrs, $_, $ev);
	++ $expanded;
    }
    return $expanded? IF::IT->new($it->tag, @$attrs) : $it;
}

sub need_start_tag {
    my ($self, $it) = @_;

    ## Return true if we need to generate a start tag for $it.
    ##	  We only need a start tag (processed with $incomplete=1)
    ##	  if the token needs an end tag, and only if it's not being
    ##	  quoted (in which case we won't be doing anything to it).

    return 0 unless ref $it;
    return 0 if $self->quoting;
    return 0 unless $it->needs_end_tag($self);
    return 1;
}

#############################################################################
###
### Processing:
###

sub step {
    my ($self) = @_;

    ## === not completed yet.  Will probably be needed for streaming. ===
}

sub run {
    my ($self) = @_;

    ## === Really needs to get input off the input queue, 
    ##	   going to a file or input stream as needed.

    $self->flush;
    return $self->streaming? $self->out_queue->as_string 
	                   : $self->out_queue->as_token;
}

sub flush {
    my ($self) = @_;

    $self->end_it;
}

#############################################################################
###
### The Parse Stack(s):
###
### ===	These are now inlined in resolve, for efficiency.
###

sub push_state {
    my ($self) = @_;

    ## Push current state onto the stack.

    my $cstack = $self->cstack;
    my $state = $self->state;
    my %newstate = %$state;	# copy the state
    push(@$cstack, \%newstate);	# push the copy
    %newstate->{_variables}=0;	# clear the variable table.
}

sub pop_state {
    my ($self) = @_;

    my $cstack = $self->cstack;
    $self->state(pop(@$cstack));
}


#############################################################################
###
### Syntactic Action Routines:
###
###	Once we have a complete token, here are the basic operations we
###	perform on it.
###

sub start_it {
    my ($self, $it) = @_;

    ## Start work on an HTML element.  
    ##	  If we are expecting more input, push the element onto the
    ##	  stack.  Otherwise, handle it appropriately for a completed
    ##	  element.

    my $syntax = $self->tagset->syntax;
    my $tag = $it->tag;
    while ($self->implicit_end($tag)) {
	$self->end_it('', 'one');
    }

    $self->resolve($it, $it->needs_end_tag);
    return;

    ## === doesn't work ===
    my $elt = $syntax->{$tag};
    if (ref $elt && $elt->attr('empty')) {
	$self->resolve($it, 0);
    } else {
	$self->resolve($it, 1);
    }
}

sub start_tag {
    my ($self, $tag, $attrs) = @_;

    ## Start tag.
    ##	The tag and attribute names have been lowercased.

    my $it = IF::IT->new($tag);
    my ($attr, $val);
    while (($attr, $val) = splice(@$attrs, 0, 2)) {
	$val = $self->expand_entities($val) unless $self->quoting;
	$it->attr($attr, $val);
	print " $attr=$val " if $main::debugging>1;
    }
    $self->start_it($it);
}

sub end_it {
    my ($self, $tag, $one) = @_;

    ## End an element.
    ##	  The tag is optional.  If $one is true, only pop one item
    ##	  whether or not it matches.  With no arguments, it pops the
    ##	  entire stack.

    my $dstack = $self->dstack;
    my $cstack = $self->cstack;
    my $it, $t;
    print " </$tag> " if $main::debugging > 1;
    while (defined($it = pop(@$dstack))) {
	my $was_parsing = $self->parsing;
	$self->state(pop(@$cstack));
	$t = $it->tag;
	$self->resolve($it, $was_parsing? 0 : -1);
	return if ($t eq $tag) or $one;
    }    
}

### stolen from HTML::TreeBuilder === should be in Tagset.pm ===

# Elements that should only be present in the header === not used
%isHeadElement = map { $_ => 1 } qw(title base link meta isindex script);

# Elements that should only be present in the body === not used
%isBodyElement = map { $_ => 1 } qw(h1 h2 h3 h4 h5 h6
				    p div pre address blockquote
				    xmp listing
				    a img br hr
				    ol ul dir menu li
				    dl dt dd
				    cite code em kbd samp strong var dfn strike
				    b i u tt small big
				    table tr td th caption
				    form input select option textarea
				    map area
				    applet param
				    isindex script
				   ),
                          # Also known are some Netscape extentions elements
                                 qw(wbr nobr center blink font basefont);

# The following elements must be directly contained in some other
# element than body.

%isPhraseMarkup = map { $_ => 1 } qw(cite code em kbd samp strong var b i u tt
				     a img br hr
				     wbr nobr center blink
				     small big font basefont
				     table
				    );

%isList         = map { $_ => 1 } qw(ul ol dir menu dl);
%isTableElement = map { $_ => 1 } qw(tr td th caption);
%isInTableRow   = map { $_ => 1 } qw(td th caption);
%isFormElement  = map { $_ => 1 } qw(input select option textarea);

%notP           = map { $_ => 1 } qw(p h1 h2 h3 h4 h5 h6 pre textarea);
%notList	= map { $_ => 1 } qw(h1 h2 h3 h4 h5 h6);

sub in_token {
    my ($self) = @_;

    ## Return the token we are inside of.
    ##	 === used only in Actors.pm to get context for element and attr. ===
    
    return $self->dstack->[-1];
}


sub implicit_end {
    my ($self, $tag) = @_;

    ## Test for implicit end tag.
    ##	  returns true if $tag implicitly ends whatever is on the stack
    
    my $in_it = $self->dstack->[-1];
    return unless defined $in_it;
    my $in = $in_it->tag;
    print " implicit_end $tag in $in?\n" if $main::debugging > 1;

    ## This needs to be done with syntax, but for now we'll ad-hoc it.

    # Handle implicit endings and insert based on <tag> and position
    if ($tag eq 'p' || $tag =~ /^h[1-6]/ || $tag eq 'form') {
	# Can't have <p>, <h#> or <form> inside these
	return $notP{$in};
    } elsif ($isList{$tag}) {
	# Can't have lists inside <h#>
	return $notList{$in};
    } elsif ($tag eq 'li') {
	print "li inside $in\n" if $main::debugging > 1;
	return $in eq 'li';
	## === can't handle li outside list.
    } elsif ($tag eq 'dt' || $tag eq 'dd') {
	return $in eq 'dt' || $in eq 'dd';
	## === can't handle li outside list.
    } elsif ($isFormElement{$tag}) {
	if ($tag eq 'option') {
	    # return unless $ptag eq 'select';
	    return $in eq 'option';
	}
    } elsif ($isTableElement{$tag}) {
	return $isInTableRow{$in} || ($tag eq 'tr' && $in eq 'tr');
    } elsif ($isPhraseMarkup{$tag}) {
	## should insert missing <p> after 'body'
    }
    return 0;
}

#############################################################################
###
###  The ``Resolver'':
###
###	This is the heart of the Interpretor:  it takes a start tag, end 
###	tag, or completed subtree and ``does the right thing'' with it.
###	This means:
###	   1.	push or pop the parse stack
###	   2.	apply any interested actors
###	   3.	put the token in the right place.
###

sub resolve {
    my ($self, $it); local $incomplete;
    ($self, $it, $incomplete) = @_;

    ## Do the right thing to an incoming token.  
    ##	  $incomplete = 0 -- complete subtree
    ##	  $incomplete > 0 -- start tag
    ##	              = 2 -- reprocess tree
    ##	  $incomplete < 0 -- end tag

    my $dstack = $self->dstack;
    my $cstack = $self->cstack;

    $incomplete = 0 unless defined $incomplete;

    if ($incomplete == 2 || (ref $it && $it->is_list)) {
	$self->push_into($it);
	$it = $self->next_input;
	$incomplete = 0;
    } elsif (! defined $it) {
	$it = $self->next_input;
    }
    return unless defined $it;

    ## === the ref($it) below shouldn't be needed ===
    $it = $self->expand_attrs($it) unless (ref($it) || $self->quoting);

    ## === if we get a list for $it, it has problems. ===

    while ($it) {
	## Loop as long as there are tokens to be processed.

	if ($incomplete < 0 && ! ref($it)) {
	    my $was_parsing = $self->parsing;
	    $self->state(pop(@$cstack));
	    $it = pop(@$dstack);
	    $incomplete = $was_parsing? 0 : -1;
	}
	$self->token($it);
	$it->status($incomplete) if (ref($it) && ! $it->is_list);

	print " (" . (ref($it)? $it->tag : "...") . " $incomplete) "
	    if $main::debugging > 1;

	if ($incomplete > 0) {

	    ## Start tag: 
	    ## check for interested actors.
	    ##	keep track of any that register as handlers.

	    $self->handlers([]);	# Clear the handler list.

	    ## Push $it onto the data stack, 
	    ## and push $state onto the control stack.
	    ##	  We push here instead of waiting, because the handlers
	    ##	  want to control the parse in the new context, so we need
	    ##	  local parsing and quoting flags for them to set.

	    my $state = $self->state;
	    my %newstate = %$state; 	# copy the state
	    push(@$cstack, \%newstate);	# Push it on the control stack
	    push(@$dstack, $it);	# push token on the data stack

	    $self->check_for_interest($it, 1);

	    ## See if any interested actor modified the token 

	    $it = $self->token;	# might have been changed.
	    if (!defined($it) || (! ref($it) && $it eq '')) {
		## Some actor has deleted the token.
		print " deleted " if $main::debugging > 1;
		$self->state(pop(@$cstack));
		pop(@$dstack);
	    } elsif (!ref($it) || !$it->status) {
		## Some actor has marked the token as finished
		##	so pop it and go 'round again.
		print " finished " if $main::debugging > 1;
		$self->state(pop(@$cstack));
		pop(@$dstack);
		$incomplete = 0;
		next;
	    } else {
		## Nothing serious happened--it stays pushed.
		##	Clean up the new state.
		$self->variables(0);	# clear the variable table.
		$self->handlers([]);
		$self->pass_it($it, 1) if $self->passing;
	    }
	} elsif ($incomplete <= 0) {
	    ## End tag or complete token (already popped)
	    ## 	check for interested actors and handler actions.

	    $self->check_for_interest($it);
	    $self->check_for_handlers($it);

	    $it = $self->token;	# might have been changed or even deleted.
	    ## === should really get the status at this point. 
	    $incomplete = 0 unless ref($it);
	    if (defined($it) && $it ne '') {
		$self->push_it($it) if $self->parsing;
		$self->pass_it($it, $incomplete) if $self->passing;
	    }
	}

	## get another token and figure out what it was.

	do {
	    $it = $self->next_input;
	    return unless $it;
	    if (is_list($it)) {
		## The end of some tag's content.
		if (ref $it->[0]) {
		    $it = $it->[0]->end_input($it, $self);
		} else {
		    $it = $it->[0];
		}
		$incomplete = $it? -1 : 0;
	    } elsif ($self->need_start_tag($it)) {
		## Something that needs processing on its contents.
		$it = $self->push_into($it);
		$incomplete = 1;
	    } else {
		## Complete token which is either empty, a string, or quoted
		$it = $self->expand_attrs($it) if ! $self->quoting;
		$incomplete = 0;
	    }
	} until ($it);
    }
}


#############################################################################
###
### Output Routines:
###
###	These are called to output a completely-processed token to the 
###	appropriate place. 
###

sub pass_it {
    my ($self, $it, $incomplete) = @_;

    ## Pass a token or tree to the output.

    return unless defined $it;
    my $out_queue = $self->out_queue;

    if (! $self->streaming) {
	$it = $it->endtag if $incomplete < 0;
	$out_queue->push($it);
    } elsif ($incomplete > 0) {
	print "  passing ". $it->starttag ." \n" if $main::debugging > 1;
	$out_queue->push($it->starttag);
    } elsif ($incomplete < 0) {
	print "  passing ".$it->endtag." \n" if $main::debugging > 1;
	$out_queue->push($it->endtag);
    } elsif (! ref($it)) {
	print "  passing $it \n" if $main::debugging > 1;
	$out_queue->push($it);
    } elsif (ref($it) eq 'ARRAY' || $it->is_list) {
	print "  passing [...] \n" if $main::debugging > 1;
	$out_queue->push(@$it);
    } else {
	print "  passing ".$it->starttag."... \n" if $main::debugging > 1;
	$out_queue->push($it->as_string); # === as_HTML? ===
    }
}

sub push_it {
    my ($self, $it) = @_;

    ## Push a completed tree onto the contents of its parent,
    ##	  or the output queue if we're at the top level.

    return unless defined $it;
    my $dstack = $self->dstack;
    if (! @$dstack) {
	## If there's no parent, pass it to the output instead.
	$self->pass_it($it);
    } else {
	print " pushing to ". $dstack->[-1]->tag if $main::debugging > 1;
	$dstack->[-1]->push($it);
    }
}



#############################################################################
###
### Checking for actors:
###

sub check_for_interest {
    my ($self, $it, $incomplete) = @_;
    my $a;
    my $quoting = $self->quoting;

    ## We'd like to do this, but many actors use act_on for syntax.
    #return if $quoting;		# no action if quoting, by definition.

    foreach $a (@{$self->tagset->passive}) {
	if ($a->matches($it, $self, $incomplete, $quoting)) {
	    $a->act_on($it, $self, $incomplete, $quoting);
	    print "    actor ".$a->name." matched it\n" if $main::debugging>1;
	}
    }
    my $t = ref($it)? $it->tag : '';
    if (defined($a = $self->tagset->actors->{$t})) {
	$a->act_on($it, $self, $incomplete, $quoting);
	print "    actor ".$a->name." matched tag\n" if $main::debugging>1;
    }
}

sub check_for_handlers {
    my ($self, $it) = @_;
    my $handlers = $self->handlers;
    my $a;

    while ($a = shift @$handlers) {
	print "    actor ".$a->name." handles it\n" if $main::debugging>1;
	$a->handle($it, $self);
    }
}



#############################################################################
###
### Routines called by actors:
###

sub add_handler {
    my ($self, $actor) = @_;
    my $handlers = $self->handlers;
    push(@$handlers, $actor);
}

sub complete_it {
    my ($self, $it) = @_;

    ## Mark the token as completed.
    ##	  This tells the parser not to expect an end tag.
    if (ref ($it)) {
	$it->status(0);
	$it->empty(1);
	print "  Completed " . $it->tag . " status " . $it->status . "\n"
	    if $main::debugging > 1;
    }
}

sub parse_it {
    my ($self) = @_;

    $self->parsing(1);
}

sub quote_it {
    my ($self, $v) = @_;

    $v = 1 unless defined $v;
    $self->parsing(1);
    $self->quoting($v);
}

sub replace_it {
    my ($self, $it) = @_;

    $self->state->{_token} = ($it);	# handles undefined
    $incomplete = 0;
}

sub delete_it {
    my ($self) = @_;

    $self->token('');
}


sub open_actor_context {
    my ($self, $tagset) = @_;

    ## Start using a new set of actors.
    ##	  The old set is cloned unless a new one is supplied

    if ($tagset) {
	$tagset = $self->tagset($tagset);
	$self->has_local_tagset(0);
    } else {
	$tagset = $self->tagset($self->tagset->clone);
	$self->has_local_tagset(1);
    }
    $tagset;
}


sub define_entity {
    my ($self, $name, $value) = @_;
    $self->{_entities}->{$name} = $value;
}

sub define_actor {
    my ($self, $actor, @attrs) = @_;

    ## Define an actor local to the current interpretor.
    ##	  Exactly what to do with names and tags is unsettled so far.

    $self->open_actor_context unless $self->has_local_tagset;
    $self->tagset->define_actor($actor, @attrs);
}


1;
