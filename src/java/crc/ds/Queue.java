// Queue.java
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

import java.util.Enumeration;

public class Queue {
  /**
   * Attribute index - a collection of computational codes.
   */
  protected List queue;

  /**
   * queue -- returns queue of elements
   * 
   */  
  public Enumeration queue() {
    if(queue == null)
      queue = new List();
    return queue.elements();
  } 

  /**
   * shift -- remove and returns the first element of the queue .
   * If there is no element returns null.
   */  
  public Object shift() {
    if(queue!=null && queue.size() > 0)
      return queue.elements();
    else return null;
  } 

  /**
   * unshift -- put an element to the front of the queue 
   * returns the number of elements
   */  
  public int unshift( Object obj ) {
    if( obj != null ){
      if( queue == null )
	queue = new List();
      queue.unshift( obj );
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
	queue = new List();
      queue.push( obj );
    }  
    return queue.size();
  } 

  /**
   * pop -- removes an agent from the back of the queue and returns it. 
   * returns the number of elements
   */  
  public Object pop() {
   if(queue!= null && queue.size() > 0)
      return queue.pop();
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
    queue = new List();
  }

}


