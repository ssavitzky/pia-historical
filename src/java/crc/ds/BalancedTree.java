// BalancedTree.java
// $Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/

package crc.ds;

/** A BalancedTree is a tree of Association objects; it is used primarily for
 *	performing a lexicographic or numeric insertion sort of arbitrary 
 *	objects.   Note that an empty BalancedTree is used as a header.<p>
 *
 *	@see crc.ds.Association
 * 
 *	@see <em>The Art of Computer Programming</em> by Donald Knuth,
 *	Section 6.2.3, Volume III, page 455.
 */

import crc.ds.Association;
import crc.ds.Sorter;

public class BalancedTree extends Sorter {

  /************************************************************************
  ** Components:
  ************************************************************************/

  /** The Association at the current node. */
  protected Association assoc;

  /** The left, lower-valued branch of the tree. */
  protected BalancedTree llink;
  
  /** The right, higher-valued branch of the tree. */
  protected BalancedTree rlink;

  /** The balance factor of the current node. */
  protected int balance;
  

  /************************************************************************
  ** Insertion:
  ************************************************************************/

  /** Insert anAssociation into a tree.  The root node is only a header;
   *	its <code>rlink</rlink> points to the true root of the tree.
   */
  public void insert(Association anAssociation, boolean ifAbsent) {

    // Handle empty header:

    if (rlink == null) {
      rlink = new BalancedTree(anAssociation);
      return;
    }

    // A1: initialization

    BalancedTree t = this;
    BalancedTree s = rlink;
    BalancedTree p = rlink;
    BalancedTree q = null;
    BalancedTree r = null;

    for ( ; ; ) {
      int comp = anAssociation.compareTo(p.assoc);	// A2: Compare
      if (comp == 0 && ifAbsent) return;
      if (comp <= 0) {					// A3: Move Left
	q = p.llink;
	if (q == null) {
	  q = new BalancedTree(anAssociation);
	  p.llink = q;
	  break;
	}
      } else {						// A4: Move Right
	q = p.rlink;
	if (q == null) {
	  q = new BalancedTree(anAssociation);
	  p.rlink = q;
	  break;
	}
      }
      p = q;
      if (q.balance != 0) { t = p; s = q; }
    }
    /* really, initialize.  Done. */			// A5: Insert

				// A6: Adjust Balance Factors
				// Change the balance factors between s and q
				// from 0 to +- 1
    if (anAssociation.compareTo(s.assoc) <= 0) {
      r = p = s.llink;
    } else {
      r = p = s.rlink;
    }
    while (p != q) {
      if (anAssociation.compareTo(p.assoc) <= 0) {
	p.balance = -1;
	p = p.llink;
      } else {
	p.balance = 1;
	p = p.rlink;
      }
    }
				// A7: Balancing Act
    int a = (anAssociation.compareTo(s.assoc) <= 0) ? -1 : 1;
    if (s.balance == 0) {	  //	  i) tree has grown higher
      s.balance = a;
      return;
    } else if (s.balance == -a) { //	 ii) tree got more balanced
      s.balance = 0;
      return;
    } 				  // 	iii) tree got less balanced

    if (r.balance == a) {	// A8: Single Rotation
      p = r;
      s.link(a, r.link(-a));
      r.link(-a, s);
      s.balance = r.balance = 0;
    } else {			// A9: Double Rotation
      p = r.link(-a);
      r.link(-a, p.link(a));
      p.link(a, r);
      s.link(a, p.link(-a));
      p.link(-a, s);
      if (p.balance == a) {
	s.balance = -a; r.balance =  0;
      } else if (p.balance == 0) {
	s.balance =  0; r.balance =  0;
      } else {
	s.balance =  0; r.balance = -a;
      }
    }

				// A10: Finishing Touch
    /* At this point s points to the new root, and t to the father
       of the old root. */
    if (s == t.rlink) t.rlink = p;
    else t.llink = p;
  }

  private final BalancedTree link(int a) {
    return (a < 0)? llink : rlink;
  }

  private final void link(int a, BalancedTree n) {
    if (a < 0) llink = n; else rlink = n;
  }

  /************************************************************************
  ** Traversal:
  ************************************************************************/

  /** Append to a list, in ascending order.  Construct a new List if
   *	necessary. */
  public List ascending(List aList) {
    if (llink != null) aList = llink.ascending(aList);
    if (aList == null) aList = new List();
    if (assoc != null) aList.push(assoc);
    return (rlink == null)? aList : rlink.ascending(aList);
  }

  /** Append to a list, in descending order.  Construct a new List if
   *	necessary.  */
  public List descending(List aList) {
    if (rlink != null) aList = rlink.descending(aList);
    if (aList == null) aList = new List();
    if (assoc != null) aList.push(assoc);
    return (llink == null)? aList : llink.descending(aList);
  }

  /** Append associated values to a list, in ascending order.
   *	Construct a new List if necessary. */
  public List ascendingValues(List aList) {
    if (llink != null) aList = llink.ascendingValues(aList);
    if (aList == null) aList = new List();
    if (assoc != null) aList.push(assoc.value());
    return (rlink == null)? aList : rlink.ascendingValues(aList);
  }

  /** Append associated values to a list, in descending order.
   *	Construct a new List if necessary.  */
  public List descendingValues(List aList) {
    if (rlink != null) aList = rlink.descendingValues(aList);
    if (aList == null) aList = new List();
    if (assoc != null) aList.push(assoc.value());
    return (llink == null)? aList : llink.descendingValues(aList);
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Return a new BalancedTree leaf node. */
  public BalancedTree(Association anAssociation) {
    this.assoc = anAssociation;
    this.balance = 0;
    this.llink = null;
    this.rlink = null;
  }

  /** Return a new BalancedTree head node. */
  public BalancedTree() {
    this.assoc = null;
    this.balance = 0;
    this.llink = null;
    this.rlink = null;
  }


}


