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

  public DOMFactory getFactory(){ return null; }

  /** Return the CRCDOM's only instance.  */
  public static CRCDOM instance() {
    if( instance != null )
      return instance;
    else{
      String[] args = new String[1];
      args[0] = HTML;
      makeInstance(args);
      return instance;
    }
  }

  /** Create the CRCDOM's single instance. */ 
  private CRCDOM() {
    instance = this;
  }

  /** Make a new instance */
  private static void makeInstance(String[] args){
    String docType = null;

    /* Create a CRCDOM instance */

    CRCDOM crcdom = new CRCDOM();
    crcdom.commandLine = args;
    docType = args[0];

    if( docType.equalsIgnoreCase(HTML) ){
      factory = new CRCDOMFactory();
    }else{
      System.out.println("Can not create factory because of unkown document type.");
      System.exit(1);
    }
  }

  /** The command-line options passed to Java on startup.
   */
  private String[] commandLine;

  /* reference to instance */
  private static CRCDOM    instance	= null;

  /* reference to instance of dom factory */
  private static AbstractDOMFactory factory = null;

  /* kind of factory needed */
  private static String HTML = "HTML";

  
}




