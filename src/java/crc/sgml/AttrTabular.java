////// AttrTabular.java:  Wrap an arbitrary Tabular object
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

import crc.sgml.Util;

import crc.ds.Tabular;
import crc.ds.List;

import java.util.Enumeration;

/**
 * Wraps any implementation of the Tabular interface as an Attrs.
 */
public class AttrTabular implements Attrs {

  Tabular items = null;
  
  /************************************************************************
  ** Attrs interface:
  ************************************************************************/

  /** Test whether attributes exist. */
  public boolean hasAttrs() {
    return items != null;
  }

  /** Return the number of defined. */
  public int nAttrs() {
    Enumeration keys = items.keys();
    if (keys == null) return 0;
    int i = 0;
    for (i = 0; keys.hasMoreElements(); ++i) keys.nextElement();
    return i;			// expensive to compute.
  }

  /** Test whether an attribute exists. */
  public boolean hasAttr(String name) {
    return items.get(name) != null;
  }
  
  /** Retrieve an attribute by name.  Returns null if no such
   *	attribute exists. */
  public SGML attr(String name) {
    return Util.toSGML(items.get(name));
  }

  /** Enumerate the defined attributes. */
  public java.util.Enumeration attrs() {
    return items.keys();
  }

  /** Retrieve an attribute by name, returning its value as a String. */
  public String attrString(String name) {
    Object o = items.get(name);
    return (o == null)? null : o.toString();
  }

  /** Retrieve an attribute by name, returning its value as a boolean.
   *	Anything except a null string, the string "false", or the
   *	string "0" is considered to be true.
   */
    public boolean attrTrue(String name) {
    return Util.valueIsTrue(attr(name));
  }

  /** Set an attribute. */
  public void attr(String name, SGML value) {
    items.put(name, value);
  }
  
  /** Set an attribute to a String value. */
  public void attr(String name, String value) {
    items.put(name, new Text(value));
  }

  /** Add an attribute.  Returns the object itself.  In this implementation
   *	addAttr is equivalent to attr. */
  public Attrs addAttr(String name, SGML value) {
    items.put(name, value);
    return this;
  }

  /** Add an attribute.  Returns the object itself. */
  public Attrs addAttr(String name, String value) {
    items.put(name, new Text(value));
    return this;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public AttrTabular() {
    super();
  }

  public AttrTabular(Tabular t) {
    items = t;
  }

  public AttrTabular(Enumeration e) {
    this();
    append(e);
  }

  public AttrTabular(Enumeration e, boolean lowercase) {
    this();
    append(e, lowercase);
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  public Object clone() {
    return new AttrTabular(items);
  }

  /** Append Attrs */
  public void append(Attrs t) {
    Enumeration e = t.attrs();

    while (e.hasMoreElements()) {
      String k = e.nextElement().toString();
      items.put(k, t.attr(k));	
    }
  }

  /** Append an Enumeration */
  public void append(Enumeration e) {
    while (e.hasMoreElements()) {
      String k = e.nextElement().toString();
      items.put(k, k);	
    }
  }

  /** Append an Enumeration */
  public void append(Enumeration e, boolean lowercase) {
    while (e.hasMoreElements()) {
      String v = e.nextElement().toString();
      String k = (lowercase)? v.toLowerCase() : v;
      items.put(k, v);	
    }
  }

  /** Append a tabular */
  public void append(Tabular t) {
    Enumeration e = t.keys();
    while (e.hasMoreElements()) {
      String k = e.nextElement().toString();
      items.put(k, t.get(k));	
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
