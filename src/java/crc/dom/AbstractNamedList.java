// AbstractNamedList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;


/**
 * AbstractNamedList behaves like a map, allowing one
 * to associate key with an Object.  Also, it allows 
 * access to Object by indexing.
 */
public abstract class AbstractNamedList {

  /**
   * Retreive a value base on key.
   * @param name Key used to retreive value.
   * @return Value associated with name.
   */
  public Object getItem(String name)
  {
    return nameSpace.get( name );
  }

  /**
   *  Add a new item to the end of the list and associate it with the given
   *  name. If the name already exists, the previous object is replaced,
   *  and returned. 
   *  If no object of the same name exists, null is returned, and the
   *  named Attribute is added to the end of the item list; that is, it is
   *  accessible via the item method using the index one less than the value returned
   *  by getLength(). 
   *
   * Put an association into table
   * @param name The key.
   * @param o The value.
   * @return Previous value associated with name; otherwise null.
   */
  public Object setItem(String name, Object o)
  {
    // never exist before
    if( getItem( name ) == null ){
      itemList.addElement( o ); 
      nameSpace.put(name, o);
      return null;
    }else{
      int pos = -1;
      Object prev = nameSpace.put(name, o);
      pos = itemList.indexOf( prev );
      itemList.setElementAt( o, pos );
      return prev;
    }
  }
  
  /**
   * Remove value indicated by name.
   * @param name The key.
   * @return Value associated with key
   * @exception NoSuchNodeException if name does not exist. 
   */
  public Object removeItem(String name) 
       throws NoSuchNodeException
  {
    if( ! nameSpace.containsKey( name ) )
      throw new NoSuchNodeException("No such node exists.");

    Object item = nameSpace.remove( name );
    itemList.removeElement( item );
    return item;
  }
       
  /**
   * Access to value base on index.
   * @param index The position in list.
   * @exception NoSuchNodeException if index is < 0 or index >= getItemListLength()
   */
  public Object itemAt(long index)
       throws NoSuchNodeException
  {
    
    if( index >= nameSpace.size() || index < 0){
      String err = ("No such node exists.");
      throw new NoSuchNodeException(err);
    }

    return itemList.elementAt( (int)index );
  }

  /**
   * The number of object in this list.
   * @return list size.
   *
   */
  public long getItemListLength(){return nameSpace.size();}


  /**
   * Give NodeEnumerator base on the sequential list of values.
   */
  protected NodeEnumerator getListEnumerator()
  {
    LinkedNodeList le = new LinkedNodeList();

    try{
      for( int i = 0; i < itemList.size(); i++ )
	le.insert( le.getLength(), (Node)itemList.elementAt( i ) );
      //Report.debug("v length is-->"+ Integer.toString( (int)builtList.getLength() ) );
    }catch(NoSuchNodeException err){
      Report.debug(this, "can not insert");
    }
    return new LinkedNodeListEnumerator( le );
  }

  /**
   * Copy another list.
   */
  protected void initialize( AbstractNamedList list )
  {
    if( list == null ) return;

    int len = list.itemList.size();
    Vector seqList = list.itemList;
    itemList.setSize( len );


    Hashtable ht = list.nameSpace;
    if( ht != null ){
      Enumeration e = ht.keys();
      String key = null;
      Object v = null;
      AbstractNode clone;

      for(;e.hasMoreElements();)
	{
	  key = new String( (String)e.nextElement() );
	  v = ht.get( key );
	  // now where is it in item List
	  int pos = seqList.indexOf( v );
	  clone = (AbstractNode)((AbstractNode)v).clone();
	  nameSpace.put( new String( key ), clone );
	  itemList.setElementAt( clone, pos );
	}
    }
  }
  protected Hashtable nameSpace = new Hashtable();
  Vector itemList = new Vector();
}



