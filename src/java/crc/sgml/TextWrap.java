////// TextWrap.java:  SGML text
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

import crc.ds.Index;

/**
 * SGML text wrapper for arbitrary objects.
 *	Text is used in the InterForm package to represent sequences of
 *	characters that contain no markup.  The SGML interface is used to
 *	speed up the kinds of testing that the interpretor has to do. <p>
 */
public class TextWrap extends Text {
  private Object value;

  /************************************************************************
  ** Object operations:
  ************************************************************************/

  public String toString() {
    return value == null? "" :  value.toString();
  }

  public Object clone() {
    return new TextWrap(value);
  }

  /************************************************************************
  ** SGML interface:
  ************************************************************************/

  /** The result of appending a string.  returns this. */
  public SGML append(String t) {
    if (t == null) return this;
    return new Text(value.toString() + t);
  }

  /** The result of appending some text.  Same as this if isText(). */
  public SGML appendText(Text t) {
    return append(t.toString());
  }

  /** Append this as text. */
  public void appendTextTo(SGML t) {
    t.append(this);
  }

  /** Append contents to a Tokens list. */
  public void appendContentTo(Tokens list) {
    list.append(this);
  }


  /************************************************************************
  ** Construction:
  ************************************************************************/

  public TextWrap() {
  }

  public TextWrap(TextWrap t) {
    value = t.value;
  }

  public TextWrap(Object v) {
    value = v;
  }

}
