// Stuff.java -- interface for Thing and related classes
// 	$Id$
// 	(c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.ds;

/** This is the interface for objects in the PIA that have a combination
 *	of indexed list items and named attributes.  It is designed to 
 *	make conversion of legacy PERL code as painless as possible.<p>
 *
 *	The names of some of the methods, e.g. push and pop, come from
 *	the original PERL implementation of the PIA; they have the
 *	virtue of being short and descriptive.  Access functions are
 *	overloaded (almost all are called <code>at</code>) so as to
 *	avoid polluting the name space; they also follow the PERL
 *	convention of adding a final argument for "set" functions.
 *	Specialized implementations are expected to define typed
 *	<code>item</code> and <code>attr</code> methods.<p>
 *
 *	Unlike the normal Java classes, which throw exceptions on
 *	out-of-bounds conditions, Stuff will either do nothing or return a
 *	null value (like PERL), as appropriate.  <p>
 *
 *	It is expected that some implementations of Stuff will be subclassed
 *	to have type-specific content.  In this case, it is expected that:
 *
 *	<ol>
 *	    <li> The insertion routines <code>push</code>, 
 *		 <code>unshift</code>, <code>at(int, Object)</code> and
 *		 <code>at(String, Object)</code> will perform type
 *		 checking.
 *	    <li> Hence, casting the result of a retrieval should never
 *		 raise an exception.
 *	    <li> Type-specific operations will be defined.  By convention
 *		 these are called <code>attr</code> for the keyed operations
 *		 and <code>itemAt</code> for the indexed ones.  Insertion
 *	 	 should use the <code>super</code> operations to bypass type
 *		 checking.
 *	</ol>
 */
public interface Stuff extends java.lang.Cloneable, java.io.Serializable {
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

  /** Return a List of all the attribute keys. */
  List keyList();

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

