////// OutputTrace: debugging shim for an Output
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
import crc.dps.util.Log;

import java.io.PrintStream;

/**
 * A debugging shim for Outputs.  All operations are proxied to a
 *	``real'' target Output, and also logged to a PrintStream. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 */

public class OutputTrace extends Proxy {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected PrintStream log	= System.err;
  protected static String NL	= "\n";

  public void setLog(PrintStream s) { log = s; }

  public void trace(String message) {
    log.print(message);
  }

  public void trace(String message, int indent) {
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
    trace("put " + logNode(aNode) + NL, depth);
    if (target != null) target.putNode(aNode);
  }
  public void startNode(Node aNode) { 
    trace("start " + logNode(aNode) + NL, depth);
    depth++;
    if (target != null) target.startNode(aNode);
  }
  public boolean endNode() { 
    depth --;
    trace("end " + NL, depth);
    return (target != null)? target.endNode() : depth >= 0;;
  }
  public void startElement(Element anElement) {
    trace("start " + logNode(anElement) + NL, depth);
    depth++;
    if (target != null) target.startElement(anElement);
  }
  public boolean endElement(boolean optional) {
    depth --;
    trace("end(" + (optional? "true" : "false") +")" + NL, depth);
    return (target != null)? target.endElement(optional) : depth >= 0;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public OutputTrace() {
  }

  public OutputTrace(Output theTarget) {
    super(theTarget);
  }

  public OutputTrace(Output theTarget, PrintStream theLog) {
    super(theTarget);
    log = theLog;
  }
}
