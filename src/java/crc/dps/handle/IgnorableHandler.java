////// IgnorableHandler.java: Ignorable Node Handler implementation
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


package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.NodeType;

import crc.dps.*;
import crc.dps.active.*;

import crc.ds.Table;

/**
 * Handler for ignorable nodes.  
 *
 *	<p> The children, if any, are processed, but nothing is done with
 *	    the node itself.  
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.handle.GenericHandler
 * @see crc.dps.Processor
 * @see crc.dps.Tagset
 * @see crc.dps.BasicTagset
 * @see crc.dps.Input 
 * @see crc.dps.Output
 * @see crc.dom.Node
 */

public class IgnorableHandler extends AbstractHandler {

  /************************************************************************
  ** Standard handlers:
  ************************************************************************/

  /** The default IgnorableHandler.  
   *	Its <code>getActionForNode</code> method should be capable of
   *	returning the correct handler. 
   */
  public static final IgnorableHandler DEFAULT  = new IgnorableHandler();

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Since we know what has to be done, it's cheaper to actually perform 
   *	the expansion if the ignorable is active. 
   *
   *	=== Eventually should return a code that implies <code>getValue</code>
   */
  public int actionCode(Input in, Processor p) {
    action(in, p, p.getOutput());
    return Action.COMPLETED;
  }

  /** Process the content, but do nothing with the node itself. */
  public void action(Input in, Context aContext, Output out) {
    if (in.hasChildren()) { 
      aContext.subProcess(in, out).processChildren();
    }
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public IgnorableHandler() {}

}

