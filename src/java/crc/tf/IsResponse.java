// IsResponse.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

 
package crc.tf;

import crc.ds.UnaryFunctor;
import crc.pia.Transaction;

public final class IsResponse implements UnaryFunctor{

  /**
   * Is this a response transaction
   * @param o Transaction 
   * @return true if this is a reponse transaction
   */
    public Object execute( Object o ){
      Transaction trans = (Transaction) o;
      return new Boolean( trans.isResponse() );
    }
}





