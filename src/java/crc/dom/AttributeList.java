// AttributeList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

public interface AttributeList {

  Attribute getAttribute(String name);
  Attribute setAttribute(String name, Attribute attr);

  Attribute remove(String name) 
    throws NoSuchNodeException;

  Node item(long index)
    throws NoSuchNodeException;

  long getLength();

};
