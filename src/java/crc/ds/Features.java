// Features.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/**
 *	A Features list is attached to a Thing when it is first 
 *	needed.  It is used to perform lazy evaluation of named
 *	features.  The association between names and functions is made
 *	using a hash; a reference to this is returned by $f->computers, 
 *	which returns a reference to a class-specific hash that
 *	associates feature names with functions.
 *
 *	The Features list is normally accessed only by a method on
 *	its parent:  $something->is('feature').  Note that there is
 *	no backlink to the parent from its features; this makes
 *	objects easier to delete by avoiding circularities.
 *
 *	Features may actually have any value, but the matching process
 *	is normally only interested in truth value.  Some agents,
 *	however, make use of feature values.  In particular, 'agent'
 *	binds to the name of the agent at which a request is directed.
 */

package crc.ds;

import java.util.Hashtable;
import java.util.Vector;
import crc.pia.Transaction;
import crc.pia.Agent;
import crc.tf.UnknownNameException;

public class Features{
  /**
   * Attribute index - feature table
   * 
   */
  protected Hashtable featureTable;

  /**
   * Compute and assert the value of some initial features.
   *    We do this here because the features are closely related, so
   *    we can get many assertions out of a small number of requests.  
   */
  protected void initialize( Object parent ){
    
    featureTable.put( "NEVER", new Boolean( false ) );
    
    /*
    * NEVER is useful for creating things that never match.
    *	  A null criteria list will always match, so there's no need
    *	  for an ALWAYS.
    */
  }

  /************************************************************************
  ** Setting Feature Values:
  ************************************************************************/

  /**
   * Assert a named feature, with a value. 
   */
  public Object assert(String feature, Object v){
    Object value;

    if (v == null) {
      value = new Boolean( true );
      featureTable.put( feature, value );
    } else if (v instanceof String && "".equals(v)) {
      value = new Boolean( false );
      featureTable.put( feature, value );
    } else {
        featureTable.put( feature, v );
	value = v;
    }
    return value;
  }

  /**
   * Assert a named feature, i.e. assign it a value of true. 
   */
  public Object assert(String feature){
    Object value = new Boolean( true );

    featureTable.put( feature, value );
    return value;
  }


  /**
   * Deny a named feature, i.e. assign it a value of false
   */
  public boolean deny(String feature){
    featureTable.put( feature, new Boolean( false ) );
    return false;
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
  public final boolean test(String feature, Object parent){
    Object value = featureTable.get( feature );
    
    if (value == null) value = compute(feature, parent);

    return test(value);
  }

  /**
   *  Return the raw value of the feature.
   */
  public final Object feature( String featureName, Object parent ){
    Object val = featureTable.get(featureName);
    if (val == null)
      return compute(featureName, parent); 
    return val;
  }


  /**
   *  Return the raw value of the feature.
   */
  public final Object getFeature( String featureName, Object parent ){
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
  public final Object compute(String feature, Object parent){
    Object val;

    try{
      if( parent instanceof Transaction )
	val = ((Transaction) parent).computeFeature( feature );
      else
	val = ((Agent) parent).computeFeature( feature );
      return setFeature( feature, val );
    }catch(UnknownNameException e){
      return assert(feature, "");
    }
  }


  /************************************************************************
  ** Matching:
  ************************************************************************/

  /** Test a value.  
   *	@return false for null, False, and "", true otherwose.
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

  /** Matching.  Returns true if the list of criteria matches the parent. */
  public boolean matches (Criteria criteria, Object parent) {
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
    initialize( parent );
   }

  public Features() {
    featureTable = new Hashtable();
    initialize(null);
  }

}









