// BasicNamedNodeList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;


public class BasicNamedNodeList extends AbstractNamedList implements NamedNodeList {

  public BasicNamedNodeList(){
  }

  public BasicNamedNodeList(BasicNamedNodeList l){
    initialize( l );
  }


  // Core get and set public interface. Note that implementations may
  // build the list lazily

  public Node getNode(String name)
  {
    Node n = (Node)getItem( name );
    return ( n != null ) ? (Node)n : null;
  }

  public Node setNode(String name, Node node)
  {
    if( name == null || node == null ) return null;
    Node n = (Node)setItem( name, node );
    return ( n != null ) ? (Node)n : null;
  }
 
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

  public long getLength(){return getItemListLength();}

  public NodeEnumerator getEnumerator(){return getListEnumerator();}
}



