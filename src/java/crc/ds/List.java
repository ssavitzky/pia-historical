// List.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.ds;

import java.util.Vector;

public class List implements Stuff {

  /************************************************************************
  ** Components:
  ************************************************************************/

  /** The actual items. */
  Vector items = new Vector();

  /************************************************************************
  ** Stuff interface:
  ************************************************************************/

  /** The list of indexed list items. 
   *	This may be the same as <code>this</code> if the Stuff is a pure 
   *	list with no attributes.
   */
  public Stuff content() {
    return this;
  }

  /** The number of indexed items. */
  public int nItems() {
    return items == null? 0 : items.size();
  }

  /** Access an individual item */
  public Object at(int i) {
    if (i >= items.size()) { return null; }
    return items.elementAt(i);
  }

  /** Replace an individual item <em>i</em> with value <em>v</em>. */
  public Stuff at(int i, Object v) {
    if (i > items.size()) {
      // === not clear what to do here ===
    } else if (i == items.size()) {
      items.addElement(v);
    } else {
      items.setElementAt(v, i);
    }
    return this;
  }


  /** Remove and return the last item. */
  public Object pop() {
    if (items.size() == 0) { return null; }
    Object t = items.lastElement();
    items.removeElementAt(items.size()-1);
    return t;
  }

  /** Remove and return the first item. */
  public Object shift() {
    if (items.size() == 0) { return null; }
    Object t = items.firstElement();
    items.removeElementAt(0);
    return t;
  }


  /** Append a new value <em>v</em>.  
   *	Returns the modified Stuff, to simplify chaining. */
  public Stuff push(Object v) {
    items.addElement(v);
    return this;
  }

  /** Prepend a new value <em>v</em>.  
   *	Returns the modified Stuff, to simplify chaining. */
  public Stuff unshift(Object v) {
    items.insertElementAt(v, 0);
    return this;
  }


  /** Access a named attribute */
  public Object at(String a) {
    return null;
  }

  /** Add or replace an attribute */
  public Stuff at(String a, Object v) {
    push(a);
    return push(v);
  }

  /** Return an array of all the attribute keys. */
  public String[] keyList() {
    return null;
  }

  /** Return true if the Stuff is a pure hash table, with no items. */
  public boolean isTable() {
    return false;
  }

  /** Return true if the Stuff is a pure list, with no attributes. */
  public boolean isList() {
    return true;
  }

  /** Return true if the Stuff is an empty list. */
  public boolean isEmpty() {
    return nItems() == 0;
  }

  /** Return true if the Stuff is pure text, equivalent to a 
   * 	singleton list containing a String. */
  public boolean isText() {
    return nItems() == 1 && isText(at(0));
  }

  /** Return true if o is a String or StringBuffer. */
  public boolean isText(Object o) {
    return false;
  }

}
