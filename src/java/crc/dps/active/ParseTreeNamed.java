// ParseTreeNamed.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dps.active;

import java.io.*;
import crc.dom.*;
import crc.dps.Handler;
import crc.dps.Namespace;

/** 
 * Abstract base class for nodes with names and values. 
 *	Should really distinguish between values in the children,
 *	and values in a nodelist.
 */
public abstract class ParseTreeNamed extends ParseTreeNode  {

  protected NodeList value = null;
  protected Namespace names = null;

  protected boolean isAssigned = false;
  public void setIsAssigned(boolean value) { isAssigned = value; }

  /** Has the node been explicitly assigned a value. */
  public boolean getIsAssigned() { return isAssigned; }

  /** Get the node's value. 
   *
   *	Eventually we may want a way to distinguish values stored in
   *	the children from values stored in a separate nodelist.
   */
  public NodeList getValue(){ return value/*getChildren()*/; }

  /** Get the associated namespace, if any. */
  public Namespace asNamespace() { return names; }

  /** Set the node's value.  If the value is <code>null</code>, 
   *	the value is ``un-assigned''.  Hence it is possible to 
   *	distinguish a null value (no value) from an empty one.
   *
   * === WARNING! This will change substantially when the DOM is updated!
   */
  public void setValue(NodeList newValue) {
    if (newValue == null) {
      isAssigned = false;
      value = null;
      names = null;
      return;
    } else {
      isAssigned = true;
      if (newValue instanceof Namespace) {
	names = (Namespace)newValue;
	value = newValue;
      } else value = new ParseNodeList(newValue);
    }
    //setChildren(newValue);
  }

  public ParseTreeNamed() {
    super();
  }

  public ParseTreeNamed(String n, Handler h) {
    super(h);
    setName( n );
  }

  public ParseTreeNamed(String n) {
    super();
    setName( n );
  }

  public ParseTreeNamed(String n, NodeList v) {
    this( n );
    setValue( v );
  }

  /**
   * deep copy constructor.
   */
  public ParseTreeNamed(ParseTreeNamed attr, boolean copyChildren){
    super(attr, copyChildren);
    setName( attr.getName() );
    setValue( attr.getValue() );
  }

  /**
   * Set attribute name.
   * @param name attribute name.
   */
  public void setName(String name){ this.name = name; }

  /**
   * Returns the name of this attribute. 
   * @return attribute name.
   */
  public String getName(){ return name; }
  
  /* attribute name */
  protected String name;

}



