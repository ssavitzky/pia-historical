////// FromParseTree.java: Input from ParseTree
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.input;

import crc.dps.*;
import crc.dps.util.*;
import crc.dps.active.*;

import crc.dom.Node;
import crc.dom.Element;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Input from a parse tree, comprised entirely of Active nodes.<p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Fromken
 * @see crc.dps.Input
 * @see crc.dps.Processor
 */

public class FromParseTree extends ActiveInput implements Input {

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
  public FromParseTree() {
  }

  public FromParseTree(ActiveNode newRoot) {
    setRoot(newRoot);
  }

  public FromParseTree(String tagName) {
    this(new ParseTreeElement(tagName, null));
  }
}
