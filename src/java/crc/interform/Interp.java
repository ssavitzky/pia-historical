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

  /** Parser/interpretor state stack depth. */
  int	depth;

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
  ** Debugging:
  ************************************************************************/

  /** Debugging flag. */
  boolean debug;

  public final void debug(String s) {
    if (debug) { System.err.print(s); }
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


  /************************************************************************
  ** State stack operations:
  ************************************************************************/

  final State context(int level) {
    State s = state;
    for ( ; level > 0 && s != null; --level) { s = s.next; }
    return s;
  }

  /** Pop the parse state stack.  (We actually leave the final state
   *	on the stack and zap its token, to avoid null-reference
   *	problems.)
   * @return true if we did not pop the last state.  */
  final boolean popState() {
    if (state.next == null) {
      state.token = null;
      return false;
    }
    state = state.next;
    depth--;
    if (debug && depth != state.depth) debug("!!depth mismatch\n");
    return true;
  }

  final void pushState() {
    state = new State(state);
    depth++;
    if (debug && depth != state.depth) debug("!!depth mismatch\n");
  }

  /************************************************************************
  ** Syntax Checking:
  ************************************************************************/

  /** Return true if we are currently nested inside an element with
   *  the given tag. */
  public final boolean insideElement(String tag) {
    for (State s = state; s != null; s = s.next) {
      if (tag.equals(s.tag())) return true;
    } 
    return false;
  }

  /** Return the tag of the Element the current token is nested inside of.
   *	Looks at state.next, not state, because state contains
   *	whatever token we are currently working on. */
  public final String elementTag() {
    return (state.next != null && state.next.token != null) ?
      state.next.token.tag() : null;
  }

  /** Return true if there is an element with the given tag at the
   *  given level in the stack (with zero being the top). */
  public final boolean isElementAt(String tag, int level) {
    State s = context(level);
    return s != null && tag.equals(s.tag());
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
    if (it == Token.empty) return it;
    if (it.isList()) {
      Tokens old = it.content();
      if (old.nItems() == 0) return it;
      Tokens tl = new Tokens();
      for (int i = 0; i < old.nItems(); ++i) {
	tl.append(expandEntities(old.itemAt(i)));
      }
      return tl;
    } else if (it.tag().equals("&")) {
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

  /************************************************************************
  ** The Resolver (interpretor) itself:
  ************************************************************************/

  /** The ``Resolver'' is the InterForm Interpretor's main loop.
   *	If passed a token it processes just that token, otherwise it
   *	pulls incoming tokens or completed subtrees off the input
   *	stack and processes them.  This allows the interpretor to be
   *	used either in ``push'' or in ``pull'' mode.
   */
  public final void resolve(SGML token) {
    SGML it = (token == null)? nextInput() : token;

    /* Loop on incoming tokens.  This and "incomplete" used to be passed 
     *	as function arguments; it's better to use the input stack.
     */
    for ( ; it != null; ) {
      byte incomplete = it.incomplete();

      debug(" ["+(incomplete<0? "/" : incomplete>0? "\\" : "")+it.tag()+"]");
      
      if (incomplete < 0) {
	/* We just got an end tag. 
	 *	Pop the parse stack, marking the token we find there as
	 *	complete if we were actually parsing, otherwise change
	 *	it to an end tag.  (It will still have its attributes.)
	 */
	String tag = it.tag();
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
	  debug("inside ");
	  pushInput(it);
	} else {
	  /* Unmatched end tag.  Discard it. */
	  debug("unmatched\n");
	  it = null;
	}
	if (it != null) {
	  boolean was_parsing = state.parsing;
	  if (! popState()) {
	    debug("Stack empty.\n");
	    return; 		// We're done.
	  }
	  it = state.token;
	  incomplete = (byte)(was_parsing? 0 : -1);
	  if (it != null) it.incomplete(incomplete);
	}
      } else if (state.quoting == 0) {
	/* At this point even empty tokens are marked as start tags,
	 *    in order to make expandAttrs suppress the copy.
	 */
	// === worry about that.  Add checkForSyntax? ===
	it = expandAttrs(it);
	state.token = it;
      }

      if (incomplete > 0) {
	/* Start tag.  Check for interested actors.
	 *	keep track of any that register as handlers.
	 */
	state.handlers = new List();
	pushState();		// state = new State(state);
	state.token=it;

	debug("depth="+depth+" ");
	debug("start ");

	checkForInterest(it, incomplete);

	it = state.token;	// See if any actor has modified the token
	if (it == null) {
	  // Some actor has deleted the token: pop the stack.
	  popState();		// state = state.next;
	  debug("deleted\n");
	} else if (it.incomplete() == 0) {
	  // Some actor has marked it as finished: pop it.
	  debug("completed\n");
	  popState();		// state = state.next;
	  continue;
	} else {
	  // Nothing happened; it stays pushed.
	  //	Clean out the state for the content.
	  debug("pushed\n");
	  state.variables = null;
	  state.handlers = new List();
	  if (state.passing) passToken(it);
	}
      } else if (it != null) {
	/* End tag or complete token. */
	debug("depth="+depth+" ");
	debug(it.incomplete()<0?"end " : "comp ");
	checkForInterest(it, incomplete);
	checkForHandlers(it);

	//it = state.token;
	if (it != null) {
	  debug("passed\n");
	  if (state.parsing) pushToken(it);
	  if (state.passing) passToken(it);
	} else {
	  debug("deleted\n");
	}
      }

      /* Get another token, unless called to resolve a single token.
       *    Do it here rather than in the for loop so that a
       *    "continue" above will keep the token that's there.  
       */
      it = (token == null)? nextInput() : null;
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
    resolve(Token.endTagFor(null));
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

  public Interp() {
    state = new State();
    setParsing();
    output = new Tokens();
  }

  public Interp(Tagset tagset, Table entities, boolean parse) {
    state = new State();
    state.tagset = tagset;
    output = new Tokens();
    this.entities = entities;
    if (parse) setParsing(); else setPassing();
  }

  public Interp(Tagset tagset, Table entities, Input in, Tokens out) {
    state = new State();
    state.tagset = tagset;
    output = (out != null)? out : new Tokens();
    this.entities = entities;
    setPassing();
    if (in != null) pushInput(in);
  }

}

/**
 * Interpretor state (parse/execution stack frame).
 *	State objects don't have many methods; they're really local
 *	to the interpretor.
 */
class State {
  SGML	token;
  List	handlers = new List();
  Table variables;

  boolean passing;
  boolean parsing;
  boolean skipping;
  boolean streaming;
  int quoting;
  int depth;

  boolean hasLocalTagset;
  Tagset tagset;

  /** Tag of the current token */
  final String tag() {
    return (token == null) ? null : token.tag();
  }

  /** Link to previous state */
  State next;

  /** Default constructor */
  State() {
    depth=0;
  }

  /** Construct from a previous state. */
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
    depth	= s.depth+1;
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

