////// Actor.java:  InterForm Actors
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
import crc.interform.Token;
import crc.interform.Tokens;
import crc.interform.Handler;
import crc.interform.Interp;

/**
 * The representation of an InterForm <em>actor</em>.
 *	This is the parent class for actors that operate inside of
 *	Interforms.  An actor is basically an active SGML element;
 *	indeed, it would be more correct to say that an element is an
 *	especially trivial and passive actor.
 */
public class Actor extends Token {

  /** Name attribute. */
  public String name() {
    return this.attr("name").toString();
  }


  /************************************************************************
  ** Matching tokens:
  ************************************************************************/

  /** List of match criteria, as a sequence of attribute, value, etc.
   *	Attribute names are Text; values are Text or List.  A value of
   *	null matches if the attribute is present; an empty list
   *	matches if it is missing.  (This seems backwards, but testing
   *	for presence is very common and this speeds it up.) Text
   *	matches the value converted to lowercase.  A null criteria
   *	list matches anything. */
  Tokens criteria = null;

  /** Determine whether this actor matches a given token.  Note that a
   *  null criteria list will match anything; a non-null list will
   *  match only a Token. */
  public boolean matches(SGML it, Interp ii, byte incomplete, int quoting) {
    if (criteria == null) return false;
    if (! it.isElement()) return criteria.nItems() == 0;

    // Note that at this point it must be a Token.

    Token itt = it.toToken();

    for (int i = 0; i < criteria.nItems(); i += 2) {
      String a = criteria.itemAt(i).toString();
      SGML v = criteria.itemAt(i+1);
      SGML av = itt.attr(a);

      if (v == null) {		// Null v matches if attr is present.
	if (av == null) return false;
      } else if (v.isList()) { 	// (null) List matches if attr is missing
	// === eventually perhaps a list of alternate values.
	if (av != null) return false; 
      } else if (av == null) {	// Anything else fails if the attr is missing
	return false;
      } else if (! v.toString().equalsIgnoreCase(av.toString())) {
	return false;
      }
    }
    return true;
  }


  /************************************************************************
  ** Syntactic Actions:
  ************************************************************************/

  /** Handler for this actor's actOn method. */
  Handler action = null;

  /** Tell the default action routine whether to quote the content. */
  int quoteContent = 0;

  /** True if there is no content, i.e. the Actor matches an empty tag. 
   *	=== This should really be done with syntax. */
  boolean noContent = false;

  /** Act on a matching token.  Normally what this does is push the
   *	actor itself as a handler, and set the interpretor to parse
   *	the content.  Some syntactic analysis can also be done here to
   *	determine exactly <em>which</em> handler to push.
   */
  public void actOn(SGML it, Interp ii, byte incomplete, int quoting) {
    if (action != null) {
      action.actOn(this, it, ii, incomplete, quoting);
    } else if (quoting != 0 || incomplete <= 0) {
      return;
    } else if (handler != null || !isEmpty()) {
      if (noContent) {
	ii.completeIt();
      } else {
	ii.setQuoting(quoteContent);
	ii.setParsing();
      }
      ii.addHandler(this);
    } else if (noContent) {
      ii.completeIt();
    }
  }


  /************************************************************************
  ** Semantic Handle:
  ************************************************************************/

  /** Handler for this actor. */
  Handler handler;

