////// ParseTreeDecl.java -- implementation of ActiveDeclaration
//	$Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


package crc.dps.active;

import crc.dom.Node;
import crc.dom.NodeList;

import crc.dom.PI;

import crc.dps.*;
import crc.dps.util.Copy;

/**
 * An implementation of the ActiveDecl interface, suitable for use in 
 *	DPS parse trees.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dom.Node
 * @see crc.dps.active.ActiveNode
 */
public class ParseTreeDecl extends ParseTreeNamed implements ActiveDeclaration {

  /************************************************************************
  ** Declaration interface:
  ************************************************************************/

  String data = "";

  public void setData(String value) 	{ data = value; }
  public String getData() 	   	{ return data; }

  String tagName = "";
  public void setTagName(String name) 	{ tagName = name; }
  public String getTagName() 	   	{ return tagName; }

  protected int nodeType = NodeType.DECLARATION;
  public int getNodeType()		{ return nodeType; }

  /** In some cases it may be necessary to make the node type more specific. */
  void setNodeType(int value) { nodeType = value; }
  
  /************************************************************************
  ** ActiveNode interface:
  ************************************************************************/

  // At most one of the following will return <code>this</code>:

  public ActiveDeclaration asDeclaration() { return this; }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public ParseTreeDecl() {
  }

  public ParseTreeDecl(ParseTreeDecl e, boolean copyChildren) {
    super(e, copyChildren);
    setData(e.getData());
  }

  /** Construct a node with given data. */
  public ParseTreeDecl(String tagName, String name, String data) {
    super(name);
    setTagName(tagName);
    setData(data);
  }

  /** Construct a node with given data and handler. */
  public ParseTreeDecl(String tagName, String name, String data,
		       Handler handler) {
    super(name, handler);
    setTagName(tagName);
    setData(data);
  }


  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Token's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString() {
    return "<!" + getTagName() + " "
      + ((getName() == null)? "" : getName() + " ");
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


  /** Convert the Token to a String.
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
    return new ParseTreeDecl(this, false);
  }

  public ActiveNode deepCopy() {
    return new ParseTreeDecl(this, true);
  }

}
