////// ToParseTree.java: Token output Stream to ParseTree
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

import crc.dps.*;
import crc.dps.util.*;
import crc.dps.active.*;

import crc.dom.Node;
import crc.dom.Element;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Output to a parse tree, comprised entirely of Active nodes.<p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Token
 * @see crc.dps.Input
 * @see crc.dps.Processor
 */

public class ToParseTree extends ActiveOutput implements Output {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected ActiveNode root = null;

  /************************************************************************
  ** Methods:
  ************************************************************************/

  public ActiveNode getRoot() { return root; }
  public void setRoot(ActiveNode newRoot) { root = newRoot; setNode(newRoot); }

  /************************************************************************
  ** Construction:
  ************************************************************************/
  public ToParseTree() {
  }

  public ToParseTree(ActiveNode newRoot) {
    setRoot(newRoot);
  }

  public ToParseTree(String tagName) {
    this(new ParseTreeElement(tagName, null));
  }
}
