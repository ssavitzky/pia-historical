////// State.java: the Interform Interpretor state
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;

import crc.sgml.SGML;

import crc.ds.Table;
import crc.ds.List;

/**
 * Interpretor state (parse/execution stack frame).
 *	State objects don't have many methods; they're really local
 *	to the interpretor.  They are kept in a linked list.<p>
 *
 * Note:
 *	It would be possible to maintain a list of "free" States.
 */
class State {
  /** The current token. */
  SGML it;

  /** The handlers associated with the current token. */
  List handlers;

  /** Parser/interpretor state stack link. */
  State stack;

  /** Parser/interpretor state stack depth. */
  int	depth;

  /** Interpretor control: true if tokens are being passed to the output. */
  boolean passing;

  /** Interpretor control: true if tokens are being appended to a 
   *	parse tree under construction. */
  boolean parsing;

  /** Interpretor control: 0 if actors are being invoked, 1 if tokens are
   *	being quoted, -1 if tokens are being treated as literal text. */
  int quoting;

  Table variables;
  Tagset tagset;

  /** Tag of the current token */
  final String tag() {
    return (it == null) ? null : it.tag();
  }


  /** Default constructor */
  State() {
    depth=0;
  }

  /** Construct from a previous state. */
  State(State s) {
    stack 	= s.stack;
    depth	= s.depth;

    it	 	= s.it;
    handlers 	= s.handlers;
    passing 	= s.passing;
    parsing 	= s.parsing;
    quoting 	= s.quoting;
    tagset 	= s.tagset;
    variables 	= s.variables;
  }

  /** Copy values from a previous state.  
   *	This has the effect of popping the stack. */
  final void copyFrom(State s) {
    stack 	= s.stack;
    depth	= s.depth;
    variables 	= s.variables;

    it	 	= s.it;
    handlers 	= s.handlers;
    passing 	= s.passing;
    parsing 	= s.parsing;
    quoting 	= s.quoting;
    tagset 	= s.tagset;

    /* null out s's object variables to speed up garbage collection */

    s.it	= null;
    s.tagset	= null;
    s.handlers	= null;
    s.variables = null;
    s.stack	= null;
  }

  /************************************************************************
  ** Setting and testing syntax flags:
  ************************************************************************/

  public final boolean isPassing() {
    return passing;
  }
  public final void setPassing() {
    passing = true;
    parsing = false;
  }

  public final boolean isParsing() {
    return parsing;
  }
  public final void setParsing() {
    parsing = true;
    passing = false;
  }

  /** True if tokens should not be passed to the output at all.  This
   *	is a ``virtual'' flag, computed from <code>parsing</code> and
   *	<code>passing</code> */
  public final boolean isSkipping() {
    return !parsing && !passing;
  }
  public final void setSkipping() {
    parsing = false;
    passing = false;
  }

  /** Test the quoting flag.
   *	If non-zero, no processing is done on incoming tokens.
   *	If negative, the current tag contains unparsed character data.
   */
  public final boolean isQuoting() {
    return quoting != 0;
  }
  public final boolean isUnparsed() {
    return quoting < 0;
  }
  public final void setQuoting(int i) {
    quoting = i;
  }

  /************************************************************************
  ** State stack operations:
  ************************************************************************/

  /** Pop the parse state stack.
   * @return true if the stack was already null.  */
  final boolean popState() {
    if (stack == null) {
      it = null;
      return false;
    }
    copyFrom(stack);
    return true;
  }

  final void pushState() {
    stack = new State(this);
    depth++;
  }

  final State context(int level) {
    State s = this;
    for ( ; level > 0 && s != null; --level) { s = s.stack; }
    return s;
  }

  /************************************************************************
  ** Syntax Checking:
  ************************************************************************/

  /** Return the innermost enclosing element with the given tag. */
  public final SGML enclosingElement(String tag) {
    for (State s = stack; s != null; s = s.stack) {
      if (tag.equals(s.tag())) return s.it;
    } 
    return null;
  }

  /** Return true if we are currently nested inside an element with
   *  the given tag. */
  public final boolean insideElement(String tag) {
    for (State s = stack; s != null; s = s.stack) {
      if (tag.equals(s.tag())) return true;
    } 
    return false;
  }

  /** Return the tag of the immediately-surrounding Element. */
  public final String elementTag() {
    return (stack != null) ? stack.tag() : null;
  }

  /** Return true if there is an element with the given tag at the
   *  given level in the stack (with zero being the top). */
  public final boolean isElementAt(String tag, int level) {
    State s = context(level);
    return s != null && tag.equals(s.tag());
  }

}


