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
    //Report.debug(this,"createDocument");
    return new BasicDocument();
  }

  public DocumentContext   createDocumentContext(){ return null; }

  /**
   *Create an element based on the tagName. Note that the instance returned may
   *implement an interface derived from Element. The attributes parameter can be
   *null if no attributes are specified for the new Element. 
   */
  public Element           createElement(String tagName, AttributeList attributes){
    //Report.debug(this,"createElement...");
    if( tagName == null ) return null;
    Element e = new BasicElement();
    e.setTagName( tagName );
    if ( attributes != null ) 
      e.setAttributes( attributes );
    return e;
  }

  /**
   *  Create a Text node given the specified string. 
   */
  public Text              createTextNode(String data){
    //Report.debug(this,"createTextNode...");
    return new BasicText( data ); 
  }

  /**
   *  Create a Comment node given the specified string. 
   */
  public Comment           createComment(String data){
    //Report.debug(this,"createComment...");
    return new BasicComment( data );
  }

  /**
   *  Create a PI node with the specified name and data string.
   */
  public PI                createPI(String name, String data){
    //Report.debug(this,"createPI...");
    return new BasicPI( name, data );
  }

  /**
   *Create an Attribute of the given name and specified value. Note that the
   *Attribute instance can then be set on an Element using the setAttribute method.
   */
  public Attribute         createAttribute(String name, NodeList value){
    //Report.debug(this,"createAttribute...");
    if( name == null ) return null;
    return new BasicAttribute( name, value );
  }

}




