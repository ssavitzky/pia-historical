////// Handler.java:  Handles for InterForm Actors
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
import crc.interform.Actor;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.ds.Table;

/** Handler functor (<b>Strategy</b> pattern) for Actors.  Handlers
 *	can be used to reify either the <code>handle</code> method or
 *	the <code>actOn</code> method; it is possible but very unusual
 *	for both methods to be overridden in the same Handler
 *	subclass.
 */
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
