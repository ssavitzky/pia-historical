////// ParseTreeExternal.java -- Entity that refers to an external resource
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.active;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.DOMFactory;

import crc.dom.Entity;

import crc.dps.*;
import crc.dps.util.Copy;
import crc.dps.input.FromParseNodes;
import crc.dps.output.ToNodeList;
import crc.dps.output.ToWriter;

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

  // === The following should really be protected and have proper accessors...

  public boolean readable 	= true;
  public boolean writeable 	= false;
  public String  tsname 	= null;
  public boolean append 	= false;
  public boolean createIfAbsent = true;
  public boolean doNotOverwrite = false;
  public String  method 	= "GET";
  public String  tagsetName	= null;
  public int	 readCount	= 0;
  public int	 writeCount	= 0;

  /** Date last modified.  Can be used to determine whether the resource needs
   *	to be re-read.
   */
  public long	 lastModified	= 0;

  public void setMode(String mode) {
    if (mode != null) mode = mode.toLowerCase();
    if (mode == null || mode.equals("read")) {
      method = "GET";
      readable = true;
      writeable = false;
    } else if (mode.equals("write")) {
      method = "PUT";
      writeable = true;
      readable  = false;
      append = false;
      createIfAbsent = true;
      doNotOverwrite = false;
    } else if (mode.equals("update")) {
      method = "PUT";
      writeable = true;
      readable  = true;
      append = false;
      createIfAbsent = false;
      doNotOverwrite = false;
    } else if (mode.equals("append")) {
      method = "POST";
      writeable = true;
      readable = true;
      append = true;
      createIfAbsent = true;
      doNotOverwrite = false;
    } else if (mode.equals("create")) {
      method = "PUT";
      writeable = true;
      readable  = true;
      append = false;
      createIfAbsent = true;
      doNotOverwrite = true;
    }
  }

  public void setMethod(String meth) {
    if (meth == null) {
      if (method == null) setMode("read");
      return;
    }
    meth = meth.toUpperCase();
    method = meth;
    if (meth.equals("PUT")) {
      writeable = true;
    } else if (meth.equals("POST")) {
      writeable = true;
      readable = true;
      append = true;
    }
  }

  // === Connection state.  These should be set up by the handler. 

  public volatile Tagset  tagset	= null; 
  public volatile boolean located 	= false;
  public volatile boolean local   	= false;
  public volatile File    resourceFile	= null;
  public volatile URL     resourceURL	= null;
  public volatile URLConnection resourceConnection	= null;

  protected volatile OutputStream outStream = null;
  protected volatile InputStream inStream = null;
  protected volatile Reader reader = null;
  protected volatile Writer writer = null;

  /** The context in which to read it.  Set up by the handler. */
  public  volatile Context context 	= null;

  /** Locate the connected resource, returning its location in either
   *	resourceFile or resourceURL.  
   */
  protected void locateResource(Context cxt) {
    TopContext top  = cxt.getTopContext();
    String url = resourceName;
    if (url == null) return;
    if (url.indexOf(":") < 0 || url.startsWith("file:") ||
	url.indexOf("/") >= 0 && url.indexOf(":") > url.indexOf("/")) {
      resourceFile = top.locateSystemResource(url, writeable);
      local = true;
    } else {
      resourceURL = top.locateRemoteResource(url, writeable);
      // === Should really make a URLConnection at this point.
      local = false;
    }
    located = true;
  }

  protected Input readResource(Context cxt) {
    // === getting status on input requires a URLConnection ===
    // === readResource should locate the resource.
    TopContext top  = cxt.getTopContext();
    inStream = null;
    try {
      inStream = top.readExternalResource(resourceName);
    } catch (IOException e) {
      cxt.message(-2, e.getMessage(), 0, true);
      return null;
    }
    Tagset      ts  = top.loadTagset(tsname);
    TopContext proc = null;
    Parser p  = ts.createParser();
    reader = new InputStreamReader(inStream);
    p.setReader(reader);
    setWrappedInput(p);
    return p;
  }

  protected ToWriter writeResource(Context cxt) {
    // === getting status on output requires a URLConnection ===
    TopContext top  = cxt.getTopContext();
    outStream = null;
    try {
      outStream = top.writeExternalResource(resourceName, append,
					    createIfAbsent, doNotOverwrite);
    } catch (IOException e) {
      cxt.message(-2, e.getMessage(), 0, true);
      return null;
    }
    writer = new OutputStreamWriter(outStream);
    return new ToWriter(writer);
  }

  protected void writeValueToResource(Context cxt) {
    Output out = writeResource(cxt);
    Copy.copyNodes(getValue(), out);
    try {
      writer.flush();
      writer.close();
      outStream.flush();
      outStream.close();
    } catch (IOException e) {}
  }

  /************************************************************************
  ** Access to Value:
  ************************************************************************/

  /** Get the node's value as an Input. 
   */
  public Input getValueInput(Context cxt) { 
    context = cxt;
    if (value != null) return new FromParseNodes(getValue());
    if (resourceName != null && wrappedInput == null) {
      return readResource(cxt);
    }
    return getWrappedInput();
  }

  public Output getValueOutput(Context cxt) {
    return null; // === getValueOutput
  }

  /** Get the node's value. 
   *
   * <p> There will be problems if this is called while reading the value.
   */
  public NodeList getValue() {
    if (value != null || wrappedInput == null) return value;
    ToNodeList out = new ToNodeList();
    Input in = getValueInput(context);
    Copy.copyNodes(in, out);
    value = out.getList();
    try {
      reader.close();
      inStream.close();
    } catch (IOException e) {}
    wrappedInput = null;
    reader = null;
    inStream = null;
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
    if (resourceName != null) writeValueToResource(context);
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

  /** Construct a node with given resource name.
   *	Note that a context will be needed when we get around to actually
   *	expanding the node, but it is <em>not</em> necessarily needed
   *	when we define it.  It may, for example, be in a tagset.
   */
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
