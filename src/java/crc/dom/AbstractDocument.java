// AbstractDocument.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's Document interface. 
 */

package crc.dom;

import java.io.*;











public abstract class AbstractDocument implements Document {

  /**
   * implements DOMFactory interfaces
   */

  /**
   *  DocumentType object provides an interface to access all of the
   *  entity declarations, notation declarations, and all the element type declarations.
   *
   */

  public abstract void setDocumentType(Node documentType);
  public abstract Node getDocumentType();

  public abstract void setDocumentElement(Element documentElement);
  public abstract Element getDocumentElement();

  /**
   * Produces an enumerator which iterates over all of the Element nodes that are
   *  contained within the document whose tagName matches the given name. The iteration
   *  order is a depth first enumeration of the elements as they occurred in the
   *  original document. 
   */
  
  public abstract NodeEnumerator getElementsByTagName(String name);

}




