// CRCDOMFactory.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's DOMFactory interface. 
 */

package crc.dom;

import java.io.*;

public class CRCDOMFactory extends AbstractDOMFactory {

  public CRCDOMFactory(){}

  /**
   * implements DOMFactory interfaces
   */
  public Document createDocument(){
    //Report.debug("Creating CRCDocument...");
    return new CRCDocument();
  }

  public DocumentContext   createDocumentContext(){ return null; }

  /**
   *Create a CRCElement based on the tagName. Note that the instance returned may
   *implement an interface derived from Element. The attributes parameter can be
   *null if no attributes are specified for the new Element. 
   */
  public Element           createElement(String tagName, AttributeList attributes){
    if( tagName == null ) return null;
    Element e = new CRCElement();
    e.setTagName( tagName );
    if ( attributes != null )
      e.setAttributes( attributes );
    return e;
  }

  public Text              createTextNode(String data){ return null; }
  public Comment           createComment(String data){ return null; }
  public PI                createPI(String name, String data){ return null; }

  /** create a CRCAttribute which has a reference to AttributeDefinition */
  public Attribute         createAttribute(String name, NodeList value){
    if( name == null ) return null;
    return new CRCAttribute( name, value );
  }

}




