////// Output.java: Document Builder
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


package crc.dps;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.Element;

/**
 * The interface for a consumer of Nodes. <p>
 *
 *	An Output receives the Nodes that comprise a Document in depth-first
 *	order.  Output subclasses exist to:
 *
 *	<ul>
 *	    <li> Copy parse trees.
 *	    <li> Construct arbitrary Document trees
 *	    <li> Convert a subtree into a String.
 *	</ul>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Token
 * @see crc.dps.Input
 * @see crc.dps.Processor
 */

public interface Output {

  /** Adds <code>aNode</code> and its children to the document under 
   *	construction as a new child of the current node.  The new node
   *	is copied unless it has no parent and has a type compatible with
   *	the document under construction.
   */
  public void putNode(Node aNode);

  /** Adds <code>aNode</code> to the document under construction, and
   *	makes it the current node.
   */
  public void startNode(Node aNode);

  /** Ends the current Node and makes its parent current.
   * @return <code>false</code> if the current Node has no parent.
   */
  public boolean endNode();

  /** Adds <code>anElement</code> to the document under construction, and
   *	makes it the current node.  An element may be ended with either
   *	<code>endElement</code> or <code>endNode</code>.
   */
  public void startElement(Element anElement);

  /** Ends the current Element.  The end tag may be optional.  
   *	<code>endElement(true)</code> may be used to end an empty element. 
   */
  public boolean endElement(boolean optional);

}
