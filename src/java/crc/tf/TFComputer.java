// TFComputer.java
// $Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


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
