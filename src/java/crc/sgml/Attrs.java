////// Attrs.java:  Interface for collections of SGML attributes
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

/** Interface for collections of SGML objects indexed by String keys.
 *	They are used not only for element attributes but for entity
 *	lookup tables and InterForm interpretor variables.
 */
public interface Attrs extends java.lang.Cloneable {

  /** Test whether attributes exist.  Almost always returns true; may
   *	true even if no attributes are currently defined. */
  boolean hasAttrs();

  /** Return the number of defined attributes. */
  int nAttrs();

  /** Test whether an attribute exists. */
  boolean hasAttr(String name);

  /** Retrieve an attribute by name.  Returns null if no such
   *	attribute exists. */
  SGML attr(String name);

  /** Retrieve an attribute by name, returning its value as a String. */
  String attrString(String name);

  /** Set an attribute.  Returns the object itself. */
  Attrs attr(String name, SGML value);

  /** Set an attribute.  Returns the object itself. */
  Attrs attr(String name, String value);

  /** Add an attribute.  Returns the object itself.  In some
   *	implementations this will be equivalent to attr, in others it
   *	will append to a list. */
  Attrs addAttr(String name, SGML value);

  /** Add an attribute with a String value. */
  Attrs addAttr(String name, String value);

  /** === security -- unclear at this point === */
}
