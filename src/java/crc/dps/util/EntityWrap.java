////// EntityWrap.java -- implementation of ActiveEntity
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.util;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Entity;

import crc.dps.active.*;
import crc.dps.*;
import crc.dps.util.Copy;

import crc.ds.Tabular;

/**
 * An implementation of the ActiveEntity interface that wraps an arbitrary 
 *	object.  This is intended for use in NamespaceWrap.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dom.Node
 * @see crc.dps.active.ActiveNode
 */
public class EntityWrap extends ParseTreeEntity {

  /************************************************************************
  ** The wrapped Object:
  ************************************************************************/

  /** The object being wrapped. */
  protected Object wrappedObject = null;

  /** Retrieve the wrapped object. */
  public Object getWrappedObject() { return wrappedObject; }

  /** Set the wrapped object.  Wrap it, if possible. */
  public void setWrappedObject(Object o) {
    wrappedObject = o;
    if (o instanceof NodeList) {
      value = (NodeList)o;
      names = (o instanceof Namespace)? (Namespace) o : null;
    } else if (o instanceof ActiveNode) {
      value = new ParseNodeList((ActiveNode)o);
      names = (o instanceof Namespace)? (Namespace) o : null;
    } else if (o instanceof Tabular) {
      names = new NamespaceWrap(getName(), (Tabular)o);
      value = null;
    } else {
      value = null;
    }
  }

  /************************************************************************
  ** Access to Value:
  ************************************************************************/

  /** Get the node's value. 
   *
   *	Eventually we may want a way to distinguish values stored in
   *	the children from values stored in a separate nodelist.
   */
  public NodeList getValue() {
    if (value != null || wrappedObject == null) return value;
    // Have to wrap the object. 
    if (names != null) return new ParseNodeList(names.getBindings());
    return new ParseNodeList(new ParseTreeText(wrappedObject.toString()));
  }

  /** Set the node's value.  If the value is <code>null</code>, 
   *	the value is ``un-assigned''.  Hence it is possible to 
   *	distinguish a null value (no value) from an empty one.
   *
   * === WARNING! This will change substantially when the DOM is updated!
   */
  public void setValue(NodeList newValue) {
    wrappedObject = newValue;
    super.setValue(newValue);
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public EntityWrap() {
    super("");
  }

  /** Note that this has to do a shallow copy */
  public EntityWrap(EntityWrap e, boolean copyChildren) {
    super(e, copyChildren);
    wrappedObject = e.getWrappedObject();
  }

  /** Construct a node with given name. */
  public EntityWrap(String name) {
    super(name);
  }

  /** Construct a node with given data. */
  public EntityWrap(String name, Object o) {
    super(name);
    setWrappedObject(o);
  }

  /** Construct a node with given handler. */
  public EntityWrap(String name, Handler handler) {
    super(name, handler);
  }

  /** Construct a node with given data and handler. */
  public EntityWrap(String name, Object o, Handler handler) {
    super(name, handler);
    setWrappedObject(o);
  }


  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Token.  Attributes, if any, are
   *	copied, but children are not.
   */
  public ActiveNode shallowCopy() {
    return new EntityWrap(this, false);
  }

  /** Return a deep copy of this Token.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    return new EntityWrap(this, true);
  }
}
