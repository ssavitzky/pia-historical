////// ParseTreeText.java -- implementation of ActiveText
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.active;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.ArrayNodeList;
import crc.dom.NodeEnumerator;
import crc.dom.DOMFactory;
import crc.dom.Element;
import crc.dom.BasicText;

import crc.dom.Text;
import crc.dom.Comment;
import crc.dom.PI;

import crc.dps.*;
import crc.dps.aux.*;

/**
 * An implementation of the ActiveText interface, suitable for use in 
 *	DPS parse.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dom.Node
 * @see crc.dps.Context
 * @see crc.dps.Processor
 */
public class ParseTreeText extends ParseTreeNode implements ActiveText {

  /************************************************************************
  ** Instance Variables:
  ************************************************************************/

  protected String data = "";
  protected boolean ignorableWhitespace = false;

  /** flag for whether the Text is whitespace. */
  protected boolean isWhitespace = false;

  /************************************************************************
  ** Text interface:
  ************************************************************************/

  public void setIsIgnorableWhitespace(boolean isIgnorableWhitespace){ 
    ignorableWhitespace = isIgnorableWhitespace;
  }
  public boolean getIsIgnorableWhitespace() { return ignorableWhitespace; }

  public int getNodeType(){ return NodeType.TEXT; }

  public void setData(String data) { this.data = data; }
  public String getData() 	   { return data; }

  /************************************************************************
  ** ActiveNode interface:
  ************************************************************************/

  // Exactly one of the following will return <code>this</code>:

  public ActiveText 	 asText()	{ return this; }

  /************************************************************************
  ** ActiveText interface:
  ************************************************************************/

  public boolean getIsWhitespace() { return isWhitespace; }
  public void setIsWhitespace(boolean value) { isWhitespace = value; }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public ParseTreeText() {
  }

  public ParseTreeText(ParseTreeText e, boolean copyChildren) {
    super(e, copyChildren);
    handler = e.handler;
    action = e.action;
    data = e.data;
    ignorableWhitespace = e.ignorableWhitespace;
    isWhitespace = e.isWhitespace;
  }

  /** Construct a node with given data. */
  public ParseTreeText(String data) {
    this.data = data;
    setIsWhitespace(Test.isWhitespace(data));
  }

  /** Construct a node with a single character as data. */
  public ParseTreeText(char data) {
    this(String.valueOf(data));
  }

  /** Construct a node with an integer value. */
  public ParseTreeText(long data) {
    this(String.valueOf(data), false, false);
  }

  /** Construct a node with a floating-point value. */
  public ParseTreeText(double data) {
    this(String.valueOf(data), false, false);
  }

  /** Construct a node with given data and handler. */
  public ParseTreeText(String data, Handler handler) {
    this(data);
    setHandler(handler);
  }

  /** Construct a node with given data, flags, and handler. */
  public ParseTreeText(String data, boolean isIgnorable,
		       boolean isWhitespace, Handler handler) {
    this.data = data;
    ignorableWhitespace = isIgnorable;
    this.isWhitespace = isWhitespace;
    setHandler(handler);
  }

  /** Construct a node with given data, flags, and handler. */
  public ParseTreeText(String data, boolean isIgnorable,
		       boolean isWhitespace) {
    this(data, isIgnorable, isWhitespace, null);
  }

  /** Construct a node with given data, flags, and handler. */
  public ParseTreeText(String data, boolean isIgnorable) {
    this(data);
    setIsIgnorableWhitespace(isIgnorable);
  }



  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Token's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString() {
    return "";		// insert character entities ===
  }

  /** Return the String equivalent of the Token's content or
   *	<code>data()</code>.  Entities are substituted for characters
   *	with special significance, such as ampersand.
   */
  public String contentString() {
    return getData();		// insert character entities ===
    //return (getChildren() == null)? getData() : getChildren().toString();
  }

  /** Return the String equivalent of the Token's end tag (for an element)
   *	or the part that comes after the <code>data()</code>.
   */
  public String endString() {
    return "";
  }


  /** Convert the Token to a String using the Handler's
   *	<code>convertToString</code> method, if there is one.
   *	Otherwise it uses  <code>basicToString</code>.
   */
  public String toString() {
    return contentString();
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Token.  Attributes, if any, are
   *	copied, but children are not.
   */
  public ActiveNode shallowCopy() {
    return new ParseTreeText(this, false);
  }

  /** Return new node corresponding to this Token, made using the given 
   *	DOMFactory.  Children <em>are not</em> copied.
   */
  public Node createNode(DOMFactory f) {
    Text e = f.createTextNode(getData()); 
    return e;
  }

}
