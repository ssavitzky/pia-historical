// ElementToken.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

public interface ElementToken extends Node {

  void setName(String name);
  String getName();

  // The ints for the following two methods should be
  // constants defined in the OccurrenceType class.

  void setOccurrence(int occurrence);
  int getOccurrence();

};
