////// Criterion.java:  Superclass for match criteria
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.ds;

import crc.ds.Features;
import crc.ds.HasFeatures;

/**
 *  A Criterion performs a matching operation on a named Feature.
 */
public class Criterion implements java.io.Serializable {

  String name;

  /** Return the feature that this criterion matches. */
  public final String name() {
    return name;
  }

  public String toString() {
    return name;
  }

  /************************************************************************
  ** Matching:
  ************************************************************************/

  /** Match the feature's value.  The default is to match if the
   *	feature's value is anything but False, "", or null. */
  public boolean match(Object s) {
    return Features.test(s);
  }

  /** Match the given features, using a parent object to compute them 
   *	if necessary.  This can be overridden for subclasses that match 
   *	more than one feature.
   */
  public boolean match(Features features, HasFeatures parent) {
    // crc.pia.Pia.instance().debug(this, "     feature "+name);
    return match(features.getFeature(name, parent));
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public Criterion(String nm) {
    name = Features.cannonicalName(nm);
  }

  /************************************************************************
  ** Factory Methods:
  ************************************************************************/

  /** Return a Criterion subclass suitable for matching a String in the form
   *	<code>name</code> or <code>name=value</code> */
  public static Criterion toMatch(String s) {
    int i = s.indexOf('=');
    if (i < 0) return new Criterion(s);
    String value = (i == s.length()-1) ? null : s.substring(i+1);
    if (! Features.test(value)) value = null;
    return new ValueCriterion(s.substring(0, i), value);
  }

  /** Return a Criterion subclass suitable for matching the given value.
   */
  public static Criterion toMatch(String name, Object value) {
    return new ValueCriterion(name, value);
  }

  /** Return a Criterion subclass suitable for making the given test. */
  public static Criterion toMatch(String name, boolean test) {
    return test? new Criterion(name) : new ValueCriterion(name, null);
  }

}
