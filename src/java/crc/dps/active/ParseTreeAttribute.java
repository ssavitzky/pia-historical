////// ParseTreeAttribute.java -- implementation of ActiveAttribute
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

import crc.dom.Attribute;

import crc.dps.*;

/**
 * An implementation of the ActiveAttribute interface, suitable for use in 
 *	DPS parse.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dom.Node
 * @see crc.dps.active.ActiveNode
 */
public class ParseTreeAttribute extends ParseTreeNamed
	implements ActiveAttribute
{

  /************************************************************************
  ** Instance Variables:
  ************************************************************************/

  protected boolean specified = false;

  /************************************************************************
  ** Attribute interface:
  ************************************************************************/

  public int getNodeType() { return NodeType.ATTRIBUTE; }

  public NodeList getValue(){ return super.getValue(); }

  public void setValue(NodeList value){
    setIsAssigned( true );
    setSpecified(value != null);
    super.setValue(value);
  }

  public void setSpecified(boolean specified){ this.specified = specified; }
  public boolean getSpecified(){return specified;}


  /************************************************************************
  ** ActiveNode interface:
  ************************************************************************/

  public ActiveAttribute asAttribute() 	{ return this; }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public ParseTreeAttribute() {
    super();
  }

  public ParseTreeAttribute(ParseTreeAttribute e, boolean copyChildren) {
    super(e, copyChildren);
    specified = e.specified;
  }

  /** Construct a node with given data. */
  public ParseTreeAttribute(String name, NodeList value) {
    super(name, value);
    
    // explicitly assigned value
    setIsAssigned( true );
    setSpecified( value != null );
  }

  /** Construct a node with given data and handler. */
  public ParseTreeAttribute(String name, NodeList value, Handler handler) {
    super(name, value);
    setHandler(handler);
  }


  /************************************************************************
  ** Presentation:
  ************************************************************************/

  public String startString() {
    return getName() + ((! getSpecified() || getValue() == null)? "" : "=");
  }

  public String contentString() {
    return (! getSpecified() || getValue() == null)
      ? ""
      : "'" + getValue().toString() + "'";
  }

  public String endString() {
    return "";
  }


  public String toString() {
    return startString() + contentString() + endString();
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Token. 
   *	Since an attribute's value is kept in its children, we actually
   *	need to do a deep copy.
   */
  public ActiveNode shallowCopy() {
    return new ParseTreeAttribute(this, false);
  }

  /** Return a deep copy of this Token.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    return new ParseTreeAttribute(this, true);
  }

}
