////// ToExternalForm.java: Output to external form
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


package crc.dps.output;

import crc.dom.*;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;

import java.util.NoSuchElementException;

/**
 * Output to external form. <p>
 *
 * <p> This is an abstract class that has all the machinery for deciding when
 *	to convert text to its external form (i.e. with special characters
 *	replaced by entities), and when not to.  It relies on the Text nodes
 *	to do the actual conversion, which they do by default.
 *
 * <p> The test for whether to use external form or not is rather
 *	simpleminded: it only looks at <code>parseEntitiesInContent</code> in
 *	the Syntax of the current parent (as given by <code>getNode</code>).
 *	This is OK because if we're not parsing entities, we're certainly not
 *	parsing elements either, so none of the children of the current node
 *	will have children that need expanding.
 *
 * <p> Similarly, only Text nodes are output literally (by writing their
 *	<code>getData</code> string).  This has the elegant side-effect that
 *	cloning the content of a literal element and putting it into another
 *	context will automagically do the right thing for that context.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Output
 * @see crc.dps.Processor */

public abstract class ToExternalForm extends CursorStack implements Output {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected EntityTable defaultEntityTable = null;

  /************************************************************************
  ** Internal utilities:
  ************************************************************************/

  /** This is the hook needed to actually output a string. */
  protected abstract void write(String s);

  /** Return true if we are inside the content of a literal. */
  protected boolean inLiteralContent() {
    if (getActive() == null) return false;
    else return hasLiteralContent(getActive());
  }

  /** Write a node out as the content of a literal. */
  protected void writeLiteralData(Node aNode) {
    if (aNode.getNodeType() == crc.dps.NodeType.TEXT) {
      write(((Text)aNode).getData());
    } else if (aNode.getNodeType() == crc.dps.NodeType.ENTITY) {
      // Convert character entities back to characters.
      Entity e = (Entity)aNode;
      NodeList value = defaultEntityTable.getEntityValue(null, e.getName());
      // === new DOM: check entity's value first. 
      if (value != null) write(value.toString());
      else write(aNode.toString());
    } else write(aNode.toString());
  }

  /** Return true if a node has literal content. */
  protected boolean hasLiteralContent(ActiveNode e) {
    if (e == null || e.getSyntax() == null) return false;
    return !e.getSyntax().parseEntitiesInContent();
  }

  /************************************************************************
  ** Operations:
  ************************************************************************/

  public void putNode(Node aNode) { 
    // === should probably use syntax if defined.
    if (inLiteralContent()) writeLiteralData(aNode);
    else if (aNode.hasChildren() && aNode instanceof ActiveNode
	     && hasLiteralContent((ActiveNode)aNode)) {
      startNode(aNode);
      Copy.copyNodes(aNode.getChildren(), this);
      endNode();
    } else {
      write(aNode.toString());
    }
  }

  public void startNode(Node aNode) { 
    pushInPlace();
    setNode(aNode);
    if (active != null) {
      write(active.startString());
    } else {
      // === punt -- should never happen.
    }
  }

  public boolean endNode() {
    if (active != null) {
      write(active.endString());
    } else if (node == null) {
      // null node indicates nothing to do.
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
  public ToExternalForm() {
    defaultEntityTable = TextUtil.getCharacterEntities();
  }

}
