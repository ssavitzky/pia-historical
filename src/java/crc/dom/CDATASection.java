// CDATASection.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

public interface CDATASection extends Node {

  void setContent(String content);
  String getContent();

};