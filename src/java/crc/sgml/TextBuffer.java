////// TextBuffer.java:  SGML text wrapping a StringBuffer
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

import crc.ds.Index;

/**
 * SGML Text Buffer.
 *	TextBuffer is meant to be appended to.  It is the only subclass
 *	of Text that, like Tokens, appends ``in place''. <p>
 */
public class TextBuffer extends TextLike {

  /** The StringBuffer being wrapped. */
  protected final StringBuffer buffer;

  /************************************************************************
  ** Object operations:
  ************************************************************************/

  public String toString() {
    return buffer == null? "" :  buffer.toString();
  }

  public Object clone() {
    return new TextBuffer(buffer);
  }

  public StringBuffer getBuffer() {
    return buffer;
  }

  /************************************************************************
  ** SGML interface:
  ************************************************************************/

  /** Return true for an empty list or a token with no content. */
  public boolean isEmpty() {
    return buffer == null || buffer.length() == 0;
  }

  /** Convert the entire object to text */
  public Text toText() {
    return new Text(toString());
  }

  /** The result of appending some SGML tokens.  returns this. */
  public SGML append(SGML sgml) {
    if (sgml == null) return this;
    return append(sgml.toString());
  }

  /** The result of appending a string.  returns this. */
  public SGML append(String t) {
    if (t == null) return this;
    buffer.append(t);
    return this;
  }

  /** The result of appending some text.  Same as this if isText(). */
  public SGML appendText(Text t) {
    if (t == null) return this;
    buffer.append(t.toString());
    return this;
  }


  /** Convert to a single token if it's a singleton. */
  public SGML simplify() {
    return this;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public TextBuffer() {
    super();
    buffer = new StringBuffer();
  }

  public TextBuffer(Text t) {
    super();
    buffer = new StringBuffer(t == null? "" : t.toString());
  }

  public TextBuffer(String s) {
    super();
    buffer = new StringBuffer(s == null? "" : s);
  }

  /** When constructing a TextBuffer out of a StringBuffer, the StringBuffer
   *	is <em>not</em> copied. */
  public TextBuffer(StringBuffer s) {
    super();
    buffer = s;
  }

}
