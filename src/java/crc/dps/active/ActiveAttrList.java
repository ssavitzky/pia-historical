////// ActiveNode.java: Active Node (parse tree element) interface
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
import crc.dom.Element;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;

/**
 * A DOM AttributeList that includes additional convenience functions.
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

public interface ActiveAttrList extends AttributeList {

  /************************************************************************
  ** Attribute convenience functions:
  ************************************************************************/

  /** Convenience function: get an Attribute by name and return its value. */
  public NodeList getAttributeValue(String name);

  /** Convenience function: get an Attribute by name and return its value
   *	as a String.
   */
  public String getAttributeString(String name);

  /** Convenience function: get an Attribute by name and return its value
   *	as a boolean
   */
  public boolean hasTrueAttribute(String name);

  /** Convenience function: Set an attribute's value to a Node. */
  public void setAttributeValue(String name, Node value);

  /** Convenience function: Set an attribute's value to a NodeList. */
  public void setAttributeValue(String name, NodeList value);

  /** Convenience function: Set an attribute's value to a String. */
  public void setAttributeValue(String name, String value);

  /** Convenience function: Set an attribute's value to a NodeList,
   *	given the optimistic assumption that the attribute is currently
   *	undefined.
   */
  public void addAttribute(String name, NodeList value);

}
