////// Text.java:  SGML text
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

/**
 * SGML text strings.
 *	Text is used in the InterForm package to represent sequences of
 *	characters that contain no markup.  The SGML interface is used to
 *	speed up the kinds of testing that the interpretor has to do. <p>
 *
 *	Like all SGML objects, SGML Text can be appended to; if 
 *	StringBuffer wasn't final we could simply extend it.  As it is,
 *	there are some advantages to being forced not to. <p>
 *
 *	=== Text should probably descend from Token. <p>
 *
 *	=== Text should have subclasses for wrapping StringBuffer and
 *	Object, and maybe another for wrapping Number.
 */
public class Text implements SGML {
  private Object content;
  private boolean isStringBuffer = false;
  private boolean isString = false;

  /************************************************************************
  ** Object operations:
  ************************************************************************/

  public String toString() {
    return content == null? "" :  content.toString();
  }

  public Object clone() {
    return new Text(this);
  }

  /************************************************************************
  ** SGML interface:
  ************************************************************************/

  /** Return true if the object is an individual SGML token. */
  public boolean isToken() {
    return true;
  }

  /** Return true if the object is an individual SGML element. */
  public boolean isElement() {
    return false;
  }

  /** Return true for a list of tokens. */
  public boolean isList() {
    return false;
  }

  /** Return true for an empty list or a token with no content. */
  public boolean isEmpty() {
    // === not clear what to do for "" === 
    return content == null;
  }

  /** Return true if the SGML is pure text, equivalent to a 
   * 	singleton list containing a String. */
  public boolean isText() {
    return true;
  }

  /** Return true if the object implements the Attrs interface */
  public boolean isAttrs() { return false; }

  /** Parser state:  0 for a complete element. */
  public byte incomplete() {
    return 0;
  }

  /** Set parser state.  Ignored for all but Token. */
  public void incomplete(byte i) {
  }

  /** A string ``tag'' that is guaranteed to be null if isList(),
   *	and "" if istext(). */
  public String tag() {
    return "";
  }

  /** Convert the entire object to text */
  public Text toText() {
    return this;
  }

  /** The object's content.  This is the same as this if isList(); 
   *	it is null if isEmpty(). */
  public Tokens content() {
    return null;
  }

  /** The object's content converted to a string. */
  public String contentString() {
    return toString();
  }

  /** The text part of the object's content. */
  public Text contentText() {
    return this;
  }

  /** The result of appending some SGML tokens. */
  public SGML append(SGML sgml) {
    if (sgml == null) return this;
    return appendText(sgml.toText());
  }

  /** The result of appending a string.  returns this. */
  public SGML append(String t) {
    if (t == null) return this;
    if (isStringBuffer) {
      ((StringBuffer)content).append(t);
    } else if (content == null) {
      content = new StringBuffer(t);
      isStringBuffer = true;
    } else {
      content = new StringBuffer(toString());
      ((StringBuffer)content).append(t);
      isStringBuffer = true;
    }
    return this;
  }

  /** The result of appending some text.  Same as this if isText(). */
  public SGML appendText(Text t) {
    if (t == null) return this;
    if (isStringBuffer) {
      ((StringBuffer)content).append(t);
    } else if (content == null) {
      content = new StringBuffer(t.toString());
      isStringBuffer = true;
    } else {
      content = new StringBuffer(toString());
      ((StringBuffer)content).append(t.toString());
      isStringBuffer = true;
    }
    return this;
  }

  /** Append this as text. */
  public void appendTextTo(SGML t) {
    t.append(this);
  }

  /** Append contents to a Tokens list. */
  public void appendContentTo(Tokens list) {
    list.append(this);
  }

  /** Convert to a single token if it's a singleton. */
  public SGML simplify() {
    return this;
  }

  /** Retrieve an attribute by name.  Text doesn't have any.*/
  public SGML attr(String name) {
    return null;
  }

  /** Retrieve an attribute by name, returning its value as a String. */
  public String attrString(String name) {
    return null;
  }

  /** Test whether an attribute exists.  It doesn't. */
  public boolean hasAttr(String name) {
    return false;
  }

  /** Return the numeric value of the Text.  Anything that isn't a number
   *	gets returned as 0.0; no exceptions are thrown. */
  public double numValue() {
    String s = toString();
    if ("".equals(s)) return 0.0;
    try {
      return java.lang.Double.valueOf(s).doubleValue();
    } catch (Exception e) {
      return 0.0;
    }
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public Text() {
  }

  public Text(Text t) {
    content = t.content;
    isStringBuffer = t.isStringBuffer;
    isString = t.isString;
    if (isStringBuffer) content = new StringBuffer(t.toString());
  }

  public Text(String s) {
    content = s;
    isStringBuffer = false;
    isString = true;
  }

  public Text(StringBuffer s) {
    content = s;
    isStringBuffer = true;
    isString = false;
  }

  public Text(Object v) {
    content = v;
    isStringBuffer = false;
    isString = false;
  }

  /** Join the elements of a Tokens list. */
  public static Text join(String sep, Tokens tl) {
    Text t = new Text();
    for (int i = 0; i < tl.nItems(); ++i) {
      if (i != 0 && sep != null) t.append(sep);
      t.append(tl.itemAt(i));
    }
    return t;
  }

}
