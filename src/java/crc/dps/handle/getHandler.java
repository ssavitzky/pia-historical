////// getHandler.java: <get> Handler implementation
//	$Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


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
 * Handler for &lt;get&gt;  <p>
 *
 *	This is an approximation to the legacy &gt;get&gt;; it lacks many
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

public class getHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** This will normally be the only thing to customize. */
  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    // Actually do the work. 
    String name = atts.getAttributeString("name");
    Copy.copyNodes(Index.getIndexValue(aContext, name), out);
  }

  /** This does the parse-time dispatching. <p>
   *
   *	Action is dispatched (delegated) to a subclass if the string
   *	being passed to <code>dispatch</code> is either the name of an
   *	attribute or a period-separated suffix of the tagname. <p>
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    //if (dispatch(e, "element"))	 return get_element.handle(e);
    //if (dispatch(e, "local"))	 return get_local.handle(e);
    //if (dispatch(e, "global")) return get_global.handle(e);
    //if (dispatch(e, "index"))	 return get_index.handle(e);

    //if (dispatch(e, "pia"))	 return get_pia.handle(e);
    //if (dispatch(e, "env"))	 return get_env.handle(e);
    //if (dispatch(e, "agent"))	 return get_agent.handle(e);
    //if (dispatch(e, "trans"))	 return get_trans.handle(e);
    //if (dispatch(e, "form"))	 return get_form.handle(e);

    //if (dispatch(e, "file"))	 return read_file.handle(e); OBSOLETE
    //if (dispatch(e, "href"))	 return read_href.handle(e); OBSOLETE
    return this;
  }
   
  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public getHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = EMPTY; 		// NORMAL, QUOTED, 0 (check)
  }
}
