// BasicNamedNodeList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;

/**
 * Implementing NamedNodeList.
 */

public class BasicNamedNodeList extends AbstractNamedList implements NamedNodeList {

  public BasicNamedNodeList(){
  }

  /**
   * Copy from another list.
   */
  public BasicNamedNodeList(AbstractNamedList l){
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







