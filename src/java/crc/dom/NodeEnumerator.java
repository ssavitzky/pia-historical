// NodeEnumerator.java
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

package crc.dom;


 /**This class provides a generic iteration mechanism over an arbitrary collection of
  * nodes. The nodes may be enumerated in either forward or reverse order, and the
  * direction of enumeration may be changed at any time. The enumerator behaves as
  * though it had an internal "pointer" to the current node, and provides methods for
  * abstractly changing the notion of what the current node is.
  * 
  * Typical usage (in some C++ like language) might look like:
  * 
  *     NodeEnumerator nodeEnum = document.getChildren().getEnumerator();
  * 
  *     for (Node node = nodeEnum.first(); node != null; node = nodeEnum.next()) {
  * 
  *         // ... do some computation on that node
  *     }
  */

public interface NodeEnumerator {


  /**
   * Returns the first node that the enumeration refers to, and resets the
   * enumerator to reference the first node. If there are no nodes in the
   * enumeration, null is returned. 
   */
  Node getFirst();

  /**
   *  Return the next node in the enumeration, and advances the enumeration. Returns
   *  null after the last node in the list has been passed, and leaves the current
   *  pointer at the last node. 
   */
  Node getNext();

  /**
   * Return the previous node in the enumeration, and regresses the enumeration.
   * Returns null after the first node in the enumeration has been returned, and
   * leaves the current pointer at the first node. 
   */ 
  Node getPrevious();
  
 /**
  * Returns the last node in the enumeration, and sets the enumerator to reference
  * the last node in the enumeration. If the enumeration is empty, this method will
  * return null. Doing a getNext() immediately after this operation will return null. 
  * 
  */
  Node getLast();

 /**
  * This returns the node that the enumeration is currently referring to, without
  * affecting the state of the enumeration object in any way. When invoked before
  * any of the enumeration positioning methods above, the node returned will be the
  * first node in the enumeration, or null if the enumeration is empty. 
  */
  Node getCurrent();


 /**
  * Returns true if the enumeration's "pointer" is positioned at the start of the
  * set of nodes, i.e. if getCurrent() will return the same node as getFirst() would
  * return. For empty enumerations, true is always returned. Does not affect the
  * state of the enumeration in any way. 
  */
  boolean atStart();



 /**
  * Returns true if the enumeration's "pointer" is positioned at the end of the set
  * of nodes, i.e. if getCurrent() will return the same node as getLast() would return.
  * For empty enumerations, true is always returned. Does not affect the state of
  * the enumeration in any way. 
  */
  boolean atEnd();

};

