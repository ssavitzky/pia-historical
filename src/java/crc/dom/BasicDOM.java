// BasicDOM.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's DOM interface. 
 */

package crc.dom;

import java.io.*;

public class BasicDOM implements DOM {

  /**
   * give a BasicDOMFactory.  In the future will have
   * parameter to indicate which factory is desired.
   */

  public DOMFactory getFactory(){ return instance().factory; }

  /** Return the BasicDOM's only instance.  */
  public static BasicDOM instance() {
    if( instance != null )
      return instance;
    else{
      makeInstance();
      return instance;
    }
  }

  /** Create the BasicDOM's single instance. */ 
  private BasicDOM() {
    instance = this;
  }

  /** Make a new instance */
  private static void makeInstance(){
    /* Create a BasicDOM instance */

    BasicDOM basicdom = new BasicDOM();
    factory = new BasicDOMFactory();
  }

   /* reference to instance */
  private static BasicDOM    instance	= null;

  /* reference to instance of dom factory */
  private static BasicDOMFactory factory = null;
}




