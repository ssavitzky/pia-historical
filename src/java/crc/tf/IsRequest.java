// IsRequest.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

 
package crc.tf;

import crc.ds.UnaryFunctor;
import crc.pia.Transaction;

public final class IsRequest implements UnaryFunctor{

  /**
   * Is this a request transaction
   * @param o Transaction 
   * @return true if this transaction is a request
   */
    public Object execute( Object o ){
      Transaction trans = (Transaction) o;
      return new Boolean( trans.isRequest() );
    }
}

