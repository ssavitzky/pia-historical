// IsRequest.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

 
package crc.tf;

import crc.pia.Transaction;
import crc.tf.TFComputer;

public final class IsRequest extends TFComputer {

  /**
   * Is this a request transaction
   * @param o Transaction 
   * @return true if this transaction is a request
   */
    public Object  computeFeature(Transaction trans) {
      return trans.isRequest() ? True : False;
    }
}

