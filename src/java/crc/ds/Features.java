// Features.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

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
import java.util.Vector;
import crc.pia.Transaction;
import crc.pia.Agent;

import crc.ds.HasFeatures;
import crc.ds.Criterion;

public class Features {

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
    return assert(feature, new Boolean(true));
  }


  /**
   * Deny a named feature, i.e. assign it a value of false
   */
  public Object deny(String feature){
    return assert(feature, new Boolean(false));
  }

  /************************************************************************
  ** Computing Feature Values:
  ************************************************************************/

  /**
   *Test for the presence of a named feature
   */
  public final boolean has(String feature){
    return featureTable.containsKey( feature );
  }

  /**
   * Test a named feature and return a boolean.
   */
  public final boolean test(String feature, HasFeatures parent){
    Object value = featureTable.get( feature );
    
    if (value == null) value = compute(feature, parent);

    return testCannonical(value);
  }

  /**
   *  Return the raw value of the feature.
   */
  public final Object feature( String featureName, HasFeatures parent ){
    Object val = featureTable.get(featureName);
    if (val == null)
      return compute(featureName, parent); 
    return val;
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
    Object val;

    try {
      val = parent.computeFeature( feature );
      return setFeature( feature, val );
    }catch(Exception e){
      return deny(feature);
    }
  }


  /************************************************************************
  ** Cannonical Form:
  ************************************************************************/

  public static final Boolean True       = new Boolean(true);
  public static final Boolean False      = new Boolean(false);
  public static final Object  Nil        = new Object();
  public static final String  NullString = "";

  /** Convert name to cannonical form. */
  public static final String cannonicalName(String name) {
    return crc.interform.Util.javaName(name);
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
    if (value == null) return false;
    else if (value instanceof Boolean) {
      return ((Boolean)value).booleanValue();
    } else if (value instanceof String) {
      return ! "".equals(value);
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
   * Create a new Features for a Transaction.  Compute a few features that
   * are closely related and that we know are always needed.
   */

  public Features (Transaction parent) {
    featureTable = new Hashtable();
    parent.setFeatures( this );
   }

  public Features() {
    featureTable = new Hashtable();
  }

}









