// HasFeatures.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/** HasFeatures is the interface for objects that have an associated Features
 *	object. <p>
 */

package crc.ds;


import crc.ds.Features;
import crc.ds.Criteria;

public interface HasFeatures {

  /************************************************************************
  ** Access to features:
  ************************************************************************/

  /**
   * return the Features object.
   */
  public Features features();

  /**
   * Get the value of the named feature.  If does not exist,
   * compute it and return the value
   */
  public Object getFeature( String name );

  /**
   * Get the value of the named feature as a string.  If does not exist,
   * compute it and return the value.
   */
  public String getFeatureString( String name );

  /**
   * Test a named feature and return a boolean.
   */
  public boolean test( String name );

  /**
   * Compute and assert the value of the given feature.
   * Can be used to recompute features after changes
   */
  public Object compute( String name );

  /**
   * assert a given feature with a value of true
   */
  public void assert( String name );

  /**
   * assert a given feature with the given value
   */
  public void assert( String name, Object value );

  /**
   * deny a given feature (i.e. give it a value of false).
   */
  public void deny( String name );

  /**
   * Test to see if the transaction has this feature.
   * @param name feature name
   * @return true if it does
   */
  public boolean has( String name );

  /**
   * Compute a feature.  Return null if there is no way to compute it.
   * @param featureName the name of the feature
   */
  public Object computeFeature( String featureName );

  /**
   * Test whether a list of Criteria matches this object's Features.
   * @param criteria a Criteria list.
   * @return true if there is a match
   */
  public boolean matches(Criteria criteria);
}
