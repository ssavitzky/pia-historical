// NodeList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

public interface NodeList {

  NodeEnumerator getEnumerator();

  Node item(long index)
    throws NoSuchNodeException;

  long getLength();

};
