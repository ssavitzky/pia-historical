// Table.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.ds;

public class Table extends java.util.Hashtable implements Stuff {

  /************************************************************************
  ** Components:
  ************************************************************************/

  /************************************************************************
  ** Stuff interface:
  ************************************************************************/

  /** The number of indexed items. */
  public int nItems() {
    return 0;
  }

  /** Access an individual item */
  public Object at(int i) {
    return null;
  }

  /** Replace an individual item <em>i</em> with value <em>v</em>. */
  public Stuff at(int i, Object v) {
    // === maybe this should throw an exception ===
    return this;
  }

  /** Remove and return the last item. */
  public Object pop() {
    return null;
  }

  /** Remove and return the first item. */
  public Object shift() {
    return null;
  }

  /** Append a new value <em>v</em>.  
   *	Returns the modified Stuff, to simplify chaining. */
  public Stuff push(Object v) {
    return this;
  }

  /** Prepend a new value <em>v</em>.  
   *	Returns the modified Stuff, to simplify chaining. */
  public Stuff unshift(Object v) {
    return this;
  }

  /** Access a named attribute */
  public Object at(String a) {
    return get(a);
  }

  /** Add or replace an attribute */
  public Stuff at(String a, Object v) {
    put(a, v);
    return this;
  }

  /** Test for presence of  an individual item */
  public boolean has(String a) {
    return containsKey(a);
  }

  /** Return an array of all the attribute keys. */
  public String[] keyList() {
    // ===
    return null;
  }


  /** Return true if the Stuff is a pure hash table, with no items */
  public boolean isTable() {
    return true;
  }

  /** Return true if the Stuff is a pure list, with no attributes. */
  public boolean isList() {
    return false;
  }

  /** Return true if the Stuff is an empty list. */
  public boolean isEmpty() {
    return true;
  }

  /** Return true if the Stuff is pure text, equivalent to a 
   * 	singleton list containing a String. */
  public boolean isText() {
    return false;
  }

}
