////// StringCriterion.java:  Match a string.
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

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
