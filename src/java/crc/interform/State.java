////// State.java: the Interform Interpretor state
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
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

  boolean passing;
  boolean parsing;
  boolean skipping;
  boolean streaming;
  int quoting;

  Table variables;
  boolean hasLocalTagset;
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
    skipping 	= s.skipping;
    streaming 	= s.streaming;
    quoting 	= s.quoting;
    hasLocalTagset = s.hasLocalTagset;
    tagset 	= s.tagset;
    variables 	= s.variables;
  }

  /** Copy values from a previous state.  This has the effect of popping. */
  void copyFrom(State s) {
    stack 	= s.stack;
    depth	= s.depth;
    variables 	= s.variables;

    it	 	= s.it;
    handlers 	= s.handlers;
    passing 	= s.passing;
    parsing 	= s.parsing;
    skipping 	= s.skipping;
    streaming 	= s.streaming;
    quoting 	= s.quoting;
    hasLocalTagset = s.hasLocalTagset;
    tagset 	= s.tagset;

    /* null out s's object variables to speed up garbage collection */

    s.it	= null;
    s.tagset	= null;
    s.handlers	= null;
    s.variables = null;
    s.stack	= null;
  }

}


