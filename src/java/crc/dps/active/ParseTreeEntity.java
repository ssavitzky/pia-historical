////// ParseTreeEntity.java -- implementation of ActiveEntity
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.active;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.DOMFactory;

import crc.dom.BasicNamedNode;
import crc.dom.Entity;

import crc.dps.*;
import crc.dps.input.FromParseNodes;
import crc.dps.output.ToNodeList;
import crc.dps.util.Copy;

/**
 * An implementation of the ActiveEntity interface, suitable for use in 
 *	DPS parse trees.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dom.Node
 * @see crc.dps.active.ActiveNode
 */
public class ParseTreeEntity extends ParseTreeNamed implements ActiveEntity {

  // At most one of the following will return <code>this</code>:

  public ActiveEntity 	 asEntity() 	{ return this; }

  /************************************************************************
  ** Entity Interface:
  ************************************************************************/

  public int getNodeType() { return NodeType.ENTITY; }

  protected boolean isParameterEntity = false;
  public void setIsParameterEntity(boolean value) { isParameterEntity = value; }
  public boolean getIsParameterEntity() { return isParameterEntity; }

  public Input getValueInput(Context cxt) {
    return new FromParseNodes(getValue());
  }

  public Output getValueOutput(Context cxt) {
    // === changes when value becomes children ===
    ToNodeList out = new ToNodeList();
    setValue(out.getList());
    return out;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public ParseTreeEntity() {
    super("");
  }

  /** Note that this has to do a shallow copy */
  public ParseTreeEntity(ParseTreeEntity e, boolean copyChildren) {
    super(e, copyChildren);
    isParameterEntity = e.isParameterEntity;
  }

  /** Construct a node with given name. */
  public ParseTreeEntity(String name) {
    super(name);
  }

  /** Construct a character entity. */
  public ParseTreeEntity(String name, char c) {
    super(name, new ParseNodeList(new ParseTreeText(c)));
    // === really needs additional flag ===
  }

  /** Construct a node with given data. */
  public ParseTreeEntity(String name, NodeList value) {
    super(name, value);
  }

  /** Construct a node with given handler. */
  public ParseTreeEntity(String name, Handler handler) {
    super(name);
    setHandler(handler);
  }

  /** Construct a node with given data and handler. */
  public ParseTreeEntity(String name, NodeList value, Handler handler) {
    super(name, value);
    setHandler(handler);
  }


  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Token's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString() {
    return "&";
  }

  /** Return the String equivalent of the Token's content or
   *	<code>data()</code>.  Entities are substituted for characters
   *	with special significance, such as ampersand.
   */
  public String contentString() {
    return getName();
  }

  /** Return the String equivalent of the Token's end tag (for an element)
   *	or the part that comes after the <code>data()</code>.
   */
  public String endString() {
    return ";";
  }


  /** Convert the Token to a String using the Handler's
   *	<code>convertToString</code> method, if there is one.
   *	Otherwise it uses  <code>basicToString</code>.
   *
   * === It's an interesting question whether to return name or value ===
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
    return new ParseTreeEntity(this, false);
  }

  /** Return a deep copy of this Token.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    return new ParseTreeEntity(this, true);
  }

  /** Return new node corresponding to this Token, made using the given 
   *	DOMFactory.  Children <em>are not</em> copied.
   */
  public Node createNode(DOMFactory f) {
    return null; // === DOMFactory cannot create entities ===
  }

}
