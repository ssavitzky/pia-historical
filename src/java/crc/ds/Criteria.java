////// Criteria.java:  List of match criteria
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.ds;

import crc.dps.util.ListUtil;

/**
 *  A Criteria performs a matching operation on a Features object.
 *	It consists of a List the elements of which are Criterion objects.
 */
public class Criteria extends List {

  /** Match the features in parent (which we need for computing values). */
  public boolean match(Features features, HasFeatures parent) {
    for (int i = 0; i < nItems(); ++i) {
      Criterion c = (Criterion)at(i);
      crc.pia.Pia.debug(this, "         "+c.toString()+"?");
      if (! c.match(features, parent)) {
	crc.pia.Pia.debug(this, "         failed");
	return false;
      }
    }
    return true;
  }

  public Criteria() {
    super();
  }

  /** Initialize from a space-separated list of name=value pairs. 
   *	A name without a value matches any non-null value; nothing after the
   *	"=" matches null, "", or False.
   */
  public Criteria(String str) {
      // this(crc.sgml.Util.split(str));
      this(List.split(str));
  }

  /** Initialize from a list of String or Criterion objects.  Anything else
   * 	is silently ignored. */
  public Criteria(List l) {
    for (int i = 0; i < l.nItems(); ++i) {
      Object o = l.at(i);
      if (o instanceof String) push(Criterion.toMatch(o.toString()));
      else if (o instanceof Criterion) push(o);
    }
  }

  /** Convert to a space-separated list of name=value pairs. */
  public String toString() {
    String s = "";
    java.util.Enumeration e = elements();
    while (e.hasMoreElements()) {
      s += e.nextElement().toString();
      if (e.hasMoreElements()) s += " ";
    }
    return s;
  }
}
