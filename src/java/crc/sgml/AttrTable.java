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

  /** Enumerate the defined attributes. */
  public java.util.Enumeration attrs() {
    return keys();
  }

  /** Retrieve an attribute by name, returning its value as a String. */
  public String attrString(String name) {
    Object o = at(name);
    return (o == null)? null : o.toString();
  }

  /** Retrieve an attribute by name, returning its value as a boolean.
   *	Anything except a null string, the string "false", or the
   *	string "0" is considered to be true.
   */
    public boolean attrTrue(String name) {
    return Util.valueIsTrue(attr(name));
  }

  /** Add or replace an attribute.  Redefine the parent class's method
   *	to wrap the object in SGML. */
  public Stuff at(String a, Object v) {
    return super.at(a, Util.toSGML(v));
  }

  /** Set an attribute. */
  public void attr(String name, SGML value) {
    super.at(name, value);
  }
  
  /** Set an attribute to a String value. */
  public void attr(String name, String value) {
    super.at(name, new Text(value));
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

  public AttrTable(Attrs t) {
    this(t.nAttrs());
    append(t);
  }

  public AttrTable(List l) {
    this(l.nItems());
    append(l);
  }

  public AttrTable(AttrTable t) {
    append(t);
  }

  public AttrTable(Table t) {
    append(t);
  }

  public AttrTable(Enumeration e) {
    this();
    append(e);
  }

  public AttrTable(Enumeration e, boolean lowercase) {
    this();
    append(e, lowercase);
  }

  /** Construct from an HTML query string. */
  public AttrTable(String s) {
    List l = Util.split(s, '&');

    for (int i = 0; i < l.nItems(); ++i) 
      append(l.at(i).toString());
  }

  /** Construct from a Tokens list. */
  public AttrTable(Tokens t) {
    addAttrs(t);
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  public Object clone() {
    return new AttrTable(this);
  }

  /** Append Attrs */
  public void append(Attrs t) {
    Enumeration e = t.attrs();

    while (e.hasMoreElements()) {
      String k = e.nextElement().toString();
      at(k, t.attr(k));	
    }
  }

  /** Append an AttrTable */
  public void append(AttrTable t) {
    append((Attrs)t);
  }

  /** Append a table */
  public void append(Table t) {
    Enumeration e = t.keys();

    while (e.hasMoreElements()) {
      String k = e.nextElement().toString();
      at(k, t.get(k));	
    }
  }

  public void append(String s) {
    int i = s.indexOf('=');
    if (i < 0) {
      addAttr(s, s);
    } else {
      String name = s.substring(0, i);
      String value= (i == s.length()-1) ? "" : s.substring(i+1);
      value = Util.urlDecode(value);
      addAttr(name, value);
    }
  }

  public void append(SGML s) {
    String k = s.contentText().toString();
    addAttr(k, s);
  }


  /** Add attrs from a Tokens list.  &lt;li&gt; items are quietly removed
   *	if necessary, and associated with themselves.  &lt;dt&gt; and
   *	&lt;dd&lt; items are associated in pairs; missing &lt;dd&gt;'s
   *	go in as Token.empty. */
  public void addAttrs(Tokens t) {
    t = Util.removeSpaces(t);
    String k = null;
    SGML v;
    String tag;
    boolean dl = false;
    for (int i = 0; i < t.nItems(); ++i) {
      v = t.itemAt(i);
      tag = v.tag();
      //System.err.println("v=" + v + " tag=" + tag + " k=" + k + " i=" + i);
      if (tag == null) {
	if (k != null) attr(k, v); else append(v);
      } else if (tag.equals("li")) {
	v = v.content();
	if (v != null) v = v.simplify();
	if (k != null) attr(k, v); else append(v);
      } else if (tag.equals("dt")) {
	dl = true;
	v = Util.removeSpaces(v);
	k = v.contentText().toString();
	if (i == t.nItems()-1 || "dt".equals(t.itemAt(i+1).tag())) {
	  // missing dd : goes in as Token.empty
	  //attr(k, Token.empty);
	} 
      } else if (tag.equals("dd")) {
	dl = true;
	v = Util.removeSpaces(v);
	// dl's seem to be very odd indeed.
	if (k != null && attr(k) == null) attr(k, v);
      } else if (dl) {
	// ignore extraneous text in dl's
      } else if (v.isText()) {
	if (k != null) attr(k, v); else append(v.toString());
      } else {
	if (k != null) attr(k, v); else append(v);
      }
    }
  }

}
