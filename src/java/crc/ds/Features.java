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

  /**
   * Assert a named feature, with a value. 
   */
  public Object assert(String feature, Object v){
    Object value;

    if(v==null){
      value = new Boolean( true );
      featureTable.put( feature, value );
    }else if( v instanceof String && v == "" ){
      value = new Boolean( false );
      featureTable.put( feature, value );
    }else {
        featureTable.put( feature, v );
	value = v;
    }
    return value;
  }

  /**
   * Assert a named feature. 
   */
  public Object assert(String feature){
    Object value = new Boolean( true );

    featureTable.put( feature, value );
    return value;
  }


  /**
   *Deny a named feature, i.e. assign it a value of false
   */
  public boolean deny(String feature){
    featureTable.put( feature, new Boolean( false ) );
    return false;
  }

  /**
   *Test for the presence of a named feature
   */
  public boolean has(String feature){
    return featureTable.containsKey( feature );
  }

  /**
   * Test a named feature and return a boolean.
   */
  public boolean test(String feature, Object parent){
    Object value = featureTable.get( feature );
    
    if (value == null) value = compute(feature, parent);

    return test(value);
  }

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

  /**
   *  Return the raw value of the feature.
   */
  public Object feature( String featureName, Object parent ){
    Object val ;

    val = featureTable.get( featureName );
    if ( val == null )
      return compute(featureName, parent); 
    return val;
    
  }


  /**
   * Associate a named feature with an arbitrary value.
   *
   */
  public Object setFeature( String featureName, Object value ){
    return assert( featureName, value );
    
  }


  /**
   * Compute and assert the value of the given feature.
   * Can be used to recompute features after changes
   */
  public Object compute(String feature, Object parent){
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

  /**
   * Matching.
   *
   *	The match criteria are a list (not a hash, because order might be
   *	significant), of sublists or name=>value pairs.
   *
   *		[criteria]	ORed, i.e. fail if not matched.
   *		x => bool	fail if !test(x) != !b
   *		x => \&subr	fail if &subr(test(x)) returns false
   *		x => \$var	$var = test(x)
   *
   * === maybe 	x => ["op" value]	comparison
   *		x => [value] 		eq
   *
   *            x => [subr args...] (splice test(x) in as first arg.)
   */
  public boolean matches ( Vector criteria, Object parent ) throws NullPointerException{
    int i;
    if( criteria == null ) throw new NullPointerException("bad criteria.");

    for( i = 0; i < criteria.size(); i++){
      Object o = criteria.elementAt( i );

      if( o instanceof String ){
	String c = (String)o;
	boolean feature = test( c, parent );
	
	Object ov = criteria.elementAt( ++i );
	if( ov instanceof Boolean ){
	  Boolean value = (Boolean) ov;
	  if (!feature != !value.booleanValue())
	    return false;
	}
      }

    }
    return true;
  }


  
}









