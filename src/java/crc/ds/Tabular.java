// Tabular.java -- interface for tabular data
// 	$Id$
// 	(c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.ds;

import java.util.Enumeration;

/** This is the interface for objects in the PIA that behave like
 *	<code>Map</code>s or <code>HashTable</code>s.
 *
 * <p> The names of some of the methods, e.g. <code>get</code>,
 * 	<code>set</code>, and <code>size</code>, come from Java's
 * 	<code>HashTable</code> and <code>Map</code>. This is in contrast with
 * 	the older <code>Stuff</code>, which is derived from Perl.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * @see java.util.HashTable
 * @see crc.ds.Stuff
 */
public interface Tabular {
  /** The number of items. */
  int size();

  /** Access an individual item by name. */
  Object get(String key);

  /** Replace an individual named item with value <em>v</em>. */
  void set(String key, Object v);

  /** Return an enumeration of all the  keys. */
  Enumeration keys();
}

