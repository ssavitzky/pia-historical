////// AbstractOutput.java: Output abstract base class
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.output;

import crc.dps.aux.CurrentNode;
import crc.dps.*;
import crc.dom.*;

/**
 * An abstract base class for implementations of the Output interface
 *	that operate on generic Node's.<p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 */

public abstract class AbstractOutput extends CurrentNode implements Output {

  public void putNode(Node aNode) { super.putNode(aNode); }
  public void startNode(Node aNode) { super.startNode(aNode); }
  public boolean endNode() { return super.endNode(); }
  public void startElement(Element anElement) { super.startElement(anElement); }
  public boolean endElement(boolean optional) {
    return super.endElement(optional);
  }
}
