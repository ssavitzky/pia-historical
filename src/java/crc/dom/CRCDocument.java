// CRCDocument.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's Document interface. 
 */

package crc.dom;

import java.io.*;

public class CRCDocument extends AbstractDocument implements Document {

  /**
   * implements DOMFactory interfaces
   */

  /* documentType provides access to DTD */
  public CRCDocument(){
    setDocumentType( null );
    setDocumentElement( null );
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
  public NodeEnumerator getElementsByTagName(String name){ return null; }

  /* implementing Node interface */
  public int getNodeType(){ return NodeType.DOCUMENT; }
  public Node getParentNode(){ return null; }
  public NodeList getChildren(){ return null; }
  public boolean hasChildren(){ return false; }
  public Node     getFirstChild(){ return null; }
  public Node     getPreviousSibling(){ return null; }
  public Node     getNextSibling(){ return null; }

  public void insertBefore(Node newChild, Node refChild)
       throws NotMyChildException{}

  public Node replaceChild(Node oldChild, Node newChild)
       throws NotMyChildException{ return null; }

  public Node removeChild(Node oldChild)
       throws NotMyChildException{ return null; }


  /**
   * document DTD.  This could be null for html document 
   * If a document object has a DTD, then attributeDefinitions
   * and entity references can be retrieve through DTD
   */
  protected Node documentType;

  /* root node */
  protected Element documentElement;

}




