// ArrayNodeList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;











import java.util.Vector;

/**
 * Mutable node collection.  This list could be used
 * to return accmulated links in a document.  Each node
 * in this collection bears no relation to each other.
 */

public class ArrayNodeList extends Vector implements EditableNodeList {

  public ArrayNodeList(){ nodeCollection = null; }
  public ArrayNodeList(NodeList l){ nodeCollection = null; }

  public void replace(long index,Node replacedNode) 
       throws NoSuchNodeException{}

  public void insert(long index,Node newNode) 
       throws NoSuchNodeException{}

  public Node remove(long index)
       throws NoSuchNodeException{ return null; }

  public NodeEnumerator getEnumerator(){ return null; }
  public Node item(long index)
       throws NoSuchNodeException{ return null; }
  public long getLength(){ return 0; }
  

  protected Vector nodeCollection;
}

