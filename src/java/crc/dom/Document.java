// Document.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

public interface Document extends Node {

  void setDocumentType(Node documentType);
  Node getDocumentType();

  void setDocumentElement(Element documentElement);
  Element getDocumentElement();

  NodeEnumerator getElementsByTagName(String name);

};
