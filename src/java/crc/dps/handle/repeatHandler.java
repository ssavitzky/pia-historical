////// repeatHandler.java: <repeat> Handler implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Element;
import crc.dom.NodeEnumerator;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;
import crc.dps.input.FromParseNodes;

import crc.ds.Association;
import crc.ds.List;

import java.util.Enumeration;

/**
 * Handler for &lt;repeat&gt;....&lt;/&gt;  <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class repeatHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    // Actually do the work.  
    // There were no attributes to dispatch on, so repeat the content until
    // something stops the processor.
    String tag = in.getTagName();
    FromParseNodes src = iterationSrc(content);
    Processor process  = iterationCxt(src, aContext, out, null, tag);
    List repeatSubs = new List();

    if (iterationInit(content, process, repeatSubs) > 0) {
      while (process.run()) src.toFirstNode();
      iterationFinish(process, repeatSubs);
    }
  }

  /** This does the parse-time dispatching. <p>
   *
   *	Action is dispatched (delegated) to a subclass if the string
   *	being passed to <code>dispatch</code> is either the name of an
   *	attribute or a period-separated suffix of the tagname. <p>
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "numeric"))	 return repeat_numeric.handle(e);
    if (dispatch(e, "start"))	 return repeat_numeric.handle(e);
    if (dispatch(e, "stop"))	 return repeat_numeric.handle(e);
    if (dispatch(e, "step"))	 return repeat_numeric.handle(e);
    if (dispatch(e, "list")) 	 return repeat_list.handle(e);
    return this;
  }


  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Perform one iteration through the content. */
  public void iterate(Processor p, ActiveNode n,
		      ActiveEntity var, FromParseNodes src) {
    var.setValue(new ParseNodeList(n));
    p.run();
    src.toFirstNode();
  }

  /** Scan the content and tell each sub-element to set itself up.
   *	The sub-elements are counted and pushed onto a list.
   */
  public int iterationInit(NodeList content, Processor process, List subs) {
    int count = 0;
    NodeEnumerator items = content.getEnumerator();
    for (Node n = items.getFirst(); n != null; n =  items.getNext()) {
      ActiveNode item = (ActiveNode)n;
      Action a = item.getAction();
      if (a != null && a instanceof repeatHandler) {
	repeatHandler rh = (repeatHandler)a;
	subs.push(item);
	count++;
	rh.initialize(item.asElement(), process); 
      }
    }
    return count;
  }

  /** Go through the list of sub-elements made by iterationInit, and
   *	execute the <code>finish</code> method of each.
   */
  public void iterationFinish(Processor process, List subs) {
    Enumeration items = subs.elements();
    while (items.hasMoreElements()) {
      ActiveNode item = (ActiveNode)items.nextElement();
      Action a = item.getAction();
      if (a != null && a instanceof repeatHandler) {
	repeatHandler rh = (repeatHandler)a;
	rh.finish(item.asElement(), process); 
      }
    }
  }

  /** Return the name of the iteration variable (entity) */
  String getEntityName(ActiveAttrList atts, String dflt) {
    return (atts == null)? dflt
      : (atts.hasTrueAttribute("entity"))? atts.getAttributeString("entity")
      : dflt;
  }

  /** Return an ActiveEntity to use as an iteration variable. */
  public ActiveEntity iterationVar(ActiveAttrList atts, Context cxt, 
				   String dflt) {
    String name = atts.getAttributeString("entity");
    Tagset ts = cxt.getTopContext().getTagset();
    if (name == null) name = dflt;
    return ts.createActiveEntity(name, null);
  }

  /** Return a suitable Input for iterating through the content. */
  public FromParseNodes iterationSrc(NodeList nl) {
    return new FromParseNodes(nl);
  }

  /** Return a suitable context in which the iteration variables are bound. */
  public Processor iterationCxt(Input in, Context cxt, Output out,
				ActiveEntity var, String tag) {
    BasicEntityTable ents = new BasicEntityTable(tag);
    if (var != null) ents.setBinding(var.getName(), var);
    return cxt.subProcess(in, out, ents);
  }

  /** Stop the iteration. */
  public void iterationStop(Context aContext) {
    aContext.getProcessor().stop();
  }

  /** Intended for sub-elements: perform initialization. */
  public void initialize(ActiveElement e, Processor p) {
  }

  /** Intended for sub-elements: perform cleanup. */
  public void finish(ActiveElement e, Processor p) {
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public repeatHandler() {
    /* Expansion control: */
    expandContent = false;	// true 	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = QUOTED;  		// EMPTY, QUOTED, 0 (check)
  }
}

/* ***********************************************************************
 * Attribute Subclasses:
 ************************************************************************/

class repeat_numeric extends repeatHandler {
  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    Association start = MathUtil.getNumeric(atts, "start", "0");
    Association stop  = MathUtil.getNumeric(atts, "stop", "0");
    Association step  = MathUtil.getNumeric(atts, "step", "1");

