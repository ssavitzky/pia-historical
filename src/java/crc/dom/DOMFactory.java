// DOMFactory.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

public interface DOMFactory {

  Document          createDocument();
  DocumentContext   createDocumentContext();
  Element           createElement(String tagName, AttributeList attributes);
  Text              createTextNode(String data);
  Comment           createComment(String data);
  PI                createPI(String name, String data);
  Attribute         createAttribute(String name, NodeList value);

};
