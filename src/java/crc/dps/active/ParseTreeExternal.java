////// ParseTreeExternal.java -- Entity that refers to an external resource
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.active;

import java.io.*;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.DOMFactory;

import crc.dom.Entity;

import crc.dps.*;
import crc.dps.util.Copy;
import crc.dps.input.FromParseNodes;
import crc.dps.output.ToNodeList;

import crc.ds.Tabular;

/**
 * An implementation of the ActiveEntity interface that refers to an external
 *	resource, for example, a file.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dom.Node
 * @see crc.dps.active.ActiveNode
 */
public class ParseTreeExternal extends ParseTreeEntity {

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

  /** The name of a resource to be input. */
  protected String resourceName = null;

  public String getResourceName() { return resourceName; }
  public void setResourceName(String s) { resourceName = s; }

  /** The context in which to read it. */
  protected Context context = null;

  // === The following should really be protected and have proper accessors...

  public  boolean readable = true;
  public  boolean writeable = false;
  public  String  tsname = null;
  public  boolean append = false;
  public  boolean createIfAbsent = true;
  public  boolean  doNotOverwrite = true;

  protected Input openResource() {
    TopContext top  = context.getTopContext();
    InputStream stm = null;
    try {
      stm = top.readExternalResource(resourceName);
    } catch (IOException e) {
      context.message(-2, e.getMessage(), 0, true);
      return null;
    }
    Tagset      ts  = top.loadTagset(tsname);
    TopContext proc = null;
    Parser p  = ts.createParser();
    p.setReader(new InputStreamReader(stm));
    setWrappedInput(p);
    return p;
  }

  protected void writeResource(Context cxt) {
				// === writeResource
  }

  protected void writeValueToResource() {
    TopContext top  = context.getTopContext();
    OutputStream stm = null;
    try {
      stm = top.writeExternalResource(resourceName, append, createIfAbsent,
				      doNotOverwrite);
    } catch (IOException e) {
      context.message(-2, e.getMessage(), 0, true);
      return;
    }
    OutputStreamWriter w = new OutputStreamWriter(stm);
    try {
      w.write(value.toString());
      w.close();
      stm.close();
    } catch (IOException e) {
      context.message(-2, e.getMessage(), 0, true);
      return;
    }
  }

  /************************************************************************
  ** Access to Value:
  ************************************************************************/

  /** Get the node's value as an Input. 
   */
  public Input getValueInput() { 
    if (value != null) return new FromParseNodes(getValue());
    if (resourceName != null && wrappedInput == null) {
      return openResource();
    }
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
    if (resourceName != null) writeValueToResource();
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public ParseTreeExternal() {
    super("");
  }

  /** Note that this has to do a shallow copy */
  public ParseTreeExternal(ParseTreeExternal e, boolean copyChildren) {
    super(e, copyChildren);
    resourceName = e.getResourceName();
  }

  /** Construct a node with given name. */
  public ParseTreeExternal(String name) {
    super(name);
  }

  /** Construct a node with given data. */
  public ParseTreeExternal(String name, Input in) {
    super(name);
    setWrappedInput(in);
  }

  /** Construct a node with given resource name. */
  public ParseTreeExternal(String name, String rname, Context cxt) {
    super(name);
    resourceName = rname;
    context = cxt;
  }


  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Token.  Attributes, if any, are
   *	copied, but children are not.
   */
  public ActiveNode shallowCopy() {
    return new ParseTreeExternal(this, false);
  }

  /** Return a deep copy of this Token.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    return new ParseTreeExternal(this, true);
  }
}
