// TFCWrapper.java
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
