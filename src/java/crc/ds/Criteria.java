////// Criteria.java:  List of match criteria
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.ds;

/**
 *  A Criteria performs a matching operation on a Features object.
 *	It consists of a List the elements of which are Criterion objects.
 */
public class Criteria extends List {

  /** Match the features in parent (which we need for computing values). */
  public boolean match(Features features, Object parent) {
    for (int i = 0; i < nItems(); ++i) {
      Criterion c = (Criterion)at(i);
      if (! c.match(features, parent)) return false;
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
    this(crc.sgml.Util.split(str));
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
}
