// AttrList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;

public class AttrList extends AbstractNamedList implements AttributeList {

  public AttrList(){
  }
  public AttrList(AbstractNamedList l){
    initialize( l );
  }

  public Attribute getAttribute(String name)
  {
    Attribute n = (Attribute)getItem( name );
    return ( n != null ) ? (Attribute)n : null;
  }

  public Attribute setAttribute(String name, Attribute attr)
  { 
    if( name == null || attr == null ) return null;
    Attribute n = (Attribute)setItem( name, attr );
    return ( n != null ) ? (Attribute)n : null;
  }
  
  public Attribute remove(String name) 
       throws NoSuchNodeException
  {
    try{
      Attribute n = (Attribute)removeItem( name );
      return ( n != null ) ? (Attribute)n : null;
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
       
  public long getLength(){ return getItemListLength();}
}


