// AbstractDOMFactory.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's DOMFactory interface. 
 */

package crc.dom;

import java.io.*;






















public abstract class AbstractDOMFactory implements DOMFactory {

  /**
   * implements DOMFactory interfaces
   */
  public abstract Document          createDocument();
  public abstract DocumentContext   createDocumentContext();
  public abstract Element           createElement(String tagName, AttributeList attributes);
  public abstract Text              createTextNode(String data);
  public abstract Comment           createComment(String data);
  public abstract PI                createPI(String name, String data);
  public abstract Attribute         createAttribute(String name, NodeList value);

}




