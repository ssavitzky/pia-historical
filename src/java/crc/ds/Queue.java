// Queue.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.ds;

import java.util.Enumeration;
import jgl.Array;

public class Queue{
  /**
   * Attribute index - a collection of computational codes.
   */
  protected Array queue;

  /**
   * queue -- returns queue of elements
   * 
   */  
  public Enumeration queue() {
    if(queue == null)
      queue = new Array();
    return queue.elements();
  } 

  /**
   * shift -- remove and returns the first element of the queue .
   * If there is no element returns null.
   */  
  public Object shift() {
    if(queue!=null && queue.size() > 0)
      return queue.popFront();
    else return null;
  } 

  /**
   * unshift -- put an element to the front of the queue 
   * returns the number of elements
   */  
  public int unshift( Object obj ) {
    if( obj != null ){
      if( queue == null )
	queue = new Array();
      queue.pushFront( obj );
    }  
    return queue.size();
  } 

  /**
   * push -- push an agent onto the end of the queue 
   * returns the number of elements
   */  
  public int push( Object obj) {
    if( obj != null ){
      if( queue == null )
	queue = new Array();
      queue.pushBack( obj );
    }  
    return queue.size();
  } 

  /**
   * pop -- removes an agent from the back of the queue and returns it. 
   * returns the number of elements
   */  
  public Object pop() {
   if(queue!= null && queue.size() > 0)
      return queue.popBack();
    else return null;
  } 

  /**
   * Number of elements in queue
   *
   */
  public int size() {
    return queue.size();
  }

  public Queue(){
    queue = new Array();
  }

}


