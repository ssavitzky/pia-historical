// ParseTreeNamed.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dps.active;

import java.io.*;
import crc.dom.*;
import crc.dps.Handler;

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
    value = new ParseNodeList(newValue);
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

}



