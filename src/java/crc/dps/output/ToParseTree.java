////// ToParseTree.java: Token output Stream to ParseTree
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

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
