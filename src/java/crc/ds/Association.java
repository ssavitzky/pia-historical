// Association.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.ds;

/** Association between a String or number and an arbitrary object.
 *	Associations are often used to permit arbitrary objects to be
 *	compared and sorted, in which case the String or number is
 *	actually <em>derived from</em> the value, rather than
 *	arbitrarily associated with it.  <p>
 *
 *	Associations are created using the <code>associate</code>
 *	operation rather than using <code>new</code>; this will
 *	eventually allow for more efficient implementations using
 *	String-, integer-, and float-specific subclasses.<p>
 */

import java.lang.String;
import java.lang.Double;
import java.lang.Long;

public class Association {

  /************************************************************************
  ** Components:
  ************************************************************************/

  /** The (String) key. */
  protected String key;

  /** The object being associated. */
  protected Object value;

  /** The (double) numeric value. */
  protected double doubleValue;

  /** The (long) integer numeric value. */
  protected long longValue;

  /** True if the Association should be compared as a number. */
  protected boolean isNumeric;

  /** True if the Association should be compared as an integer.  False if 
   *	the Association is compared as a String or a double.
   */
  protected boolean isIntegral;


  /************************************************************************
  ** Access:
  ************************************************************************/

  /** The (String) key. */
  public final String key() {
    return key;
  }

  /** The object being associated. */
  public final Object value() {
    return value;
  }

  /** The (long) integer numeric value.  */
  public final long longValue() {
    return longValue;
  }

  /** The (double) numeric value. */
  public final double doubleValue() {
    return doubleValue;
  }

  /** True if the Association should be compared as a number. */
  public final boolean isNumeric() {
    return isNumeric;
  }

  /** True if the Association should be compared as an integer.  False if 
   *	the Association is compared as a String or a double.
   */
  public final boolean isIntegral() {
    return isIntegral;
  }


  /************************************************************************
  ** Comparison:
  ************************************************************************/

  /** Compare two Associations for equality by comparing their keys or 
   *	(if both Associations are numeric) numeric values.
   */
  public boolean equals(Association anAssociation) {
    if (isNumeric && anAssociation.isNumeric) {
      if (isIntegral && anAssociation.isIntegral)
	return longValue == anAssociation.longValue;
      else
	return doubleValue == anAssociation.doubleValue;
    } else 
      return key.equals(anAssociation.key);
  }

  /** Compare the Association's key with aString. */
  public boolean equals(String aString) {
    return key.equals(aString);
  }

  /** Compare the Association's value with anObject. */
  public boolean valueEquals(Object anObject) {
    return key.equals(anObject.toString());
  }

  /** Compare two Associations by lexicographically comparing their keys or 
   *	(if both Associations are numeric) numeric values.
   *
   * @returns The value 0 if the argument's key is equal to this
   * 	Association's key; a value less than 0 if this Association's
   * 	key is lexicographically less than the argument's key; and a
   * 	value greater than 0 if this Association's key is
   * 	lexicographically greater than the argument's key.
   */
  public int compareTo(Association anAssociation) {
    if (isNumeric && anAssociation.isNumeric) {
      if (isIntegral && anAssociation.isIntegral)
	return longValue == anAssociation.longValue? 0 
	  : longValue < anAssociation.longValue? -1 : 1;
      else
	return doubleValue == anAssociation.doubleValue? 0 
	  : doubleValue < anAssociation.doubleValue? -1 : 1;
    } else 
      return key.compareTo(anAssociation.key);

  }
    
  /** Compare the Association's key to aString. */
  public int compareTo(String aString) {
    return key.compareTo(aString);
  }
    
    
  /************************************************************************
  ** Conversion:
  ************************************************************************/

