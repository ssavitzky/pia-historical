// AbstractNamedList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

public abstract class AbstractNamedList {

  public Object getItem(String name)
  {
    return nameSpace.get( name );
  }
  public Object setItem(String name, Object o)
  {
    Object prev = nameSpace.put(name, o);
    return prev;
  }
  
  public Object removeItem(String name) 
       throws NoSuchNodeException
  {
    if( ! nameSpace.containsKey( name ) )
      throw new NoSuchNodeException("No such node exists.");

    Object item = nameSpace.remove( name );
    return item;
  }
       
  public Object itemAt(long index)
       throws NoSuchNodeException
  {
    
    if( index >= nameSpace.size() || index < 0){
      String err = ("No such node exists.");
      throw new NoSuchNodeException(err);
    }

    Enumeration e = nameSpace.elements();
    EditableNodeList v = buildList( e );
    
    return v.item( index );
  }

  public long getItemListLength(){return nameSpace.size();}
  protected EditableNodeList buildList(Enumeration e )
  {
    LinkedNodeList v = new LinkedNodeList();

    try{
    for(; e.hasMoreElements();)
      v.insert( v.getLength(), (Node)e.nextElement() );
    }catch(NoSuchNodeException err){
      Report.debug(this, "can not insert");
    }

    return v;
  }

  public NodeEnumerator getListEnumerator()
  {
    Enumeration e = nameSpace.elements();
    LinkedNodeList v = (LinkedNodeList)buildList( e );
    return new LinkedNodeListEnumerator( v );
  }

  protected Enumeration getKeys(){
    return nameSpace.keys();
  }


  protected void initialize( AbstractNamedList list )
  {
    Enumeration e = list.getKeys();
    
    String key = null;
    for(;e.hasMoreElements();)
      {
	key = (String)e.nextElement();
	setItem( key, list.getItem( key ));
      }
  }

  protected Hashtable nameSpace = new Hashtable();
}


