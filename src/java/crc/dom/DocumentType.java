// DocumentType.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

public interface DocumentType extends Node {

  void setName(String name);
  String getName();

  void setExternalSubset(NodeList externalSubset);
  NodeList getExternalSubset();

  void setInternalSubset(NodeList internalSubset);
  NodeList getInternalSubset();

  void setGeneralEntities(NamedNodeList generalEntities);
  NamedNodeList getGeneralEntities();
  
  void setNotations(NamedNodeList notations);
  NamedNodeList getNotations();

  void setElementTypes(NamedNodeList elementTypes);
  NamedNodeList getElementTypes();

};
