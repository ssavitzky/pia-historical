////// DiscardOutput
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.output;

import crc.dom.*;
import crc.dps.*;
import crc.dps.aux.Log;

import java.io.PrintStream;

/**
 * An Output that discards all output.
 *
 *	Slightly more efficient than a Proxy with no target.  The real
 *	benefit is better documentation of the programmer's intent.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 */

public class DiscardOutput implements Output {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected int depth		= 0;

  /************************************************************************
  ** Operations:
  ************************************************************************/

  public void putNode(Node aNode) { 
  }
  public void startNode(Node aNode) { 
    depth++;
  }
  public boolean endNode() { 
    depth --;
    return depth >= 0;;
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

  public DiscardOutput() {
  }

}
