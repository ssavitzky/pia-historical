// CRCDocument.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's Document interface. 
 */

package crc.dom;

import java.io.*;

public class CRCDocument extends BasicDocument implements Document {

  /**
   * implements DOMFactory interfaces
   */

  /* documentType provides access to DTD */
  public CRCDocument(){
    setDocumentType( null );
    setDocumentElement( null );
  }

  public CRCDocument(CRCDocument d)
  {
    super( d );
  }

  public Object clone(){
    CRCDocument n = (CRCDocument)super.clone();
    return n;
  }


}




