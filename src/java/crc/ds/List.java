// List.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.ds;

import java.util.Vector;
import java.util.Enumeration;

/** List of objects.  Essentially the same as
 *	<code>java.util.Vector</code> except that indexing with
 *	out-of-range indices (using <code>at</code>) returns null
 *	instead of causing an exception.  We can't simply extend
 *	Vector, though, because almost all of Vector's operations are
 *	final, including <code>toString</code>.<p>
 *
 *	List is intended to make life as easy as possible for people
 *	porting programs from PERL; many PERL array operations are
 *	supported, especially when the elements are Strings.
 *
 *	@see java.util.Vector */
public class List implements Stuff {

  /************************************************************************
  ** Components:
  ************************************************************************/

  /** The actual items. */
  protected Vector items = new Vector();

  /** The list of indexed list items. 
   *	This may be the same as <code>this</code> if the Stuff is a pure 
   *	list with no attributes.
   */
  public List itemList() {
    return this;
  }

  public Enumeration elements() {
    return items.elements();
  }

  /************************************************************************
  ** Stuff interface:
  ************************************************************************/

  /** The number of indexed items. */
  public int nItems() {
    return items == null? 0 : items.size();
  }

  /** The number of indexed items. */
  public int size() {
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
      // === not clear what to do here.  Throw an exception for now. ===
      items.setElementAt(v, i);
    } else if (i == items.size()) {
      items.addElement(v);
    } else {
      items.setElementAt(v, i);
    }
    return this;
  }

  /** Return the index of the first occurrance of a value equal to a
   *  given object.  Returns -1 if not found. */
  public int indexOf(Object o) {
    Object it;
    for (int i = 0; i < nItems(); ++i) {
      it = items.elementAt(i);
      if (o == it || (o != null && o.equals(it))) return i;
    } 
    return -1;
  }

  /** Return the index of the last occurrance of a value equal to a
   *  given object.  Returns -1 if not found. */
  public int lastIndexOf(Object o) {
    Object it;
    for (int i = nItems() - 1; i >= 0; --i) {
      it = items.elementAt(i);
      if (o == it || (o != null && o.equals(it))) return i;
    } 
    return -1;
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
  public List keyList() {
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

  /************************************************************************
  ** additional operations:
  ************************************************************************/

  public void clear() {
    items.removeAllElements();
  }
   
  public String join(String seperator) {
    String result = "";
    
    for (int i = 0; i < nItems(); ++i) result += at(i) + seperator;
    return result;
    
  } 
  /************************************************************************
  ** Construction and copying:
  ************************************************************************/

  public List() {
  }

  public List(List l) {
    for (int i = 0; i < l.nItems(); ++i) push(l.at(i));
  }

  public List(Enumeration e) {
    append(e);
  }

  public Object clone() {
    return new List(this);
  }

  public void append(List l) {
    for (int i = 0; i < l.nItems(); ++i) push(l.at(i));
  }

  public void append(Enumeration e) {
    while (e.hasMoreElements()) push(e.nextElement());
  }

}


