// NamedNodeList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;


public interface NamedNodeList {

  // Core get and set public interface. Note that implementations may
  // build the list lazily

  Node getNode(String name);
  Node setNode(String name, Node node);
 
  Node remove(String  name) 
    throws NoSuchNodeException;
 
  Node item(long index)
    throws NoSuchNodeException;
 
  long getLength();
 
  NodeEnumerator getEnumerator();

};
