////// FilterText: text-only filter for an Output
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

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
