////// StringCriterion.java:  Match a string.
//	$Id$

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


package crc.ds;

/** 
 *  Match a feature exactly by value.  Null matches null (missing) features, 
 *	as well as features with the value (Boolean)False and "".
 */
public class ValueCriterion extends Criterion {

  Object value;
 
  /** Return the value that this criterion matches. */
  public final Object value() {
    return value;
  }

  /** Convert to a string. */
  public String toString() {
    return super.toString() + "=" + ((value == null) ? value : "");
  }

  /** Match the feature's value.  The default is to match if the
   *	feature has a non-null value that exactly matches.  A null value
   *	will match any object for which Features.test returns false. */
  public boolean match(Object s) {
    if (value == null) return !Features.test(s) ^ negate;
    else return ((s != null) && s.equals(value)) ^ negate;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public ValueCriterion(String nm, Object v) {
    super(nm);
    value = v;
  }

}
