////// SecureAttrs.java:  Wrap class for Attrs interface
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;

import crc.interform.Interp;
import crc.interform.Environment;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Attrs;
import crc.sgml.AttrTable;
import crc.sgml.AttrWrap;

import crc.ds.List;

/** A ``secure'' SGML wrapper for an Attrs object.  It is secure in
 *	the sense that it can check the Interp and its Environment to
 *	see whether certain operations are permitted in the current
 *	context.  Since it extends <code>crc.sgml.AttrSGML</code>, it
 *	can be put into an <code>attrs</code> object. 
 */
public class SecureAttrs extends AttrWrap {

  protected Interp context;

  /************************************************************************
  ** Attrs interface:  Abstract:
  ************************************************************************/

  /** Return the number of defined attributes. */
  public int nAttrs() {
    // === security checks unimplemented ===
    return (attributes == null)? 0 : attributes.nAttrs();
  }

  /** Test whether an attribute exists. */
  public boolean hasAttr(String name) {
    // === security checks unimplemented ===
    return (attributes == null)? false : attributes.hasAttr(name);
  }
  
  /** Retrieve an attribute by name.  Returns null if no such
   *	attribute exists. */
  public SGML attr(String name) {
    // === security checks unimplemented ===
    return (attributes == null)? null : attributes.attr(name);
  }

  /** Enumerate the defined attributes. */
  public java.util.Enumeration attrs() {
    // === security checks unimplemented ===
    return (attributes == null)? new AttrTable().attrs() : attributes.attrs();
  }

  /** Set an attribute. */
  public void attr(String name, SGML value) {
    // === security checks unimplemented ===
    if (attributes == null) attributes = new AttrTable();
    attributes.attr(name, value);
  }    

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public SecureAttrs(Interp context) {
    super();
    this.context = context;
  }

  /** Make an SecureAttrs out of an Attrs by wrapping it.  The Attrs is
   *	<em>not</em> cloned or copied. */
  public SecureAttrs(Attrs t, Interp context) {
    super(t);
    this.context = context;
  }

}
