////// Actor.java:  InterForm Actors
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Token;
import crc.sgml.Tokens;
import crc.sgml.Text;

import crc.ds.List;
import crc.ds.Table;

import java.util.Enumeration;

/**
 * The representation of an InterForm <em>actor</em>.
 *	This is the parent class for actors that operate inside of
 *	Interforms.  An actor is basically an active SGML element;
 *	indeed, it would be more correct to say that an element is an
 *	especially trivial and passive actor.<p>
 *
 *	Actors may carry both semantic and syntactic information.
 *	Those that are purely syntactic have the tag
 *	<code>-syntax-</code>, and do not have a <code>name</code>
 *	attribute.  */
public class Actor extends Token {

  /** Cache for name attribute. */
  String name = null;

  /** Name attribute, as a string.  The string is cached for speed, so
   *  don't try to change the attribute after calling this function! */
  public String name() {
    if (name == null) {
      name = attrString("name");
    }
    return name;
  }


  /************************************************************************
  ** Matching tokens:
  ************************************************************************/

  /** List of match criteria, as a sequence of attribute, value, etc.
   *	Attribute names are Text; values are Text or List.  A value of
   *	null matches if the attribute is missing; an empty list
   *	matches if it is present.  Text matches the value converted to
   *	lowercase.  A null criteria list matches anything. */
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

      if (v == null) {		// Null v matches if attr is missing.
	if (av == null) return false;
      } else if (v.isList()) { 	// (null) List matches if attr is present
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
  ** Syntactic Flags:
  ************************************************************************/

  /** True if content should be parsed, false if it should be streamed. */
  boolean parseContent = false;

  /** True if the tags should be passed */
  boolean passTags = false;

  /** Tell the default action routine whether to quote the content. */
  int quoteContent = 0;

  /** True if there is no content, i.e. the Actor matches an empty tag. 
   *	=== This should really be done with syntax. */
  boolean noContent = false;

  /** Set of elements inside which this tag is not permitted. */
  Table implicitlyEnds = null;

  /** Return true if this kind of token implicitly ends the given one. */
  public boolean implicitEnd(String tag) {
    return implicitlyEnds != null && tag != null && implicitlyEnds.has(tag);
  }

  /** Set or append to the implicitlyEnds tag table */
  public void implicitlyEnds(Table tt) {
    if (implicitlyEnds == null) implicitlyEnds = new Table();
    implicitlyEnds.append(tt);
  }

  /** List of elements this kind of token must occur inside of, from
   *  the outside in. */
  List implicitlyBegins = null;

  /** List of elements the start tags of which are missing. */
  public List implicitBegin() {
    // ===
    return null;
  }

  /************************************************************************
  ** Syntactic Actions:
  ************************************************************************/

  /** Handler for this actor's actOn method. */
  Handler action = null;

  /** Act on a matching token.  Normally what this does is push the
   *	actor itself as a handler, and set the interpretor to parse
   *	the content.  Some syntactic analysis can also be done here to
   *	determine exactly <em>which</em> handler to push.
   */
  public void actOn(SGML it, Interp ii, byte incomplete, int quoting) {
    if (action != null) {
      action.actOn(this, it, ii, incomplete, quoting);
    } else {
      defaultAction(it, ii, incomplete, quoting);
    }
  }

  /** The purely syntactic action taken if no action handler is
   *  present.  Provided as a separate function for the benefit of the
   *  majority of action handlers that need to combine special
   *  semantics with the standard syntax.  */
  public final void defaultAction(SGML it, Interp ii,
				  byte incomplete, int quoting) {
    if (incomplete <= 0) {
      return;
    } else {
      if (noContent) {
	ii.completeIt();
      } else {
	if (quoting == 0 || quoteContent < 0) ii.setQuoting(quoteContent);
	if (parseContent) ii.setParsing();
      }
      //if (quoting != 0) return;
      if (handler != null || !isEmpty()) ii.addHandler(this);
    }
  }


  /************************************************************************
  ** Semantic Handle:
  ************************************************************************/

  /** Handler for this actor's <code>handle</code> method. */
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
      ii.pushInto(content());

      // === Should save context on input stack, otherwise we lose at the end.

      ii.defvar("element", it);
      ii.defvar("content", it.content());
      String ts = attrString("tagset");
      if (ts != null) { ii.useTagset(ts); }
      if (!passTags) ii.deleteIt();
    }
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Create an actor out of an SGML element (typically an &lt;actor&gt;
   * element) */
  public Actor(Token it) {
    super("-actor-");
    initialize(it, null, null);
  }

