////// Interp.java: the Interform Interpretor
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
import crc.interform.Actor;
import crc.interform.Token;
import crc.interform.Tokens;
import crc.interform.Input;

import crc.ds.List;
import crc.ds.Table;

/**
 *	The Interform Interpretor parses a string or file, evaluating any 
 *	Interform Actors it runs across in the process.  Evaluation is
 *	usually done concurrently with parsing because new tags and
 *	entities can be defined at any time.  However, it is also
 *	possible to execute a saved parse tree.  This is a good thing,
 *	because actors are *stored* as parse trees.
 */
public class Interp {

  /************************************************************************
  ** Components:
  ************************************************************************/

  /** Parser/interpretor state stack.  
   *	Contains everything that needs to be pushed when the interpretor
   *	starts processing a start tag.  State objects form a linked list.
   */
  State state;

  /** Entity table for this document. */
  Table entities;

  /** Output queue. 
   *	The output queue is kept in a Tokens list in order to take advantage
   *	of the automatic merging of strings.
   */
  Tokens output;

  /** Input stack. 
   *	The input stack is a linked list of Input objects.
   */
  Input input;


  /************************************************************************
  ** Access to global state:
  ************************************************************************/

  public final Tokens output() {
    return output;
  }

  public final Table entities() {
    return entities;
  }

  /************************************************************************
  ** State stack operations:
  ************************************************************************/

  final State context(int level) {
    State s = state;
    for ( ; level > 0 && s != null; --level) { s = s.next; }
    return s;
  }

  /************************************************************************
  ** Access to things in the stack frame:
  ************************************************************************/

  public final SGML token() {
    return state.token;
  }
  public final void token(Token t) {
    state.token = t;
  }

  public final List handlers() {
    return state.handlers;
  }

  public final Table variables() {
    return state.variables;
  }

  public final boolean isPassing() {
    return state.passing;
  }
  public final void setPassing() {
    state.passing = true;
    state.parsing = false;
    state.skipping= false;
  }

  public final boolean isParsing() {
    return state.parsing;
  }
  public final void setParsing() {
    state.parsing = true;
    state.passing = false;
    state.skipping= false;
  }

  public final boolean isSkipping() {
    return state.skipping;
  }
  public final void setSkipping() {
    state.parsing = false;
    state.passing = false;
    state.skipping= true;
  }

  /** Test the streaming flag.  
   * 	If true, the interpretor converts tokens to strings before
   *	putting them on the output queue.
   */
  public final boolean isStreaming() {
    return state.streaming;
  }
  public final void setStreaming() {
    state.streaming= true;
    setPassing();
  }

  /** Test the quoting flag.
   *	If non-zero, no processing is done on incoming tokens.
   *	If negative, the current tag contains unparsed character data.
   */
  public final boolean isQuoting() {
    return state.quoting != 0;
  }
  public final boolean isUnparsed() {
    return state.quoting < 0;
  }
  public final void setQuoting(int i) {
    state.quoting = i;
  }

  /** Test to see if we can define actors in the current tagset.  If
   *	true, we are using a local copy of a named tagset or we're
   *	inside a &gt;tagset&lt; element, and it's OK to define local
   *	actors in it.  Otherwise, we're in a global tagset that has to
   *	be copied if we want to change it.  */
  public final boolean tagsetUnlocked() {
    return ! state.tagset.isLocked;
  }

  /** Get the current tagset */
  public final Tagset tagset() {
    return state.tagset;
  }

  /** Use a tagset.  Make a copy of the current one if tagset is null. */
  public final void useTagset(Tagset t) {
    if (t != null) {
      state.tagset = t;
    } else {
      state.tagset = (Tagset)state.tagset.clone();
    }
  }

  /** Use a named tagset.  Make a copy of the current one if the name
   *	is null. */
  public final void useTagset(String name) {
    // === Ignore documentation for now -- do it with tags.
    if (name != null) {
      state.tagset = Tagset.tagset(name);
    } else {
      state.tagset = (Tagset)state.tagset.clone();
    }
  }

  /** Make a copy of the current tagset if we don't already have one. */
  public final void useTagset() {
    if (state.tagset.isLocked) {
      state.tagset = (Tagset)state.tagset.clone();
    }
  }

