// BasicDOMFactory.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's DOMFactory interface. 
 */

package crc.dom;

import java.io.*;

public class BasicDOMFactory extends AbstractDOMFactory {

  public BasicDOMFactory(){}

  /**
   * implements DOMFactory interfaces
   */
  public Document createDocument(){
  Report.debug("createDocument");
    return new AppDocument();
  }

  public DocumentContext   createDocumentContext(){ return null; }

  public Element           createElement(String tagName, AttributeList attributes){
    if( tagName == null ) return null;
    Element e = new BasicElement();
    e.setTagName( tagName );
    if ( attributes != null )
      e.setAttributes( attributes );
  }

  public Text              createTextNode(String data){ return null; }
  public Comment           createComment(String data){ return null; }
  public PI                createPI(String name, String data){ return null; }

  /** create a basic attribute */
  public Attribute         createAttribute(String name, NodeList value){
    if( name == null ) return null;
    return new BasicAttr( name, value );
  }

}




