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
    cursor = null;
  }

  /**
   *Returns the first node that the enumeration refers to, and resets the
   *enumerator to reference the first node. If there are no nodes in the
   *enumeration, null is returned. 
   */
  public Node getFirst(){
    if( l.getLength() == 0 ) return null;
    try{
      cursor = l.item( 0 );
      return cursor;
    }catch( NoSuchNodeException e ){
      return null;
    }
  }

  /**
   *Return the next node in the enumeration, and advances the enumeration. Returns
   *null after the last node in the list has been passed, and leaves the current
   *pointer at the last node. 
   */
  public Node getNext(){ 
    // pass last element
    if( cursor != null && cursor.getNextSibling() == null ) return null;
    cursor = cursor.getNextSibling();
    return cursor;
  }

  /**
   *Return the previous node in the enumeration, and regresses the enumeration.
   *Returns null after the first node in the enumeration has been returned, and
   *leaves the current pointer at the first node. 
   */
  public Node getPrevious(){
    if( cursor != null && cursor.getPreviousSibling() == null )
      return null;

    cursor = cursor.getPreviousSibling();
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

    long last = l.getLength() - 1;
    try{
      cursor = l.item( last );
      return cursor;
    }catch(NoSuchNodeException e){
      return null;
    }
  }

  /**
   * This returns the node that the enumeration is currently referring to, without
   * affecting the state of the enumeration object in any way. When invoked before
   * any of the enumeration positioning methods above, the node returned will be the
   * first node in the enumeration, or null if the enumeration is empty. 
   */
  public Node getCurrent(){
    if(isEmpty()) return null;

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
    try{
      return cursor == l.item( 0 );
    }catch(NoSuchNodeException e){
      return true;
    }
  }

  /**
   * Returns true if the enumeration's "pointer" is positioned at the end of the set
   *  of nodes, i.e. if getCurrent() will return the same node as getLast() would return.
   *  For empty enumerations, true is always returned. Does not affect the state of
   *  the enumeration in any way. 
   */

  public boolean atEnd(){
    if ( isEmpty() ) return true;
    try{
      int len = (int)l.getLength();
      return cursor == l.item( len - 1 );
    }catch(NoSuchNodeException e){
      return true;
    }
  }

  protected boolean isEmpty(){ 
    return l.getLength() == 0;
  }

  protected Node cursor = null;
  protected ChildNodeList l;
}




