////// OutputTrace: debugging shim for an Output
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.output;

import crc.dom.*;
import crc.dps.*;
import crc.dps.aux.Log;

import java.io.PrintStream;

/**
 * A debugging shim for Outputs.  All operations are proxied to a
 *	``real'' target Output, and also logged to a PrintStream. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 */

public class OutputTrace implements Output {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Output target	= null;
  protected PrintStream log	= System.err;
  protected int depth		= 0;
  protected static String NL	= "\n";

  public Output getTarget() { return target; }
  public void setTarget(Output theTarget) { target = theTarget; }

  public void setLog(PrintStream s) { log = s; }

  public void debug(String message) {
    log.print(message);
  }

  public void debug(String message, int indent) {
    String s = "=>";
    for (int i = 0; i < indent; ++i) s += " ";
    s += message;
    log.print(s);
  }

  public String logNode(Node aNode) { return Log.node(aNode); }
  public String logString(String s) { return Log.string(s); }

  /************************************************************************
  ** Operations:
  ************************************************************************/

  public void putNode(Node aNode) { 
    debug("put " + logNode(aNode) + NL, depth);
    if (target != null) target.putNode(aNode);
  }
  public void startNode(Node aNode) { 
    debug("start " + logNode(aNode) + NL, depth);
    depth++;
    if (target != null) target.startNode(aNode);
  }
  public boolean endNode() { 
    depth --;
    debug("end " + NL, depth);
    return (target != null)? target.endNode() : depth >= 0;;
  }
  public void startElement(Element anElement) {
    debug("start " + logNode(anElement) + NL, depth);
    depth++;
    if (target != null) target.startElement(anElement);
  }
  public boolean endElement(boolean optional) {
    depth --;
    debug("end(" + (optional? "true" : "false") +")" + NL, depth);
    return (target != null)? target.endElement(optional) : depth >= 0;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public OutputTrace() {
  }

  public OutputTrace(Output theTarget) {
    target = theTarget;
  }

  public OutputTrace(Output theTarget, PrintStream theLog) {
    this(theTarget);
    log = theLog;
  }
}
