////// Proxy: filtering proxy for an Output
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
