// Node.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

public interface Node {

  // getNodeType() returns one of the NodeType constants
  int getNodeType();

  Node     getParentNode();
  NodeList getChildren();
  boolean  hasChildren();
  Node     getFirstChild();
  Node     getPreviousSibling();
  Node     getNextSibling();

  void insertBefore(Node newChild, Node refChild)
    throws NotMyChildException;

  Node replaceChild(Node oldChild, Node newChild)
    throws NotMyChildException;

  Node removeChild(Node oldChild)
    throws NotMyChildException;
};



