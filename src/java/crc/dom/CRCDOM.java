// CRCDOM.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's DOM interface. 
 */

package crc.dom;

import java.io.*;
import crc.ds.Table;


public class CRCDOM extends DOM {

  /**
   * give a CRCDOMFactory.  
   */

  public static DOMFactory getFactory()
  {
    Object df = instance().factoryTable.get(DOM.CRCFACTORY);
    if ( df != null )
      return (DOMFactory)df; 
    else
      return null;
  }

  /**
   * Return desire factory.  If null, CRCFACTORY is returned.
   */
  public static DOMFactory getFactory(String whichFactory)
  {
    Object df;

    if( whichFactory == null )
      df = instance().factoryTable.get(DOM.CRCFACTORY);
    else
      df = instance().factoryTable.get( whichFactory );
 
    if ( df != null )
	return (DOMFactory)df; 
    else
      return null;
  }


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
    crcdom.factoryTable.at( DOM.BASICFACTORY, new BasicDOMFactory() );
    crcdom.factoryTable.at( DOM.CRCFACTORY, new CRCDOMFactory() );
  }

   /* reference to instance */
  private static CRCDOM    instance	= null;

  /**
   * Associations of mime-types to content-type names
   */
  protected static Table factoryTable = new Table();
}




