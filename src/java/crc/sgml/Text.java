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
 *	characters that contain no markup. <p>
 *
 *	Text, like String, is immutable.  A corresponding mutable TextBuffer
 *	is available for speeding up append operations by performing them
 *	in place.  A variety of other wrapper classes descended from 
 *	crc.sgml.TextLike are also available. <p>
 *
 *	@see crc.sgml.TextBuffer
 *	@see crc.sgml.TextLike
 */
public class Text extends TextLike {

  /** The String being wrapped as SGML Text. */
  protected final String content;

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

  public Text(long v) {
    super("");
    content = ""+v;
  }

  public Text(double v) {
    super("");
    content = ""+v;
  }

  public Text(SGML s) {
    super("");
    content = (s == null)? "" : s.toString();
  }

  /** Join the elements of a Tokens list. */
  public static Text join(String sep, Tokens tl) {
    TextBuffer t = new TextBuffer();
    for (int i = 0; i < tl.nItems(); ++i) {
      if (i != 0 && sep != null) t.append(sep);
      t.append(tl.itemAt(i));
    }
    return t.toText();
  }

  /** Convert an object to a Text if necessary. */
  public static Text valueOf(Object v) {
    if (v instanceof Text) return (Text)v;
    else return new Text(v.toString());
  }
}
