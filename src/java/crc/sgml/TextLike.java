////// TextLike.java:  SGML text-like objects
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

import java.io.Writer;
import java.io.StringWriter;

import crc.ds.Index;

/**
 * Base class for SGML Text and Text-like objects.
 *	This is the base class for Tokens that behave syntactically
 *	like Text; i.e., they contain no markup.   <p>
 *
 *	Unlike Text itself, some subclasses are mutable and can be 
 *	modified.  Append may be done in place. <p>
 *
 *	@see crc.sgml.Text
 *
 */
public class TextLike extends Token {

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
    return new Text(toString());
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
    return new Text(toString());
  }

  /** The result of appending some SGML tokens. */
  public SGML append(SGML sgml) {
    if (sgml == null) return this;
    return append(sgml.toString());
  }

  /** The result of appending a string. */
  public SGML append(String s) {
    if (s == null) return this;
    if ("".equals(s)) return this;
    return new Text(toString() + s);
  }

  /** The result of appending some text. */
  public SGML appendText(Text t) {
    if (t == null) return this;
    return new Text(toString() + t.toString());
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

  /** Return the integer  value of the Text.  Anything that isn't a number
   *	gets returned as 0; no exceptions are thrown. */
  public long intValue() {
    String s = toString();
    if ("".equals(s)) return 0L;
    try {
      return java.lang.Long.valueOf(s).longValue();
    } catch (Exception e) {
      return 0L;
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

  public TextLike() {
    super("");
  }

  public TextLike(String tag) {
    super(tag);
  }


}
