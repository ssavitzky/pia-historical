////// ToWriter.java: Token output Stream to Writer
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.output;

import crc.dps.*;
import crc.dps.aux.*;
import crc.dom.*;

import java.util.NoSuchElementException;

/**
 * Output a Token stream to a String. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Output
 * @see crc.dps.Processor
 */

public class ToString extends CursorStack implements Output {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected String destination = "";

  public final String getString() { return destination; }

  /************************************************************************
  ** Internal utilities:
  ************************************************************************/

  protected final void write(String s) {
    destination += s;
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

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct an Output. */
  public ToString() {}
}
