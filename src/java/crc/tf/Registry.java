// Registry.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/**
 * A registry of calculators for computing features.
 *
 */

package crc.tf;

import crc.tf.UnknownNameException;

public class Registry{
  /**
   * Store previously created feature calculators
   */
  protected static HashTable calcTable = new HashTable();

  /**
   * Given a featurName, returns the corresponding feature calculator.
   * Create one if none existed.
   */
  public static Object calculatorFor( String featureName ) throws UnknownNameException{
    Object calc;

    calc = calcTable.get( featureName );
    if( calc != null ) return calc;
    else{
      try{
	calc = Class.forName( featureName ).newInstance() ;
	calcTable.put( featureName, calc );
	return calc;
      }catch(Exception e){
	String err = ("Unable to create calculator of class ["
			      + featureName +"]"
			      + "\r\ndetails: \r\n"
			      + e.getMessage());
		throw new UnknownName(err);
      }
    }

  }
}

