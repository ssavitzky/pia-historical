// ParseNodeTable.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dps.active;

import java.io.*;
import crc.dom.*;

import java.util.Hashtable;
import java.util.Enumeration;


/**
 * ParseNodeTable behaves like a map, allowing one
 * to associate (String) key with a Node.  
 *
 * === Should probably implement Namespace
 */
public abstract class ParseNodeTable implements Serializable {

  /**
   * Retreive a value base on key.
   * @param name Key used to retreive value.
   * @return Value associated with name.
   */
  public Node getItem(String name) {
    return (Node) nameSpace.get( name );
  }

  /**
   *  Add a new item to the end of the list and associate it with the given
   *  name. If the name already exists, the previous object is replaced,
   *  and returned. 
   *  If no object of the same name exists, null is returned, and the
   *  named Attribute is added to the end of the item list; that is, it is
   *  accessible via the item method using the index one less than the value
   *  returned by getLength(). 
   *
   * Put an association into table
   * @param name The key.
   * @param o The value.
   * @return Previous value associated with name; otherwise null.
   */
  public Node setItem(String name, Node o) {
    // never exist before
    if( getItem( name ) == null ){
      itemList.append( o ); 
      nameSpace.put(name, o);
      return null;
    }else{
      Node prev = (Node)nameSpace.put(name, o);
      long pos = itemList.indexOf( prev );
      if (pos >= 0) try {
	itemList.replace( pos, o );
      } catch (NoSuchNodeException ex) {}
      return prev;
    }
  }
  
  /**
   * Remove value indicated by name.
   * @param name The key.
   * @return Value associated with key
   * @exception NoSuchNodeException if name does not exist. 
   */
  public Node removeItem(String name) throws NoSuchNodeException {
    if( ! nameSpace.containsKey( name ) ) 
      throw new NoSuchNodeException("No such node exists.");

    Node item = (Node) nameSpace.remove( name );
    itemList.remove( item );
    return item;
  }
       
  /**
   * Access to value base on index.
   * @param index The position in list.
   * @exception NoSuchNodeException if index is < 0 or index >= getItemListLength()
   */
  public Node itemAt(long index) throws NoSuchNodeException {
    
    if( index >= itemList.size() || index < 0){
      String err = ("No such node exists.");
      throw new NoSuchNodeException(err);
    }

    return itemList.item( index );
  }

  /**
   * The number of object in this list.
   * @return list size.
   *
   */
  public long getItemListLength() { return nameSpace.size(); }


  /**
   * Give NodeEnumerator base on the sequential list of values.
   */
  protected NodeEnumerator getListEnumerator() {
    return itemList.getEnumerator();
  }

  /**
   * Copy another list.
   */
  protected void initialize( ParseNodeTable list ) {
    if( list == null ) return;
    itemList = new ParseNodeArray(list.itemList);

    Hashtable ht = list.nameSpace;
    if( ht != null ){
      Enumeration e = ht.keys();
      Object v = null;

      for( ; e.hasMoreElements(); )
	{
	  String key = (String)e.nextElement();
	  nameSpace.put( key, ht.get(key) );
	}
    }
  }
  protected Hashtable nameSpace = new Hashtable();
  protected ParseNodeArray itemList = new ParseNodeArray();
}



