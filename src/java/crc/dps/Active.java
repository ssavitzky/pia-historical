////// Active.java: Interface for things with actions.
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


package crc.dps;

/**
 * Interface for objects that have an Action. <p>
 *
 *	By convention, a class that implements Active, or an interface
 *	that extends it, has the name <code>Active<em>Xxxx</em></code>. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dom.Node
 * @see crc.dps.Action
 * @see crc.dps.Context
 * @see crc.dps.Processor
 */

public interface Active {

  /** Gets the Action for this object.  */
  public Action getAction();

  /** Sets the Action for this object.  May be a no-op if the Active
   *	object is immutable.
   */
  public void setAction(Action newAction);

}
