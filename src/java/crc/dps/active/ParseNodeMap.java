// ParseNodeMap.java
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

import java.io.*;
import crc.dom.*;

/**
 * Implementing NamedNodeList, which associates <em>named</em> nodes with
 * their own names.
 */
public class ParseNodeMap extends ParseNodeTable implements NamedNodeList {

  public ParseNodeMap() {
  }

  /**
   * Copy from another list.
   */
  public ParseNodeMap(ParseNodeTable l){
    if( l != null )
      initialize( l );
  }


  /**
   * Returns the node specified by name.
   * @return node if exists otherwise null.
   */
  public Node getNode(String name)
  {
    Node n = (Node)getItem( name );
    return ( n != null ) ? (Node)n : null;
  }

  /**
   * Maps the name to the specified node.
   * @param name Name associated with a given node.
   * @param node Node associated with the name.
   * @Return The previous node of the specified name, or null if it did not
   * have one.
   */
  public Node setNode(String name, Node node)
  {
    if( name == null || node == null ) return null;
    Node n = (Node)setItem( name, node );
    return ( n != null ) ? (Node)n : null;
  }
 
  /**
   * Remove node specified by name.
   */
  public Node remove(String  name)
       throws NoSuchNodeException
  {
    try{
      Node n = (Node)removeItem( name );
      return ( n != null ) ? (Node)n : null;
    }catch(NoSuchNodeException e){
      throw e;
    }
  }

  /**
   * return node at the indicated index.
   */
  public Node item(long index)
       throws NoSuchNodeException
  {
    try{
      Node n = (Node)itemAt( index );
      return ( n != null ) ? (Node)n : null;
    }catch(NoSuchNodeException e){
      throw e;
    }
  }

  /**
   * Return the size of the list.
   */
  public long getLength(){return getItemListLength();}

  public NodeEnumerator getEnumerator(){return getListEnumerator();}
}







