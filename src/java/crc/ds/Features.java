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

//import Thing;
//import UnaryFunctor;

public class Features{
  /**
   * Attribute index - feature table
   * 
   */
  protected HashTable featureTable;

  /**
   * Compute and assert the value of some initial features.
   *    We do this here because the features are closely related, so
   *    we can get many assertions out of a small number of requests.  
   */
  protected void initialize( Thing parent ){
    
    featureTable.put( "NEVER", false );
    
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

    if(!v){
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
   * Test a named feature and return its value.
   */
  public boolean test(String feature, Thing parent){
    Object value = featureTable.get( feature );

    if( value == null )
      value = compute(feature, parent);

    return !!value;
  }

  /**
   * @return the value associated with a named feature. 
   */

  public Object getFeature( String featureName ){
    Object val ;

    val = featureTable.get( feature );
    if ( val == null )
      return compute(feature, parent); 
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
  public Object compute(String feature, Thing parent){
    try{
      Object val = parent.computeFeature( feature );
      return setFeature( feature, val );
    }catch(UnknownNameException e){
      assert(feature, "");
    }
  }

  /**
   *Register a subroutine that computes a feature. 
   */
  public void register(String feature, Object sub, Thing parent){

    HashTable computers = parent.featureComputers( this, 1 );
    computers.put( feature, sub );
  }

  /**
   * Create a new FEATURES.  We pass the parent, even though 
   * no link is kept, so that we can compute a few features that
   * are closely related and that we know are always needed.
   */

  public Features( Thing parent ){
    featureTable = new HashTable();
    parent.features( this );
    initialize( parent );
   }

  /**
   *Matching.
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
  public boolean matches ( Vector criteria ) throws badcriteria{
    int i;
    if( criteria == null ) throw badcriteria;

    for( i = 0; i < criteria.size(); i++){
      Object o = criteria.elementAt( i );

      if( o instanceof String ){
	String c = (String)o;
	boolean feature = test( c );
	
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









