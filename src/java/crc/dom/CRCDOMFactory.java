// CRCDOMFactory.java
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


public abstract class CRCDOMFactory extends AbstractDOMFactory {

  public CRCDOMFactory(){}

  /**
   * implements DOMFactory interfaces
   */
  public abstract Document createDocument();
  public abstract DocumentContext   createDocumentContext();
  public Element           createElement(String tagName, AttributeList attributes){ return null; }
  public Text              createTextNode(String data){ return null; }
  public Comment           createComment(String data){ return null; }
  public PI                createPI(String name, String data){ return null; }
  public Attribute         createAttribute(String name, NodeList value){ return null; }

}




