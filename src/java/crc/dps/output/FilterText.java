////// FilterText: text-only filter for an Output
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


package crc.dps.output;

import crc.dom.*;
import crc.dps.*;
import crc.dps.NodeType;

import java.io.PrintStream;

/**
 * An Output filter that passes only Text (and Entity) nodes <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 */

public class FilterText extends Proxy {

  /************************************************************************
  ** Operations:
  ************************************************************************/

  public void putNode(Node aNode) { 
    if (target != null && (aNode.getNodeType() == NodeType.TEXT
			   || aNode.getNodeType() == NodeType.ENTITY))
      target.putNode(aNode);
    else if (aNode.hasChildren()) {
      startNode(aNode);
      for (Node n = aNode.getFirstChild(); n != null; n = n.getNextSibling())
	putNode(n);
      endNode();
    }
  }
  public void startNode(Node aNode) { 
    depth++;
  }
  public boolean endNode() { 
    depth --;
    return depth >= 0;
  }
  public void startElement(Element anElement) {
    depth++;
  }
  public boolean endElement(boolean optional) {
    depth --;
    return depth >= 0;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public FilterText() {
  }

  public FilterText(Output theTarget) {
    super(theTarget);
  }

}
