////// elsfHandler.java: Node Handler generic implementation
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
import crc.dom.Element;
import crc.dom.NodeList;
import crc.dom.NodeEnumerator;
import crc.dom.Attribute;
import crc.dom.AttributeList;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;

/**
 * Handler for &lt;elsf&gt;. <p>
 *
 *	This is really just a syntactic placeholder, but the fact that
 *	it's a subclass makes it possible for &lt;if&gt; to use a faster
 *	and more reliable test than comparing tagnames.
 *	<p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Processor
 * @see crc.dps.Tagset
 * @see crc.dps.BasicTagset
 * @see crc.dps.Input 
 * @see crc.dom.Node
 */

public class elsfHandler extends GenericHandler {

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

    out.startElement(element);
    Copy.copyNodes(content, out);
    out.endElement(e.isEmptyElement() || e.implicitEnd());
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public elsfHandler() {
    expandContent = false;	// true		expand content?
  }
}
