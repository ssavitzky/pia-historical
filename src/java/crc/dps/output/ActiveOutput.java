////// ActiveOutput.java: Token output Stream abstract base class
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.output;

import crc.dps.aux.CurrentActive;
import crc.dps.*;
import crc.dom.*;

/**
 * An abstract base class for implementations of the Output interface
 *	that operate exclusively on ActiveNode's.<p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Token
 * @see crc.dps.Input
 * @see crc.dps.Processor
 */

public abstract class ActiveOutput extends CurrentActive implements Output {

  public void putNode(Node aNode) { super.putNode(aNode); }
  public void startNode(Node aNode) { super.startNode(aNode); }
  public boolean endNode() { return super.endNode(); }
  public void startElement(Element anElement) { super.startElement(anElement); }
  public boolean endElement(boolean optional) {
    return super.endElement(optional);
  }
  public void putAttribute(String name, NodeList value) {
    super.putAttribute(name, value);
  }
  public void startAttribute(String name) {
    super.startAttribute(name);
  }
}
