////// setHandler.java: <set> Handler implementation
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
import crc.dps.util.*;

/**
 * Handler for &lt;set&gt;  <p>
 *
 *	This is an approximation to the legacy &gt;set&gt;; it lacks many
 *	of the old extraction modifiers, which have moved to &lt;find&gt;. <p>
 *
 *	It is permissible for the <code>name</code> attribute to be missing,
 *	in which case the entire namespace will be returned.  The 
 *	<code>keys</code>, <code>values</code>, and <code>bindings</code>
 *	attributes are supported.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class setHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** This will normally be the only thing to customize. */
  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    // Actually do the work. 
    String name = atts.getAttributeString("name");
    Index.setIndexValue(aContext, name, content);
  }

   
  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public setHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL; 		// EMPTY, QUOTED, 0 (check)
  }
}
