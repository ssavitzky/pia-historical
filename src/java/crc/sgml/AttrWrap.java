////// AttrWrap.java:  Wrap class for Attrs interface
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

import crc.sgml.Util;

import crc.ds.List;
import java.util.Enumeration;

/** Minimum concrete extension of AttrSGML, done by wrapping some
 * other Attr object and delegating to it.
 */
public class AttrWrap extends AttrSGML {

  protected Attrs attributes;

  /************************************************************************
  ** Attrs interface:  Abstract:
  ************************************************************************/

  /** Return the number of defined attributes. */
  public int nAttrs() {
    return (attributes == null)? 0 : attributes.nAttrs();
  }

  /** Test whether an attribute exists. */
  public boolean hasAttr(String name) {
    return (attributes == null)? false : attributes.hasAttr(name);
  }
  
  /** Retrieve an attribute by name.  Returns null if no such
   *	attribute exists. */
  public SGML attr(String name) {
    return (attributes == null)? null : attributes.attr(name);
  }

  /** Enumerate the defined attributes. */
  public java.util.Enumeration attrs() {
    return (attributes == null)? new AttrTable().attrs() : attributes.attrs();
  }

  /** Set an attribute. */
  public void attr(String name, SGML value) {
    if (attributes == null) attributes = new AttrTable();
    attributes.attr(name, value);
  }    

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public AttrWrap() {
    attributes = new AttrTable();
  }

  public AttrWrap(int initialCapacity) {
    attributes = new AttrTable(initialCapacity);
  }

  /** Make an AttrWrap out of an Attrs by wrapping it.  The Attrs is
   *	<em>not</em> cloned or copied. */
  public AttrWrap(Attrs t) {
    attributes = t;
  }

  public AttrWrap(List l) {
    this(l.nItems());
    append(l);
  }

  public AttrWrap(Enumeration e) {
    this();
    append(e);
  }

  public AttrWrap(Enumeration e, boolean lowercase) {
    this();
    append(e, lowercase);
  }

}
