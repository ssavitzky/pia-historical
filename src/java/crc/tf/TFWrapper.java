// TFCWrapper.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/**
 * Wrap a UnaryFunctor (legacy code) to turn it into a TFComputer.
 *	This is an interim hack. 
 */

package crc.tf;

import crc.pia.Transaction;
import crc.ds.Features;
import crc.ds.UnaryFunctor;

import crc.tf.TFComputer;

public class TFWrapper extends TFComputer {

  private UnaryFunctor wrapped; 

  /** Compute the value corresponding to the given feature.  
   */
  public Object computeFeature(Transaction parent) {
    return wrapped.execute(parent);
  }

  public TFWrapper(UnaryFunctor f) {
    wrapped = f;
  }
}
