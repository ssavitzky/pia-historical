// ChildNodeListEnumerator.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's Document interface. 
 */

package crc.dom;

import java.io.*;

import w3c.dom.Node;
import w3c.dom.NodeEnumerator;


public class ChildNodeListEnumerator implements NodeEnumerator {

  public ChildNodeListEnumerator(ChildNodeList l){
    cursor = 0;
  }

  public Node getFirst(){ return null; }
  public Node getNext(){ return null; }
  public Node getPrevious(){ return null; }
  public Node getLast(){ return null; }

  public Node getCurrent(){ return null; }

  // The rationale for their existence is that the enumerator may be used
  // internally to a method, which may return some interesting value, and
  // therefore cannot also indicate whether the start or end of enumeration
  // was reached.  Any of the traversal methods affects the state, and
  // so are not suitable for usage as predicates (unless possible state
  // manipulation is acceptable).

  public boolean atStart(){ return false; }
  public boolean atEnd(){ return false; }

  protected int cursor;

}