    ActiveEntity ent   = iterationVar(atts, aContext, "n");
    FromParseNodes src = iterationSrc(content);
    Processor process  = iterationCxt(src, aContext, out, ent, in.getTagName());

    if (start.isIntegral() && stop.isIntegral() && step.isIntegral()) {
      long iiter = start.longValue();
      long istop  = stop.longValue();
      long istep  = step.longValue();

      if (istep > 0) {
	for ( ; iiter <= istop; iiter += istep)
	  iterate(process, new ParseTreeText(iiter), ent, src);
      } else {
	for ( ; iiter >= istop; iiter += istep)
	  iterate(process, new ParseTreeText(iiter), ent, src);
      }
    } else {
      double fiter = start.doubleValue();
      double fstop = stop.doubleValue();
      double fstep = step.doubleValue();

      if (fstep > 0.0) {
	for ( ; fiter <= fstop; fiter += fstep)
	  iterate(process, new ParseTreeText(fiter), ent, src);
      } else {
	for ( ; fiter >= fstop; fiter += fstep)
	  iterate(process, new ParseTreeText(fiter), ent, src);
      }
    }
  }
  static repeat_numeric handle = new repeat_numeric();
  static Action handle(ActiveElement e) { return handle; }
}


class repeat_list extends repeatHandler {
  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    ActiveEntity ent   = iterationVar(atts, aContext, "li");
    FromParseNodes src = iterationSrc(content);
    Processor process  = iterationCxt(src, aContext, out, ent, in.getTagName());

    Enumeration iter = ListUtil.getListItems(atts.getAttributeValue("list"));
    while (iter.hasMoreElements())
      iterate(process, (ActiveNode) iter.nextElement(), ent, src);
  }
  static repeat_list handle = new repeat_list();
  static Action handle(ActiveElement e) { return handle; }
}


/* ***********************************************************************
 * Sub-elements:
 ************************************************************************/

/** Generic parent class for sub-elements. */
class repeat_subHandler extends repeatHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Restore the default action. */
  public void action(Input in, Context aContext, Output out) {
    defaultAction(in, aContext, out);
  }

  /** Don't do any parse-time dispatching.  Let the subclasses handle it. */
  public Action getActionForNode(ActiveNode n) {
    return this;
  }

  public repeat_subHandler() {
    /* Expansion control: */
    expandContent = true;	// false 	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }
}

/** &lt;first&gt; expands only during initialization. */
class firstHandler extends repeat_subHandler {

  /** Special case: does nothing except during initialization. */
  public int actionCode(Input in, Processor p) {
    return Action.COMPLETED;
  }
  public void action(Input in, Context aContext, Output out) {
  }

  /** During initialization, &lt;first&gt; expands */
  public void initialize(ActiveElement e, Processor p) {
    Expand.processChildren(e, p, p.getOutput());
  }
}

/** &lt;finally&gt; expands only during cleanup. */
class finallyHandler extends repeat_subHandler {

  /** Special case: does nothing except during cleanup. */
  public int actionCode(Input in, Processor p) {
    return Action.COMPLETED;
  }
  public void action(Input in, Context aContext, Output out) {
  }

  /** During cleanup, &lt;finally&gt; expands */
  public void finish(ActiveElement e, Processor p) {
    Expand.processChildren(e, p, p.getOutput());
  }
}

/** &lt;while&gt; stops expansion if its contents are empty. */
class whileHandler extends repeat_subHandler {

  // No need for initialization 

  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    if (! Test.orValues(content, aContext)) iterationStop(aContext);
  }
}


/** &lt;until&gt; stops expansion if its contents are not empty. */
class untilHandler extends repeat_subHandler {

  // No need for initialization 

  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    if (Test.orValues(content, aContext)) iterationStop(aContext);
  }
}


/** &lt;foreach&gt; iterates through its content. */
class foreachHandler extends repeat_subHandler {

  /** Set up iteration variables ENTITY, ENTITY-list, and ENTITY-index. */
  public void initialize(ActiveElement e, Processor p) {
    String name = getEntityName(Expand.expandAttrs(p, e.getAttributes()), "li");
    NodeList items = Expand.processChildren(e, p);
    items = new ParseNodeList(ListUtil.getListItems(items));
    p.setEntityValue(name + "-list", items, true);
    p.setEntityValue(name + "-index",
		     new ParseNodeList(new ParseTreeText(0)), true);
    p.setEntityValue(name, null, true);
  }

  /** This is the per-iteration action.  It would be more efficient if the
   *	<code>initialize</code> method replaced the handler, but that wouldn't
   *	be thread-safe.
   */
  public void action(Input in, Context cxt, Output out) {
    ActiveAttrList atts = Expand.getExpandedAttrs(in, cxt);
    String name = getEntityName(atts, "li");
    NodeList items = cxt.getEntityValue(name + "-list", true);
    String index = cxt.getEntityValue(name + "-index", true).toString();
    long n = MathUtil.getNumeric(index).longValue();

    if (n >= items.getLength()) {
      iterationStop(cxt);
      return;
    }

    ActiveNode item = null;
    try {item = (ActiveNode)items.item(n);} catch (Exception e) {}
    cxt.setEntityValue(name, new ParseNodeList(item), true);
    cxt.setEntityValue(name+"-index",
		       new ParseNodeList(new ParseTreeText(n+1)), true);
  }
}

