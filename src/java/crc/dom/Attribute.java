// Attribute.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

public interface Attribute extends Node {

  void setName(String name);
  String getName();

  void setValue(NodeList value);
  NodeList getValue();

  void setSpecified(boolean specified);
  boolean getSpecified();

  // provides a connection to the DTD 
  // attribute Node definition;

  String toString();

};


