// Sorter.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.ds;

/** Base class for objects that construct sorted collections of
 *	Association objects.  Once a sort tree has been constructed,
 *	the Sorter can return its Associations or their values in
 *	either ascending or descending order. <p>
 *
 *	@see crc.ds.Association
 *	@see crc.ds.BalancedTree
 * 
 *	@see <em>The Art of Computer Programming</em> by Donald Knuth,
 *	Section 6.2.3, Volume III, page 455.  */

import crc.ds.Association;

import java.util.Enumeration;

public abstract class Sorter {

  /************************************************************************
  ** Abstract Methods:
  ************************************************************************/

  /** Insert anAssociation, possibly only ifAbsent from the tree. */
  public abstract void insert(Association anAssociation, boolean ifAbsent);
    
  /** Append the associations to a list in ascending order. */
  public abstract List ascending(List aList);

  /** Append the associations to a list in descending order. */
  public abstract List descending(List aList);

  /** Append the associated values to a list in ascending order. */
  public abstract List ascendingValues(List aList);

  /** Append the associated values to a list in descending order. */
  public abstract List descendingValues(List aList);


  /************************************************************************
  ** Input:
  ************************************************************************/

  /** Insert a single element, preserving duplicates if present. */
  public void insert(Association anAssociation) {
    insert(anAssociation, false);
  }

  /** Append elements from an Enumeration, sorting them lexicographically.
   */
  public void appendLexical(Enumeration e) {
    while(e.hasMoreElements()) 
      insert(Association.associate(e.nextElement()));
  }

  /** Append elements from an Enumeration, sorting them numerically. */
  public void appendNumeric(Enumeration e) {
    while(e.hasMoreElements()) 
      insert(Association.associateNumeric(e.nextElement()));
  }

  /** Append elements from an Enumeration, sorting them lexicographically
   *	except in the case that they are already Association objects.
   */
  public void append(Enumeration e) {
    while(e.hasMoreElements()) {
      Object o = e.nextElement();
      if (! (o instanceof Association)) o = Association.associate(o);
      insert((Association) o);
    }
  }


  /************************************************************************
  ** Output:
  ************************************************************************/


}


