// CRCDOM.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's DOM interface. 
 */

package crc.dom;

import java.io.*;



public class CRCDOM implements DOM {

  /**
   * give a CRCDOMFactory.  In the future will have
   * parameter to indicate which factory is desired.
   */

  public DOMFactory getFactory(){ return instance().factory; }

  /** Return the CRCDOM's only instance.  */
  public static CRCDOM instance() {
    if( instance != null )
      return instance;
    else{
      makeInstance();
      return instance;
    }
  }

  /** Create the CRCDOM's single instance. */ 
  private CRCDOM() {
    instance = this;
  }

  /** Make a new instance */
  private static void makeInstance(){
    /* Create a CRCDOM instance */

    CRCDOM crcdom = new CRCDOM();
    factory = new CRCDOMFactory();
  }

   /* reference to instance */
  private static CRCDOM    instance	= null;

  /* reference to instance of dom factory */
  private static CRCDOMFactory factory = null;
}




