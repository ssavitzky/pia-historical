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

  /** Append some sgml.  Appends as text for Text, 
   *	content for Token;  merges lists and text. */
  public void appendSGML(SGML sgml) {
    if (sgml.isText()) addAttr(sgml.toString(), sgml.toText());
    else if (sgml instanceof Attrs) append((Attrs)sgml);
    else {
      attr(sgml.contentText().toString(), sgml);
    }
  }

  /** Append an attribute=value pair.  Checks for an embedded "=",
   *	which is taken as "name=value".  The value is URL-decoded.
   */
  public void appendAVPair(String s) {
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

  /** Append from a Tokens list.  &lt;li&gt; items are quietly removed
   *	if necessary, and associated with themselves.  &lt;dt&gt; and
   *	&lt;dd&lt; items are associated in pairs; missing &lt;dd&gt;'s
   *	go in as Token.empty. */
  public void addAttrs(Tokens t) {
    t = Util.removeSpaces(t);
    String k = null;
    SGML v;
    String tag;
    for (int i = 0; i < t.nItems(); ++i) {
      v = t.itemAt(i);
      tag = v.tag();
      if (tag == null) {
	if (k != null) attr(k, v); else appendSGML(v);
      } else if (v.isText()) {
	if (k != null) attr(k, v); else appendAVPair(v.toString());
      } else if (tag.equals("li")) {
	v = v.content();
	if (v != null) v = v.simplify();
	if (k != null) attr(k, v); else appendSGML(v);
      } else if (tag.equals("dt")) {
	if (i == t.nItems()-1 || "dt".equals(t.itemAt(i+1).tag())) {
	  // missing dd : goes in as Token.empty
	  attr(v.contentText().toString(), Token.empty);
	} else {
	  // otherwise just save the key for the next item.
	  k = v.contentText().toString();
	}
      } else {
	if (tag.equals("dd")) {
	  v = v.content().simplify();
	}
	if (k != null) attr(k, v); else appendSGML(v);
      }
    }
  }

}
