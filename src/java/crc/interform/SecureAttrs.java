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
import crc.sgml.AttrTabular;

import crc.ds.List;
import crc.ds.Tabular;

/** A ``secure'' SGML wrapper for an Attrs object.  It is secure in
 *	the sense that it can check the Interp and its Environment to
 *	see whether certain operations are permitted in the current
 *	context.  Since it extends <code>crc.sgml.AttrSGML</code>, it
 *	can be put into an <code>attrs</code> object. 
 */
public class SecureAttrs extends AttrTabular {

  protected Interp context;


  /************************************************************************
  ** Construction:
  ************************************************************************/

  public SecureAttrs(Interp context) {
    super();
    this.context = context;
  }

  /** Make an SecureAttrs using Tabular.  This is
   *	<em>not</em> cloned or copied. */
  public SecureAttrs(Tabular t, Interp context) {
    super(t);
    this.context = context;
  }

}
