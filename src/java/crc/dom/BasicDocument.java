// BasicDocument.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's Document interface. 
 */

package crc.dom;

import java.io.*;

public class BasicDocument extends AbstractDocument implements Document {

  /**
   * implements DOMFactory interfaces
   */

  /* documentType provides access to DTD */
  public BasicDocument(){
    setDocumentType( null );
    setDocumentElement( null );
  }

  public BasicDocument(BasicDocument bd)
  {
    if( bd != null ){
      setParent( null );
      setPrevious( null );
      setNext( null );

      //only refers to DTD here
      setDocumentType( bd.getDocumentType() );
      Element rootDoc = bd.getDocumentElement();
      if( rootDoc instanceof AbstractNode ){
	Object cloneroot = ((AbstractNode)rootDoc).clone();
	setDocumentElement( (Element)cloneroot );
      }
      else
	setDocumentElement( rootDoc );
    }
  }


  public Object clone(){
    BasicDocument n = (BasicDocument)super.clone();
    //only refers to DTD here
    n.setDocumentType( getDocumentType() );
    Element rootDoc = getDocumentElement();
    Object cloneroot = ((AbstractNode)rootDoc).clone();
    n.setDocumentElement( (Element)cloneroot );

    return n;
  }


  public void setDocumentType(Node documentType){ this.documentType = documentType; }
  public Node getDocumentType(){ return documentType; }

  public void setDocumentElement(Element documentElement){ this.documentElement = documentElement; }
  public Element getDocumentElement(){ return documentElement; }

  /**
   * Produces an enumerator which iterates over all of the Element nodes that are
   *  contained within the document whose tagName matches the given name. The iteration
   *  order is a depth first enumeration of the elements as they occurred in the
   *  original document. 
   */
  public NodeEnumerator getElementsByTagName(String name)
  {
    if( documentElement != null && name != null )
      return documentElement.getElementsByTagName( name );
    else
      return null;
  }

  /**
   * document DTD.  This could be null for html document 
   * If a document object has a DTD, then attributeDefinitions
   * and entity references can be retrieve through DTD
   */
  protected Node documentType;

  /* root node */
  protected Element documentElement;

}




