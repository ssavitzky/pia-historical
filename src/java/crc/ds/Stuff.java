// Stuff.java -- interface for Thing and related classes
// 	$Id$
// 	(c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.ds;

/** This is the interface for objects in the PIA that have a combination
 *	of indexed list items and named attributes.  It is designed to 
 *	make conversion of legacy PERL code as painless as possible.<p>
 *
 *	The names of some of the methods, e.g. push and pop, come from
 *	the original PERL implementation of the PIA.  They have the
 *	virtue of being short and descriptive.  Access functions are
 *	overloaded (almost all are called <code>at</code>) so as to
 *	avoid polluting the name space; they also follow the PERL
 *	convention of adding a final argument for "set" functions.
 *	Specialized implementations are expected to define typed
 *	<code>item</code> and <code>attr</code> methods.<p>
 *
 *	Unlike the normal Java classes, which throw exceptions on
 *	out-of-bounds conditions, Stuff will either do nothing or return a
 *	null value (like PERL).  
 */
public interface Stuff {
  /** The number of indexed items. */
  int nItems();

  /** Access an individual item */
  Object at(int i);

  /** Replace an individual item <em>i</em> with value <em>v</em>. */
  Stuff at(int i, Object v);


  /** Remove and return the last item. */
  Object pop();

  /** Remove and return the first item. */
  Object shift();


  /** Append a new value <em>v</em>.  
   *	Returns the modified Stuff, to simplify chaining. */
  Stuff push(Object v);

  /** Prepend a new value <em>v</em>.  
   *	Returns the modified Stuff, to simplify chaining. */
  Stuff unshift(Object v);


  /** Access a named attribute */
  Object at(String a);

  /** Add or replace an attribute */
  Stuff at(String a, Object v);

  /** Return an array of all the attribute keys. */
  String[] keyList();


  /** Return true if the Stuff is a pure hash table, with no items. */
  boolean isTable();

  /** Return true if the Stuff is a pure list, with no attributes. */
  boolean isList();

  /** Return true if the Stuff is an empty list. */
  boolean isEmpty();

  /** Return true if the Stuff is pure text, equivalent to a 
   * 	singleton list containing a String. */
  boolean isText();

}

