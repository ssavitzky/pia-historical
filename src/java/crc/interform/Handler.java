////// Handler.java:  Handles for InterForm Actors
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
import crc.interform.Actor;
import crc.interform.Interp;

import crc.sgml.SGML;
import crc.ds.Table;
import crc.dps.active.ActiveNode;
import crc.dps.active.ActiveAttrList;
import crc.dps.handle.LegacyHandler;

/** Handler functor (<b>Strategy</b> pattern) for Actors.  Handlers
 *	can be used to reify either the <code>handle</code> method or
 *	the <code>actOn</code> method; it is possible but unusual for
 *	both methods to be overridden in the same Handler subclass.  */
public class Handler implements java.io.Serializable {

  // === Handler should perhaps implement Registered ===

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
  ** Legacy action in the new DPS:
  ************************************************************************/

  public boolean noticeGiven = false;

  /** getActionForNode: override to perform parse-time dispatching */
  public crc.dps.Action getActionForNode(crc.dps.active.ActiveNode n,
					 crc.dps.handle.LegacyHandler h) {
    crc.dps.active.ActiveElement e = n.asElement();
    //if (h.dispatch(e, "")) return h.wrap(new _());
    return h;
  }

  /** Notify the user about a problem involving the legacy tagset. 
   *	Such notice is only given once per tag.
   */
  protected void notify(crc.dps.Context aContext, String tag,
			crc.dps.active.ActiveAttrList atts, String message) {
    if (!noticeGiven) {
      aContext.message(0, message + " in <" + tag
		       + ((atts!=null && atts.getLength()>0)? " " + atts : "")
		       + ">", 0, true);
      noticeGiven = true;
    }
  }

  /** Obtain the current InterFormContext.
   *	This is the DPS equivalent of <code>Run.environment(ii)</code>;
   *	it has the advantage of not dragging in the entire PIA if we're
   *	running in a standalone document processor.
   */
  public crc.dps.process.ActiveDoc getInterFormContext(crc.dps.Context c) {
    if (c.getTopContext() instanceof crc.dps.process.ActiveDoc) {
      return (crc.dps.process.ActiveDoc)c.getTopContext();
    } else {
      return null;
    }
  }
							
  /** Legacy action: default is to flag as unimplemented. */
  public boolean action(crc.dps.Context aContext, crc.dps.Output out,
			String tag, crc.dps.active.ActiveAttrList atts,
			crc.dom.NodeList content, String cstring) {
    if (noticeGiven) return true;
    noticeGiven = true;
    return false;
  }

  /** Legacy action: report an error if a non-legacy handler exists. */
  protected boolean bogusLegacyAction(crc.dps.Context aContext, String tag) {
    aContext.message(-1, "Legacy handler called for <" + tag
		     + "> when new one exists", 0, true);
    return true;
  }

  /** Legacy action: report as omitted. 
   *	This usually marks an actor which is meaningless or unimplementable
   *	in the new DPS.
   */
  protected boolean omittedLegacyAction(crc.dps.Context aContext, String tag,
					crc.dps.active.ActiveAttrList atts) {
    notify(aContext, tag, atts, "omitted legacy actor -- not implemented");
    return true;
  }

  /** Legacy action: report a warning.  Usually this is for an incompletely-
   *	superceded actor, and indicates that more work has to be done 
   *	somewhere, possibly in the InterForm code.
   */
  protected boolean buggyLegacyAction(crc.dps.Context aContext, String tag,
				      crc.dps.active.ActiveAttrList atts) {
    aContext.message(0, "Buggy legacy handler called for <" + tag
		     + " " + atts + ">", 0, true);
    return true;
  }

  /** Legacy action: report an error. */
  protected boolean legacyError(crc.dps.Context aContext, String tag,
				String message) {
    aContext.message(-1, "InterForm error in <" + tag + ">:" + message,
		     0, true);
    return true;
  }

  /** Legacy action: report an InterForm error. */
  protected boolean reportError(crc.dps.Context aContext, String tag, 
				ActiveAttrList atts, String message) {
    aContext.message(-1, "InterForm error in <" + tag
		     + (atts != null? " " + atts : "") + ">:" + message,
		     0, true);
    return true;
  }

  /** Legacy action: return a NodeList. */
  protected boolean putList(crc.dps.Output out, crc.dom.NodeList nl) {
    crc.dps.util.Copy.copyNodes(nl, out);
    return true;
  }

  /** Legacy action: return a string. */
  protected boolean putText(crc.dps.Output out, String s) {
    out.putNode(new crc.dps.active.ParseTreeText(s));
    return true;
  }

  /** Convenience function: create an element. */
  protected crc.dps.active.ParseTreeElement newElement(String tag) {
    return new crc.dps.active.ParseTreeElement(tag, null, null, null);
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
