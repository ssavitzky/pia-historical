////// Interp.java: the Interform Interpretor
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
import crc.interform.Actor;
import crc.interform.Input;
import crc.interform.State;

import crc.sgml.SGML;
import crc.sgml.Element;
import crc.sgml.Text;
import crc.sgml.Token;
import crc.sgml.Tokens;
import crc.sgml.Entity;

import crc.ds.List;
import crc.ds.Table;

import java.io.InputStream;
import java.io.OutputStream;

/**
 *	The Interform Interpretor parses a string or file, evaluating any 
 *	Interform Actors it runs across in the process.  Evaluation is
 *	usually done concurrently with parsing because new tags and
 *	entities can be defined at any time.  However, it is also
 *	possible to execute a saved parse tree.  This is a good thing,
 *	because actors are *stored* as parse trees.
 */
public class Interp extends State {

  /************************************************************************
  ** Components:
  ************************************************************************/

  /** Entity table for this document. */
  Table entities;


  /** Input stack. 
   *	The input stack is a linked list of Input objects.
   */
  Input input;

  /** Output queue. 
   *	The output queue is kept in a Tokens list in order to take advantage
   *	of the automatic merging of strings.
   */
  Tokens output;

  /** If true, convert tokens to strings before putting them on the
   *  output queue.  */
  boolean streaming;

  /** Points to any operating environment that might be needed by Actors.
   *	In particular, points to a Run object when the Interp is running
   *	inside the PIA on behalf of an agent, or an Environment object
   *	when running stand-alone. 
   *
   *	@see crc.interp.Run 
   *	@see crc.interp.Environment
   *	@see crc.interp.Filter*/
  public Environment environment;

  /************************************************************************
  ** Debugging and Messaging:
  ************************************************************************/

  /** Debugging flag. */
  boolean debug = false;

  public final void debug(String s) {
    if (debug) { System.err.print(s); }
  }

  /** Quiet / verbose flag. */
  int verbosity = 0;

  public final boolean quiet() {
    return verbosity < 0;
  }

  public final boolean verbose() {
    return verbosity > 0;
  }

  public final void setQuiet() {
    verbosity = -1;
  }
  public final void setVerbose() {
    verbosity = 1;
  }

  /************************************************************************
  ** Access to global state:
  ************************************************************************/

  public final Tokens output() {
    return output;
  }

  public final Table entities() {
    return entities;
  }

  /** Test the streaming flag.  
   * 	If true, the interpretor converts tokens to strings before
   *	putting them on the output queue.
   */
  public final boolean isStreaming() {
    return streaming;
  }
  public final void setStreaming() {
    streaming= true;
    setPassing();
  }


  /************************************************************************
  ** Access to things in the stack frame:
  ************************************************************************/

  public final SGML token() {
    return it;
  }
  public final void token(Token t) {
    it = t;
  }

  public final List handlers() {
    return handlers;
  }

  public final Table variables() {
    return variables;
  }

  /** Test to see if we can define actors in the current tagset.  If
   *	true, we are using a local copy of a named tagset or we're
   *	inside a &gt;tagset&lt; element, and it's OK to define local
   *	actors in it.  Otherwise, we're in a global tagset that has to
   *	be copied if we want to change it.  */
  public final boolean tagsetUnlocked() {
    return ! tagset.isLocked;
  }

  /** Get the current tagset */
  public final Tagset tagset() {
    return tagset;
  }

  /** Use a tagset.  Make a copy of the current one if tagset is null. */
  public final void useTagset(Tagset t) {
    if (t != null) {
      tagset = t;
    } else {
      tagset = (Tagset)tagset.clone();
    }
  }

  /** Use a named tagset.  Make a copy of the current one if the name
   *	is null. */
  public final void useTagset(String name) {
    // === Ignore documentation for now -- do it with tags.
    if (name != null) {
      tagset = Tagset.tagset(name);
    } else {
      tagset = (Tagset)tagset.clone();
    }
  }

  /** Make a copy of the current tagset if we don't already have one. */
  public final void useTagset() {
    if (tagset.isLocked) {
      tagset = (Tagset)tagset.clone();
    }
  }

  /** Define an actor.  Clone the current tagset if necessary. */
  public final void defineActor(Actor anActor) {
    useTagset();
    tagset.define(anActor);
  }

  /** Revert to the parse status of the containing frame. */
  public final void hoistParseFlags() {
    if (stack != null) {
      parsing = stack.parsing;
      passing = stack.passing;
      quoting = stack.quoting;
    }
  }