  /** Return true if aString represents a number. */
  public static final boolean isNumericValue(String aString) {
    if (isIntegralValue(aString)) return true;
    try {
      Double.valueOf(aString).doubleValue();
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /** Return true if aString represents an integer. */
  public static final boolean isIntegralValue(String aString) {
    try {
      Long.parseLong(aString);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /** Convert aString to a long integer.  Return 0 if the string is not
   *	a number at all; return the maximum or minumum long value if the
   *	value represented by the string would be out of range. 
   */
  public static final long longValue(String aString) {
    try {
      return Long.parseLong(aString);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  /** Convert aString to a double.  Return 0 if the string is not a
   *	number at all.
   */
  public static final double doubleValue(String aString) {
    try {
      return Double.valueOf(aString).doubleValue();
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }

  /** Convert anObject to a double.  Checks to see whether anObject is
   *	a subclass of Number; otherwise converts it to a String.
   */
  public static final double doubleValue(Object anObject) {
    if (anObject instanceof Number) return ((Number)anObject).doubleValue();
    else return doubleValue(anObject.toString());
  }

  /** Convert anObject to a long.  Checks to see whether anObject is
   *	a subclass of Number; otherwise converts it to a String.
   */
  public static final long longValue(Object anObject) {
    if (anObject instanceof Number) return ((Number)anObject).longValue();
    else return longValue(anObject.toString());
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct an Association in the most general way. */
  protected Association(Object v, String k, long lv, double dv,
			boolean numeric, boolean integral) {
    this.key = k;
    this.value = v;
    this.longValue = lv;
    this.doubleValue = dv;
    this.isNumeric = numeric;
    this.isIntegral = integral;
  }

  /** Associate aValue with aKey.  The numeric values are computed, but the 
   *	Association will not be compared numerically. */
  public static Association associate(Object aValue, String aKey) {
    return new Association(aValue, aKey, longValue(aKey), 
			   doubleValue(aKey), false, isIntegralValue(aKey));
  }

  /** Associate aValue with aKey and set up the Association to be
   *	compared numerically.  The values are derived from the key;
   *	if the key does not represent a number the Association's
   *	isNumeric flag will <em>not</em> be set. */
  public static Association associateNumeric(Object aValue, String aKey) {
    return new Association(aValue, aKey, longValue(aKey), 
			   doubleValue(aKey), isNumericValue(aKey),
			   isIntegralValue(aKey));
  }

  /** Associate aValue with aKey and set up the Association to be
   *   compared either numerically or lexically.  The values are
   *   derived from the key; if the key does not represent a number
   *   the Association's isNumeric flag will <em>not</em> be set. */
  public static Association associate(Object aValue, String aKey,
				      boolean numeric) {
    return new Association(aValue, aKey,
			   longValue(aKey), doubleValue(aKey),
			   numeric && isNumericValue(aKey),
			   isIntegralValue(aKey));
  }

  /** Associate aValue with both aKey and aLong. */
  public static Association associate(Object aValue, String aKey, long aLong) {
    return new Association(aValue, aKey, aLong, (double)aLong, true, true);
  }

  /** Associate aValue with both aKey and aDouble. */
  public static Association associate(Object aValue, String aKey,
				      double aDouble) {
    return new Association(aValue, aKey, (int)aDouble, aDouble, true, false);
  }

  /** Associate aValue with <code>aValue.toString()</code>. */
  public static Association associate(Object aValue) {
    return associate(aValue, aValue.toString());
  }

  /** Associate aValue with its numericValue. */
  public static Association associateNumeric(Object aValue) {
    String s = aValue.toString();
    return new Association(aValue, s, longValue(aValue), doubleValue(aValue),
			   true, isIntegralValue(s));
  }

  /** Associate aValue with <code>aValue.toString()</code> and give it
   *	a numeric value of <code>longValueOf(aValue)</code>. */
  public static Association associateLong(Object aValue) {
    return new Association(aValue, aValue.toString(), longValue(aValue), 
			   doubleValue(aValue), true, true);
  }

  /** Associate aValue with <code>aValue.toString()</code> and give it
   *	a numeric value of <code>doubleValueOf(aValue)</code>. */
  public static Association associateDouble(Object aValue) {
    return new Association(aValue, aValue.toString(), longValue(aValue), 
			   doubleValue(aValue), true, false);
  }

}


