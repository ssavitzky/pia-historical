////// Text.java:  SGML text
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;

/**
 * SGML text strings.
 *	Text is used in the InterForm package to represent sequences of
 *	characters that contain no markup.  The SGML interface is used to
 *	speed up the kinds of testing that the interpretor has to do.
 *
 *	Like all SGML objects, SGML Text can be appended to; if 
 *	StringBuffer wasn't final we could simply extend it.  As it is,
 *	there are some advantages to being forced not to.
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

  /** Return null: the name of the entity to which this is a reference. */
  public String entityName() {
    return null;
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
  public Text append(String t) {
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

  /** Convert the object to a single token. */
  public Token toToken() {
    return new Token("", this);
  }

  /** Convert to a single token if it's a singleton. */
  public SGML simplify() {
    return this;
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
}
