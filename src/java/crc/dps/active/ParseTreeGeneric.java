// ParseTreeGeneric.java
// $Id$

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

import java.io.*;
import crc.dom.*;
import crc.dps.Handler;
import crc.dps.Namespace;

/** 
 * Abstract base class for nodes with names, attributes, and a variable 
 *	nodeType. <p>
 *
 *	Generic nodes can be used for things like Tagset, that might
 *	be either elements or declarations in different contexts.
 */
public class ParseTreeGeneric extends ParseTreeElement  {

  /************************************************************************
  ** Instance Variables:
  ************************************************************************/

  protected int nodeType = NodeType.ELEMENT;

  protected NodeList value;
  protected Namespace names = null;
  protected boolean isAssigned = false;

  protected String name;

  /************************************************************************
  ** Accessors:
  ************************************************************************/

  public int getNodeType()		{ return nodeType; }

  /** In some cases it may be necessary to make the node type more specific. */
  void setNodeType(int value) 		{ nodeType = value; }
  
  /**
   * Set attribute name.
   * @param name attribute name.
   */
  public void setName(String name)	{ this.name = name; }

  /**
   * Returns the name of this attribute. 
   * @return attribute name.
   */
  public String getName()		{ return name; }
  
  /** Get the associated namespace, if any. */
  public Namespace asNamespace() { return names; }

  public void setIsAssigned(boolean value) { isAssigned = value; }

  /** Has the node been explicitly assigned a value. */
  public boolean getIsAssigned() 	{ return isAssigned; }

  /** Get the node's value. 
   *
   *	Eventually we may want a way to distinguish values stored in
   *	the children from values stored in a separate nodelist.
   */
  public NodeList getValue()		{ return value; }

  /** Set the node's value.  If the value is <code>null</code>, 
   *	the value is ``un-assigned''.  Hence it is possible to 
   *	distinguish a null value (no value) from an empty one.
   */
  public void setValue(NodeList newValue) {
    if (value == null) {
      isAssigned = false;
      value = null;
      return;
    } else {
      isAssigned = true;
    }
    if (newValue instanceof Namespace) {
      names = (Namespace)newValue;
      value = newValue;
    } else value = new ParseNodeList(newValue);
  }

  /************************************************************************
  ** Construction and Copying:
  ************************************************************************/

  public ParseTreeGeneric() {
    super();
  }

  public ParseTreeGeneric(String n, NodeList v) {
    super();
    setName( n );
    setValue( v );
  }

  public ParseTreeGeneric(String tag) {
    super(tag, null, null, null);
  }

  /**
   * deep copy constructor.
   */
  public ParseTreeGeneric(ParseTreeGeneric attr, boolean copyChildren){
    super((ParseTreeElement)attr, copyChildren);
    setName( attr.getName());
    setValue(attr.getValue());
  }

  public ActiveNode shallowCopy() {
    return new ParseTreeGeneric(this, false);
  }

  public ActiveNode deepCopy() {
    return new ParseTreeGeneric(this, true);
  }

}



