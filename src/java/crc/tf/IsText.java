// IsText.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

 
package crc.tf;

import crc.ds.UnaryFunctor;
import crc.pia.Transaction;

public final class IsText implements UnaryFunctor{

  /**
   * Is this transaction's content type is that of "text"
   * @param o Transaction 
   * @return true if content type starts with "text"
   */
    public Object execute( Object o ){
      Transaction trans = (Transaction) o;

      String s = trans.contentType();
      if( s != null ){
	String ls = s.toLowerCase();
	if( ls.startsWith("text") )
	  return new Boolean( true );
      }
      return new Boolean( false );
     
    }
}


