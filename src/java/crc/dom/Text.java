// Text.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

public interface Text extends Node {

  void setData(String data);
  String getData();

  void setIsIgnorableWhitespace(boolean isIgnorableWhitespace);

  boolean getIsIgnorableWhitespace();

};
