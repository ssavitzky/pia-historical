////// Text.java:  SGML text
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

import java.io.Writer;
import java.io.StringWriter;

import crc.ds.Index;

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
 *	=== Text should have subclasses for wrapping StringBuffer and
 *	Object, and maybe another for wrapping Number.
 */
public class Text extends Token {
  protected String content;

  /************************************************************************
  ** Object operations:
  ************************************************************************/

  public String toString() {
    return content == null? "" :  content;
  }

  public Object clone() {
    return new Text(this);
  }

  /************************************************************************
  ** SGML interface:
  ************************************************************************/

  /** Return true for an empty list or a token with no content. */
  public boolean isEmpty() {
    return false;
  }

  /** Return true if the SGML is pure text, equivalent to a 
   * 	singleton list containing a String. */
  public boolean isText() {
    return true;
  }

  public boolean isList() {
    return false;
  }

  public boolean isElement() {
    return false;
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
    //return new Tokens(this);
    return append(sgml.toString());
  }

  /** The result of appending a string.  returns this. */
  public SGML append(String t) {
    if (t == null) return this;
    /*
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
    */
    content += t;
    return this;
  }

  /** The result of appending some text.  Same as this if isText(). */
  public SGML appendText(Text t) {
    if (t == null) return this;
    /*
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
    */
    content += t.toString();
    return this;
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

  public void writeOn(Writer w) {
    try {
      w.write(toString());
    } catch (java.io.IOException e) {}
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public Text() {
    super("");
    content = "";
  }

  public Text(Text t) {
    super("");
    content = t.toString();
  }

  public Text(String s) {
    super("");
    content = s;
  }

  public Text(SGML s) {
    super("");
    content = (s == null)? "" : s.toString();
  }

  /** Join the elements of a Tokens list. */
  public static Text join(String sep, Tokens tl) {
    Text t = new TextBuffer();
    for (int i = 0; i < tl.nItems(); ++i) {
      if (i != 0 && sep != null) t.append(sep);
      t.append(tl.itemAt(i));
    }
    return t;
  }

}
