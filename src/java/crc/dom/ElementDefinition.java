// ElementDefinition.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

import java.util.Vector;

public interface ElementDefinition extends Node {

  void setName(String name);
  String getName();

  // The ints for the following two methods should be
  // constants defined in the ContentType class.

  void setContentType(int contentType);
  int getContentType();

  void setContentModel(ModelGroup contentModel);
  ModelGroup getContentModel();

  void setAttributeDefinitions(NamedNodeList attributeDefinitions);
  NamedNodeList getAttributeDefinitions();

  void setInclusions(Vector inclusions);
  Vector getInclusions();

  void setExceptions(Vector exceptions);
  Vector getExceptions();

};