  /** Perform the actions associated with a completed token.  The
   *	parse stack will have been popped (or never pushed in the
   *	first place), and the completed token will be in
   *	<code>ii.token()</code> as well as being passed as
   *	<code>it</code>.  If no handler object (Strategy pattern) is
   *	defined, the default is to run the content as a subroutine. */
  public void handle(SGML it, Interp ii) {
    if (handler != null) { handler.handle(this, it, ii); }
    else if (! isEmpty()) {
      ii.defvar("element", it);
      ii.defvar("content", it.content());
      ii.pushInto(content());
      SGML ts = (Tagset) attr("tagset");
      if (ts != null) { ii.useTagset(ts.toString()); }
      ii.deleteIt();
    }
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public Actor(String tag) {
    super("-actor-");
    initialize(null, tag, null);
  }

  public Actor(Tokens attrs) {
    super("-actor-");
    initialize(null, null, attrs);
  }

  public Actor(Token source) {
    super("-actor-");
    initialize(source, null, null);
  }

/* ========================================================================

#############################################################################
###
### Creation:
###

sub new {
    my ($class, $name, @attrs) = @_;
    my $self = IF::IT->new('-actor-', @attrs);
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

=========================================================================== */

  /************************************************************************
  ** Initialization:
  ************************************************************************/

  /** Initialize from a Token (typically an &ltactor&gt element)
   *	and/or a tag and attribute list.  If the attribute list has an
   *	odd number of elements, the last is the content.  The
   *	arguments are processed from left to right, so attributes of
   *	the Token may be overridden by the tag string and attribute
   *	list. */
  void initialize(Token source, String tag, Tokens attrs) {

  }


  /** Initialize the <code>handle</code> object. */
  void initHandle() {

  }

  /** Initialize the match <code>criteria</code> list. */
  void initMatch() {

  } 

/* ========================================================================
sub handler {
    my ($name, $pkg) = @_;

    ## Return a handler for $name in package $pkg.
    ##	  Requires major disgusting hacks to get around perl weirdness.

    my $foo = "${pkg}::$name";
    return \&{$foo} if defined &{$foo};
    return \&{$foo . "_handle"} if defined &{$foo . "_handle"};

    ## The first part will do the job UNLESS the handle we want is inside 
    ##	 a package that isn't loaded yet.  So load it.  This is _still_ not
    ##	 good enough, because it might be defined in a package that _uses_
    ##	 Actors.pm, (e.g. IF::Run) so the handle might not actually get 
    ##	 defined until after we need it.  You don't want to know.

    my $file = $pkg;
    $file =~ s@::@/@g;
    eval { 
	require $file . '.pm';
	print "$file loaded, looking for '$foo'\n" if $main::debugging;
	return \&{$foo} if defined \&{$foo};
	## Note that the blasted thing might not be defined.  
	##    It appears, though, that this will succeed anyway, so you 
	##    croak later if the name is never defined.
    };
}

sub initialize {
    my ($self, $name) = @_;

    ## Initialize an actor.
    ##	  Force the actor to obey the standard conventions:
    ##	    force name lowercase to match tag if active
    ##	    'active' attribute if active.

    ## If there's an 'element' attribute, this ``actor'' is just a 
    ## 	  syntactic description of an HTML element, and is not active.
    ##	  It's not used for anything at the moment.

    my $element = $self->attr('element');
    $self->tag($element? '-element-' : '-actor-');

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
	$self->hook($hook, \&act_streamed) if $self->attr('streamed');
    }

    if ($active) {
	$name = lc $name;
	$self->attr('tag', $name) unless defined $tag;
    }
    $self->attr('name', $name);

    ## Handle match='name=value...'  Should handle attr=, value= as well.
    ## 	  List is encoded as a query string.
    my $match = $self->attr('match');
    if ($match) {
	my @list = split(/\&/, $match);
	my @pairs = ();
	for $item (@list) {
	    if ($item =~ /(.+)=(.*)/) {
		$2 =~ s/\+/ /g;
		push(@pairs, $1, $2);
	    } else {
		push(@pairs, $item, $item);
	    }
	}
	$self->{'_match'} = \@pairs;
    }

    my $handle = $self->attr('handle');
    if (lc $handle eq 'handle' || $handle eq '1') {
	$handle = $name;
    } elsif (lc $handle eq 'null') {
	$handle = '';
    }
    if ($handle) {
	$handle =~ s/^[-.]//;
	$handle =~ s/[-.]$//;
	$handle =~ s/[-.]/\_/g;
	my $package = $self->attr('package') || 'IF::Actors';
	my $handler = handler($handle, $package);
	if ($handler) {
	    $self->{'_handle'} = $handler;
	} else {
	    print "Cannot find handler $handle in package $package\n";
	}
    } elsif (! $self->is_empty) {
	$self->{'_handle'} = \&generic_handle;
    }
    $self;
}


=========================================================================== */

}

/************************************************************************
** ActOn Handlers:
************************************************************************/

/** Parse and evaluate the matching element's contents */
class ActParsed extends Handler {
  public void actOn(Actor ia, SGML it, Interp ii, byte inc, int quot) {
    if (quot != 0 || inc <= 0) return;
    if (ia.handler != null || ! ia.isEmpty()) ii.addHandler(ia);
    ii.parseIt();
  }
}

/** Parse but do not evaluate the matching element's content. */
class ActQuoted extends Handler {
  public void actOn(Actor ia, SGML it, Interp ii, byte inc, int quot) {
    if (quot != 0 || inc <= 0) return;
    if (ia.handler != null || ! ia.isEmpty()) ii.addHandler(ia);
    ii.setQuoting(ia.quoteContent);
  }
}

/** Handle an empty element. */
class ActEmpty extends Handler {
  public void actOn(Actor ia, SGML it, Interp ii, byte inc, int quot) {
    if (inc > 0) {
      ii.completeIt();
      // === shouldn't need this anyway === it.attr("_endless", new Tokens());
    } else if (quot != 0) {
      if (ia.handler != null || ! ia.isEmpty()) ii.addHandler(ia);
    }
  }
}

/** Perform the handler immediately on the start tag. */
class ActStreamed extends Handler {
  public void actOn(Actor ia, SGML it, Interp ii, byte inc, int quot) {
    if (quot != 0 || inc <= 0) return;
    ia.handle(it, ii);
  }
}


/* ========================================================================

=== Not clear anything needs actGeneric anymore.  Many actors have it
set, but there are few if any actual uses of it. ===

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
	$ii->add_handler($self) if $self->{_handle};
    }
}
=========================================================================== */


