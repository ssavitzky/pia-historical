// Features.java
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


/** A cache for lazy evaluation of named values.  A Features is
 *	attached to a Transaction or other implementation of the
 *	Featured interface.  The association between names and
 *	functions is usually made using a class-specific hash that
 *	associates feature names with functors; it is class-specific
 *	so that every implementation of HasFeatures can have its own
 *	interface or superclass for feature-computers.<p>
 *
 *	The Features is normally accessed only by a method on its
 *	parent.  Note that there is no backlink to the parent from its
 *	features; this makes objects easier to delete by avoiding
 *	circularities.<p>
 *
 *	Features may actually have any value, but the matching process
 *	is normally only interested in truth value.  Some agents,
 *	however, make use of feature values.  In particular, 'agent'
 *	binds to the name of the agent at which a request is directed.  */

package crc.ds;

import java.util.Hashtable;
import java.lang.String;

import crc.ds.HasFeatures;
import crc.ds.Criterion;

public class Features implements java.io.Serializable {

  /**
   * Attribute index - feature table
   */
  protected Hashtable featureTable;


  /************************************************************************
  ** Setting Feature Values:
  ************************************************************************/

  /**
   * Assert a named feature, with a value.  Uses
   *	<code>cannonicalName<code> to convert the feature name
   *	to a cannonical form shared by each Criterion; this avoids having
   *	to convert names when testing multiple times.  Similarly, values
   *	(particularly null values) are converted to a cannonical form to 
   *	speed up testing for truth value.
   */
  public Object assert(String feature, Object value){
    value = cannonicalValue(value);
    feature = cannonicalName(feature);
    featureTable.put( feature, value );
    return value;
  }

  /**
   * Assert a named feature, i.e. assign it a value of true. 
   */
  public Object assert(String feature){
    return assert(feature, True);
  }


  /**
   * Deny a named feature, i.e. assign it a value of false
   */
  public Object deny(String feature){
    return assert(feature, False);
  }

  /************************************************************************
  ** Computing Feature Values:
  ************************************************************************/

  /**
   * Test for the presence of a named feature
   */
  public final boolean has(String feature){
    return featureTable.containsKey( feature );
  }

  /**
   * Test a named feature and return a boolean.
   */
  public final boolean test(String feature, HasFeatures parent){
    return testCannonical(getFeature(feature, parent));
  }

  /**
   *  Return the raw value of the feature.
   */
  public final Object feature( String featureName, HasFeatures parent ){
    Object val = featureTable.get(featureName);
    return (val == null)? compute(featureName, parent) : val;
  }


  /**
   *  Return the raw value of the feature.
   */
  public final Object getFeature( String featureName, HasFeatures parent ){
    Object val = featureTable.get(featureName);
    if (val == null)
      return compute(featureName, parent); 
    return val;
  }


  /**
   * Associate a named feature with an arbitrary value.
   */
  public Object setFeature( String featureName, Object value ){
    return assert( featureName, value );
  }


  /**
   * Compute and assert the value of the given feature.
   * Can be used to recompute features after changes
   */
  public final Object compute(String feature, HasFeatures parent){
    return setFeature(feature, parent.computeFeature(feature));
  }


  /************************************************************************
  ** Cannonical Form:
  ************************************************************************/

  public static final Boolean True       = new Boolean(true);
  public static final Boolean False      = new Boolean(false);
  public static final Object  Nil        = crc.ds.Nil.value;
  public static final String  NullString = "";

  /** Convert name to cannonical form. */
  public static final String cannonicalName(String name) {
      return name.toLowerCase();
  }

  /** Convert value to cannonical form. */
  public static final Object cannonicalValue(Object value) {
    if (value == null) return Nil;
    else if (value instanceof Boolean) {
      return ((Boolean)value).booleanValue()? True : False;
    } else if (value instanceof String && "".equals((String)value)) {
      return NullString;
    } else return value;
  }

  /************************************************************************
  ** Testing for Truth:
  ************************************************************************/

  /** Test an arbitrary value.  
   *	@return false for null, False, and "", true otherwise.
   */
  public static final boolean test(Object value) {
    if (value == null || value == Nil) return false;
    else if (value instanceof Boolean) {
      return ((Boolean)value).booleanValue();
    } else if (value instanceof String) {
      return ! ("".equals(value) || "0".equals(value));
    } else {
      return true;
    }
  }

  /** Test a value known to be in cannonical form.
   *	@return false for null, False, and "", true otherwise.
   */
  public static final boolean testCannonical(Object value) {
    return !(value == Nil || value == NullString || value == False);
  }

  /************************************************************************
  ** Matching:
  ************************************************************************/

  /** Matching.  Returns true if the list of criteria matches the parent. */
  public boolean matches (Criteria criteria, HasFeatures parent) {
    return criteria.match(this, parent);
  }
  

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /**
   * Create a new Features for a Transaction.
   */
  public Features() {
    featureTable = new Hashtable();
  }

}