/** &lt;for&gt; iterates through a sequence of numbers. */
class forHandler extends repeat_subHandler {

  /** Return a string, ensuring that it trims down to a number. */
  String numericParam(String s, String dflt) {
    s = s.trim();
    if (MathUtil.getNumeric(s) != null) return s;
    else return dflt;
  }

  /** Gets a text parameter from either the attributes or the items. */
  String getParameter(ActiveAttrList atts, NodeList items, 
		      String name, String dflt) {
    String result = atts.getAttributeString(name);
    if (result != null) return numericParam(result, dflt);
    if (items == null) return dflt;

    NodeEnumerator enum = items.getEnumerator();
    for (Node n = enum.getFirst(); n != null; n = enum.getNext()) {
      if (n.getNodeType() == NodeType.ELEMENT
	  && ((Element)n).getTagName().equalsIgnoreCase(name)) {
	return numericParam(ListUtil.getFirstWord(n), dflt);
      }
    }
    return dflt;
  }

  /** Get an iteration parameter from the current context. */
  Association getParameter(Context cxt, String name) {
    NodeList v = cxt.getEntityValue(name, false);
    if (v == null) return null;
    return MathUtil.getNumeric(v);
  }

  NodeList toValue(String s) {
    return new ParseNodeList(new ParseTreeText(s));
  }

  /** Set up iteration variables ENTITY, ENTITY-list, and ENTITY-index. */
  public void initialize(ActiveElement e, Processor p) {
    ActiveAttrList atts = Expand.expandAttrs(p, e.getAttributes());
    String name = getEntityName(atts, "n");
    NodeList items = Expand.processChildren(e, p);
    p.setEntityValue(name + "-step",
		     toValue(getParameter(atts, items, "step", "1")),
		     true);
    p.setEntityValue(name + "-stop",
		     toValue(getParameter(atts, items, "stop", "0")),
		     true);
    p.setEntityValue(name + "-start",
		     toValue(getParameter(atts, items, "start", "0")),
		     true);
  }

  /** This is the per-iteration action.  It would be more efficient if the
   *	<code>initialize</code> method replaced the handler, but that wouldn't
   *	be thread-safe.
   */
  public void action(Input in, Context cxt, Output out) {
    ActiveAttrList atts = Expand.getExpandedAttrs(in, cxt);
    String name = getEntityName(atts, "n");

    Association iter = getParameter(cxt, name);
    Association stop = getParameter(cxt, name+"-stop");
    Association step = getParameter(cxt, name+"-step");

    long iiter;
    long istop;
    long istep;
    double fiter;
    double fstop;
    double fstep;

    if (iter == null) {
      // First time through the loop.  Set the iteration variable to start.
      // We also have to check for immediate termination.
      iter = getParameter(cxt, name+"-start");
      if (iter.isIntegral() && stop.isIntegral() && step.isIntegral()) {
	iiter = iter.longValue();
	istop = stop.longValue();
	istep = step.longValue();
	if (istep > 0) {
	  if (iiter > istop) iterationStop(cxt);
	} else if (istep < 0) {
	  if (iiter < istop) iterationStop(cxt);
	}
	cxt.setEntityValue(name,
			   new ParseNodeList(new ParseTreeText(iiter)), true);
      } else {
	fiter = iter.doubleValue();
	fstop = stop.doubleValue();
	fstep = step.doubleValue();

	if (fstep > 0.0) {
	  if (fiter > fstop) iterationStop(cxt);
	} else {
	  if (fiter < fstop) iterationStop(cxt);
	}
	cxt.setEntityValue(name,
			   new ParseNodeList(new ParseTreeText(fiter)), true);
      }
    } else if (iter.isIntegral() && stop.isIntegral() && step.isIntegral()) {
      iiter = iter.longValue();
      istop = stop.longValue();
      istep = step.longValue();

      if (istep > 0) {
	iiter += istep;
	if (iiter > istop) iterationStop(cxt);
      } else if (istep < 0) {
	iiter -= istep;
	if (iiter < istop) iterationStop(cxt);
      }
      cxt.setEntityValue(name,
			 new ParseNodeList(new ParseTreeText(iiter)), true);
    } else {
      fiter = iter.doubleValue();
      fstop = stop.doubleValue();
      fstep = step.doubleValue();

      if (fstep > 0.0) {
	fiter += fstep;
	if (fiter > fstop) iterationStop(cxt);
      } else {
	fiter -= fstep;
	if (fiter < fstop) iterationStop(cxt);
      }
      cxt.setEntityValue(name,
			 new ParseNodeList(new ParseTreeText(fiter)), true);
    }
  }
}