  /** Define an actor.  Clone the current tagset if necessary. */
  public final void defineActor(Actor anActor) {
    useTagset();
    state.tagset.define(anActor);
  }

  /************************************************************************
  ** Access to Variables (entities):
  ************************************************************************/

  /** Get the value of a named local variable (entity).
   *	Dynamic scoping is used, with an optional variable (entity) table
   *	in each State stack frame.  Eventually we may switch to shallow 
   *	binding.  Returns null if no local binding is found.
   */
  public final SGML getvar (String name) {
    for (State context = state; state != null; state = state.next) {
      if (state.variables != null && state.variables.has(name)) {
	return (SGML)state.variables.at(name);
      }
    }
    return null;
  }

  /** Set the value of a named variable (entity).
   *	If no local binding is found, a new one is created in the current 
   *	stack frame.
   */
  public final void  setvar (String name, SGML value) {
    for (State context = state; state != null; state = state.next) {
      if (state.variables != null && state.variables.has(name)) {
	state.variables.at(name, value);
	return;
      }
    }
    defvar(name, value);
  }

  /** Define a new local variable (entity) with a given name and value.
   */
  public final void  defvar (String name, SGML value) {
    if (state.variables == null) state.variables = new Table();
    state.variables.at(name, value);
  }

  /** Get the value of a named variable (entity).
   *	Dynamic scoping is used, with an optional variable (entity) table
   *	in each State stack frame.  If no local variable is found, the 
   *	document's global entity table is used.
   */
  public final SGML getEntity (String name) {
    for (State context = state; state != null; state = state.next) {
      if (state.variables != null && state.variables.has(name)) {
	return (SGML)state.variables.at(name);
      }
    }
    return (SGML)entities.at(name);
  }

  /************************************************************************
  ** Expanding entities:
  ************************************************************************/

  /** Expand entities (according to the current bindings) in some SGML.
   *	The object is copied.  We really only have to worry about Tokens
   *	(token lists) and Token elements with a tag of ampersand.
   */
  public SGML expandEntities(SGML it) {
    if (it.isList()) {
      Tokens old = it.content();
      Tokens tl = new Tokens();
      for (int i = 0; i < old.nItems(); ++i) {
	tl.append(expandEntities(old.itemAt(i)));
      }
      return tl;
    } else if (it.tag() == "&") {
      SGML v = getEntity(it.entityName());
      return (v == null)? it : v;
    } else {
      return it;
    }
  }


  /** Expand entities in text, lists, or start tags.  Other SGML 
   *	(basically end tags and complete elments) is passed through 
   *	unchanged.  Start tags are expanded in place; others are copied.
   */
  SGML expandAttrs(SGML it) {
    if (it.isElement()) {
      if (it.incomplete() > 0) {
	Token t = it.toToken();	// should be a no-op.
	for (int i = 0; i < t.nAttrs(); ++i) 
	  t.attrValueAt(i, expandEntities(t.attrValueAt(i)));
	it = t;			// but just to make sure...
      }
    } else {
      it = expandEntities(it);
    }
    return it;
  }

  /************************************************************************
  ** Getting and Pushing Input:
  ************************************************************************/

  public void pushInput(SGML t) {
    if (t.isList()) {
      input = new InputList(t, input);
    } else {
      input = new InputToken(t, input);
    }
  }

  public void pushInput(Input in) {
    in.prev = input;
    input = in;
  }

  public void pushInto(SGML t) {
    if (t.isText()) {
      input = new InputToken(t.toText(), input);
    } else if (t.isElement()) {
      input = new InputExpand(t, input);
    } else {
      input = new InputList(t.content(), input);
    }
  }

  public SGML nextInput() {
    SGML s;
    do {
      if (input == null) return null;
      s = input.nextInput();
      if (input.endInput()) {
	input = input.prev; 
      }
    } while (s == null);
    return s;
  }


/* ========================================================================


   ===> nextInput needs to check need_start_tag, most likely.

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


============================================================= */

  /************************************************************************
  ** Pushing Input:
  ************************************************************************/

  /** Start work on an HTML element.  
   *	  If we are expecting more input, push the element onto the
   *	  stack.  Otherwise, handle it appropriately for a completed
   *	  element.
   */
  public void startIt(SGML t) {

  }

