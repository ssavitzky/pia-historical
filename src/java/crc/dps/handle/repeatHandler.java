////// repeatHandler.java: <repeat> Handler implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Element;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.aux.*;
import crc.dps.input.FromParseNodes;

import crc.ds.Association;
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

  /** This will normally be the only thing to customize. */
  public void action(Input in, Context aContext, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    // Actually do the work. 
    // === Dispatching has already shown that there is nothing to repeat. 
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
    if (dispatch(e, "step"))	 return repeat_numeric.handle(e);
    if (dispatch(e, "list")) 	 return repeat_list.handle(e);
    return this;
  }

  public void iterate(Processor p, ActiveNode n,
		      ActiveEntity var, FromParseNodes src) {
    var.setValue(new ParseNodeList(n));
    p.run();
    src.toFirstNode();
  }

  public ActiveEntity iterationVar(ActiveAttrList atts, Context cxt) {
    String name = atts.getAttributeString("entity");
    Tagset ts = cxt.getTopContext().getTagset();
    if (name == null) name = "li";
    return ts.createActiveEntity(name, null);
  }

  public FromParseNodes iterationSrc(NodeList nl) {
    return new FromParseNodes(nl);
  }

  public Processor iterationCxt(Input in, Context cxt, Output out,
				ActiveEntity var) {
    EntityTable ents = new BasicEntityTable(cxt.getEntities());
    ents.setBinding(var);
    return cxt.subProcess(in, out, ents);
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public repeatHandler() {
    /* Expansion control: */
    stringContent = false;	// true 	want content as string?
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?
    noCopyNeeded = true;	// false 	don't copy parse tree?
    passElement = false;	// true 	pass while expanding?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }
}

/* ***********************************************************************
 * Subclasses:
 ************************************************************************/

class repeat_numeric extends repeatHandler {
  public void action(Input in, Context aContext, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    Association start = MathUtil.getNumeric(atts, "start", "0");
    Association stop  = MathUtil.getNumeric(atts, "stop", "0");
    Association step  = MathUtil.getNumeric(atts, "step", "1");

    ActiveEntity ent   = iterationVar(atts, aContext);
    FromParseNodes src = iterationSrc(content);
    Processor process  = iterationCxt(src, aContext, out, ent);

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
  public void action(Input in, Context aContext, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    ActiveEntity ent   = iterationVar(atts, aContext);
    FromParseNodes src = iterationSrc(content);
    Processor process  = iterationCxt(src, aContext, out, ent);

    Enumeration iter = ListUtil.getListItems(atts.getAttributeValue("list"));
    while (iter.hasMoreElements())
      iterate(process, (ActiveNode) iter.nextElement(), ent, src);
  }
  static repeat_list handle = new repeat_list();
  static Action handle(ActiveElement e) { return handle; }
}
