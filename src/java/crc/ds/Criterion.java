////// Criterion.java:  Superclass for match criteria
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.ds;

/**
 *  A Criterion performs a matching operation on a named Feature.
 */
public class Criterion {

  String name;
 
  /** Return the feature that this criterion matches. */
  public final String name() {
    return name;
  }

  /** Match the feature's value.  The default is to match if the
   *	feature's value is anything but False, "", or null. */
  public boolean match(Object s) {
    return Features.test(s);
  }

  public Criterion() {
    name = null;
  }

  public Criterion(String nm) {
    name = nm;
  }

  /** Return a Criterion subclass suitable for matching a String in the form
   *	<code>name</code> or <code>name=value</code> */
  public static Criterion toMatch(String s) {
    int i = s.indexOf('=');
    if (i < 0) return new Criterion(s);
    return new ValueCriterion(s.substring(0, i),
			      (i == s.length()-1) ? null : s.substring(i+1));
  }

  /** Return a Criterion subclass suitable for matching the given value. */
  public static Criterion toMatch(String name, Object value) {
    return new ValueCriterion(name, value);
  }

  /** Return a Criterion subclass suitable for making the given test. */
  public static Criterion toMatch(String name, boolean test) {
    return test? new Criterion(name) : new ValueCriterion(name, null);
  }

}