// NodeList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

 /**
  * The NodeList object provides the abstraction of an immutable ordered collection of
  * Nodes, without defining or constraining how this collection is implemented,
  * allowing different DOM implementations to be tuned for their specific environments.
  * 
  * The items in the NodeList are accessible via an integral index, starting from 0. A
  * NodeEnumerator object may be created to allow simple sequential traversal over the
  * members of the list. 
  */

public interface NodeList {

  /**
   * Creates and returns an object which allows traversal of the nodes in the list
   * in an iterative fashion. Note this method may be very efficient in some
   * implementations; that is, they can return the enumerator instance even before
   * the first node in the set has been located. 
   */
  NodeEnumerator getEnumerator();

  /**
   * Returns the indexth item in the collection. If index is greater than or equal
   * to the number of nodes in the list, a NoSuchNodeException is thrown. 
   * @return a node at index position.
   * @param index Position to get node.
   * @exception NoSuchNodeException is thrown if index out of bound.
   */
  Node item(long index)
    throws NoSuchNodeException;

  /**
   * Returns the number of nodes in the NodeList instance. The range of valid child
   * node indices is 0 to getLength()-1 inclusive.  
   * @return length of list.
   */
  long getLength();

};
