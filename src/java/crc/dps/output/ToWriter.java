////// ToWriter.java: Token output Stream to Writer
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.output;

import crc.dps.*;
import crc.dps.aux.*;
import crc.dom.*;

import java.util.NoSuchElementException;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Output a Token stream to a Writer (character output stream). <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Output
 * @see crc.dps.Processor
 */

public class ToWriter extends CursorStack implements Output {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Writer destination = null;


  /************************************************************************
  ** Internal utilities:
  ************************************************************************/

  protected void write(String s) {
    try {
      destination.write(s);
    } catch (IOException e) {}
  }

  protected String encode(String s) {
    return s;			// === encode
  }

  /************************************************************************
  ** Operations:
  ************************************************************************/

  public void putNode(Node aNode) { 
    write(aNode.toString());
  }
  public void startNode(Node aNode) { 
    pushInPlace();
    setNode(aNode);
    if (active != null) {
      write(active.startString());
    } else if (node instanceof AbstractNode) {
      AbstractNode n = (AbstractNode) node;
      write(n.startString());
    } else {
      // === punt -- should never happen.
    }
  }

  public boolean endNode() {
    if (active != null) {
      write(active.endString());
    } else if (node == null) {
      // null node indicates nothing to do.
    } else if (node instanceof AbstractNode) {
      AbstractNode n = (AbstractNode) node;
      write(n.endString());
    }  else {
      // === punt -- should never happen.
    }   
    return popInPlace();
  }

  public void startElement(Element anElement) {
    startNode(anElement);
  }

  public boolean endElement(boolean optional) {
    if (optional) {
      return popInPlace();
    } else {
      return endNode();
    }
  }

  public void putAttribute(String name, NodeList value) {
    if (value == null) {
      write(name);
    } else {
      write(name + "=" + value.toString());
    }
  }
  public void startAttribute(String name) {
    write(name + "+");
    pushInPlace();
    setNode(null);
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct an Output given a destination Writer */
  public ToWriter(Writer dest) {
    destination = dest;
  }

  /** Construct an Output given a destination filaname.  Opens the file. */
  public ToWriter(String filename) throws java.io.IOException {
    destination = new FileWriter(filename);
  }
}
