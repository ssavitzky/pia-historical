////// ActiveEntity.java: Active Entity node (parse tree element) interface
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


package crc.dps.active;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Entity;

import crc.dps.Input;
import crc.dps.Output;
import crc.dps.Context;

/**
 * A DOM Entity node which includes extra syntactic and semantic
 *	information, making it suitable for use in active documents in
 *	the DPS.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dom.Node
 * @see crc.dps.Active
 * @see crc.dps.ActiveNode
 * @see crc.dps.Action
 * @see crc.dps.Syntax
 * @see crc.dps.Processor
 */

public interface ActiveEntity extends Entity, ActiveNode {

  public NodeList getValueNodes(Context cxt);
  public void setValueNodes(Context cxt, NodeList value);

  /** Get the node's value as an Input. */
  public Input getValueInput(Context cxt);

  /** Get an Output that writes into the node's value. */
  public Output getValueOutput(Context cxt);

  public boolean getIsAssigned();
  public void setIsAssigned(boolean value);
}