  /** Create an actor with the given name, tag, syntax, and handler. */
  public Actor(String name, String tag, String syntax, String handle) {
    super("-actor-");
    if (syntax != null) attr(syntax, Token.empty);
    if (handle != null) attr("handle", handle);

    initialize(null, name, tag);
  }

  /** Create a syntax actor from the given token. */
  public Actor(Token it, String syntax) {
    super("-syntax-");
    if (syntax != null) attr(syntax, Token.empty);
    initialize(it, null, null);
  }

  /** Create a syntax actor with the given tag and syntax. */
  public Actor(String tag, String syntax) {
    super("-syntax-");
    if (syntax != null) attr(syntax, Token.empty);
    initialize(null, null, tag);
  }

  /** Create a syntax actor with the given characteristics. */
  public Actor(String tag, String syntax, Table notInside) {
    super("-syntax-");
    if (syntax != null) attr(syntax, Token.empty);
    implicitlyEnds = notInside;
    initialize(null, null, tag);
  }

  /************************************************************************
  ** Initialization:
  ************************************************************************/

  /** Initialize from a Token (typically an &lt;actor&gt; element)
   *	and/or a name and tag. */
  void initialize(Token it, String name, String tag) {
    //System.err.println("<"+tag+" name="+name+"> defined");
    if (it != null) {
      copyAttrsFrom(it);
      copyContentFrom(it);
      if (name == null) name = it.attrString("name");
      if (tag == null) tag = it.attrString("tag");
    }
    if (name == null && tag != null) {
      name=tag;
      addAttr("name", tag);
    }

    this.name = name;
    if (tag != null) {
      tag = tag.toLowerCase();
      attr("tag", tag);
    }

    /* Initialize the handler, actOn, and match.  Do the handler first
     * in case it wants to do some initialization. */
    initHandle();
    initSyntax(this.tag.equals("-syntax-"));
    initAction();
    initMatch();    
  }

  /** Initialize the <code>handle</code> object. */
  void initHandle() {
    SGML s = attr("handle");
    if (s == null) return;

    String h;
    if (s == Token.empty) h = attrString("name");
    else {
       h = attrString("handle");
       if ("".equals(h)) h = attrString("name"); 
    }
    h = Util.javaName(h);
    handler = Util.loadHandler(h, "crc.interform.handle.");
    if (handler != null) handler.initializeActor(this);
  }

  /** Initialize the <code>action</code> object. */
  void initAction() {
    SGML s = attr("action");
    if (s == null) return;

    String h;
    if (s == Token.empty) h = attrString("name");
    else {
       h = attrString("action");
       if ("".equals(h)) h = attrString("name"); 
    }
    h = Util.javaName(h);
    action = Util.loadHandler(h, "crc.interform.handle.");
  }

  /** Initialize the match <code>criteria</code> list. */
  public void initMatch() {
    String m = attrString("match");
    if (m == null) return;

    List l = Util.split(m);
    criteria = new Tokens();
    for (int i = 0; i < l.nItems(); ++i) {
      String s = l.at(i).toString();
      // === non-null match criteria unimplemented
      criteria.push(new Text(s));
    }
  } 

  /** Initialize the syntax flags. */
  void initSyntax(boolean isSyntax) {
    parseContent = !isSyntax;
    passTags = isSyntax;
    if (hasAttr("quoted")) {
      quoteContent = 1;
    } else if (hasAttr("literal")) {
      quoteContent = -1;
    } else if (hasAttr("empty")) {
      noContent = true;
      parseContent = false;
      quoteContent = 0;
    }
    if (hasAttr("parsed")) {
      parseContent = true;
    } else if (hasAttr("passed")) {
      parseContent = false;
      passTags = true;
    } 
    if (implicitlyEnds != null) return;
    String s = attrString("not-inside");
    if (s != null) {
      Enumeration tags = Util.split(s).elements();
      implicitlyEnds = new Table();
      while (tags.hasMoreElements()) {
	implicitlyEnds.at(tags.nextElement().toString().toLowerCase(),
			  Token.empty);
      }
    }
    
    // ===
  }

}
