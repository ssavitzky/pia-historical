// ModelGroup.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

public interface ModelGroup extends Node {
  
  // The ints for the following two methods should
  // be constants defined in the ConnectionType class.

  void setConnector(int connector);
  int getConnector();

  // The ints for the two methods below should be
  // constants defined in the OccurrenceType class.

  void setOccurrence(int occurrence);
  int getOccurrence();

  void setTokens(NodeList tokens);
  NodeList getTokens();

};
