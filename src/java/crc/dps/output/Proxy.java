////// Proxy: filtering proxy for an Output
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.output;

import crc.dom.*;
import crc.dps.*;
import crc.dps.util.Log;

import java.io.PrintStream;

/**
 * A proxy for DPS Outputs.  All operations are proxied to a
 *	``real'' target Output. <p>
 *
 *	A Proxy can be used with no target to simply discard output.
 *	This is reasonably efficient.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 */

public class Proxy implements Output {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Output target	= null;
  protected int depth		= 0;

  public Output getTarget() { return target; }
  public void setTarget(Output theTarget) { target = theTarget; }

  /************************************************************************
  ** Operations:
  ************************************************************************/

  public void putNode(Node aNode) { 
    if (target != null) target.putNode(aNode);
  }
  public void startNode(Node aNode) { 
    depth++;
    if (target != null) target.startNode(aNode);
  }
  public boolean endNode() { 
    depth --;
    return (target != null)? target.endNode() : depth >= 0;;
  }
  public void startElement(Element anElement) {
    depth++;
    if (target != null) target.startElement(anElement);
  }
  public boolean endElement(boolean optional) {
    depth --;
    return (target != null)? target.endElement(optional) : depth >= 0;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public Proxy() {
  }

  public Proxy(Output theTarget) {
    target = theTarget;
  }
}
