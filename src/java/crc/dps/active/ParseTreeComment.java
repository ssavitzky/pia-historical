////// ParseTreeComment.java -- implementation of ActiveComment
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.active;

import crc.dom.Node;
import crc.dom.NodeList;

import crc.dom.Comment;

import crc.dps.*;
import crc.dps.util.Copy;

/**
 * An implementation of the ActiveComment interface, suitable for use in 
 *	DPS parse.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dom.Node
 * @see crc.dps.active.ActiveNode
 */
public class ParseTreeComment extends ParseTreeNode implements ActiveComment {

  /************************************************************************
  ** Comment Interface:
  ************************************************************************/

  public int getNodeType(){ return NodeType.COMMENT; }
  public void setData(String data){ this.data = data; }
  public String getData(){ return data; }


  /************************************************************************
  ** Instance Variables:
  ************************************************************************/

  String data;

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public ParseTreeComment() {
  }

  public ParseTreeComment(ParseTreeComment n, boolean copyChildren) {
    super(n, copyChildren);
    data = n.getData();
  }

  public ParseTreeComment(Comment n, boolean copyChildren) {
    super((ActiveComment)n, copyChildren);
    data = n.getData();
  }

  /** Construct a node with given data. */
  public ParseTreeComment(String data) {
    this.data = data;
  }

  /** Construct a node with given data and handler. */
  public ParseTreeComment(String data, Handler handler) {
    super(handler);
    this.data = data;
  }


  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Token's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString() {
    return "<!-- ";
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
    return " -->";
  }


  /** Convert the Node to a String, in external form.
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
    return new ParseTreeComment(this, false);
  }

  /** Return a deep copy of this Token.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    return new ParseTreeComment(this, true);
  }
 
}
