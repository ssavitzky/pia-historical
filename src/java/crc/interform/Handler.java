////// Handler.java:  Handles for InterForm Actors
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
import crc.interform.Actor;
import crc.interform.Interp;

import crc.sgml.SGML;
import crc.ds.Table;

/** Handler functor (<b>Strategy</b> pattern) for Actors.  Handlers
 *	can be used to reify either the <code>handle</code> method or
 *	the <code>actOn</code> method; it is possible but unusual for
 *	both methods to be overridden in the same Handler subclass.  */
public class Handler {

  /** Perform the actions associated with a completed token. 
   *	The parse stack will have been popped (or never pushed in the first
   *	place), and the completed token will be in <code>ii.token()</code> 
   *	as well as being passed as <code>it</code>. */
  public void handle(Actor ia, SGML it, Interp ii) {
  }

  /** Act on a matching token.  Normally what this does is push the
   *	actor itself as a handler.  Some syntactic analysis can also
   *	be done here to determine exactly <em>which</em> handler to
   *	push. */
  public void actOn(Actor ia, SGML it, Interp ii, byte inc, int quot) {
  }

  /** Initialize a new Actor object. */
  public void initializeActor(Actor ia) {
  }

  /** Initialize a new Object.  This is used for initializing
   *   something other than an Actor, e.g. a Tagset. */
  public void initializeObject(Object o) {
  }

  /************************************************************************
  ** Dispatching:
  ************************************************************************/

  /** Pass control to the handle method of another Actor.  Generates
   *	an error message if the actor is not implemented in the
   *	current tagset.  Looking up the handler in the tagset makes it
   *	possible to use tagsets as a security mechanism.  */
  public void dispatch(String name, Actor ia, SGML it, Interp ii) {
    Actor a = ii.tagset().forName(name);
    if (a == null) {
      ii.error(ia, "dispatch to unimplemented actor "+name);
    } else {
      a.handle(it, ii);
    }
  }

  /** Push another actor as the handler.  Generates an error message
   *	if the actor is not implemented in the current tagset.
   *	Looking up the handler in the tagset makes it possible to use
   *	tagsets as a security mechanism.  */
  public void actFor(String name, Actor ia, SGML it, Interp ii,
		     byte incomplete, int quoting) {
    Actor a = ii.tagset().forName(name);
    if (a == null) {
      ii.error(ia, "dispatch to unimplemented actor "+name);
    } else {
      a.actOn(it, ii, incomplete, quoting);
    }
  }


  /************************************************************************
  ** Documentation:
  ************************************************************************/

  /** A description in regular-expression notation of the syntax 
   *	of an element that invokes the handled Actor. */
  public String syntax() {
    return null;
  }

  /** A brief narrative description of the semantics of the handled Actor. */
  public String dscr() {
    return null;
  } 

  /** Any note associated with the handled Actor. */
  public String note() {
    return null;
  }

  /** Any additional documentation text associated with the handled Actor. */
  public String doc() {
    return null;
  }

  /************************************************************************
  ** Utilities:
  ************************************************************************/

  public String getName(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);
    if (name == null || "".equals(name)) {
      ii.error(ia, "name attribute required");
      return null;
    }
    return name;
  }

  /************************************************************************
  ** Global handler table:
  ************************************************************************/

  /** Table of all globally-defined tagsets, by name. */
  static Table handlers = new Table();

  /** Return the handler with a given name.
   */
  public static Handler handler(String name) {
    Handler t = (Handler)handlers.at(name);
    return t;
  }

  /** register a named handler.  Happens on creation. */
  void register(String name) {
    handlers.at(name, this);
  }

  /** The handler's name. */
  public String name;

  /** Create an anonymous handler */
  public Handler() {
    name = null;
  }

  /** Create a handler and register its name. */
  public Handler(String name) {
    this.name = name;
    register(name);
  }
}