  /** Push a token onto the parse stack. */
  public final void stackToken(SGML t) {
    it = t;
    pushState();
  }

  /************************************************************************
  ** Access to Variables (entities):
  ************************************************************************/

  /** Get the current local binding table, if any */
  public final Table getLocalBindings() {
    return variables;
  }

  /** Get the current local binding for a variable, if any */
  public final SGML getLocalBinding(String name) {
    return (variables != null)? (SGML)variables.at(name) : null;
  }

  /** Get the value of a named local variable (entity).
   *	Dynamic scoping is used, with an optional variable (entity) table
   *	in each State stack frame.  Eventually we may switch to shallow 
   *	binding.  Returns null if no local binding is found.
   */
  public final SGML getvar (String name) {
    for (State state = this; state != null; state = state.stack) {
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
  public final void setvar(String name, SGML value) {
    for (State state = this; state != null; state = state.stack) {
      if (state.variables != null && state.variables.has(name)) {
	if (value == null) value = Token.empty;
	state.variables.at(name, value);
	return;
      }
    }
    defvar(name, value);
  }

  /** Define a new local variable (entity) with a given name and value.
   */
  public final void defvar(String name, SGML value) {
    if (variables == null) variables = new Table();
    if (value == null) value = Token.empty;
    variables.at(name, value);
  }

  /** Get the value of a named variable (entity) with path lookup.
   *	Dynamic scoping is used, with an optional variable (entity) table
   *	in each State stack frame.  If no local variable is found, the 
   *	document's global entity table is used.  If the name is a dotted
   *	path, picks it apart and does a series of lookups.
   */
  public final SGML getEntity(String name) {
    // === Multi-level path lookup unimplemented. ===
    for (State state=this; state != null; state = state.stack) {
      if (state.variables != null && state.variables.has(name)) {
	return (SGML)state.variables.at(name);
      }
    }
    return (SGML)entities.at(name);
  }

  /** Get the value of a named global variable (entity).
   */
  public final SGML getGlobal(String name) {
    return (SGML)entities.at(name);
  }

  /** Set the value of a named global variable (entity).
   */
  public final void setGlobal(String name, SGML value) {
    entities.at(name, value);
  }

  /** Get the value of a named attribute.  Looks up the context tree until
   *	it finds one, or if tag is specified, an element with that tag.
   */
  public final SGML getAttr(String name, String tag) {
    for (State state=stack; state != null; state = state.stack) {
      if (state.it != null && ((tag == null && state.it.hasAttr(name)) 
			       || tag.equals(state.it.tag()))) {
	return state.it.attr(name);
      }
    }
    return null;
  }

  /** Set the value of a named attribute.  If a tag is specified, looks up 
   *	the context tree for an element with that tag.
   */
  public final void setAttr(String name, SGML value, String tag) {
    for (State state=stack; state != null; state = state.stack) {
      if (state.it != null && (tag == null || tag.equals(state.it.tag()))) {
	try {
	  ((Element)state.it).attr(name, value);
	} catch (Exception e) {}
	return;
      }
    }
  }

  /************************************************************************
  ** Expanding entities:
  ************************************************************************/

  /** Expand entities (according to the current bindings) in some SGML.
   *	The object is copied.  We really only have to worry about Tokens
   *	(token lists) and Token elements with a tag of ampersand. <p>
   *
   *	We don't have to expand elements' attributes or contents, because
   *	the interpretor will get around to them eventually.
   */
  public SGML expandEntities(SGML it) {
    if (it == null || it == Token.empty) return it;
    if (it.isList()) {
      Tokens old = it.content();
      if (old == null || old.nItems() == 0) return it;
      Tokens tl = new Tokens();
      for (int i = 0; i < old.nItems(); ++i) {
	tl.append(expandEntities(old.itemAt(i)));
      }
      return tl;
    } else if (it instanceof Entity) {
      debug("looking up &"+((Entity)it).entityName() + "; ");
      SGML v = getEntity(((Entity)it).entityName());
      return (v == null)? it : v.isText()? new Text(v) : v;
    } else if (it.isText()) {
      return new Text(it);
    } else {
      return it;
    }
  }


  /** Expand entities in text, lists, or start tags.  Complete
   *	elements have their attributes expanded and their contents
   *	copied. Other SGML (basically end tags and text) is passed
   *	through unchanged.  Start tags are expanded in place; others
   *	are copied.
   */
  SGML expandAttrs(SGML it) {
    if (it.isElement()) {
      if (it.incomplete() > 0) {
	Element t = Element.valueOf(it); // should be a no-op.
	for (int i = 0; i < t.nAttrs(); ++i) 
	  t.attrValueAt(i, expandEntities(t.attrValueAt(i)));
	it = t;			// but just to make sure...
      } else if (it.incomplete() == 0) {
	Element t = new Element(it.tag());
	t.content(it.content());
	Element itt = (Element)it;
	for (int i = 0; i < itt.nAttrs(); ++i) 
	  t.addAttr(itt.attrNameAt(i), expandEntities(itt.attrValueAt(i)));
	it = t;			// but just to make sure...
      } else {
	it = expandEntities(it);
      }
    } else {
      it = expandEntities(it);
    }
    return it;
  }

  /** Expand entities in some SGML according to a given table.
   *	The object is copied recursively.
   */
  public static SGML expandEntities(SGML it, Table tbl) {
    if (it == null || it == Token.empty) return it;
    if (it.isList()) {
      Tokens old = it.content();
      if (old.nItems() == 0) return it;
      Tokens tl = new Tokens();
      for (int i = 0; i < old.nItems(); ++i) {
	tl.addItem(expandEntities(old.itemAt(i), tbl));
      }
      return tl;
    } else if (it instanceof Entity) {
      SGML v = (SGML) tbl.at(((Entity)it).entityName());
      return (v == null)? it : v.isText()? new Text(v) : v;
    } else if (it.isElement()) {
      Element itt = (Element)it;
      Element t = new Element(it.tag());
      for (int i = 0; i < itt.nAttrs(); ++i) 
	t.attrValueAt(i, expandEntities(itt.attrValueAt(i), tbl));
      t.content(expandEntities(itt.content(), tbl));
      return t;
    } else if (it.isText()) {
      return new Text(it);
    } else {
      return it;
    }
  }

  private static Table defaultEntityTable = null;

  /** Default entity table: defined HTML entities. */
  public static Table defaultEntities() {
    if (defaultEntityTable == null) {
      defaultEntityTable = new Table();
      defaultEntityTable.at("amp", new Text("&"));
      defaultEntityTable.at("gt", new Text(">"));
      defaultEntityTable.at("lt", new Text("<"));
      // === should also have #nn == 
    }
    return defaultEntityTable;
  }

  /************************************************************************
  ** Getting and Pushing Input:
  ************************************************************************/

  public void pushInput(SGML t) {
    if (t.isList()) {
      input = new InputList(t.content(), input);
    } else {
      input = new InputToken(t, input);
    }
  }

  public void pushInput(Input in) {
    in.prev = input;
    in.interp(this);
    input = in;
  }

  public void pushInto(SGML t) {
    if (t.isText()) {
      debug("Expanding \""+t.toString().length()+"\"");
      input = new InputToken(t.toText(), input);
    } else if (t.isElement()) {
      debug("Expanding <"+t.tag()+"> ");
      input = new InputExpand(t, input);
    } else {
      debug("Expanding ["+t.content().nItems()+"]");
      input = new InputList(t.content(), input);
    }
  }

  public void pushInto(Tokens t) {
    debug("Expanding ["+t.nItems()+"]");
    input = new InputList(t.content(), input);
  }

  /** Repeatedly expand content, with the given entity bound to each
   *	element of list. */
  public void pushForeach(Tokens content, String entity, Tokens list) {
    debug("Iterating ["+content.nItems()+"]*["+list.nItems()+"]");
    input = new InputForeach(content, entity, list, input);
    input.interp(this);
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

  /************************************************************************
  ** The Resolver (interpretor) itself:
  ************************************************************************/

  /** The ``Resolver'' is the InterForm Interpretor's main loop.
   *	If passed a token it processes just that token, otherwise it
   *	pulls incoming tokens or completed subtrees off the input
   *	stack and processes them.  This allows the interpretor to be
   *	used either in ``push'' or in ``pull'' mode.<p>
   *
   * Debugging output: "[" + flag + tag + "]" + actions + EOL.  The
   *	flag is "/" for end tags, "\" for start tags, and "|" for
   *	completed elements.  Actions include <em>name</em>? for every
   *	``interested'' actor, <em>name</em>: for every actor pushed
   *	as a handler, and <em>name</em>! for every handler called.<p>
   */
  public final void resolve(SGML token) {
    Actor syntax;

    /* Get the next incoming token and make it current. */
    it = (token == null)? nextInput() : token;

    /* Loop on incoming tokens. */
    for ( ; it != null; it = nextInput()) {
      byte incomplete = it.incomplete();
      String tag = it.tag();

      debug(" ["+(incomplete<0? "/": incomplete==0? "|": "\\")+it.tag()+"] ");
      
      /* At this point, it is the new incoming token, and stack.token
       * is whatever it is nested inside of (the current element under
       * construction).  */

      /* check for an implicit end tag.
       *   Save the syntax to make checkForInterest more efficient. 
       */
      syntax = tagset().forTag(tag);
      if (incomplete > 0 && syntax != null &&
	  syntax.implicitEnd(elementTag())) {
	/* We have an implicit end. */
	pushInput(it);
	tag = elementTag();
	syntax = tagset().forTag(tag);
	incomplete = -1;
      }
      if (incomplete < 0) {
	/* We just got an end tag. 
	 *	Note that at this point, state.token is top-of-stack.
	 *	Pop the parse stack, marking the token we find there as
	 *	complete if we were actually parsing, otherwise change
	 *	it to an end tag.  (It will still have its attributes.)
	 */
	if (tag == null) {
	  /* End of file -- end whatever's open */
	  // === not clear what to do here.
	  debug("eof ");
	  if (depth > 0) pushInput(it);
	} else if (tag.equals("") || tag.equals(elementTag())) {
	  /* just end the current element */
	  debug("current ");
	} else if (insideElement(tag)) {
	  /* End the current element, but keep the tag */
	  debug("inside "+elementTag()+" (faked) ");
	  pushInput(it);
	} else {
	  /* Unmatched end tag.  Discard it. */
	  debug("unmatched in "+elementTag()+" (discarded) ");
	  it = null;
	}
	if (it != null) {
	  popState();
	  if (it != null) {
	    incomplete = (byte)(parsing? 0 : -1);
	    it.incomplete(incomplete);
	    tag = it.tag();
	    syntax = tagset().forTag(tag);
	  } else {
	    debug("popped null\n");
	  }
	}
      } else if (quoting == 0) {
	/* At this point even empty tokens are marked as start tags,
	 *    in order to make expandAttrs suppress the copy.
	 */
	// === worry about that.  Add checkForSyntax? ===
	// === should only expand start tags and entities.
	// === what happens if an entity expands into a list at this point?
	if (incomplete > 0 || ! it.isElement()) {
	  it = expandAttrs(it);
	  debug("expanded ");
	} else if (incomplete == 0) {
	  input = new InputExpand(it, input);
	  continue;
	}
      }

      if (it == null) {
	debug("deleted\n");
      } else if (incomplete > 0) {
	/* Start tag.  Check for interested actors.
	 *	keep track of any that register as handlers.
	 */
	debug("start depth="+depth+" ");
	handlers = null;

	/* Push the stack.  
	 *	This is awkward: if the token turns out to be empty,
	 *	we will only have to pop it again.  On the other hand,
	 *	all the parsing flags have to be pushed before the
	 *	syntactic action routines operate on them. */
	pushState();
	checkForInterest(it, incomplete, syntax);

	if (it == null) {	// Some actor has deleted the token.
	  popState();
	  it = null;
	} else if (it.incomplete() == 0) { // It's been marked as complete.
	  stack.handlers = handlers;
	  popState();
	  debug("completed in "+elementTag()+"\n");
	  // === the following fails for  nested actors.
	  //	 something may be getting out of sync.
	  if (!isQuoting()) checkForHandlers(it);
	  //continue;
	} else {		// Nothing happened; push it.
	  debug("pushed in "+elementTag()+"\n");
	  //pushState();
	  // === some trouble if actor has passTags = false
	  stack.handlers = handlers;
	  handlers = null;
	  if (passing) { passToken(it); debug("passed\n"); }
	  it = null;		// Skip the handlers and pushToken
	}
      } else {
	/* End tag or complete token. */
	debug("depth="+depth+" ");
	debug(it.incomplete()<0?"end " : "comp ");

	checkForInterest(it, incomplete, syntax);
	if (!isQuoting()) checkForHandlers(it);
      }

      if (it != null) {
	if (parsing) { 
	  debug("appended to "+elementTag()+"\n");
	  pushToken(it);
	}
	if (passing) { passToken(it); debug("passed\n"); }
      } else {
	debug("deleted\n");
      }

      /* Get another token, unless called to resolve a single token. */
      if (token != null) return;
    }

    if (token == null) {
      debug("No more input\n");
      /* There is no more input */
    }
  }

  /************************************************************************
  ** Checking for actors:
  ************************************************************************/

  /** Check for any actors interested in this token, and run their 
   *	actOn method.  Possibly should go into Tagset.  Syntax should
   *	perhaps be different.
   */
  final void checkForInterest(SGML it, byte incomplete, Actor syntax) {
    Tagset ts 	  = tagset();
    Actor a 	  = (syntax != null)? syntax : ts.forTag(it.tag());

    /* Find the actor interested in this tag, if any */
    if (a != null) {
      debug(" " + a.name() + "?");
      a.actOn(it, this, incomplete, quoting);
    } 

    /* now find anything that matches the token */
    int nMatching  = ts.nMatching();
    for (int i = 0; i < nMatching; ++i) {
      a = ts.matchingAt(i);
      if (a.matches(it, this, incomplete, quoting)) {
	a.actOn(it, this, incomplete, quoting);
      }
    }
  }

  /** Run the handle method of any actor that has registered its interest. */
  final void checkForHandlers(SGML it) {
    if (handlers == null) return;
    Actor a;
    while ((a = (Actor)handlers.pop()) != null) {
      debug(" " + a.name() + "!");
      a.handle(it, this);
    }
  }


  /************************************************************************
  ** Processing:
  ************************************************************************/

  /** Run the interpretor until it completes.  Return the output.
   */
  public Tokens run() {
    resolve(null);
    flush();
    return output;
  }

  /** Flush the interpretor's input queue, running it to completion.
   */
  public void flush() {
    resolve(Element.endTagFor(null));
  }


  /************************************************************************
  ** Output:
  ************************************************************************/

  /** Pass a token or tree to the output. */
  final void passToken(SGML it) {
    if (output == null) return;
    if (! streaming) {		// Not streaming: just pass the tree
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
  final void pushToken(SGML it) {
    if (it == null) return;
    if (stack == null || stack.it == null) {
      passToken(it);
    } else {
      stack.it.append(it);
    }
  }

  /************************************************************************
  ** Routines called by Actors:
  ************************************************************************/

  /** Add an actor as a handler for the current token. */
  public final void addHandler(Actor a) {
    if (handlers == null) handlers = new List();
    handlers.push(a);
    debug(a.name()+":");
  }

  /** Mark the current token as completed. */
  public final void completeIt() {
    it.incomplete((byte)0);
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

  /** Replace the current token with another. */
  public final void replaceIt(SGML t) {
    it = t;
  }

  /** Replace the current token with a String. */
  public final void replaceIt(String s) {
    it = (s == null)? null : new Text(s);
  }

  /** Delete the current token. */
  public final void deleteIt() {
    it = null;
  }
    

  /************************************************************************
  ** Constructors:
  ************************************************************************/

  public Interp() {
    super();
    setParsing();
    output = new Tokens();
  }

  /** Create an Interp with the given tagset and entity table.  If
   *	<code>parse</parse> is true, set the output to a new Tokens
   *	list, otherwise setPassing and assume that the caller will set
   *	the output to something appropriate.  The intended usage is:
   *	<code> new Interp(ts, et, parse).fromFile(fn).toStream(os)
   *	</code> or something similar. */
  public Interp(Tagset tagset, Table entities, boolean parse) {
    super();
    this.tagset = tagset;
    this.entities = entities;
    if (parse) {
      setParsing(); 
      output = new Tokens();
    } else setPassing();
  }

  /** Create an Interp with the given tagset <em>name</em>, entity
   *  table, and parsing flag.  */
  public Interp(String tagsetName, Table entities, boolean parse) {
    this(Tagset.tagset(tagsetName), entities, parse);
  }


  /************************************************************************
  ** Setting input and output:
  ************************************************************************/

  /** Take input from any input source, typically a Parser. */
  public Interp from(Input p) {
    pushInput(p);
    p.interp(this);
    return this;
  }

  /** Take input from an InputStream. */
  public Interp fromStream(InputStream in) {
    return from(new Parser(in, null));
  }

  /** Take input from a file. */
  public Interp fromFile(String filename) {
    InputStream in = null;
    try {
      if (filename != null) in = new java.io.FileInputStream(filename);
      return fromStream(in);
    } catch (Exception e) {
      System.err.println("Cannot open input file " + filename);
      return this;
    }
  }
  
  /** Take input from a string. */
  public Interp fromString(String input) {
    return fromStream(new java.io.StringBufferInputStream(input));
  }
  

  /** Send output to a stream. */
  public Interp toStream(OutputStream out) {
    output = new TokenStream(out);
    setPassing();
    return this;
  }
  
  /** Send output to a file. */
  public Interp toFile(String filename) {
    OutputStream out = null;
    try {
      if (filename != null) out = new java.io.FileOutputStream(filename);
      return toStream(out);
    } catch (Exception e) {
      System.err.println("Cannot open input file " + filename);
      return this;
    }
  }

  /** Collect output in a Tokens list.  Simplify the result if
   *	necessary using <code>run().simplify()</code>. */
  public Interp toTokens() {
    output = new Tokens();
    return this;
  }

  /** Collect output in a Text. Use <code>run().toText()</code> to get
   *	the resulting output as a Text; <code>run().toString()</code>
   *	for a String. */
  public Interp toText() {
    output = new Tokens();
    setStreaming();
    return this;
  }

  /************************************************************************
  ** Error Messages:
  ************************************************************************/

  /** Display a message to the user. */
  public final void message(String message) {
    if (debug) message = "\n" + message;
    if (debug || ! quiet()) System.err.println(message);
  }

  /** Generate an error message and display it to the user.  We need to know 
   *	which actor generated the message; the token itself is in 
   *	<code>it</code> in case we decide to display it.<p>
   *
   *	=== Need to go through environment, which may throw an exception
   */
  public final void error(Actor ia, String message) {
    String msg = errheader(ia) + message;
    /*if (debug || ! quiet())*/ System.err.println(msg); // === not if quiet?
  }

  /** Generate an "unimplemented" error message for the given actor.
   *	This is especially convenient when implementing new primitive
   *	actors; handle/Makefile reports all unimplemented actors.  */
  public final void unimplemented(Actor ia) {
    error(ia, "actor unimplemented");
  }

  /** Test a string value for null and return an ``attribute missing'' 
   *	error message.  Either null or "" is an error.  */
  public final boolean missing(Actor ia, String name, String value) {
    if (value == null || "".equals(value)) {
      error(ia, name + " attribute missing or null");
      return true;
    } else {
      return false;
    }
  }

  /** Return an error-message header for the given Actor. */
  public final String errheader(Actor ia) {
    String s = "Interform Error: ";
    if (debug) s = "\n"+s;
    String tag = (ia == null)? null : ia.attrString("tag");
    String name = (ia == null)? null : ia.name();

    if (tag != null) s += "<" + tag + "> ";
    else if (name != null) s += "<... " + name + "> ";
    return s;
  }
}



/************************************************************************
*************************************************************************
** Auxiliary Classes:
*************************************************************************
************************************************************************/


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
  int 		item;
  int		limit;

  public SGML nextInput() {
    return (item < limit)? it.itemAt(item++) : null;
  }
  public boolean endInput() {
    return item >= limit;
  }

  public InputList(Tokens t, Input p) {
    super(p);
    it    = t;
    item  = 0;
    limit = it.nItems();
  }
}

/**
 * Input stack frame for a complete element
 */
class InputExpand extends Input {
  Element 	it;
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
  public boolean endInput() {
    return item > limit;
  }

  public InputExpand(SGML t, Input p) {
    super(p);
    it = Element.valueOf(t);
    item  = -1;
    limit = it.nItems();
  }
}

/**
 * Input stack frame for a list being expanded for each element in another list.
 */
class InputForeach extends InputList {
  Tokens list;
  int	 listItem;
  int	 listSize;
  String entity;

  public SGML nextInput() {
    if (item < limit) return it.itemAt(item++);
    if (listItem < listSize) {
      interp.setvar(entity, list.itemAt(listItem++));
      item = 0;
      return it.itemAt(item++);
    } else {
      // === replace saved entity binding if any
    }
    return null;
  }
  public boolean endInput() {
    return item >= limit && listItem >= listSize;
  }

  public InputForeach(Tokens t, String ent, Tokens l, Input p) {
    super(t, p);
    entity = ent;
    list = l;
    listSize = list.nItems();
    listItem = 0;
  }

  /** Interpretor.  We need this in order to bind the iteration
   *	variable.  There is some flakiness here; if we have two nested
   *	repeats they will end up using the same variable table.
   *
   *	=== In this case we should really save the current value of
   *	the entity and replace it when we exit.  Should really be done
   *	on pop, but there's no method for it.
   */   
  Interp interp;

  /** Tell the Input what interpretor it is working for. */
  public void interp(Interp ii) {
    interp = ii;
    if (listItem < listSize) 
       interp.defvar(entity, list.itemAt(listItem++));
  }
}

