////// setHandler.java: <set> Handler implementation
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
 * Handler for &lt;set&gt;  <p>
 *
 * <p>	This is an approximation to the legacy &gt;set&gt;; it lacks many
 *	of the old extraction modifiers, which have moved to &lt;extract&gt;.
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
    if (name == null || name.equals("")) {
      aContext.message(-2, "Setting null name to "+content, 0, true);
      return;
    }
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
