// Document.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

/**
 *The Document object represents the entire HTML or XML document. Conceptually, it is
 *the root of the document tree, and provides the primary access to the document's
 *data. 
 * Node documentType --For XML, this provides access to the Document Type Definition (see
 * DocumentType) associated with this XML document. For HTML documents and XML
 * documents without a document type definition this returns the value null. 
 *
 * Element documentElement -- The element that's the root element for the given document. For HTML, this will
 * be an Element instance whose tagName is "HTML"; for XML this is the outermost
 * element, i.e. the element non-terminal in production [41] in Section 3 of the
 * XML-lang specification. 
 */
public interface Document extends Node {

  void setDocumentType(Node documentType);
  Node getDocumentType();

  void setDocumentElement(Element documentElement);
  Element getDocumentElement();

  /**
   * Produces an enumerator which iterates over all of the Element nodes that are
   * contained within the document whose tagName matches the given name. The iteration
   * order is a depth first enumeration of the elements as they occurred in the
   * original document. 
   */
  NodeEnumerator getElementsByTagName(String name);

};
