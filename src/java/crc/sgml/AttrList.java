// AttrList.java:  Attribute list
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

import crc.ds.List;
import crc.ds.Table;

/**
 * The representation of a list of named attributes.  Each list item
 *	has both an index (position) in the list, and an associated
 *	String <em>name</em).  <p>
 *
 *	Items can be retrieved either by name (<em>not</em> forced to
 *	lowercase) or by index.  Items that are defined but have no
 *	associated value have an empty token for their value; a static
 *	constant (<code>Token.empty</code>) is provided for the
 *	purpose.  Retrieval by name uses a Table for speed. <p>
 *
 *	@see crc.sgml.Tokens  */
public class AttrList extends Tokens {

  /** An empty token, used as the value for an attribute with no value */
  public static final Token empty = new Token();

  /************************************************************************
  ** Components:
  ************************************************************************/

  /** Attributes. */
  Table attrs = null;

  /** Attribute names, in the order defined.  
   *	Names are kept in their original form (i.e. uppercase), and multiple
   *	entries are permitted.
   */
  List attrNames = null;

  /** Formatting: */
  String itemBegin;
  String itemSep;
  String itemEnd;

  /************************************************************************
  ** SGML Predicates:
  ************************************************************************/
  
  /** Test whether the Token consists only of text. */
  public boolean isText() {
    return false;
  }


  /************************************************************************
  ** Access to attributes:
  ************************************************************************/
  
  /* === Not clear what to do about types.  It's plausible to require
   *	attr, etc. to return Token, and do the conversion from Object
   *	if necessary.
   */

  /** Retrieve an attribute by name. */
  public SGML attr(String name) {
    return (SGML)attrs.at(name);
  }

  /** Retrieve an attribute by name, returning its value as a String. */
  public String attrString(String name) {
    Object attr = attrs.at(name);
    return (attr == null)? null : attr.toString();
  }

  /** Set an attribute's value, recording its name if it has not yet
   *	been defined.
   */
  public Token attr(String name, SGML value) {
    if (attrs == null) {
      attrs = new Table();
      attrNames = new List();
    }
    if (! attrs.has(name)) {
      /* New attribute: make entries in attrNames and attrValues;
      attrNames.push(name);
      push(value);
    } else {
      /* fix up last occurrance in attrValues */
      int i = attrNames.lastIndexOf(name);
      at(i, value);
    }
    attrs.at(name, value);
    return this;
  }

  /** Set an attribute to an arbitrary object */
  public Token attr(String name, Object value) {
    return attr(name, new Text(value));
  }

  /** Append an attribute name and value to attrNames and attrValues, and
   *	stash its value in attrs for easy lookup.
   */
  public Token addAttr(String name, SGML value) {
    if (attrs == null) {
      attrs = new Table();
      attrNames = new List();
      attrValues= new List();
    }
    attrNames.push(name);
    attrValues.push(value);
    attrs.at(name, value);
    return this;
  }

  /** Return the number of recorded attributes */
  public int nAttrs() {
    return (attrNames == null)? 0 : attrNames.nItems();
  }

  /** Retrieve an attribute's name by its index in attrNames */
  public String attrNameAt(int i) {
    return (i >= nAttrs())? null : attrNames.at(i).toString();
  }

  /** Retrieve an attribute's value by its index in attrNames */
  public SGML attrValueAt(int i) {
    return (i >= nAttrs())? null : (SGML)attrValues.at(i);
  }

  /** Change an attribute's value by its index in attrNames.  Both the 
   *	value in attrValues and the one in the attrs Table are changed.
   */
  public Token attrValueAt(int i, SGML value) {
    at(i, value);
    attr((String)attrNames.at(i), value);
    return this;
  }

  /************************************************************************
  ** Access to content:
  ************************************************************************/
  
  public Tokens content() {
    return this;
  }

  /************************************************************************
  ** SGML list interface:
  ************************************************************************/

  public SGML append(SGML v) {
    push(v);
    attrNames.push(v.toString);
    return this;
  }

  public SGML appendText(Text v) {
    return append(v);
  }


  /************************************************************************
  ** Construction:
  ************************************************************************/

  public AttrList () {
    super();
  }

  public AttrList (SGML content) {
    append(content);
  }

  public AttrList (Attrs content) {
    append(content);
  }

  /************************************************************************
  ** Conversion to String:
  ************************************************************************/


  public Text toText() {
    Text t = new Text();
    appendTextTo(t);
    return t;
  }

  public String toString() {
    return toText().toString();
  }

  public void appendTextTo(SGML t) {
    /* Format-dependent */
    for (int i = 0; i < nItems(); ++i) {
      if (itemStart != null) t.append(itemStart);
      t.append(nameAt(i));
      if (itemAt(i) != null && itemAt(i) != Token.empty) {
	if (itemSep != null) t.append(itemSep);
	t.append(itemAt(i));
      }
      if (itemEnd != null) t.append(itemEnd);
    }
  }

  /************************************************************************
  ** Access to parts of content:
  ************************************************************************/


}
