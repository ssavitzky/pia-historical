// Element.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

public interface Element extends Node {

  void setTagName(String tagName);
  String getTagName();

  void setAttributes(AttributeList attributes);
  AttributeList getAttributes();

  void setAttribute(Attribute newAttr);

  NodeEnumerator getElementsByTagName(String name);

};