  /** End an element with a given tag.
   *	  The tag is optional.  If equal to "", it pops a single item;
   *	  if null, it pops the entire stack.
   */
  public void endIt(String tag) {

  }

/* ========================================================================

sub start_it {
    my ($self, $it) = @_;


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

============================================================= */
  /************************************************************************
  ** The Resolver (interpretor) itself:
  ************************************************************************/

  /** The ``Resolver'' is the InterForm Interpretor's main loop.
   *	If passed a token it processes just that token, otherwise it
   *	pulls incoming tokens or completed subtrees off the input
   *	stack and processes them.  This allows the interpretor to be
   *	used either in ``push'' or in ``pull'' mode.
   */
  public final void resolve(SGML it) {
    if (it == null) { it = nextInput(); }

    /* Loop on incoming tokens.  This and "incomplete" used to be passed 
     *	as function arguments; it's better to use the input stack.
     */
    for ( ; it != null; ) {
      byte incomplete = it.incomplete();

      if (incomplete < 0) {
	/* We just popped an end tag. 
	 *	Pop the stack, marking the token we find there as
	 *	complete if we were actually parsing, otherwise change
	 *	it to an end tag.  (It will still have its attributes.)
	 */
	boolean was_parsing = state.parsing;
	state = state.next;
	it = state.token;
	incomplete = (byte)(was_parsing? 0 : -1);
	it.incomplete(incomplete);
      } else if (state.quoting == 0) {
	it = expandAttrs(it);
      }
	
      /* At this point even empty tokens are marked as start tags,
       *    in order to make expandAttrs suppress the copy.
       */
      // === worry about that.  Add checkForSyntax? ===

      state.token = it;

      // print " (" . (ref($it)? $it->tag : "...") . " $incomplete) " if $main::debugging > 1;
      
      if (incomplete > 0) {
	/* Start tag.  Check for interested actors.
	 *	keep track of any that register as handlers.
	 */
	state.handlers = new List();
	state = new State(state);
	checkForInterest(it, incomplete);

	it = state.token;	// See if any actor has modified the token
	if (it == null) {
	  // Some actor has deleted the token: pop the stack.
	  state = state.next;
	} else if (it.incomplete() == 0) {
	  // Some actor has marked it as finished: pop it.
	  state = state.next;
	  continue;
	} else {
	  // Nothing happened; it stays pushed.
	  //	Clean out the state for the content.
	  state.variables = null;
	  state.handlers = new List();
	  if (state.passing) passToken(it);
	}
      } else {
	/* End tag or complete token. */
	checkForInterest(it, incomplete);
	checkForHandlers(it);

	it = state.token;
	if (it != null) {
	  if (state.parsing) pushToken(it);
	  if (state.passing) passToken(it);
	}
      }

      /* Get another token.  Do it here rather than in the for loop
       *    so that a "continue" above will keep the token that's there. 
       */
      it = nextInput();
    }
  }

  /************************************************************************
  ** Checking for actors:
  ************************************************************************/

  /** Check for any actors interested in this token, and run their 
   *	actOn method.  Possibly should go into Tagset.  Syntax should
   *	perhaps be different.
   */
  final void checkForInterest(SGML it, byte incomplete) {
    Tagset ts 	  = tagset();
    List handlers = state.handlers;
    int nPassive  = ts.nPassive();
    int quoting   = state.quoting;
    Actor a 	  = ts.activeFor(it.tag());

    /* Find the actor interested in this tag, if any */
    if (a != null) {
      a.actOn(it, this, incomplete, quoting);
    }

    /* now find anything that matches the token */
    for (int i = 0; i < nPassive; ++i) {
      a = ts.passiveAt(i);
      if (a.matches(it, this, incomplete, quoting)) {
	a.actOn(it, this, incomplete, quoting);
      }
    }
  }

  /** Run the handle method of any actor that has registered its interest. */
  final void checkForHandlers(SGML it) {
    List handlers = state.handlers;
    Actor a;
    while ((a = (Actor)handlers.pop()) != null) {
      a.handle(it, this);
    }
  }


  /************************************************************************
  ** Processing:
  ************************************************************************/

  /** Run the interpretor until it completes.  Return the output as either
   *	text or as a single token, depending on isStreaming.
   */
  public SGML run() {
    flush();
    return isStreaming()? (SGML)output.toText() : (SGML)output.toToken();
  }

