////// Tagset.java: Sets of Tags (Interform Actors)
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Element;

import crc.ds.Table;
import crc.ds.List;

import java.util.Enumeration;

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
public class Tagset extends Element {

  /************************************************************************
  ** Global tagset table:
  ************************************************************************/

  /** Table of all globally-defined tagsets, by name. */
  static Table tagsets = new Table();

  /** Return a Tagset with a given name.
   *	If it doesn't exist or the name is null, a new Tagset is created.
   */
  public static Tagset tagset(String name) {
    if (name == null) return new Tagset();
    Tagset t = (Tagset)tagsets.at(name);
    if (t == null) { 
      t = loadTagset(name);
      if (t != null) tagsets.at(name, t);
    }
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
  public Table actors = new Table();

  /** Table of syntax (nameless) actors by tag. */
  public Table syntax = new Table();

  /** Table of local entity definitions. */
  public Table entities = new Table();

  /** List of matching (tagless) actors. */
  public List matching = new List();

  /** Table of active and syntax actors, by tag. */
  public Table tagged = new Table();

  /** Names of all actors. */
  public List actorNames() {
    return actors.keyList();
  }

  /** True if the tagset is locked, so that new actors cannot be
   *	defined in it. */
  boolean isLocked = false;

  /** Lock the tagset. */
  public void lock() {
    isLocked = true;
  }

  /** Cache for name attribute. */
  String name = null;

  /** Name attribute, as a string.  The string is cached for speed, so
   *  don't try to change the attribute after calling this function! */
  public String name() {
    if (name == null) {
      SGML s = attr("name");
      name = s.toString();
    }
    return name;
  }


  /************************************************************************
  ** Actors:
  ************************************************************************/

  /** Return the actor for the given tag. */
  public Actor forTag(String t) {
    return (Actor)tagged.at(t);
  }

  /** Return the actor with a given name. */
  public Actor forName(String t) {
    return (Actor)actors.at(t);
  }

  /** Return a matching (tagless) actor. */
  public Actor matchingAt(int i) {
    return (Actor)matching.at(i);
  }

  /** Return the number of matching actors. */
  public int nMatching() {
    return matching.nItems();
  }

  /** Add an actor to the tagset. */
  public void define(Actor a) {
    
    String name = a.name();
    String tag  = a.attrString("tag");
    String type = a.tag();

    if (name == null) {
      syntax.at(tag, a);
    } else {
      actors.at(name, a);
    }
    if (tag == null) {
      matching.push(a);
    } else {
      tagged.at(tag, a);
    }

  }	


  /************************************************************************
  ** Construction:
  ************************************************************************/

  public Tagset() {
  }

  protected Tagset(String name) {
    this.addAttr("name", name);
    bootstrap();
  }

  protected Tagset(Tagset t) {
    super((Element)t);
    actors = new Table(t.actors);
    matching = new List(t.matching);
    entities = new Table(t.entities);
    tagged = new Table(t.tagged);
    syntax = new Table(t.syntax);
    isLocked=false;
    attr("name", t.name()+" clone");
  }

  public Object clone() {
    return new Tagset(this);
  }

  public void include(Tagset t) {
    actors.append(t.actors);
    matching.append(t.matching);
    entities.append(t.entities);
    tagged.append(t.tagged);
    syntax.append(t.syntax);
  }

  /************************************************************************
  ** Bootstrapping:
  ************************************************************************/

  /** Bootstrap-load a new Tagset object.  Start by attempting to load
   *   a handler object.  If that fails, fire up a suitable initial
   *   set of actors.  */
  protected void bootstrap() {
    define(crc.interform.handle.Actor.bootstrap());
    define(crc.interform.handle.Element.bootstrap());
    define(crc.interform.handle.Tagset.bootstrap());
  }

  /** Load a Tagset subclasss.  Note that because newInstance takes no
   *	arguments, the subclass's default constructor has to assign
   *	the correct name.  The name will be something like
   *	"crc.interform.handle.foo_ts". */
  protected static Tagset loadTagsetSubclass(String name) {
    try {
      name = Util.javaName(name);
      Class c = Util.loadClass(name, "crc.interform.handle.");
      return (c != null)? (Tagset)c.newInstance() : null;
    } catch (Exception e) { 
      return null;
    }
  }

  /** Load a Tagset from a file. */
  protected static Tagset loadTagsetFile(String name) {
    return null;		// ===
  }

  /** Load a named Tagset.  First tries to load a file with a ".ts"
   *	extension.  If that fails, tries to load a class, which had
   *	better be a subclass of Tagset, and create an instance of it
   *	(which had better have the right name). */
  protected static Tagset loadTagset(String name) {
    name += ".ts";
    Tagset ts = loadTagsetFile(name);
    return ts != null? ts : loadTagsetSubclass(name);
  }

  /** Define a set of empty syntax tags */
  protected void defEmpty(String emptyTags) {
    Enumeration e = Util.split(emptyTags).elements();
    while (e.hasMoreElements()) {
      String tag = e.nextElement().toString();
      define(new Actor(tag, "empty"));
    }
  }

  /** Define a set of Actors with the given syntax. */
  protected void defActors(String tags, String syntax, boolean handled) {
    Enumeration e = Util.split(tags).elements();
    while (e.hasMoreElements()) {
      String tag = e.nextElement().toString();
      define(new Actor(tag, tag, syntax,
		       handled? Util.javaName(tag) : null));
    }
  }

  /** Define a set of syntax tags with a specified implicitlyEnds table.
   *	If the tags are already defined (e.g. they are actors or empty),
   *	simply append to the implicitlyEnds table. */
  protected void defTags(String tags, String notIn) {
    Enumeration e = Util.split(tags).elements();
    Table t = Util.tagTable(notIn);
    while (e.hasMoreElements()) {
      String tag = e.nextElement().toString();
      Actor a = forTag(tag);
      if (a != null) {
	a.implicitlyEnds(t);
      } else {
	define(new Actor(tag, null, t));
      }
    }
  }


}
