// ChildNodeList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;

import w3c.dom.Node;
import w3c.dom.NodeList;
import w3c.dom.NodeEnumerator;
import w3c.dom.NoSuchNodeException;

/**
 * This list expands a children collection through the start node.
 */

public class ChildNodeList implements NodeList {

  public ChildNodeList(){}
  public ChildNodeList(Node startNode){}

  public NodeEnumerator getEnumerator(){ return null; }

  public Node item(long index)
       throws NoSuchNodeException{ return null; }

    /**
     * walk till end of child
     */
  public long getLength(){ return 0; }

}
