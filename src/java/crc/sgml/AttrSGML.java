////// AttrSGML.java:  Base class for Attrs and SGML interfaces
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

import crc.sgml.Util;

import crc.ds.List;
import crc.ds.Index;
import java.util.Enumeration;

/**
 * Abstract base class for classes that implement the Attrs interface by 
 *	delegating the <code>attrs</code> method, and also implement the full 
 *	SGML interface.
 */
public abstract class AttrSGML extends AttrBase implements SGML {

  /************************************************************************
  ** SGML Interface:
  ************************************************************************/

  /** Return true if the object is an individual SGML token. */
  public boolean isToken() { return false; }

  /** Return true if the object is an individual SGML element.  This is
   *	<em>only</em> true if the object is a member of class Token.
   */
  public boolean isElement() { return false; }

  /** Return true for a list of tokens. */
  public boolean isList() { return false; }

  /** Return true for an empty list or a token with no content. */
  public boolean isEmpty() { return true; }

  /** Return true if the SGML is pure text, or a
   * 	singleton list containing a Text. */
  public boolean isText() { return false; }

  /** Return true if the object implements the Attrs interface */
  public boolean isAttrs() { return true; }

/** Return SGML associated with index
 */
public SGML attr(Index  path)
  {
      //punt on anything other than simple string
    return attr(path.string());
  }
  

  /** Parser state:  0 for a complete element, 1 for a start tag, -1
   *	for an end tag.  */
  public byte incomplete() { return 0; }

  /** Set parser state.  Ignored for all but Token. */
  public void incomplete(byte i) {}

  /** A string ``tag'' that is guaranteed to be null if isList(),
   *	"" if istext(), and "&amp;" if instanceof Entity. */
  public String tag() { return null; }

  /** Convert the entire object to text.  The result for an Attrs is a
   *  query string.
   */
  public Text toText() {
    Text t = new Text();
    java.util.Enumeration keys = attrs();

    while (keys.hasMoreElements()) {
      String key = keys.nextElement().toString();
      t.append(key);
      t.append("=");
      t.append(java.net.URLEncoder.encode(attr(key).toString()));
      if (keys.hasMoreElements()) t.append("&");
    }
    return t;
  }

  public String toString() {
    String t = "";
    java.util.Enumeration keys = attrs();

    while (keys.hasMoreElements()) {
      String key = keys.nextElement().toString();
      t += key;
      t += ("=");
      t += java.net.URLEncoder.encode(attr(key).toString());
      if (keys.hasMoreElements()) t += '&';
    }
    return t;
  }

  /** Convert the object to a single Element.  The result is a &gt;dl&lt;
   * 	element. */
  public Element toElement() {
    return new Element("dl", (Attrs)this);
  }

  /** Convert to a single token if it's a singleton list. */
  public SGML simplify() { return this; }


  /** The object's content.  This is the same as <code>this</code> 
   *	if isList(); it is null if isEmpty(). */
  public Tokens content() { return toElement().content(); }

  /** The text part of the object's content. */
  public Text contentText() { return toElement().contentText(); }

  /** The object's content as a String.  Always returns a valid String,
   *	which may be null. */
  public String contentString() { return toString(); }

  /** The result of appending some sgml.  Appends as text for Text, 
   *	content for Token;  merges lists and text. */
  public SGML append(SGML sgml) {
    if (sgml.isText()) appendText(sgml.toText());
    else if (isAttrs()) append((Attrs)sgml);
    else {
      // unimplemented
    }
    return this;
  }

  /** The result of appending some text.  Slightly more efficient. */
  public SGML appendText(Text t) {
    addAttr(t.toString(), t);
    return this;
  }

  /** The result of appending a string.  Checks for an embedded "=",
   *	which is taken as "name=value".  The value is URL-decoded.
   */
  public SGML append(String s) {
    int i = s.indexOf('=');
    if (i < 0) {
      addAttr(s, s);
    } else {
      String name = s.substring(0, i);
      String value= (i == s.length()-1) ? "" : s.substring(i+1);
      value = Util.urlDecode(value);
      addAttr(name, value);
    }
    return this;
  }

  /** Append this as text.  Note that the destination need <em>not</em>
   * 	be pure text; it could be a list or a token.  */
  public void appendTextTo(SGML sgml) {
    sgml.appendText(toText());
  }

  /** Append content to a Tokens list. */
  public void appendContentTo(Tokens list) {
    content().appendContentTo(list);
  }

  /** Convert to a number (double, being the most general form available). */
  public double numValue() {
    return nAttrs();
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public AttrSGML() {
  }

  public AttrSGML(int initialCapacity) {
  }

  public AttrSGML(Attrs t) {
    this(t.nAttrs());
    append(t);
  }

  public AttrSGML(crc.ds.List l) {
    this(l.nItems());
    append(l);
  }

  public AttrSGML(Enumeration e) {
    this();
    append(e);
  }

  public AttrSGML(Enumeration e, boolean lowercase) {
    this();
    append(e, lowercase);
  }

  /** Construct from an HTML query string. */
  public AttrSGML(String s) {
    List l = Util.split(s, '&');

    for (int i = 0; i < l.nItems(); ++i) 
      append(l.at(i).toString());
  }

}
