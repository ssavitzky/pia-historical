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
import crc.dps.Active.*;
import crc.dps.util.Copy;
import crc.dps.util.Status;
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
  ** State:
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

  /** The content to send with the request packet. */
  protected NodeList requestContent = null;

  public NodeList getRequestContent() { return requestContent; }
  public void setRequestContent(NodeList v) { requestContent = v; }

  // === The following should really be protected and have proper accessors...

  public boolean readable 	= true;
  public boolean writeable 	= false;
  public boolean append 	= false;
  public boolean createIfAbsent = true;
  public boolean doNotOverwrite = false;
  public String  method 	= "GET";

  public String  tagsetName	= null;
  public int	 readCount	= 0;
  public int	 writeCount	= 0;

  // access functions

  /** Date last modified.  Can be used to determine whether the resource needs
   *	to be re-read.
   */
  public long	 lastModified	= 0;

  public boolean isReadable() { return readable; }
  public boolean isWritable() { return writeable; }

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
  public volatile ParseTreeElement	   headers	= null;
  public volatile ActiveAttrList	   status	= null;

  protected volatile OutputStream outStream 	= null;
  protected volatile InputStream  inStream 	= null;
  protected volatile Reader       reader 	= null;
  protected volatile Writer       writer 	= null;

  /** The context in which to read it.  Set up by the handler. */
  public  volatile Context context 	= null;

  /************************************************************************
  ** Access to Resource:
  ************************************************************************/

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
      try {
	resourceConnection = resourceURL.openConnection();
	resourceConnection.setDoOutput(writeable);
	// === there are other things we need to be able to set.
      } catch (IOException ex) {
	// error. 
	resourceConnection = null;
      }
      local = false;
    }
    located = true;
  }

  protected void getHeaders(Context cxt) {
    TopContext top  = cxt.getTopContext();
    if (resourceConnection == null) return;
    headers = new ParseTreeElement("HEADERS");// should be active
    int i = 0;
    // === MASSIVE KLUDGE!  There's no way to tell how many headers there are!
    for (; i < 100; ++i) {
      String key = resourceConnection.getHeaderFieldKey(i);
      if (key == null) continue;
      String val = resourceConnection.getHeaderField(key);
      ParseTreeElement hdr = new ParseTreeElement("HEADER");
      hdr.setAttributeValue("name", key);
      hdr.addChild(new ParseTreeText(val));
      System.err.println(key + ": " + val);
      headers.addChild(hdr);
      headers.addChild(new ParseTreeText("\n", true));
    }
    if (i == 0) headers.addChild(new ParseTreeComment("empty headers"));
  }

  protected Input readResource(Context cxt) {
    TopContext top  = cxt.getTopContext();

    locateResource(cxt);
    inStream = null;
    try {
      if (local) {
	inStream = new java.io.FileInputStream(resourceFile);
      } else {
	inStream = resourceConnection.getInputStream();
      }
    } catch (IOException e) {
      cxt.message(-2, e.getMessage(), 0, true);
      return null;
    }
    Tagset      ts  = top.loadTagset(tagsetName);
    TopContext proc = null;
    Parser p  = ts.createParser();
    reader = new InputStreamReader(inStream);
    p.setReader(reader);
    setWrappedInput(p);
    return p;
  }

  protected ToWriter writeResource(Context cxt) {
    // === getting status or setting method requires a URLConnection ===
    // === worry about append in writeExternalResource
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
    Copy.copyNodes(getValueNodes(cxt), out);
    closeOutput();
  }

  public void closeOutput() {
    try {
      writer.flush();
      writer.close();
      outStream.flush();
      outStream.close();
    } catch (IOException e) {}
  }

  public void closeInput() {
    try {
      reader.close();
      inStream.close();
    } catch (IOException e) {}
    wrappedInput = null;
    reader = null;
    inStream = null;
  }

  /************************************************************************
  ** Access to Value:
  ************************************************************************/

  /** Get the node's value as an Input. 
   */
  public Input getValueInput(Context cxt) { 
    context = cxt;
    // if (value != null) === only works if we check timestamps too
    if (resourceName != null && wrappedInput == null) {
      return readResource(cxt);
    } else if (wrappedInput != null) {
      return getWrappedInput();
    } else {
      return new FromParseNodes(getValueNodes(cxt));
    }
  }

  public Output getValueOutput(Context cxt) {
    context = cxt;
    if (resourceName != null) {
      return writeResource(cxt);
    } else {
      return null; // === getValueOutput should return wrapped Output
    }
  }

  /** Get the node's value. 
   *
   * <p> There will be problems if this is called while reading the value.
   */
  public NodeList getValueNodes(Context cxt) {
    if (resourceName == null && wrappedInput == null) return value;
    ToNodeList out = new ToNodeList();
    Input in = getValueInput(cxt);
    Copy.copyNodes(in, out);
    value = out.getList();
    closeInput();
    return value;
  }

  /** Set the node's value.  If the value is <code>null</code>, 
   *	the value is ``un-assigned''.  Hence it is possible to 
   *	distinguish a null value (no value) from an empty one.
   *
   * === WARNING! This will change substantially when the DOM is updated!
   */
  public void setValueNodes(Context cxt, NodeList newValue) {
    super.setValueNodes(cxt, newValue);
    if (resourceName != null) {
      Output out = writeResource(cxt);
      Copy.copyNodes(newValue, out);
      closeOutput();
    }
  }

  /************************************************************************
  ** Access to Document:
  ************************************************************************/

  public ParseTreeDocument getDocument(Context cxt) {
    NodeList value = getValueNodes(cxt); // force the connection to be made.
    getHeaders(cxt);
    status = Status.getStatusItems(this);
    ParseTreeDocument doc = new ParseTreeDocument("DOCUMENT",
						  status, headers, value);
    return doc;
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
