// LinkedList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.util.NoSuchElementException;

/**
 * Implementation of a singular linked list with
 * head pointing to first element and tail pointing
 * to last element
 */

public class LinkedList
{

  /**
   * Retreiving element at the requested position
   * @return element at position
   * @exception NoSuchElementException if index is less than 0 or greater or equal to size
   */
  public Object elementAt(int p){
    if( p >= size() || p < 0)
      throw new NoSuchElementException();

    moveCursorTo( p );
    return currentElement();
  }

  /**
   * Inserting element before indexed position
   * If indexed is same as size, append element
   * @exception NoSuchElementException if index is less than 0 or greater than size
   */
  public void insertElementAt(Object obj,
					   int p){
    int size = size();
    //Report.debug(this,"My size is-->"+Integer.toString( size ));
    //Report.debug(this,"p is-->"+Integer.toString( p ));
    if( (p > size ) || p < 0)
      throw new NoSuchElementException();
    moveCursorTo( p );
    insert( obj );
  }

  /**
   * Remove and return an element at the indicated position
   * Position is indexed from 0 to size -1 
   * @return element removed
   * 
   */
  public Object removeElementAt(int p){
    if( p >= size() || p < 0)
      throw new NoSuchElementException();
    moveCursorTo( p );
    return remove();
  } 

  /**
   * Replace a given element at the index position
   * @return old element at index position
   */
  public synchronized Object setElementAt(Object obj,int p){
    if( (p > size() - 1 ) || p < 0)
      throw new NoSuchElementException();
    moveCursorTo( p );

    Link cur = cursor();
    Link x = new Link(obj, cursor());
    
    if (tail == cur){
      x.next = null;
      x.data = obj;
      tail = x;
      pre.next = x;
    }else if (pre != null){
      pre.next = x;
      x.next   = cur.next;
    }else{
      head = x;
      x.next = cur.next;
    }

    return cur.data;
  }


  /**
   * Find the position of a given lement
   * @return position of an element
   */
  public synchronized int indexOf(Object o)
  {
    Link ptr = head;
    int i = 0;

    while( ptr != null ){
      if( o == ptr.data )
	return i;

      i++;
      ptr = ptr.next;
    }
    return -1;
  }

  /*==================================================*/
  /* protected functions                              */
  /*==================================================*/


   /**
    * @return true iff the cursor is not at the end of the
    * list 
    */
   protected boolean hasMoreElements()
   {
     synchronized (this){
       return cursor() != null;
     }
   }

  /** 
   * resets the cursor
   */
   
  private void reset(){
    pre = null;
  }


   /**
    * move the cursor to the next position
    * @return the current element (before advancing the 
    * position)
    * @exception NoSuchElementException if already at the
    * end of the list
    */
   private Object getNextElement()
   {  if (pre == null) pre = head; else pre = pre.next;
      if (pre == null) 
         throw new NoSuchElementException();
      return pre.data;
   }


  protected synchronized void moveCursorTo(int p){
    if (p == 0){
      reset();
      return;
    }

    if( p > size() || p < 0)
      throw new NoSuchElementException();
    reset();
    for( int i = 0; i < p; i++ )
      getNextElement();
    
  }

   /**
    * @return the current element under the cursor
    * @exception NoSuchElementException if already at the
    * end of the list
    */
   
   protected synchronized Object currentElement() 
   {  Link cur = cursor();
      if (cur == null) 
         throw new NoSuchElementException();
      return cur.data;
   }

   /**
    * insert before the iterator position
    * @param n the object to insert
    */

   protected synchronized void insert(Object n)
   {  Link p = new Link(n, cursor());

      if (pre != null)
      {  pre.next = p;
         if (pre == tail) tail = p;
      }
      else
      {  if (head == null) tail = p;
         head = p;
      }

      pre = p;
      len++;
   }

   /**
    * insert after the tail of the list
    * @param n - the value to insert
    */
   
   protected synchronized void addElement(Object n)
   {  Link p = new Link(n, null);
      if (head == null) head = tail = p;
      else
      {  tail.next = p;
         tail = p;
      }
      len++;
   }

   /**
    * remove the element under the cursor
    * @return the removed element
    * @exception NoSuchElementException if already at the 
    * end of the list
    */

   protected synchronized Object remove()
   {  Link cur = cursor();
      if (cur == null) 
         throw new NoSuchElementException();
      if (tail == cur) tail = pre;
      if (pre != null)
         pre.next = cur.next;
      else
         head = cur.next;
      len--;
      return cur.data;
   }

  public synchronized boolean isEmpty(){
    return len == 0;
  }

   /**
    * @return the number of elements in the list
    */

   public synchronized int size() 
   {  return len;
   }

   /**
    * @return an enumeration to iterate through all elements
    * in the list
    */

  public java.util.Enumeration elements(){ 
    synchronized (this){
      return new ListEnumeration(head); 
    }
  }

   private Link cursor() 
   {  if (pre == null) return head; else return pre.next;
   }
   
   protected Link head;
   protected Link tail;
   private Link pre; // predecessor of cursor
   private int len;
}

class Link
{  Object data;
   Link next;
   Link(Object d, Link n) { data = d; next = n; }
}
    /**
    * A class for enumerating a linked list
    * implements the Enumeration interface
    */
    
class ListEnumeration implements java.util.Enumeration
{  public ListEnumeration( Link l)
   {  cursor = l;
   }

   /**
    * @return true iff the iterator is not at the end of the
    * list
    */
   public boolean hasMoreElements() 
   {  return cursor != null;
   }

   /**
    * move the iterator to the next position
    * @return the current element (before advancing the 
    * position)
    * @exception NoSuchElementException if already at the
    * end of the list
    */

   public Object nextElement()
   {  if (cursor == null) 
      throw new NoSuchElementException();
      Object r = cursor.data;
      cursor = cursor.next;
      return r;
   }
   
   private Link cursor;
}


