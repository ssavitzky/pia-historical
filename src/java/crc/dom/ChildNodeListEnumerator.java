// ChildNodeListEnumerator.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's Document interface. 
 */

package crc.dom;

import java.io.*;

public class ChildNodeListEnumerator implements NodeEnumerator {

  public ChildNodeListEnumerator(ChildNodeList list) throws NullPointerException{
    if ( list == null ){
      String err = ("Illegal list.");
      throw new NullPointerException(err);
    }
    l = list;
    cursor = l.header.getPrevious();
  }

  /**
   *Returns the first node that the enumeration refers to, and resets the
   *enumerator to reference the first node. If there are no nodes in the
   *enumeration, null is returned. 
   */
  public Node getFirst(){
    cursor = l.header.getPrevious();
    return cursor;
  }

  /**
   *Return the next node in the enumeration, and advances the enumeration. Returns
   *null after the last node in the list has been passed, and leaves the current
   *pointer at the last node. 
   */
  public Node getNext(){ 
    if( cursor == l.header.getNext() )
      return null;

    cursor = cursor.getNext();
    return cursor;
  }

  /**
   *Return the previous node in the enumeration, and regresses the enumeration.
   *Returns null after the first node in the enumeration has been returned, and
   *leaves the current pointer at the first node. 
   */
  public Node getPrevious(){
    if( cursor == l.header.getPrevious() )
      return null;

    cursor = cursor.getPrevious();
    return cursor;
  }

  /**
   *Returns the last node in the enumeration, and sets the enumerator to reference
   *the last node in the enumeration. If the enumeration is empty, this method will
   *return null. Doing a getNext() immediately after this operation will return null. 
   */

  public Node getLast(){
    if( isEmpty() )
      return null;

    cursor = l.header.getNext();
    return cursor;
  }

  /**
   * This returns the node that the enumeration is currently referring to, without
   * affecting the state of the enumeration object in any way. When invoked before
   * any of the enumeration positioning methods above, the node returned will be the
   * first node in the enumeration, or null if the enumeration is empty. 
   */
  public Node getCurrent(){
    return cursor; 
  }

  /**
   *Returns true if the enumeration's "pointer" is positioned at the start of the
   *set of nodes, i.e. if getCurrent() will return the same node as getFirst() would
   *return. For empty enumerations, true is always returned. Does not affect the
   *state of the enumeration in any way. 
   */
  public boolean atStart(){
    if( isEmpty() ) return true;

    return cursor == l.header.getPrevious();
  }

  /**
   * Returns true if the enumeration's "pointer" is positioned at the end of the set
   *  of nodes, i.e. if getCurrent() will return the same node as getLast() would return.
   *  For empty enumerations, true is always returned. Does not affect the state of
   *  the enumeration in any way. 
   */

  public boolean atEnd(){
    if ( isEmpty() ) return true;
    return cursor == l.header.getNext();
  }

  protected boolean isEmpty(){ 
    return l.header.getNext() == null && l.header.getPrevious() == null;
  }

  protected AbstractNode cursor = null;
  protected ChildNodeList l;
}




