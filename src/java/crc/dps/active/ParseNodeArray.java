// ParseNodeArray.java
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


package crc.dps.active;

import crc.dom.*;

import java.io.*;
import java.util.Vector;

/**
 * Mutable node collection.  Basically an extensible array of Nodes.
 */
public class ParseNodeArray implements EditableNodeList, Serializable {

  /** The actual elements.  Should be protected, but the enumerator needs it. */
  Vector elements = new Vector();
  protected long size() { return elements.size(); }

  protected String sep = null;
  public String getSep() { return sep; }
  public void setSep(String v) { sep = v; }

  public ParseNodeArray(){
  }

  public ParseNodeArray(Node aNode){
    if (aNode != null) append(aNode);
  }

  /**
   * Deep copy of another list.
   */
  public ParseNodeArray(NodeList list) {
    if (list != null) append(list);
  }

  /**
   * Replace the indexth item the list with replacedNode, and return the old
   *	node object at that index (null is returned if the index is equal to
   *	the previous number of nodes in the list). If index is greater than
   *	the number of nodes in the list, a NoSuchNodeException is thrown.
   *
   * @param index Ranges from 0 to size - 1
   * @param replaceNode new node to be put at index
   * @return previous node at index
   * @exception NoSuchNodeException when indexed out of bound.
   */
  public Node replace(long index,Node replacedNode) 
       throws NoSuchNodeException
  {
    if( index >= elements.size() || index < 0){
      String err = ("No such node exists.");
      throw new NoSuchNodeException(err);
    }
    Node old = (Node)elements.elementAt( (int)index );
    elements.setElementAt(replacedNode, (int)index);
    return old;
  }

  /**
   * Inserts a child node into the list BEFORE zero-based location
   * index. Nodes from index to the end of list are moved up by one. If index
   * is 0, the node is added at the beginning of the list; if index is
   * self.getLength(), the node is added at the end of the list.
   *
   * @param index If 0, the node is added at the beginning of the list; if
   * index is self.getLength(), the node is added at the end of the list.
   * @param newNode new node to be put at index 
   * @exception NoSuchNodeException when index out of bounds.
   */
  public void insert(long index,Node newNode) 
       throws NoSuchNodeException
  {
    if( index == 0 ){
      elements.insertElementAt( newNode, 0 );
      return;
    }
    if( index == size() ){
      elements.addElement( newNode );
      return;
    }

    if( index >= 0 && index <= size() )
      elements.insertElementAt( newNode, (int)index );
    
  }

  /**
   * Removes the node at index from the list and returns it. 
   * 	The indices of the members of the list which followed this node are
   * 	decremented by one following the removal. If the index is provided is
   * 	larger than the number of nodes in the list, NoSuchNodeException is
   * 	thrown.
   *
   * @param index Ranges from 0 to size - 1
   * @return node at index
   * @exception NoSuchNodeException when indexed out of bound.  */
  public Node remove(long index)
       throws NoSuchNodeException
  {
    if( index >= size() || index < 0){
      String err = ("No such node exists.");
      throw new NoSuchNodeException(err);
    }

    Node n = (Node)elements.elementAt( (int)index );
    elements.removeElementAt( (int)index );
    return n;
  }

  public Node remove(Node node) throws NoSuchNodeException {
    int index = elements.indexOf(node);
    if (index < 0) throw new NoSuchNodeException("removing non-existant node");
    elements.removeElementAt(index);
    return node;
  }

  public long indexOf(Node node) {
    return elements.indexOf(node);
  }

  /**
   * @return NodeEnumerator
   */
  public NodeEnumerator getEnumerator()
  {
    return new ParseNodeArrayEnumerator( this );
  }

  /**
   * Returns the indexth item in the collection. 
   *	If index is greater than or equal to the number of nodes in the list, 
   *	a NoSuchNodeException is thrown. 
   *@param index Ranges from 0 to size - 1
   *@return node at index
   *@exception NoSuchNodeException when indexed out of bound.
   */
  public Node item(long index)
       throws NoSuchNodeException
  {
    if( index >= size() || index < 0){
      String err = ("No such node exists.");
      throw new NoSuchNodeException(err);
    }

    return  (Node)elements.elementAt( (int)index );
  }


  /**
   * Returns the number of nodes in the NodeList instance. The range of valid
   * child node indices is 0 to getLength()-1 inclusive.
   */
  public long getLength(){ return size(); }
  
  /** Append a new element.
   */
  public void append(Node newChild) { elements.addElement(newChild); }

  /** Append a list of elements.
   */
  public void append(NodeList aNodeList) {
    if (aNodeList == null) return;
    crc.dom.NodeEnumerator e = aNodeList.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      append(node);
    }
  }

  /** 
   * @return string corresponding to content
   */
  public String toString() {
    String result = "";
    long length = getLength();
    for (long i = 0; i < length; ++i) try {
      Node n = item(i);
      result += n.toString();
      if (sep != null && i != length - 1) result += sep;
    }catch(NoSuchNodeException e){
    }
    return result;
  }
}


/** Enumerator for ParseNodeArray.  */
class ParseNodeArrayEnumerator implements NodeEnumerator {

  public ParseNodeArrayEnumerator(ParseNodeArray list)
    throws NullPointerException    {
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
    //Report.debug(this, " getfirst " + Integer.toString((int)l.getLength()));
    if( l.getLength() == 0 ) return null;
    cursor = 0;
    //Report.debug(this, "before first element");
    return (Node)l.elements.firstElement();
    
  }

  /**
   *Return the next node in the enumeration, and advances the
   *enumeration. Returns null after the last node in the list has been passed,
   *and leaves the current pointer at the last node.
   */
  public Node getNext()
  {
    if( cursor == l.size() - 1 ) return null;
    cursor++;
    return (Node)l.elements.elementAt( cursor );
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
    return (Node)l.elements.elementAt( cursor );
  }

  /**
   * Returns the last node in the enumeration, and sets the enumerator to
   * reference the last node in the enumeration. If the enumeration is empty,
   * this method will return null. Doing a getNext() immediately after this
   * operation will return null.
   */
  public Node getLast()
  { 
    if( l.elements.isEmpty() )
      return null;
    cursor = l.elements.size() - 1;
    return (Node)l.elements.lastElement();
  }

  /**
   * This returns the node that the enumeration is currently referring to,
   * without affecting the state of the enumeration object in any way. When
   * invoked before any of the enumeration positioning methods above, the node
   * returned will be the first node in the enumeration, or null if the
   * enumeration is empty.  */
  public Node getCurrent()
  {
    if( l.elements.isEmpty() ) 
      return null;
    return (Node)l.elements.elementAt( cursor ); 
  }

  /**
   * Returns true if the enumeration's "pointer" is positioned at the start of
   * the set of nodes, i.e. if getCurrent() will return the same node as
   * getFirst() would return. For empty enumerations, true is always
   * returned. Does not affect the state of the enumeration in any way.
   */
  public boolean atStart()
  {
    if( l.elements.isEmpty() ) return true;
    return cursor == 0;
  }

  /**
   * Returns true if the enumeration's "pointer" is positioned at the end of
   * the set of nodes, i.e. if getCurrent() will return the same node as
   * getLast() would return.  For empty enumerations, true is always
   * returned. Does not affect the state of the enumeration in any way.  */
  public boolean atEnd()
  {
    if ( l.elements.isEmpty() ) return true;
    return cursor == l.size() - 1;
  }

  protected int cursor;
  protected ParseNodeArray l;
}




