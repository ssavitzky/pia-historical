// CRCDOM.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's DOM interface. 
 */

package crc.dom;

import java.io.*;

import w3c.dom.DOM;
import w3c.dom.DOMFactory;

public class CRCDOM implements DOM {

  /**
   * give a CRCDOMFactory.  In the future will have
   * parameter to indicate which factory is desired.
   */

  public DOMFactory getFactory(){ return null; }

}




