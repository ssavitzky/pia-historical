////// FilterMarkup: markup-only filter for an Output
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.output;

import crc.dom.*;
import crc.dps.*;
import crc.dps.NodeType;

import java.io.PrintStream;

/**
 * An Output filter that passes only Markup nodes <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 */

public class FilterMarkup extends Proxy {

  /************************************************************************
  ** Operations:
  ************************************************************************/

  public void putNode(Node aNode) { 
    if (target != null && (aNode.getNodeType() != NodeType.TEXT
			   && aNode.getNodeType() != NodeType.ENTITY))
      target.putNode(aNode);
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public FilterMarkup() {
  }

  public FilterMarkup(Output theTarget) {
    super(theTarget);
  }

}
