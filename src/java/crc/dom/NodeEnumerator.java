// NodeEnumerator.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

public interface NodeEnumerator {

  Node getFirst();
  Node getNext();
  Node getPrevious();
  Node getLast();

  Node getCurrent();

  // The rationale for their existence is that the enumerator may be used
  // internally to a method, which may return some interesting value, and
  // therefore cannot also indicate whether the start or end of enumeration
  // was reached.  Any of the traversal methods affects the state, and
  // so are not suitable for usage as predicates (unless possible state
  // manipulation is acceptable).

  boolean atStart();
  boolean atEnd();

};
