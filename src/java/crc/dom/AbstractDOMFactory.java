// AbstractDOMFactory.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's DOMFactory interface. 
 */

package crc.dom;

import java.io.*;

import w3c.dom.DOMFactory;
import w3c.dom.Document;
import w3c.dom.DocumentContext;
import w3c.dom.Element;
import w3c.dom.Text;
import w3c.dom.Comment;
import w3c.dom.PI;
import w3c.dom.Attribute;
import w3c.dom.AttributeList;
import w3c.dom.NodeList;

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