  /** Flush the interpretor's input queue, running it to completion.
   */
  public void flush() {
    endIt(null);
  }

  /* Step: undefined; may not be needed. */


  /************************************************************************
  ** Output:
  ************************************************************************/

  /** Pass a token or tree to the output. */
  void passToken(SGML it) {
    if (! state.streaming) {	// Not streaming: just pass the tree
      output.append(it);
    } else {
      /* The PERL version used to elaborately check "incomplete" and 
       *   do the right thing, including expand lists.  appendTextTo
       *   does the right thing directly.
       */
      it.appendTextTo(output);
    }
  }

  /** Push a completed tree onto the contents of its parent,
   *	or onto the output queue if we're at the top level.
   */
  void pushToken(SGML it) {
    if (it == null) return;
    if (state.next == null) {
      passToken(it);
    } else {
      state.next.token.append(it);
    }
  }

  /************************************************************************
  ** Routines called by Actors:
  ************************************************************************/

  /** Add an actor as a handler for the current token. */
  public final void addHandler(Actor a) {
    state.handlers.push(a);
  }

  /** Mark the current token as completed. */
  public final void completeIt() {
    state.token.incomplete((byte)0);
  }

  /** Parse (i.e. construct a parse tree for) the contents of the
   *	current token. */
  public final void parseIt() {
    setParsing();
  }

  /** Parse the contents of the current token, but don't expand actors
   *	or entities.  Optionally ignore all markup and suck in the
   *	content as a single Text. */
  public final void quoteIt(boolean ignoreMarkup) {
    setQuoting(ignoreMarkup? -1 : 1);
  }

  public final void replaceIt(SGML it) {
    state.token = it;
  }

  public final void deleteIt() {
    state.token = null;
  }
    

  /************************************************************************
  ** Constructors:
  ************************************************************************/

/* ========================================================================

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

 ======================================================================== */
}

/**
 * Interpretor state (parse/execution stack frame).
 *	State objects don't have many methods; they're really local
 *	to the interpretor.
 */
class State {
  SGML	token;
  List	handlers;
  Table variables;

  boolean passing;
  boolean parsing;
  boolean skipping;
  boolean streaming;
  int quoting;

  boolean hasLocalTagset;
  Tagset tagset;

  /** Link to previous state */
  State next;

  State() {
  }

  State(State s) {
    token 	= s.token;
    handlers 	= s.handlers;
    variables 	= null;
    passing 	= s.passing;
    parsing 	= s.parsing;
    skipping 	= s.skipping;
    streaming 	= s.streaming;
    quoting 	= s.quoting;
    hasLocalTagset = s.hasLocalTagset;
    tagset 	= s.tagset;

    next 	= s;
  }
}



/**
 * Input stack frame for a single token.
 */
class InputToken extends Input {
  SGML 		it;

  public SGML nextInput() {
    return it;
  }
  public InputToken(SGML t, Input p) {
    super(p);
    it = t;
  }
}

/**
 * Input stack frame for a list being expanded.
 */
class InputList extends Input {
  Tokens 	it;
  int 		item 	= 0;
  int		limit	= 0;

  public SGML nextInput() {
    return (item < limit)? it.itemAt(item++) : null;
  }
  public boolean moreInput() {
    return item < limit;
  }

  public InputList(SGML t, Input p) {
    super(p);
    it = t.content();
    limit = it.nItems();
  }
}

/**
 * Input stack frame for a complete element
 */
class InputExpand extends Input {
  Token 	it;
  int 		item;
  int		limit;

  /** nextInput has to start by returning a <em>copy</em> of the start tag,
   *	followed by the content, followed by the end tag if necessary.
   */
  public SGML nextInput() {
    if (item < 0) {
      item++;
      if (! it.hasEndTag()) {
	item = 1; // no content or end tag required.
      }
      return it.startToken();
    } else if (item < limit) {
      return it.itemAt(item++);
    } else {
      item++;
      return it.endToken();
    }
  }
  public boolean moreInput() {
    return item <= limit;
  }

  public InputExpand(SGML t, Input p) {
    super(p);
    it = t.toToken();
    item  = -1;
    limit = it.nItems();
  }
}

