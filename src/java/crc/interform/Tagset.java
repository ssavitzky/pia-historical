////// Tagset.java: Sets of Tags (Interform Actors)
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
import crc.ds.Table;
import crc.ds.List;

/** A Tagset is a collection of related Actors.  It is defined using a
 *	&lt;tagset name="..."&gt;...&lt;/tagset&gt element containing
 *	the &lt;actor&gt elements that are part of the set.  A Tagset
 *	element can contain arbitrary text, so it can also serve as
 *	its own documentation.<p>
 *
 *	The &lt;process tagset=name&gt;...&lt;/process&gt; actor
 *	actually selects a tagset for use.  The tagset name may be
 *	omitted when &lt;process&gt; is inside a &lt;tagset&gt;
 *	element.<p>
 *
 *	There will eventually be three sets of actors for processing
 *	tagsets: one that actually defines the tagset and its actors,
 *	one that formats it as a document, and one that deletes
 *	extraneous text for more efficient loading.  The latter two
 *	will usually be used to generate .html and .ts files, respectively.  
 */
public class Tagset extends Token {

  /************************************************************************
  ** Global tagset table:
  ************************************************************************/

  /** Table of all globally-defined tagsets, by name. */
  static Table tagsets = new Table();

  /** Return a Tagset with a given name.
   *	If one doesn't exist, it is created.
   */
  public static Tagset tagset(String name) {
    Tagset t = (Tagset)tagsets.at(name);
    if (t == null) {
      t = new Tagset(name);
      tagsets.at(name, t);
    }
    return t;
  }

  /************************************************************************
  ** Components:
  ************************************************************************/

  /** Table of actors by name. */
  public Table actors;

  /** List of passive actors. */
  public List passive;

  /** Table of local entity definitions. */
  public Table entities;

  /** Table of syntax actors. */
  public Table syntax;

  /** Names of all actors. */
  public String[] actorNames() {
    return actors.keyList();
  }

  /** Name attribute. */
  public String name() {
    return this.attr("name").toString();
  }

  /************************************************************************
  ** Actors:
  ************************************************************************/

  /** Return the actor with a name matching the given tag. */
  public Actor forTag(String t) {
    return (Actor)actors.at(t);
  }

  /** Add an actor to the tagset. */
  public void define(Actor a) {
    
    String name = a.name();
    String tag  = a.tag();

    if (tag == "-element-") {
      syntax.at(name, a);
    } else {
      actors.at(name, a);
      if (tag == null) {
	passive.push(a);
      } else {
	syntax.at(name, a);
      }
    }

    /* === let's skip the documentation stuff for now; do it in the actor.
    if ($doc) {
	if ($doc eq 'doc') {
	    return IF::IT->new('h3', IF::IT->new('a', 'name'=> $name,
						 "Actor $name"));
	} else {
	    return IF::IT->new($doc, $name);
	}
    } else {
	return '';
    }
    ====================================================== */
  }	


  /************************************************************************
  ** Construction:
  ************************************************************************/

  public Tagset() {
  }

  protected Tagset(String name) {
    this.attr("name", name);
  }

/* ========================================================================


sub new {
    my ($class, $name, @attrs) = @_;

    ## Return a tagset with the given name.
    ##	  If the name is non-null, we return a global tagset with that name
    ##	  if it exists.

    if (ref tagset($name)) {
	return tagset($name);
    }
    my $self = IF::IT->new('_tagset_', @attrs);
    bless $self, $class;

    $$self{_actors} = {} unless $$self{_actors};
    $$self{_passive} = [] unless $$self{_passive};
    $$self{_entities}= {} unless $$self{_entities};
    $$self{_syntax}= {} unless $$self{_syntax};

    $self->attr('name', $name) if $name;
    $tagsets{$name} = $self if $name;

    print ("Defined $name tagset\n") if ($name && ! $main::quiet);
    return $self;
}

sub clone {
    my ($self) = @_;

    ## Return an anonymous clone of $self, with disjoint actor tables.
    ##	 Note that there is no need to copy the content, if any.

    my $actors   = $$self{_actors};
    my $passive  = $$self{_passive};
    my $entities = $$self{_entities};
    my $syntax   = $$self{_syntax};

    my %newactors  = %$actors;
    my @newpassive = @$passive;
    my %newents    = %$entities;
    my %newsyntax  = %$syntax;

    ## === We can worry about attributes later if necessary.

    return IF::Tagset->new('', _actors=>\%newactors, _passive=>\@newpassive,
			   _entities=>\%newents, _syntax=>\%newsyntax);
}

sub include {
    my ($self, $ts) = @_;

    ## Include all of $ts's tags and entities in $self.

    $ts = tagset($ts) unless ref $ts;
    print "Undefined tagset $ts\n" unless ref $ts;

    my ($k, $v);

    for $v (values %{$ts->actors}) {
	$self->define_actor($v);
    }

    my $entities = $self->entities;
    while (($k, $v) = each %{$ts->entities}) {
	$$entities{$k} =$v;
    }
    print "Including " . $ts->name . " in " . $self->name . "\n"
	unless $main::quiet;

    return $self->attr('doc')? IF::IT->new('h3', "Include $name") : '';
}
    

#############################################################################
###
### initialization:
###
###	These are the tagsets and actors required to initialize the
###	InterForm Interpretor and the PIA.  We have to be able to
###	process START-UP.html (which initializes the PIA) and
###	Standard.if (which defines the Standard tagset).  This
###	requires the actors -form-submit-, tagset, and actor.


============================================================= */
}
