////// EntityTable.java: Entity Lookup Table interface
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

import crc.dps.active.ActiveEntity;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.NodeEnumerator;
import crc.dom.Attribute;
import crc.dom.AttributeList;

import java.util.Enumeration;

/**
 * The interface for a EntityTable -- a lookup table for values. 
 *
 *	Entities are named values that can be substituted into a document.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dom.Entity
 * @see crc.dps.active.ActiveEntity
 * @see crc.dps.util.BasicEntityTable
 */

public interface EntityTable extends Namespace {

  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Look up a name and return the corresponding Entity. */
  public ActiveEntity getEntityBinding(String name);

  public NodeList getEntityValue(Context cxt, String name);

  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/

  /** Returns an Enumeration of the entity names defined in this table. 
   */
  public Enumeration entityNames();

}
