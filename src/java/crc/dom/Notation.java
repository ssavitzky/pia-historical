// Notation.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

public interface Notation extends Node {

  void setName(String name);
  String getName();

  void setIsPublic(boolean isPublic);
  boolean getIsPublic();

  void setPublicIdentifier(String publicIdentifier);
  String getPublicIdentifier();

  void setSystemIdentifier(String systemIdentifier);
  String getSystemIdentifier();

};
