////// AttrTable.java:  Table of SGML attributes
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

import crc.sgml.Util;

import crc.ds.Table;
import crc.ds.List;
import crc.ds.Stuff;

import java.util.Enumeration;

/**
 * A hash table of SGML objects indexed by String keys.
 */
public class AttrTable extends Table implements Attrs {

  /************************************************************************
  ** Attrs interface:
  ************************************************************************/

  /** Test whether attributes exist. */
  public boolean hasAttrs() {
    return nItems() > 0;
  }

  /** Return the number of defined. */
  public int nAttrs() {
    return nItems();
  }

  /** Test whether an attribute exists. */
  public boolean hasAttr(String name) {
    return has(name);
  }
  
  /** Retrieve an attribute by name.  Returns null if no such
   *	attribute exists. */
  public SGML attr(String name) {
    return (SGML)at(name);
  }

  /** Retrieve an attribute by name, returning its value as a String. */
  public String attrString(String name) {
    Object o = at(name);
    return (o == null)? null : o.toString();
  }

  /** Add or replace an attribute.  Redefine the parent class's method
   *	to wrap the object in SGML. */
  public Stuff at(String a, Object v) {
    return super.at(a, Util.toSGML(v));
  }

  /** Set an attribute.  Returns the object itself. */
  public Attrs attr(String name, SGML value) {
    super.at(name, value);
    return this;
  }
  
  /** Set an attribute.  Returns the object itself. */
  public Attrs attr(String name, String value) {
    super.at(name, new Text(value));
    return this;
  }

  /** Add an attribute.  Returns the object itself.  In this implementation
   *	addAttr is equivalent to attr. */
  public Attrs addAttr(String name, SGML value) {
    super.at(name, value);
    return this;
  }

  /** Add an attribute.  Returns the object itself. */
  public Attrs addAttr(String name, String value) {
    super.at(name, new Text(value));
    return this;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public AttrTable() {
    super();
  }

  public AttrTable(int initialCapacity) {
    super(initialCapacity);
  }

  public AttrTable(AttrTable t) {
    this(t.size());
    append(t);
  }

  public AttrTable(List l) {
    this(l.nItems());
    append(l);
  }

  public AttrTable(Enumeration e) {
    this();
    append(e);
  }

  public AttrTable(Enumeration e, boolean lowercase) {
    this();
    append(e, lowercase);
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  public Object clone() {
    return new AttrTable(this);
  }

  /** Append a table */
  public void append(AttrTable t) {
    Enumeration e = t.keys();

    while (e.hasMoreElements()) {
      Object k = e.nextElement();
      put(k, t.get(k));		// can use put because t is an AttrTable
    }
  }


}
