////// AttrBase.java:  Base class for Attrs interface
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

import crc.sgml.Util;

import crc.ds.List;
import java.util.Enumeration;

/**
 * Abstract base class for classes that implement the Attrs interface by 
 *	delegating the <code>attrs</code> method.
 */
public abstract class AttrBase implements Attrs {

  /************************************************************************
  ** Attrs interface:  Abstract:
  ************************************************************************/

  /** Return the number of defined. */
  public abstract int nAttrs();

  /** Test whether an attribute exists. */
  public abstract boolean hasAttr(String name);
  
  /** Retrieve an attribute by name.  Returns null if no such
   *	attribute exists. */
  public abstract SGML attr(String name);

  /** Enumerate the defined attributes. */
  public abstract java.util.Enumeration attrs();

  /** Set an attribute. */
  public abstract void attr(String name, SGML value);
  
  /************************************************************************
  ** Attrs interface:
  ************************************************************************/

  /** Test whether attributes exist. */
  public boolean hasAttrs() {
    return true;
  }

  /** Retrieve an attribute by name, returning its value as a String. */
  public String attrString(String name) {
    SGML s = attr(name);
    return (s == null)? null : s.toString();
  }

  /** Retrieve an attribute by name, returning its value as a boolean.
   *	Anything except a null string, the string "false", or the
   *	string "0" is considered to be true.
   */
  public boolean attrTrue(String name) {
    return Util.valueIsTrue(attr(name));
  }

  /** Set an attribute with a String value. */
  public void attr(String name, String value) {
    attr(name, new Text(value));
  }

  /** Add an attribute.  Returns the object itself.  In this implementation
   *	addAttr is equivalent to attr. */
  public Attrs addAttr(String name, SGML value) {
    attr(name, value);
    return this;
  }

  /** Add an attribute with a String value.  Returns the object itself. */
  public Attrs addAttr(String name, String value) {
    attr(name, value);
    return this;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public AttrBase() {
  }

  public AttrBase(int initialCapacity) {
  }

  public AttrBase(Attrs t) {
    this(t.nAttrs());
    append(t);
  }

  public AttrBase(crc.ds.List l) {
    this(l.nItems());
    append(l);
  }

  public AttrBase(Enumeration e) {
    this();
    append(e);
  }

  public AttrBase(Enumeration e, boolean lowercase) {
    this();
    append(e, lowercase);
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Append attributes */
  public void append(Attrs t) {
    Enumeration e = t.attrs();

    while (e.hasMoreElements()) {
      String k = e.nextElement().toString();
      attr(k, t.attr(k));
    }
  }

  /** Append a List. */
  public void append(List l) {
    append(l.elements());
  }

  /** Append an Enumeration. */
  public void append(Enumeration e) {
    while (e.hasMoreElements()) {
      Object v = e.nextElement();
      attr(v.toString(), Util.toSGML(v));
    }
  }

  /** Append an Enumeration, optionally lowercasing the keys. */
  public void append(Enumeration e, boolean lowercase) {
    if (!lowercase) {
      append(e);
      return;
    }
    while (e.hasMoreElements()) {
      Object v = e.nextElement();
      attr(v.toString().toLowerCase(), Util.toSGML(v));
    }
  }

  /** Append key, value pairs from an enumeration. */
  public void appendPairs(Enumeration e) {
    while (e.hasMoreElements()) {
      Object k = e.nextElement();
      Object v = e.nextElement();
      attr(k.toString(), Util.toSGML(v));
    }
  }

  /** Append a list of [key, value...] pairs. */
  public void appendPairs(List l) {
    appendPairs(l.elements());
  }


}
