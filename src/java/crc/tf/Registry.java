// Registry.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/**
 * A registry of calculators for computing features.
 *
 */

package crc.tf;

import crc.tf.UnknownNameException;
import crc.ds.UnaryFunctor;
import crc.ds.Table;

public class Registry{
  /**
   * Store previously created feature calculators
   */
  protected static Table calcTable = new Table();

  protected static String packagePrefix = "crc.tf.";

  /**
   * Given a feature name, returns the corresponding feature calculator.
   * Create one if none existed.
   */
  public static Object calculatorFor( String featureName ) throws UnknownNameException{
    Object calc;

    String zname = packagePrefix + featureName;
    calc = calcTable.get( zname );
    if( calc != null ) return calc;
    else{
      try{
	calc = Class.forName( zname ).newInstance() ;
	calcTable.put( zname, calc );
	return calc;
      }catch(Exception e){
	String err = ("Unable to create calculator of class ["
			      + featureName +"]"
			      + "\r\ndetails: \r\n"
			      + e.getMessage());
		throw new UnknownNameException(err);
      }
    }

  }

  /**
   * test loading classes
   */
  public static void main(String[] args){
    UnaryFunctor c;

    try{
      c = (UnaryFunctor) Registry.calculatorFor( "IsAgentRequest" );
    }catch(Exception e){
      System.out.println( e.toString() );
    }
  }



}


