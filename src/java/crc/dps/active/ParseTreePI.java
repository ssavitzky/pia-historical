////// ParseTreePI.java -- implementation of ActivePI
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.active;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.PI;

import crc.dps.*;
import crc.dps.util.Copy;

/**
 * An implementation of the ActivePI interface, suitable for use in 
 *	DPS parse trees.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dom.Node
 * @see crc.dps.active.ActiveNode
 */
public class ParseTreePI extends ParseTreeNamed implements ActivePI {

  /************************************************************************
  ** PI interface:
  ************************************************************************/

  String data = "";

  public void setData(String data) { this.data = data; }
  public String getData() 	   { return data; }

  public int getNodeType(){ return NodeType.PI; }
  
  /************************************************************************
  ** ActiveNode interface:
  ************************************************************************/

  // At most one of the following will return <code>this</code>:

  public ActivePI	 asPI() { return this; }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public ParseTreePI() {
  }

  public ParseTreePI(ParseTreePI e, boolean copyChildren) {
    super(e, copyChildren);
    setData(e.getData());
  }

  /** Construct a node with given data. */
  public ParseTreePI(String name, String data) {
    super(name);
    setData(data);
  }

  /** Construct a node with given data and handler. */
  public ParseTreePI(String name, String data, Handler handler) {
    super(name, handler);
    setData(data);
  }


  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Token's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString() {
    return "<?" + getName() + " ";
  }

  /** Return the String equivalent of the Token's content or
   *	<code>data()</code>.  Entities are substituted for characters
   *	with special significance, such as ampersand.
   */
  public String contentString() {
    return (getChildren() == null)? getData() : getChildren().toString();
  }

  /** Return the String equivalent of the Token's end tag (for an element)
   *	or the part that comes after the <code>data()</code>.
   */
  public String endString() {
    return ">";
  }


  /** Convert the Token to a String using the Handler's
   *	<code>convertToString</code> method, if there is one.
   *	Otherwise it uses  <code>basicToString</code>.
   */
  public String toString() {
    return startString() + contentString() + endString();
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Token.  Attributes, if any, are
   *	copied, but children are not.
   */
  public ActiveNode shallowCopy() {
    return new ParseTreePI(this, false);
  }

}
