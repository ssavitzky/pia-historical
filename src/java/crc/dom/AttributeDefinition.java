// AttributeDefinition.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

import java.util.Vector;

public interface AttributeDefinition extends Node {

  void setName(String name);
  String getName();

  void setAllowedTokens(Vector allowedTokens);
  Vector getAllowedTokens();

  // The ints for the following two methods should be
  // constants declared in the DeclaredValueType class.

  void setDeclaredType(int declaredType);
  int getDeclaredType();

  // The ints for the following two methods should be
  // constants declared in the DefaultValueType class.

  void setDefaultType(int defaultType);
  int getDefaultType();

  void setDefaultValue(NodeList defaultValue);
  NodeList getDefaultValue();

};
