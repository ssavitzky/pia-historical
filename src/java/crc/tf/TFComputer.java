// TFComputer.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/**
 * Superclass for functors that compute features of a Transaction.
 *	The default action is to return null, signifying a non-existant
 *	feature.
 */

package crc.tf;

import crc.pia.Transaction;
import crc.ds.Features;

public class TFComputer {

  protected static final Boolean True = Features.True;
  protected static final Boolean False = Features.False;

  /** Compute the value corresponding to the given feature.  
   */
  public Object computeFeature(Transaction parent) {
    return null;
  }

  /** Feature name.  Normally unused. */
  protected String name = null;

  /** Retrieve the feature name. */
  public String name() {
    return name;
  }

  public TFComputer() {}

  protected TFComputer(String name) {
    this.name = name;
  }

  /** Computer for features without a defined computer.  (This is a little 
   *	twisted, but the idea is to always return a valid computer from
   *	<code>Registry.calculatorFor</code>, and store it in the table.
   */
  public static final TFComputer UNDEFINED = new TFComputer("UNDEFINED");
}
