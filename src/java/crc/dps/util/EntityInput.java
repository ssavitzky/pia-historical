////// EntityInput.java -- Wrapper for arbitrary input.
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.util;

import java.io.*;

import crc.dom.Node;
import crc.dom.NodeList;

import crc.dom.Entity;

import crc.dps.active.*;
import crc.dps.*;
import crc.dps.util.Copy;
import crc.dps.input.FromParseNodes;
import crc.dps.output.ToNodeList;

import crc.ds.Tabular;

/**
 * An implementation of the ActiveEntity interface that wraps an arbitrary 
 *	Input object.  Sometimes it will be a Parser looking at a file, but
 *	it is more likely to be used to lazily-evaluate the content of an
 *	active node (i.e. as the value of <code>&amp;content;</code>).
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dom.Node
 * @see crc.dps.active.ActiveNode
 */
public class EntityInput extends ParseTreeEntity {

  /************************************************************************
  ** The wrapped Object:
  ************************************************************************/

  /** The Input being wrapped. */
  protected Input wrappedInput = null;

  /** Retrieve the wrapped object. */
  public Input getWrappedInput() { return wrappedInput; }

  /** Set the wrapped object.  Wrap it, if possible. */
  public void setWrappedInput(Input in) {
    wrappedInput = in;
  }

  /************************************************************************
  ** Access to Value:
  ************************************************************************/

  /** Get the node's value as an Input. 
   */
  public Input getValueInput(Context cxt) { 
    if (value != null) return new FromParseNodes(getValue());
    return getWrappedInput();
  }

  /** Get the node's value. 
   *
   * <p> There will be problems if this is called while reading the value.
   */
  public NodeList getValue() {
    if (value != null || wrappedInput == null) return value;
    ToNodeList out = new ToNodeList();
    Input in = getValueInput();
    Copy.copyNodes(in, out);
    value = out.getList();
    wrappedInput = null;
    return value;
  }

  /** Set the node's value.  If the value is <code>null</code>, 
   *	the value is ``un-assigned''.  Hence it is possible to 
   *	distinguish a null value (no value) from an empty one.
   *
   * === WARNING! This will change substantially when the DOM is updated!
   */
  public void setValue(NodeList newValue) {
    super.setValue(newValue);
    wrappedInput = null;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public EntityInput() {
    super("");
  }

  /** Note that this has to do a shallow copy */
  public EntityInput(EntityInput e, boolean copyChildren) {
    super(e, copyChildren);
    setValue(e.getValue());
  }

  /** Construct a node with given name. */
  public EntityInput(String name) {
    super(name);
  }

  /** Construct a node with given data. */
  public EntityInput(String name, Input in) {
    super(name);
    setWrappedInput(in);
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Token.  Attributes, if any, are
   *	copied, but children are not.
   */
  public ActiveNode shallowCopy() {
    return new EntityInput(this, false);
  }

  /** Return a deep copy of this Token.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    return new EntityInput(this, true);
  }
}
