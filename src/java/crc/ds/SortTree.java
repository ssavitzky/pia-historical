// SortTree.java
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

/** A SortTree is a tree of Association objects; it is used primarily for
 *	performing a lexicographic or numeric insertion sort of arbitrary 
 *	objects.<p>
 *
 *	@see crc.ds.Association
 * 
 *	@see <em>The Art of Computer Programming</em> by Donald Knuth,
 *	Section 6.2.3, Volume III, page 455.
 */

import crc.ds.Association;
import crc.ds.Sorter;

public class SortTree extends Sorter {

  /************************************************************************
  ** Components:
  ************************************************************************/

  /** The Association at the current node. */
  protected Association assoc;

  /** The left, lower-valued branch of the tree. */
  protected SortTree llink;
  
  /** The right, higher-valued branch of the tree. */
  protected SortTree rlink;


  /************************************************************************
  ** Insertion:
  ************************************************************************/

  /** Insert anAssociation into a tree.  The root node is only a header;
   *	its <code>rlink</rlink> points to the true root of the tree.
   */
  public void insert(Association anAssociation, boolean ifAbsent) {

    // Handle empty header:

    if (assoc == null) {
      assoc = anAssociation;
      return;
    }

    int comp = anAssociation.compareTo(assoc);
    if (comp == 0 && ifAbsent) return;

    if (comp < 0) {
      if (llink == null) llink = new SortTree(anAssociation);
      else llink.insert(anAssociation);
    } else {
      if (rlink == null) rlink = new SortTree(anAssociation);
      else rlink.insert(anAssociation);
    }
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

  /** Return a new leaf node. */
  public SortTree(Association anAssociation) {
    this.assoc = anAssociation;
    this.llink = null;
    this.rlink = null;
  }

  /** Return a new head node. */
  public SortTree() {
    this.assoc = null;
    this.llink = null;
    this.rlink = null;
  }


}


