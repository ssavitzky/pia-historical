////// actionHandler.java: <action> Handler implementation
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
 * Handler for &lt;action&gt;....&lt;/&gt;  <p>
 *
 *	This is a sub-element of &lt;define&gt;.  It actually performs no
 *	actions; we just need to make sure a corresponding node ends up in
 *	the output where <code>defineHandler</code> can find it.
 *
 *	The handler's class is used to recognize the corresponding element.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class actionHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  public int actionCode(Input in, Processor p) {
    return Action.COPY_NODE;
  }

  public void action(Input in, Context aContext, Output out,
  		     ActiveAttrList atts, NodeList content) {
    ActiveElement e = in.getActive().asElement();
    ActiveElement element = e.editedCopy(atts, null);
    // === should be able to skip expanding the attrs altogether for <action>
    out.startElement(element);
    Copy.copyNodes(content, out);
    out.endElement(e.isEmptyElement() || e.implicitEnd());
  }


  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public actionHandler() {
    /* Expansion control: */
    expandContent = false;	// true		Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = QUOTED;  		// EMPTY, QUOTED, 0 (check)
  }

}

