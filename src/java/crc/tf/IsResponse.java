// IsResponse.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

 
package crc.tf;

import crc.ds.UnaryFunctor;
import crc.pia.Transaction;

import crc.tf.TFComputer;

public final class IsResponse extends TFComputer {

  /**
   * Is this a response transaction
   * @return true if this is a reponse transaction
   */
    public Object  computeFeature(Transaction trans) {
      return trans.isResponse() ? True : False;
    }
}





