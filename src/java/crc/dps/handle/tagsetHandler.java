////// tagsetHandler.java: <tagset> Handler implementation
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
import crc.dps.tagset.BasicTagset;

/**
 * Handler for &lt;tagset&gt;....&lt;/&gt;  <p>
 *
 *	
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class tagsetHandler extends GenericHandler {

  /************************************************************************
  ** Parse-time Operations:
  ************************************************************************/

  /** In this case we must actually create a Tagset object. */
  public ActiveElement createElement(String tagname, AttributeList attributes) {
    BasicTagset ts = new BasicTagset();
    ts.setTagName(tagname);
    ts.setAttributes(attributes);

    // Actually looking at the attributes has to be done at processing time

    return ts;
  }

  /* === This is very twisty. ===
    
     The element we create at parse time with createElement becomes part of
     the parse tree.  We probably don't want that to become the ``official''
     tagset, although it's possible we might. 

     When we get around to processing we need to make a Tagset, or track down
     an old one, and put it into a new TopContext where it can be hacked on.
     The parser is still using the old one.

     When we want a recursive Tagset, though, we really need to hack the one
     the parser is using.  DTD's and PI's may also need to do this (e.g. the
     doctype and the XML case- and space-handling pragmas).

     A recursive Tagset _really_ only changes the language used in <value> and 
     <action> tags, not otherwise. 
   */

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** This will get a little tricky.  
   *	There are really two distinct possibilities here -- are we 
   *	<em>using</em> a tagset, or <em>defining</em> one?
   */
  public void action(Input in, Context aContext, Output out) {
    BasicTagset n = (BasicTagset) in.getNode();
    if (in.hasActiveChildren() || in.hasActiveAttributes()) {
      aContext.subProcess(in, out).expandCurrentNode();
    } else {
      Copy.copyNode(n, in, out);
    }
  }

  /** Action for &lt;tagset&gt; node. */
  public void action(Input in, Context cxt, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    // === Do we need to go through the content at this point?
  }

  /** This does the parse-time dispatching. <p>
   *
   *	Action is dispatched (delegated) to a subclass if the string
   *	being passed to <code>dispatch</code> is either the name of an
   *	attribute or a period-separated suffix of the tagname. <p>
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    return this;
  }
   
  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public tagsetHandler() {
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

