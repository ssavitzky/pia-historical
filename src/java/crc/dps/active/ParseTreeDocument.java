// ParseTreeDocument.java
// $Id$

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

import java.io.*;
import crc.dom.*;
import crc.dps.Handler;
import crc.dps.Namespace;
import crc.dps.util.Copy;

/** 
 * Class for Document nodes. <p>
 *
 *	Document nodes can also be used as Element nodes.
 */
public class ParseTreeDocument extends ParseTreeElement
				       /* implements ActiveDocument */
{

  /************************************************************************
  ** Instance Variables:
  ************************************************************************/

  protected int nodeType = NodeType.DOCUMENT;

  /************************************************************************
  ** Accessors:
  ************************************************************************/

  public int getNodeType()		{ return nodeType; }

  /** In some cases it may be necessary to make the node type more specific. */
  void setNodeType(int value) 		{ nodeType = value; }
  
  public void setTagName(String name) { 
    super.setTagName(name);
    setNodeType(name == null? NodeType.DOCUMENT : NodeType.ELEMENT);
  }

  /************************************************************************
  ** Construction and Copying:
  ************************************************************************/

  public ParseTreeDocument() {
    super();
  }

  public ParseTreeDocument(String tag) {
    super(tag, null, null, null);
    nodeType = (tag == null)? NodeType.DOCUMENT : NodeType.ELEMENT;
  }

  public ParseTreeDocument(String tag, ActiveAttrList attrs, NodeList content) {
    super(tag, attrs, content);
    nodeType = (tag == null)? NodeType.DOCUMENT : NodeType.ELEMENT;
  }

  /** Construct a document given headers and content. */
  public ParseTreeDocument(String tag, ActiveAttrList attrs, 
			   ActiveElement headers, NodeList content) {
    this(tag, attrs, null);
    if (headers != null) addChild(headers);
    if (headers != null) addChild(new ParseTreeText("\n", true));
    Copy.appendNodes(content, this);
  }

  /**
   * deep copy constructor.
   */
  public ParseTreeDocument(ParseTreeDocument attr, boolean copyChildren){
    super((ParseTreeElement)attr, copyChildren);
    nodeType = attr.getNodeType();
  }

  public ActiveNode shallowCopy() {
    return new ParseTreeDocument(this, false);
  }

  public ActiveNode deepCopy() {
    return new ParseTreeDocument(this, true);
  }

}



