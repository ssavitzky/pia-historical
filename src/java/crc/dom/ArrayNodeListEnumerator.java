// ArrayNodeListEnumerator.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's Document interface. 
 */

package crc.dom;

import java.io.*;


public class ArrayNodeListEnumerator implements NodeEnumerator {

  public ArrayNodeListEnumerator(ArrayNodeList list) throws NullPointerException{
    if ( list == null ){
      String err = ("Illegal list.");
      throw new NullPointerException(err);
    }
    l = list;
    cursor = 0;
  }

  /**
   *Returns the first node that the enumeration refers to, and resets the
   *enumerator to reference the first node. If there are no nodes in the
   *enumeration, null is returned. 
   */
  public Node getFirst()
  { 
    Report.debug(this, " getfirst " + Integer.toString((int)l.getLength()));
    if( l.getLength() == 0 ) return null;
    cursor = 0;
    Report.debug(this, "before first element");
    return (Node)l.firstElement();
    
  }

  /**
   *Return the next node in the enumeration, and advances the enumeration. Returns
   *null after the last node in the list has been passed, and leaves the current
   *pointer at the last node. 
   */
  public Node getNext()
  {
    if( cursor == l.size() - 1 ) return null;
    cursor++;
    return (Node)l.elementAt( cursor );
  }

  /**
   *Return the previous node in the enumeration, and regresses the enumeration.
   *Returns null after the first node in the enumeration has been returned, and
   *leaves the current pointer at the first node. 
   */
  public Node getPrevious()
  {
    if( cursor == 0 ) return null;
    cursor--;
    return (Node)l.elementAt( cursor );
  }

  /**
   *Returns the last node in the enumeration, and sets the enumerator to reference
   *the last node in the enumeration. If the enumeration is empty, this method will
   *return null. Doing a getNext() immediately after this operation will return null. 
   */
  public Node getLast()
  { 
    if( l.isEmpty() )
      return null;
    cursor = l.size() - 1;
    return (Node)l.lastElement();
  }

  /**
   * This returns the node that the enumeration is currently referring to, without
   * affecting the state of the enumeration object in any way. When invoked before
   * any of the enumeration positioning methods above, the node returned will be the
   * first node in the enumeration, or null if the enumeration is empty. 
   */
  public Node getCurrent()
  {
    if( l.isEmpty() ) 
      return null;
    return (Node)l.elementAt( cursor ); 
  }

  /**
   *Returns true if the enumeration's "pointer" is positioned at the start of the
   *set of nodes, i.e. if getCurrent() will return the same node as getFirst() would
   *return. For empty enumerations, true is always returned. Does not affect the
   *state of the enumeration in any way. 
   */
  public boolean atStart()
  {
    if( l.isEmpty() ) return true;
    return cursor == 0;
  }

  /**
   * Returns true if the enumeration's "pointer" is positioned at the end of the set
   *  of nodes, i.e. if getCurrent() will return the same node as getLast() would return.
   *  For empty enumerations, true is always returned. Does not affect the state of
   *  the enumeration in any way. 
   */ 
  public boolean atEnd()
  {
    if ( l.isEmpty() ) return true;
    return cursor == l.size() - 1;
  }

  protected int cursor;
  protected ArrayNodeList l;
}




