// Document.java
// $Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/

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
